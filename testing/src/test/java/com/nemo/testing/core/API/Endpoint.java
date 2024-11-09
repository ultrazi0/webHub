package com.nemo.testing.core.API;

import io.restassured.http.Method;

public record Endpoint(Method method, String uri) {}
