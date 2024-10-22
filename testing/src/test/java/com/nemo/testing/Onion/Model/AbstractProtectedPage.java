package com.nemo.testing.Onion.Model;

import com.nemo.testing.Onion.Model.Sect.LoginPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractProtectedPage extends AbstractPage {

    @Autowired
    private LoginPage loginPage;

    public void performLogin(String username, String password) {
        loginPage.openPage();

        loginPage.enterCredentials(username, password);
        loginPage.pressSubmit();
    }

}
