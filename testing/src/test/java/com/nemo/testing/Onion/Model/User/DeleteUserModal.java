package com.nemo.testing.Onion.Model.User;

import com.nemo.testing.Onion.Model.AbstractModal;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
class DeleteUserModal extends AbstractModal {

    private final By DELETE_BUTTON = By.xpath(".//div[@class='modal-footer']/button[@type='submit']");

    @Override
    public String modalTitle() {
        return "Delete User";
    }

    public final void pressDeleteButton() {
        modalElement(DELETE_BUTTON).click();
    }
}
