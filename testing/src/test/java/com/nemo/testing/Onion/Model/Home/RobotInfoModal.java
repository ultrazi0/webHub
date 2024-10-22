package com.nemo.testing.Onion.Model.Home;

import com.nemo.testing.Onion.Model.AbstractModal;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
public class RobotInfoModal extends AbstractModal {

    private final By ROBOT_NAME = By.xpath(".//div[@class='modal-body']/p/b[text()='Name:']/following-sibling::text()[2]");
    private final By ROBOT_OWNED_BY = By.xpath(".//div[@class='modal-body']/p/b[text()='Owned by:']/following-sibling::text()[2]");
    private final By ROBOT_ONLINE_STATUS = By.xpath(".//div[@class='modal-body']/p/i/span");

    public final String getRobotName() {
        return modalElement(ROBOT_NAME).getText();
    }

    public final String getRobotOwnedBy() {
        return modalElement(ROBOT_OWNED_BY).getText();
    }

    public final String getRobotOnlineStatus() {
        return modalElement(ROBOT_ONLINE_STATUS).getText();
    }

}
