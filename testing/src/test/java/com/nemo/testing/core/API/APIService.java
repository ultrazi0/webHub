package com.nemo.testing.core.API;

import io.restassured.filter.session.SessionFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.stereotype.Service;

@Service
public class APIService {

    private static SessionFilter sessionFilter = new SessionFilter();
    private static final CsrfFilter csrfFilter = new CsrfFilter(getCsrfConfig());

    private static final String CSRF_URI = "/csrf";
    private static final String LOGOUT_URI = "/logout";

    public Response sendGet(Request request) {

        return prepared(request).get(request.getUri());
    }

    public Response sendPost(Request request) {

        return prepared(request).post(request.getUri());
    }

    private RequestSpecification prepared(Request request) {
        return request.filter(csrfFilter).filter(sessionFilter);
    }

    public void reset() {
        logout();
        sessionFilter = new SessionFilter();
    }

    private static CsrfConfig getCsrfConfig() {
        return new CsrfConfig(CSRF_URI, sessionFilter);
    }

    private void logout() {
        sendPost(new Request().to(LOGOUT_URI));
    }
}
