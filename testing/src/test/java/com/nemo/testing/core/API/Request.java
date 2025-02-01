package com.nemo.testing.core.API;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.experimental.Delegate;

/**
 * The main class for creating requests. It is a wrapper that delegates most calls to
 * {@link io.restassured.internal.RequestSpecificationImpl}, while providing some additional functionality,
 * such as the {@code uri} filled. It is more tightly integrated with {@link APIService}
 *
 * @see APIService
 * @see RestAssured
 * @see io.restassured.internal.RequestSpecificationImpl
 * */
public class Request implements RequestSpecification {

    private static final String BASE_URI = "http://backend:8080/";
    private static final String BASE_PATH = "/api";

    @Delegate
    private final RequestSpecification delegate;

    @Getter
    private Endpoint endpoint = null;

    protected Request() {
        this.delegate = RestAssured.with().baseUri(BASE_URI).and().basePath(BASE_PATH);
        this.config(RestAssuredConfig.config().logConfig(
            LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails()));
    }

    public static Request createTo(Endpoint endpoint) {
        return new Request().to(endpoint);
    }

    public Request to(Endpoint endpoint) {
        this.endpoint = endpoint;

        return this;
    }
}

