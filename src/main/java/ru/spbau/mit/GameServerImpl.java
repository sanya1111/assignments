package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;




public class GameServerImpl implements GameServer {
    private class ClientConnection implements Runnable{
        private List<String> pendingMessages = Collections.synchronizedList(new ArrayList<String>());
        //
        //modificated once
        private final Connection connection;
        private final String id;
        //used at one thread
        private Thread receiveLoopThread;
        //
        
        public Connection getConnection() {
            return connection;
        }
        
        public ClientConnection(Connection connection, String id) {
            this.connection = connection;
            this.id = id;
        }
        
        private void invokeSendTask(Task task){
            connection.send(task.getParam());
        }
        
        private void invokeReceiveTask(Task task){
            try {
                String result = connection.receive(Long.parseLong(task.getParam()));
                if(result != null){
                    plugin.onPlayerSentMsg(id, result);
                }
            } catch (NumberFormatException | InterruptedException | IllegalStateException e) {
                // TODO Auto-generated catch block
//                e.printStackTrace();
            }
        }
        
        private void runTask(Task task){
            synchronized (task) {
                switch (task.getType()) {
                case SEND_TASK:
                    invokeSendTask(task);
                    break;
                case RECEIVE_TASK:
                    invokeReceiveTask(task);
                    break;
                case RECEIVE_LOOP_TASK:
                    runReceiveLoop(task);
                }
                finishTask(task);
            }
        }
        
        private void runReceiveLoop(Task task){
            if(receiveLoopThread == null || !receiveLoopThread.isAlive()){
                receiveLoopThread = new Thread(new ReceiveLoopRunnable(task));
                receiveLoopThread.start();
            }
        }
        
        
        private void tasksOperations(){
            while(pendingMessages.size() > 0 && !connection.isClosed()){
                int id = 0;
                Task last = pendingMessages.get(id);
                pendingMessages.remove(id);
                runTask(last);
            }
        }
        
        
        private void init(){
            plugin.onPlayerConnected(id);
        }
        
        @Override
        public void run() {
            synchronized (connection) {
                init();
                while(!connection.isClosed()){
                    if(pendingMessages.size() == 0){
                        try {
                            connection.wait();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    tasksOperations();
                }
            }
            for(Task task : pendingMessages){
                finishTask(task);
            }
        }
        
        public void send(String msg){
            synchronized (connection) {
                pendingMessages.add(new Task(ClientConnectionTaskType.SEND_TASK, msg));
                connection.notifyAll();
            }
        }
        
        public void receive(long timeout){
            receive(new Task(ClientConnectionTaskType.RECEIVE_LOOP_TASK, String.valueOf(timeout)));
        }
        
        private void receive(Task task){
            synchronized (connection) {
                pendingMessages.add(task);
                connection.notifyAll();
            }
        }
        
        public void receiveLoop(long timeout){
            synchronized (connection) {
                pendingMessages.add(new Task(ClientConnectionTaskType.RECEIVE_LOOP_TASK, String.valueOf(timeout)));
                connection.notifyAll();
            }
        }
    }
    
    private final Game plugin ;
    private List<ClientConnection> clientsPool = Collections.synchronizedList(new ArrayList<ClientConnection>());
    
    private String getSetterName(String key){
        String result = "";
        result += key.charAt(0);
        result = result.toUpperCase();
        result = "set" + result + key.substring(1);
        return result;
    }
    
    public GameServerImpl(String gameClassName, Properties properties) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
        this.plugin = (Game) Class.forName(gameClassName).getConstructor(GameServer.class).newInstance(this);
        for(String key : properties.stringPropertyNames()){
            String value = properties.getProperty(key);
            try{
                Long intValue = Long.parseLong(value);
                Method method = plugin.getClass().getMethod(getSetterName(key), Long.class);
                method.invoke(plugin, intValue);
            }catch(NumberFormatException excp){
                Method method = plugin.getClass().getMethod(getSetterName(key), String.class);
                method.invoke(plugin, value);
            }
        }
        
    }
    
    static final Long DEFAULT_TIMEOUT = 2L;
    
    @Override
    public void accept(final Connection connection) {
        (new Thread(new Runnable() {
            
            @Override
            public void run() {
                synchronized (connection) {
                    String id = String.valueOf(clientsPool.size()); 
                    ClientConnection client = new ClientConnection(connection, id);
                    clientsPool.add(client);
                    (new Thread(client)).start();
                    connection.send(id);
                    client.receiveLoop(DEFAULT_TIMEOUT);
                }
            }
        })).start();
      
    }

    @Override
    public void broadcast(final String message) {
        for(int i = 0; i < clientsPool.size(); i++){
            if(!clientsPool.get(i).getConnection().isClosed()){
                sendTo(String.valueOf(i), message);
            }
        }
    }

    @Override
    public void sendTo(String id, String message) {
        clientsPool.get(Integer.parseInt(id)).send(message);
    }
}
