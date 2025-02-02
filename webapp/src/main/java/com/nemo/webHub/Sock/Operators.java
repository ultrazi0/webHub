package com.nemo.webHub.Sock;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Operators {

    private final HashMap<String, Integer> operatorSessionToRobot = new HashMap<>();
    private final HashMap<Integer, String> robotToOperatorSession = new HashMap<>();

    public Integer getRobotId(String sessionId) {
        return operatorSessionToRobot.get(sessionId);
    }

    public String getOperatorSessionId(Integer robotId) {
        return robotToOperatorSession.get(robotId);
    }

    public void addOperator(String sessionId, int robotId) {
        if (operatorSessionToRobot.containsKey(sessionId)) {
            throw new IllegalArgumentException(
                    "This session already controls another robot. You cannot control more than one!"
            );
        }

        if (robotToOperatorSession.containsKey(robotId)) {
            throw new IllegalArgumentException("This robot is already controlled by another session");
        }

        operatorSessionToRobot.put(sessionId, robotId);
        robotToOperatorSession.put(robotId, sessionId);
    }

    public void removeOperator(String sessionId) {
        if (!operatorSessionToRobot.containsKey(sessionId)) {
            throw new IllegalArgumentException("There is no entry with this key (session)");
        }

        robotToOperatorSession.remove(operatorSessionToRobot.remove(sessionId));
    }

}
