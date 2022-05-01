package controller;

import view.Observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Martin Stemmer
 *
 */
public abstract class Subject {

    private List<Observer> observerList = new CopyOnWriteArrayList<>();

    /**
     * Add viewpoint to this subject if new
     *
     * @param obs
     */
    public void attach(Observer obs){
        if(obs == null)
            throw new IllegalArgumentException();

        synchronized (observerList){
            if(!observerList.contains(obs)){
                observerList.add(obs);
            }
        }
    }

    /**
     * Delete viewpoint from this subject
     *
     * @param obs viewpointS
     */
    public void detach(Observer obs){
        if(obs == null)
            throw new IllegalArgumentException();

        synchronized (observerList){
            observerList.remove(obs);
        }
    }

    /**
     * This function notifies all viewpoints of this subject. Call this function when you change the state of the program.
     */
    public void notifyObservers(){
        observerList.forEach(x -> x.update(this));
    }
}