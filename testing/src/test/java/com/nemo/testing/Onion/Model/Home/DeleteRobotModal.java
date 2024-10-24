package com.nemo.testing.Onion.Model.Home;

import com.nemo.testing.Onion.Model.AbstractModal;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
public class DeleteRobotModal extends AbstractModal {

    private final By DELETE_BUTTON = By.xpath(".//div[@class='modal-footer']/button[@type='submit']");

    @Override
    public String modalTitle() {
        return "Delete robot";
    }

    public final void pressDeleteButton() {
        modalElement(DELETE_BUTTON).click();
    }

}
