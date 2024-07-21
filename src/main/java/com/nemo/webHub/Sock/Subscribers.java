package com.nemo.webHub.Sock;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;

@Deprecated
public class Subscribers {
    private HashMap<String, WebSocketSession> subscribers = new HashMap<>();

    public HashMap<String, WebSocketSession> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(HashMap<String, WebSocketSession> subscribers) {
        this.subscribers = subscribers;
    }

    public void addSubscriber(String id, WebSocketSession session) {
        this.subscribers.put(id, session);
    }

    public boolean removeSubscriber(String id) {
        if (this.subscribers.containsKey(id)) {
            this.subscribers.remove(id);
            return true;
        }

        return false;
    }
}
