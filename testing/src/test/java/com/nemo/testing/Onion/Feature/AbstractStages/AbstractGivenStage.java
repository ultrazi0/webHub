package com.nemo.testing.Onion.Feature.AbstractStages;

import com.codeborne.selenide.Selenide;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.tngtech.jgiven.annotation.AfterScenario;
import org.assertj.core.api.Assumptions;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.assertj.core.api.Assumptions.assumeThat;

public abstract class AbstractGivenStage<T extends AbstractGivenStage<T, P>, P extends AbstractPage> extends AbstractStage<T, P> {

    protected void openPage() {
        mainPage.openPage();
        Assumptions.assumeThatThrownBy(() -> mainPage.waitUntilRendered())
            .as("Page has not been rendered!")
            .doesNotThrowAnyException();
        assumeOnMainPage();
    }

    protected void assumeOnMainPage() {
        assumeThat(url())
            .as("Oops... wrong page :(")
            .isEqualTo(baseUrl + mainPage().uri());
    }

    @AfterScenario
    private void cleanup() {
        if (mainPage().isLoggedIn()) {
            mainPage().performLogout();
        }

        Selenide.sessionStorage().clear();
        Selenide.clearBrowserLocalStorage();
        Selenide.clearBrowserCookies();
        Selenide.refresh();
    }

}
