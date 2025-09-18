package Observers;

import Core.Entity;
import Observers.Events.Event;

public interface Observer {

    void onNotify(Entity entity, Event event);
}
