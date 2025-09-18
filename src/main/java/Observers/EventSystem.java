package Observers;

import Core.Entity;
import Observers.Events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {

    private static final List<Observer> observers = new ArrayList<>();

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    public static void notify(Entity entity, Event event) {
        for(Observer observer : observers) {
            observer.onNotify(entity, event);
        }
    }
}
