package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;




public class GameServerImpl implements GameServer {
    private enum ClientConnectionTaskType{
        SEND_TASK, 
        RECEIVE_TASK,
        RECEIVE_LOOP_TASK,
    }
    
    private class ClientConnection implements Runnable{
        private class Task{
            private volatile boolean finished = false;
            //modificated at one thread and THAN pushed to another
            private ClientConnectionTaskType type;
            //modificated once
            private final String param;
            //
            
            public ClientConnectionTaskType getType() {
                return type;
            }

            public String getParam() {
                return param;
            }
           
            public Task(ClientConnectionTaskType type,  String params) {
                super();
                this.type = type;
                this.param = params;
            }
            
            public void setFinished(){
                finished = true;
            }
            
            public void setStart(){
                finished = false;
            }
            
            public boolean isFinished(){
                return finished;
            }
        }
        
        private class ReceiveLoopRunnable implements Runnable{
            //using at one thread
            private Task receiveTask;
            //
            public ReceiveLoopRunnable(Task task) {
                this.receiveTask = task;
                receiveTask.type = ClientConnectionTaskType.RECEIVE_TASK;
            }
            
            @Override
            public void run() {
                while(!connection.isClosed()){
                    receiveTask.setStart();
                    receive(receiveTask);
                    synchronized (receiveTask) {
                        try {
                            while(!receiveTask.isFinished()){
                                receiveTask.wait();
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }
        
        private List<Task> pendingTasks = Collections.synchronizedList(new ArrayList<Task>());
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
                task.setFinished();
                task.notify();
            }
        }
        
        private void runReceiveLoop(Task task){
            if(receiveLoopThread == null || !receiveLoopThread.isAlive()){
                receiveLoopThread = new Thread(new ReceiveLoopRunnable(task));
                receiveLoopThread.start();
            }
        }
        
        
        private void tasksOperations(){
            while(pendingTasks.size() > 0){
                int id = 0;
                Task last = pendingTasks.get(id);
                pendingTasks.remove(id);
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
                    tasksOperations();
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
        
        public void send(String msg){
            synchronized (connection) {
                pendingTasks.add(new Task(ClientConnectionTaskType.SEND_TASK, msg));
                connection.notifyAll();
            }
        }
        
        public void receive(long timeout){
            receive(new Task(ClientConnectionTaskType.RECEIVE_LOOP_TASK, String.valueOf(timeout)));
        }
        
        private void receive(Task task){
            synchronized (connection) {
                pendingTasks.add(task);
                connection.notifyAll();
            }
        }
        
        public void receiveLoop(long timeout){
            synchronized (connection) {
                pendingTasks.add(new Task(ClientConnectionTaskType.RECEIVE_LOOP_TASK, String.valueOf(timeout)));
                connection.notifyAll();
            }
        }
    }
    
    private Game plugin = null;
    private List<ClientConnection> clientsPool = Collections.synchronizedList(new ArrayList<ClientConnection>());
    
    private String getSetterName(String key){
        String result = "";
        result += key.charAt(0);
        result = result.toUpperCase();
        result = "set" + result + key.substring(1);
        return result;
    }
    
    public GameServerImpl(String gameClassName, Properties properties) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
        plugin = (Game) Class.forName(gameClassName).getConstructor(GameServer.class).newInstance(this);
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
        String id = String.valueOf(clientsPool.size()); 
        ClientConnection client = new ClientConnection(connection, id);
        clientsPool.add(client);
        (new Thread(client)).start();
        synchronized (connection) {
            connection.send(id);
        }
        client.receiveLoop(DEFAULT_TIMEOUT);
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
