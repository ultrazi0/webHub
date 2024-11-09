package com.nemo.testing.core.API;

import io.restassured.http.Method;

/**
 * Record to represent endpoints. It has two parameters: method of type {@link Method} and uri of type {@link String}
 * */
public record Endpoint(Method method, String uri) {}
