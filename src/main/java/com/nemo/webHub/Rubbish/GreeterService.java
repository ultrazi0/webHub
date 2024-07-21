package com.nemo.webHub.Rubbish;

import org.springframework.stereotype.Service;

@Service
public class GreeterService {

    public String buildGreetingPlease(MoveType direction) {
        return String.format("Thanks to my user, I have successfully been moved a bit %s! I am happy!!",
                direction.toString().toLowerCase());
    }

}
