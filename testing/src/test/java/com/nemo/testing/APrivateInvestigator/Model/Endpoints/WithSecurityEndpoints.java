package com.nemo.testing.APrivateInvestigator.Model.Endpoints;

import com.nemo.testing.core.API.Endpoint;
import io.restassured.http.Method;

public interface WithSecurityEndpoints {

    Endpoint LOGIN_ENDPOINT = new Endpoint(Method.POST, "/login");
    Endpoint LOGOUT_ENDPOINT = new Endpoint(Method.POST, "/logout");

    Endpoint REGISTER_ENDPOINT = new Endpoint(Method.POST, "/register");

    Endpoint USER_ENDPOINT = new Endpoint(Method.GET, "/user");
}
