package com.nemo.testing.Onion.Model.Home;

import com.nemo.testing.Onion.Model.AbstractModal;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
public class EditRobotModal extends AbstractModal {

    private final By ROBOT_NAME = By.id("formName");
    private final By EDIT_ROBOT_BUTTON = By.xpath(".//div[@class='modal-footer']/button[@type='submit']");

    public final String getRobotName() {
        return modalElement(ROBOT_NAME).getText();
    }

    public final void enterRobotName(String name) {
        modalElement(ROBOT_NAME).setValue(name);
    }

    public final void pressEditRobotButton() {
        modalElement(EDIT_ROBOT_BUTTON).click();
    }
}
