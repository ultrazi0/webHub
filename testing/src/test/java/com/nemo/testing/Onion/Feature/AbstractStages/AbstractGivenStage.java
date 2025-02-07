package com.nemo.testing.Onion.Feature.AbstractStages;

import com.codeborne.selenide.Selenide;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.core.Persistence.PersistenceServiceMapper;
import com.nemo.testing.core.Persistence.UserService;
import com.nemo.testing.core.Persistence.WithPersistence;
import com.nemo.testing.core.TypedClassInstanceMap;
import com.nemo.webHub.Decibel.UserEntity;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.Assumptions;
import org.assertj.core.api.WithAssumptions;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.generated.tables.records.UsersRecord;
import org.openqa.selenium.Cookie;
import org.springframework.beans.factory.annotation.Autowired;

import static com.codeborne.selenide.WebDriverRunner.driver;
import static com.codeborne.selenide.WebDriverRunner.url;

/**
 * The AbstractGivenStage class serves as a base stage for JGiven testing scenarios.
 * It extends the AbstractStage class and provides utility methods to ensure the correct
 * page is loaded and rendered within the testing framework.
 */
@Slf4j
@JGivenStage
public abstract class AbstractGivenStage<T extends AbstractGivenStage<T>> extends AbstractStage<T>
    implements WithAssumptions {

    private static final String TEST_USER_USERNAME = "testUser";
    private static final String TEST_USER_PASSWORD = "testUserPassword";

    @Autowired
    private UserService userService;
    @Autowired
    private PersistenceServiceMapper persistenceServiceMapper;

    @BeforeScenario
    protected void beforeScenario() {
        createdEntities = new TypedClassInstanceMap();
    }

    /**
     * Opens the page. Implicitly waits for the page to render
     *
     * @param page page to open
     * */
    protected void open(AbstractPage page) {
        page.openPage();
        assumeOnPage(page);
        assumeRendered(page);
    }

    /**
     * Assumes that the current URL matches the URL of the given page.
     * This method verifies that the browser is currently on the page by comparing the current URL
     * with the expected URL constructed from the base URL and the page's URI.
     * If the URLs do not match, an assumption failure is triggered.
     */
    protected void assumeOnPage(AbstractPage page) {
        assumeThat(url())
            .as("Oops... wrong page :(")
            .isEqualTo(fullUrlOf(page.uri()));
    }

    /**
     * Assumes that the given page has been rendered.
     * Uses assumptions to ensure that the method `waitUntilRendered` does not throw any exceptions.
     *
     * @param page the page to check if it has been rendered
     */
    protected void assumeRendered(AbstractPage page) {
        Assumptions.assumeThatThrownBy(page::waitUntilRendered)
            .as("Check page rendered")
            .withFailMessage("Page has not been rendered!")
            .doesNotThrowAnyException();
    }

    /**
     * A wrapper around AssertJ {@code assumeThat()} that adds screenshot to a JGiven report
     *
     * @param condition boolean condition to assert
     * @param description the <b>description</b> of the assertion,
     *                    do <u>NOT</u> write your error message here - use {@code withFailMessage(String)}
     *                    if you really wish to add one!
     * @return {@code AbstractBooleanAssert<?>}, so that you can chain all the following checks and conditions
     * */
    protected AbstractBooleanAssert<?> assumeTakingScreenshotThat(boolean condition, String description, Object ...args) {
        return Assumptions.assumeThat(condition)
            .as(addScreenshotToDescription(description, args));
    }

    /**
     * Cleans up the browser state after a testing scenario.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Logs out the user</li>
     *     <li>Clears the browser's session storage</li>
     *     <li>Clears the browser's local storage</li>
     *     <li>Clears all cookies from the browser</li>
     *     <li>Refreshes the browser page</li>
     * </ul>
     */
    @AfterScenario
    private void cleanup() {
        // Most of the time the test will require login, thus sending an additional, third, check request
        //  for all tests would only increase the total execution time, since the two requests saved in a few
        //  tests without login would be overly compensated by the third request sent in all other cases
        logout();
        cleanupCreatedEntities();

        Selenide.sessionStorage().clear();
        Selenide.clearBrowserLocalStorage();
        Selenide.clearBrowserCookies();
        Selenide.refresh();
    }

    private void logout() {
        String sessionId = null;
        Cookie sessionIdCookie = driver().getWebDriver().manage().getCookieNamed(apiService.getSessionCookieName());
        if (sessionIdCookie != null) {
            sessionId = sessionIdCookie.getValue();
        }
        apiService.reset(sessionId);
    }

    private void cleanupCreatedEntities() {
        createdEntities.forEach((type, entitySet) -> {
            persistenceServiceMapper.getPersistenceService(type).deleteAll(entitySet);
            entitySet.clear();
        });
    }

    /**
     * Automatically creates and registers for deletion an entity of the given type from the given record
     *
     * @param cls type of the entity
     * @param record database record to create DB entry from
     * */
    protected <E> E createEntity(Class<E> cls, Record record) {
        E createdEntity;
        WithPersistence<E> persistenceService = persistenceServiceMapper.getPersistenceService(cls);

        try {
            createdEntity = persistenceService.createEntityFrom(record);
            createdEntities.addInstance(cls, createdEntity);
        } catch (DataAccessException ignored) {
            // Even if the user was not created in this test, it still uses a test username, and therefore should be deleted
            createdEntity = persistenceService.getFrom(record);
            createdEntities.addInstance(cls, createdEntity);
            log.warn("Record {} already exists, it will be deleted after this test", record);
        }

        assumeThat(createdEntity)
            .as("Assume entity is created")
            .isNotNull();
        return createdEntity;
    }

    /**
     * An action. Ensures that the user is not currently logged in by sending a request to the API
     * to retrieve the currently logged-in user. If the user is already logged in,
     * an assumption failure with a specific message is triggered.
     *
     * @return the current instance (self) for method chaining
     */
    public T not_logged_in() {
        assumeThat(isLoggedIn())
            .as("Check if already logged in")
            .withFailMessage("I am already logged in")
            .isFalse();

        return self();
    }

    /**
     * Logs in as the test user by sending an API request and providing session cookie to the driver.
     *
     * <p>Also provides the ID of the currently logged-in user to the stage state</p>
     *
     * @return the current instance (self) for method chaining
     */
    public T test_user() {

        UsersRecord usersRecord = new UsersRecord();
        usersRecord.setUsername(TEST_USER_USERNAME);
        usersRecord.setPassword(TEST_USER_PASSWORD);
        createEntity(UserEntity.class, usersRecord);

        logInAs(TEST_USER_USERNAME, TEST_USER_PASSWORD);

        return self();
    }

    protected void logInAs(String username, String password) {
        mainPage().openHomePage();

        assumeThat(apiService.login(username, password))
            .as("Check if automatic login successful")
            .isTrue();

        driver().getWebDriver().manage().addCookie(
            new Cookie(apiService.getSessionCookieName(), apiService.getSessionId()));

        CURRENT_USER = userService.getUserByUsername(username);
    }
}
