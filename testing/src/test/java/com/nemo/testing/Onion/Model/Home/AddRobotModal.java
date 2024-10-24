package com.nemo.testing.Onion.Model.Home;

import com.nemo.testing.Onion.Model.AbstractModal;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
public class AddRobotModal extends AbstractModal {

    private final By ROBOT_NAME_INPUT = By.id("formName");
    private final By ADD_ROBOT_BUTTON = By.xpath( ".//div[@class='modal-footer']/button[@type='submit']");

    @Override
    public String modalTitle() {
        return "Add robot";
    }

    public final void enterRobotName(String name) {
        modalElement(ROBOT_NAME_INPUT).setValue(name);
    }

    public final void pressAddRobotButton() {
        modalElement(ADD_ROBOT_BUTTON).click();
    }

}
