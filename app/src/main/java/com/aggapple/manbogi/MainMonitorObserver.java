package com.aggapple.manbogi;

import java.util.Observable;
import java.util.Observer;

public class MainMonitorObserver extends Observable {
    public static MainMonitorObserver INSTANCE = new MainMonitorObserver();

    public static MainMonitorObserver getInstance() {
        return INSTANCE;
    }

    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }
}
