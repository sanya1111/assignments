package ru.spbau.mit;

import java.util.*;


public class SumTwoNumbersGame implements Game {
    
    private class State{
        private int a, b;

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }

        public State(int a, int b) {
            super();
            this.a = a;
            this.b = b;
        }
        
        @Override
        public String toString() {
            return String.valueOf(a) + " " + String.valueOf(b);
        }
    }

    private GameServer server = null;
    private Random random = new Random(42);
    private Map<String, State> playerStates = Collections.synchronizedMap(new HashMap<String , State>());
    
    public SumTwoNumbersGame(GameServer server) {
        this.server = server;
    }

    @Override
    public void onPlayerConnected(String id) {
        State state = new State(random.nextInt(10), random.nextInt(10));
        playerStates.put(id, state);
        server.sendTo(id, state.toString());
    }
    
    @Override
    public void onPlayerSentMsg(String id, String msg) {
        int result =  Integer.parseInt(msg);
        if(result == playerStates.get(id).getA() + playerStates.get(id).getB()){
            server.sendTo(id, "Right");
            server.broadcast(id + " won");
            onPlayerConnected(id);
        } else {
            server.sendTo(id, "Wrong");
        }
    }
}
