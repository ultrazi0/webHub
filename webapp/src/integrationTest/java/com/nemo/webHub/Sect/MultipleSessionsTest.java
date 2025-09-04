/*
*
* File was taken from Spring Security documentation:
* https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html
*
*/

package com.nemo.webHub.Sect;

import com.nemo.webHub.Decibel.UserEntity;
import com.nemo.webHub.Decibel.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MultipleSessionsTest {

    private static final String loginUri = "/api/login";
    private static final String userUri = "/api/user";

    private static final String username = "testUser";
    private static final String password = "testPassword";

    private UserEntity user;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        user = userRepository.addNewUser(username, passwordEncoder.encode(password));
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteUser(user.getId());
    }

    @Test
    void testLogin() throws Exception {
        mvc.perform(formLogin(loginUri).user(username).password(password)).andExpect(authenticated());
    }

    @Test
    void testLogout() throws Exception {
        MvcResult loginResult = mvc
            .perform(formLogin(loginUri).user(username).password(password))
            .andExpect(authenticated())
            .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();
        Assert.notNull(session, "Mock session cannot be null");

        mvc.perform(post("/api/logout").session(session).with(csrf()));

        mvc.perform(get(userUri).session(session)).andExpect(unauthenticated());
    }

    @Test
    void loginOnSecondLoginThenFirstSessionTerminated() throws Exception {
        MvcResult firstLoginResult = mvc
            .perform(formLogin(loginUri).user(username).password(password))
            .andExpect(authenticated())
            .andReturn();

        MockHttpSession firstLoginSession = (MockHttpSession) firstLoginResult.getRequest().getSession();

        Assert.notNull(firstLoginSession, "Mock session cannot be null");
        mvc.perform(get(userUri).session(firstLoginSession)).andExpect(authenticated());

        mvc.perform(formLogin(loginUri).user(username).password(password)).andExpect(authenticated());

        // First session is terminated by second login
        mvc.perform(get(userUri).session(firstLoginSession)).andExpect(unauthenticated());
    }
}
