package com.example.tom_project22;
/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import java.util.Observable;

public class ChangeButtonStatus extends Observable {

    private boolean play = false;
    /**
     *alarms the observers to change the button what setPlay(true)
    */
    public void setPlay(boolean play){
        synchronized (this){
            this.play = play;
        }
        setChanged();
        notifyObservers();
    }

    public synchronized boolean getPlay(){
        return play;
    }
}
