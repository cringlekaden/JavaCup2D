package Observers.Events;

public class Event {

    public EventType eventType;

    public Event() {
        eventType = EventType.UserEvent;
    }

    public Event(EventType eventType) {
        this.eventType = eventType;
    }
}
