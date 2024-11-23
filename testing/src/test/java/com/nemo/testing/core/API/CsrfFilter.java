package com.nemo.testing.core.API;

import io.restassured.config.LogConfig;
import io.restassured.config.CsrfConfig.CsrfPrioritization;
import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Handles the CSRF tokens.
 * This is an adaptation of {@link io.restassured.internal.filter.CsrfFilter} for this application
 *
 * @see CsrfConfig
 * */
public class CsrfFilter implements OrderedFilter {

    private final CsrfConfig csrfConfig;

    public CsrfFilter() {
        this.csrfConfig = new CsrfConfig();
    }

    public CsrfFilter(CsrfConfig csrfConfig) {
        this.csrfConfig = csrfConfig;
    }

    /**
     * Sets order of the filter. It should be applied before {@link io.restassured.filter.session.SessionFilter},
     * so that {@code sessionId} cookie can be reused in the original request
     *
     * @see io.restassured.filter.session.SessionFilter
     * */
    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public Response filter(
        FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {

        // Skip is GET or HEAD
        if (requestSpec.getMethod().equalsIgnoreCase("GET")
            || requestSpec.getMethod().equalsIgnoreCase("HEAD")) {
            return ctx.next(requestSpec, responseSpec);
        }

        // Create new request
        Request request = new Request();
        // Log if needed
        if (csrfConfig.isLoggingEnabled()) {
            LogConfig logConfig = csrfConfig.getLogConfig();
            LogDetail logDetail = csrfConfig.getLogDetail();

            if (logDetail != LogDetail.STATUS) {
                request.filter(new RequestLoggingFilter(logDetail, logConfig.isPrettyPrintingEnabled(),
                    logConfig.defaultStream(), logConfig.shouldUrlEncodeRequestUri(), logConfig.blacklistedHeaders()));
            }

            if (logDetail != LogDetail.PARAMS) {
                request.filter(new ResponseLoggingFilter(logDetail,
                    logConfig.isPrettyPrintingEnabled(), logConfig.defaultStream()));
            }
        }

        // Send GET request with SessionFilter to obtain the CSRF token
        Response csrfResponse = request.filter(csrfConfig.getSessionFilter()).get(csrfConfig.getCsrfTokenEndpoint());
        JsonPath csrfData = csrfResponse.jsonPath();

        // Add CSRF token to the current request
        if (csrfConfig.isCsrfPrioritization(CsrfPrioritization.HEADER)) {
            requestSpec.header(
                csrfData.getString(csrfConfig.getCsrfHeaderNameField()),
                csrfData.getString(csrfConfig.getCsrfTokenFieldName()));
        } else {
            requestSpec.formParam(
                csrfData.getString(csrfConfig.getCsrfParameterNameField()),
                csrfData.getString(csrfConfig.getCsrfTokenFieldName()));
        }

        return ctx.next(requestSpec, responseSpec);
    }
}
