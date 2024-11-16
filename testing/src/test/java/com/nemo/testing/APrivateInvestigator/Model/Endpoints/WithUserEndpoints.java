package com.nemo.testing.APrivateInvestigator.Model.Endpoints;

import com.nemo.testing.core.API.Endpoint;
import io.restassured.http.Method;

public interface WithUserEndpoints {

    Endpoint EDIT_USER_ENDPOINT = new Endpoint(Method.PUT, "/user");
    Endpoint DELETE_USER_ENDPOINT = new Endpoint(Method.DELETE, "/user");

}
