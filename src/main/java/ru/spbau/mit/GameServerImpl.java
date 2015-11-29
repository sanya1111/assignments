package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class GameServerImpl implements GameServer {
    private class ClientConnection implements Runnable {
        private final List<String> pendingMessages = Collections
                .synchronizedList(new ArrayList<String>());

        private final Connection connection;
        private final String id;

        private final static int DEFAULT_RECEIVE_TIMEOUT = 2;

        public ClientConnection(Connection connection, String id) {
            this.connection = connection;
            this.id = id;
            pendingMessages.add(id);
        }

        private void messagesOperations() {
            synchronized (connection) {
                while (pendingMessages.size() > 0 && !connection.isClosed()) {
                    int id = 0;
                    String msg = pendingMessages.get(id);
                    pendingMessages.remove(id);
                    connection.send(msg);
                }
            }
        }

        private void receive() throws InterruptedException {
            synchronized (connection) {
                if (connection.isClosed()) {
                    return;
                }
                String receivedMsg = connection
                        .receive(DEFAULT_RECEIVE_TIMEOUT);
                if (receivedMsg != null) {
                    plugin.onPlayerSentMsg(id, receivedMsg);
                }
            }
        }

        private void init() {
            plugin.onPlayerConnected(id);
        }

        @Override
        public void run() {
            init();
            while (!connection.isClosed()) {
                messagesOperations();
                try{
                    receive();
                }catch(InterruptedException e){
                    e.printStackTrace();
                    return;
                }
            }
        }

        public void send(String msg) {
            pendingMessages.add(msg);
        }
    }

    private final Game plugin;
    private final List<ClientConnection> clientsPool = Collections
            .synchronizedList(new ArrayList<ClientConnection>());

    private String getSetterName(String key) {
        String result = "";
        result += key.charAt(0);
        result = result.toUpperCase();
        result = "set" + result + key.substring(1);
        return result;
    }

    public GameServerImpl(String gameClassName, Properties properties)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        this.plugin = (Game) Class.forName(gameClassName)
                .getConstructor(GameServer.class).newInstance(this);
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            Method method;
            Object arg;
            try {
                arg = Integer.parseInt(value);
                method = plugin.getClass().getMethod(getSetterName(key),
                        int.class);
            } catch (NumberFormatException excp) {
                arg = value;
                method = plugin.getClass().getMethod(getSetterName(key),
                        String.class);
            }
            method.invoke(plugin, arg);
        }

    }

    @Override
    public void accept(final Connection connection) {
        String id = String.valueOf(clientsPool.size());
        ClientConnection client = new ClientConnection(connection, id);
        clientsPool.add(client);
        new Thread(client).start();
    }

    @Override
    public void broadcast(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < clientsPool.size(); i++) {
                    sendTo(String.valueOf(i), message);
                }
            }
        }).start();
    }

    @Override
    public void sendTo(String id, String message) {
        clientsPool.get(Integer.parseInt(id)).send(message);
    }
}
