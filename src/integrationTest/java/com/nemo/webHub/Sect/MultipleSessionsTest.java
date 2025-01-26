/*
*
* File was taken from Spring Security documentation:
* https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html
*
*/

package com.nemo.webHub.Sect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.Assert;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MultipleSessionsTest {

    @Autowired
    private MockMvc mvc;
    private final String loginUri = "/api/login";

    @Test
    void testLogin() throws Exception {
        mvc.perform(formLogin(loginUri)).andExpect(authenticated());
    }

    @Test
    void testLogout() throws Exception {
        RequestBuilder loginRequest = formLogin(loginUri);
        MvcResult loginResult = mvc.perform(loginRequest).andExpect(authenticated()).andReturn();
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();
        Assert.notNull(session, "Mock session cannot be null");

        mvc.perform(post("/api/logout").session(session).with(csrf()));

        mvc.perform(get("/api/user").session(session)).andExpect(unauthenticated());
    }

    @Test
    void loginOnSecondLoginThenFirstSessionTerminated() throws Exception {
        MvcResult firstLoginResult = mvc.perform(formLogin(loginUri)).andExpect(authenticated()).andReturn();

        MockHttpSession firstLoginSession = (MockHttpSession) firstLoginResult.getRequest().getSession();

        Assert.notNull(firstLoginSession, "Mock session cannot be null");
        mvc.perform(get("/api/user").session(firstLoginSession)).andExpect(authenticated());

        mvc.perform(formLogin(loginUri)).andExpect(authenticated());

        // First session is terminated by second login
        mvc.perform(get("/api/user").session(firstLoginSession)).andExpect(unauthenticated());
    }
}
