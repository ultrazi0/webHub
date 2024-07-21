package com.nemo.webHub.Rubbish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/move")
public class Movement {

    @Autowired
    private GreeterService greeterService;

    @GetMapping("/{direction}")
    public String move(@PathVariable MoveType direction) {

        return greeterService.buildGreetingPlease(direction);

    }

    @PostMapping("/test")
    String test(@RequestParam(name = "name") String name) {

        return "Hello, " + name + "!";
    }
}
