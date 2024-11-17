package com.nemo.testing.core.API;

import io.restassured.config.SessionConfig;
import io.restassured.filter.session.SessionFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.stereotype.Service;

@Service
public class APIService implements WithBaseEndpoints {

    private static SessionFilter sessionFilter = new SessionFilter();
    private static CsrfFilter csrfFilter = new CsrfFilter(getCsrfConfig());

    private static final String CSRF_URI = "/csrf";

    public Response send(Request request) {
        Endpoint endpoint = request.getEndpoint();
        if (endpoint == null) {
            throw new IllegalStateException("No endpoint specified");
        }
        return prepared(request).request(endpoint.method(), endpoint.uri());
    }

    private RequestSpecification prepared(Request request) {
        return request.filter(csrfFilter).filter(sessionFilter);
    }

    public boolean login(String username, String password) {
        Request request = Request.createTo(LOGIN_ENDPOINT);
        request.formParam("username", username).formParam("password", password);

        return send(request).statusCode() == 200;
    }

    public String getSessionCookieName() {
        return SessionConfig.DEFAULT_SESSION_ID_NAME;
    }

    public String getSessionId() {
        return sessionFilter.getSessionId();
    }

    public void reset() {
        reset(null);
    }

    public void reset(String sessionId) {
        logout(sessionId);
        sessionFilter = new SessionFilter();
        csrfFilter = new CsrfFilter(getCsrfConfig());
    }

    private static CsrfConfig getCsrfConfig() {
        return new CsrfConfig(CSRF_URI, sessionFilter);
    }

    private void logout(String sessionId) {
        Request request = Request.createTo(LOGOUT_ENDPOINT);
        if (sessionId != null) request.sessionId(sessionId);
        send(request);
    }
}
