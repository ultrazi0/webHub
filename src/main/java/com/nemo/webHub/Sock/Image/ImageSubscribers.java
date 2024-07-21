package com.nemo.webHub.Sock.Image;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Component
public class ImageSubscribers {
    private final HashMap<Integer, LinkedList<WebSocketSession>> robotIdToSessionsHashMap = new HashMap<>();
    private final Set<Integer> connectedRobots = new HashSet<>();

    public LinkedList<WebSocketSession> getSessionsByRobotId(Integer id) {
        return robotIdToSessionsHashMap.get(id);
    }

    public boolean hashMapHasThisRobotId(Integer id) {
        return robotIdToSessionsHashMap.containsKey(id);
    }

    public boolean robotIsConnected(Integer robotId) {
        return connectedRobots.contains(robotId);
    }

    public void addSession(Integer robotId, WebSocketSession session) {
        if (hashMapHasThisRobotId(robotId)) {
            robotIdToSessionsHashMap.get(robotId).add(session);
        } else {
            LinkedList<WebSocketSession> newSessionList = new LinkedList<>() {};
            newSessionList.add(session);
            robotIdToSessionsHashMap.put(robotId, newSessionList);
        }
    }

    public void addRobot(Integer robotId) {
        if (connectedRobots.contains(robotId)) {
            // Technically should never happen
            throw new IllegalArgumentException("Robot with ID #" + robotId + " is already connected");
        }

        connectedRobots.add(robotId);

        if (hashMapHasThisRobotId(robotId)) {
            // If it exists, it means the session-listener was connected earlier - in this case just do nothing
            return;
        }

        robotIdToSessionsHashMap.put(robotId, new LinkedList<>());
    }

    public void removeSession(Integer robotId, WebSocketSession session) {
        if (!hashMapHasThisRobotId(robotId)) {
            throw new IllegalArgumentException("HashMap has no robot with ID #" + robotId);
        }

        LinkedList<WebSocketSession> sessionsList = robotIdToSessionsHashMap.get(robotId);

        if (!sessionsList.remove(session)) {
            throw new IllegalArgumentException("Session is not in the list");
        }

        if ((!connectedRobots.contains(robotId)) && (sessionsList.isEmpty())) {
            // Robot has already disconnected and the sessions list is empty - it is safe to remove the entry
            robotIdToSessionsHashMap.remove(robotId);
        }
    }

    public void removeRobot(Integer robotId) {
        if (!connectedRobots.remove(robotId)) {
            throw new IllegalArgumentException("Robot with ID #" + robotId + " was not connected");
        }

        if (robotIdToSessionsHashMap.get(robotId).isEmpty()) {
            // There are no sessions that still listen - it is safe to remove the entry
            robotIdToSessionsHashMap.remove(robotId);
        }
    }

    @Deprecated
    public void printEverythingOut() {
        // This function exists only to test the working

        System.out.print("HashMap: ");
        System.out.println(robotIdToSessionsHashMap);
        System.out.print("List: ");
        System.out.println(connectedRobots);
    }
}
