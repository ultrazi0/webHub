package com.nemo.testing.APrivateInvestigator.Model.Endpoints;

import com.nemo.testing.core.API.Endpoint;
import io.restassured.http.Method;

public interface WithHomeEndpoints {
    Endpoint COMMANDS_ENDPOINT = new Endpoint(Method.GET, "/robots/{robotId}/commands");
    Endpoint INSERT_COMMANDS_ENDPOINT = new Endpoint(Method.POST, "/robots/{robotId}/commands");
    Endpoint DELETE_COMMANDS_ENDPOINT = new Endpoint(Method.DELETE, "/robots/{robotId}/commands");

    Endpoint GET_USER_ROBOTS_ENDPOINT = new Endpoint(Method.GET, "/robots");
    Endpoint INSERT_ROBOT_ENDPOINT = new Endpoint(Method.POST, "/robots");
    Endpoint GET_ROBOT_ENDPOINT = new Endpoint(Method.GET, "/robots/{robotId}");
    Endpoint EDIT_ROBOT_ENDPOINT = new Endpoint(Method.PUT, "/robots/{robotId}");
    Endpoint DELETE_ROBOT_ENDPOINT = new Endpoint(Method.DELETE, "/robots/{robotId}");

    Endpoint SHARE_ROBOT_ENDPOINT = new Endpoint(Method.POST, "/robots/{robotId}/share");
    Endpoint UNSHARE_ROBOT_ENDPOINT = new Endpoint(Method.DELETE, "/robots/{robotId}/unshare");
}
