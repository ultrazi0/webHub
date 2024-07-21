package com.nemo.webHub.Onion.Controller;

import com.nemo.webHub.Commands.CommandType;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class OnionWebController {

    @ModelAttribute("allCommandTypes")
    public List<CommandType> allCommandTypes() {
        return Arrays.asList(CommandType.values());
    }
    @GetMapping("/cp")
    public String controlPanel(Model model) {
        model.addAttribute("moveCommandType", CommandType.MOVE);
        model.addAttribute("turretCommandType", CommandType.TURRET);
        model.addAttribute("stopCommandType", CommandType.STOP);
        return "controlPanel";
    }

    @PostMapping("/getCommandValues")
    public String getCommandValues(Model model, @Nullable @RequestParam("commandType") CommandType commandType) {

        model.addAttribute("arguments", commandType == null ? new String[] {} : commandType.getKeys());
        return "commandValues :: valuesTable";
    }
}
