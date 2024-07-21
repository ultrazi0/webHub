package com.nemo.webHub.Sock;


import java.util.ArrayList;

@Deprecated
public class AvailableRobots {

    private ArrayList<Integer> robotIds = new ArrayList<>();

    public ArrayList<Integer> getRobotIds() {
        return robotIds;
    }

    public void addRobotId(Integer id) {
        if (robotIds.contains(id)) {
            throw new IllegalArgumentException("Object with this ID already exists");
        }
        robotIds.add(id);
    }

    public void removeRobotId(int id) {
        robotIds.remove((Integer) id);
    }

    public boolean robotIsConnected(int id) {
        return robotIds.contains(id);
    }
}
