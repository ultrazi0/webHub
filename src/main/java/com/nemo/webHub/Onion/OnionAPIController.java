package com.nemo.webHub.Onion;

import com.nemo.webHub.Commands.CommandType;
import jakarta.annotation.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OnionAPIController {

    @GetMapping("/getAllCommands")
    public CommandType[] getAllCommands() {

        return CommandType.values();
    }

    @Nullable
    @PostMapping("/commandValues")
    public String[] commandValues(@Nullable @RequestParam("commandType") CommandType commandType) {
        if (commandType == null) {
            return null;
        }

        return commandType.getKeys();
    }
}
