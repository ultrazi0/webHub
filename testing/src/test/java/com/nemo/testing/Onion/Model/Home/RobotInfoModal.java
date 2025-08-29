package com.nemo.testing.Onion.Model.Home;

import com.nemo.testing.Onion.Model.AbstractModal;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
public class RobotInfoModal extends AbstractModal {

    private final By ROBOT_NAME = By.id("robot-info-modal-robot-name-span");
    private final By ROBOT_ID = By.id("robot-info-modal-robot-id-span");
    private final By ROBOT_PASSWORD = By.id("robot-info-modal-robot-password-span");
    private final By ROBOT_OWNED_BY = By.id("robot-info-modal-robot-owner-username-span");
    private final By ROBOT_CREATED_AT = By.id("robot-info-modal-robot-created-at-span");
    private final By ROBOT_ONLINE_STATUS = By.id("robot-info-modal-robot-online-status-span");

    @Override
    public String modalTitle() {
        return "Robot Info";
    }

    public final String getRobotName() {
        return modalElement(ROBOT_NAME).getText();
    }

    public final String getRobotId() {
        return modalElement(ROBOT_ID).getText();
    }

    public final String getRobotPassword() {
        return modalElement(ROBOT_PASSWORD).getText();
    }

    public final String getRobotOwnedBy() {
        return modalElement(ROBOT_OWNED_BY).getText();
    }

    public final String getRobotCreatedAt() {
        return modalElement(ROBOT_CREATED_AT).getText();
    }

    public final String getRobotOnlineStatus() {
        return modalElement(ROBOT_ONLINE_STATUS).getText();
    }

}
