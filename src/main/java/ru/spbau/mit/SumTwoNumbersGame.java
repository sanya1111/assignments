package ru.spbau.mit;

import java.util.*;

public class SumTwoNumbersGame implements Game {

    private class State {
        private final int a, b, result;

        public State(int a, int b) {
            super();
            this.a = a;
            this.b = b;
            this.result = a + b;
        }

        public int getResult() {
            return result;
        }

        @Override
        public String toString() {
            return String.valueOf(a) + " " + String.valueOf(b);
        }
    }

    private final GameServer server;
    private final Random random = new Random(42);
    private volatile State currentState;

    private static final int MAX_VALUE_BOUND = 100;

    private void genState() {
        currentState = new State(random.nextInt(MAX_VALUE_BOUND),
                random.nextInt(MAX_VALUE_BOUND));
    }

    private void newRound() {
        genState();
        server.broadcast(currentState.toString());
    }

    public SumTwoNumbersGame(GameServer server) {
        this.server = server;
        genState();
    }

    @Override
    public synchronized void onPlayerConnected(String id) {
        server.sendTo(id, currentState.toString());
    }

    @Override
    public synchronized void onPlayerSentMsg(String id, String msg) {
        int result = Integer.parseInt(msg);
        if (result == currentState.getResult()) {
            server.sendTo(id, "Right");
            server.broadcast(id + " won");
            newRound();
        } else {
            server.sendTo(id, "Wrong");
        }
    }
}
