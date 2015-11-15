package ru.spbau.mit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class QuizGame implements Game {
    private class QuizTask{
        private String question;
        private String answer;
        public QuizTask(String question, String answer) {
            super();
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        
        }
        
        public String getAnswer() {
            return answer;
        }
    }

    private enum State{
        STOPPED,
        RUNNING
    }
    
    private class HintRunnable implements Runnable{
        private volatile boolean over = false;
        @Override
        public synchronized void run() {
            while(!over){
                try {
                    wait(delayUntilNextLetter);
                }  catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if(over){
                    return;
                }
                boolean correct = publishNextHint();
                if(!correct){
                    publishAnswer();
                    nextRound();
                }
            }
        }
        
        public synchronized void off(){
            over = true;
            notify();
        }
    }
    
    private volatile List<QuizTask> quiz = new ArrayList<QuizTask>();
    private volatile int quizIndex = -1;
    private volatile int letterIndex = 0;
    private volatile State state = State.STOPPED;
    private volatile HintRunnable hrunnable = null;
    
    private GameServer server;
    private Long delayUntilNextLetter; 
    private Long maxLettersToOpen; 
    private String dictionaryFilename;
    
    
    public QuizGame(GameServer server) {
        this.server = server;   
    }
    
    public void setDelayUntilNextLetter(Long value){
        delayUntilNextLetter = value;
    }
    
    public void setMaxLettersToOpen(Long value){
        maxLettersToOpen = value;
    }
    
    public void setDictionaryFilename(String dictionaryFilename) {
        this.dictionaryFilename = dictionaryFilename;
        loadFile();
    }
    
    private void loadFile(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream (new File(dictionaryFilename))));
            String line = null;
            try {
                while((line = reader.readLine()) != null){
                    String [] kV = line.split(";");
                    quiz.add(new QuizTask(kV[0], kV[1]));
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerConnected(String id) {    }
    
    
    
    private synchronized boolean publishNextHint(){
        letterIndex++;
        if(letterIndex > maxLettersToOpen){
            return false;
        }
        server.broadcast("Current prefix is " + quiz.get(quizIndex).getAnswer().substring(0, letterIndex));
        return true;
    }

    @Override
    public synchronized void onPlayerSentMsg(String id, String msg) {
        if(state == State.STOPPED){
            if(msg.equals("!start")){
                startGame();
            }
        } else {
            if(msg.equals("!start")){
                return;
            }
            if(msg.equals("!stop")){
                stopGame(id);
            } else {
                checkAnswer(id, msg);
            }
        }
    }
    
    private synchronized void startGame(){
        state = State.RUNNING;
        nextRound();
    }
    
    private synchronized void stopGame(String id){
        state = State.STOPPED;
        server.broadcast("Game has been stopped by " + id);
    }
    
    private synchronized void publishQuestion(){
        server.broadcast("New round started: " +  quiz.get(quizIndex).getQuestion() + " (" + String.valueOf( quiz.get(quizIndex).getAnswer().length()) + " letters)");
    }
    
    private synchronized void nextRound(){
        if(hrunnable != null){
            hrunnable.off();
        }
        quizIndex = (quizIndex + 1) % quiz.size();
        letterIndex = 0;
        publishQuestion();
        hrunnable = new HintRunnable();
        (new Thread(hrunnable)).start();
    }
    
    private synchronized void publishAnswer(){
        server.broadcast("Nobody guessed, the word was " + quiz.get(quizIndex).getAnswer());
    }
    
    private synchronized void publishWin(String id){
        server.broadcast("The winner is " + id);
    }
    
    private synchronized void checkAnswer(String id, String msg){
        if(msg.equals(quiz.get(quizIndex).answer)){
            publishWin(id);
            nextRound();
        } else {
            server.sendTo(id, "Wrong try");
        }
    }
}
