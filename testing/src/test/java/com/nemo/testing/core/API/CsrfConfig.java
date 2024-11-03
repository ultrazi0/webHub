package com.nemo.testing.core.API;

import io.restassured.config.Config;
import io.restassured.config.LogConfig;
import io.restassured.config.CsrfConfig.CsrfPrioritization;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.session.SessionFilter;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class CsrfConfig implements Config {

    public static final String DEFAULT_CSRF_ENDPOINT = "/csrf";
    public static final String DEFAULT_CSRF_HEADER_NAME_FIELD = "headerName";
    public static final String DEFAULT_CSRF_PARAMETER_NAME_FIELD = "parameterName";
    public static final String DEFAULT_CSRF_TOKEN_FIELD_NAME = "token";
    public static final CsrfPrioritization DEFAULT_CSRF_PRIORITIZATION = CsrfPrioritization.HEADER;

    @Getter(AccessLevel.NONE)
    private final boolean isUserConfigured;
    private final String csrfTokenEndpoint;
    private final String csrfHeaderNameField;
    private final String csrfParameterNameField;
    private final String csrfTokenFieldName;
    private final CsrfPrioritization csrfPrioritization;
    private final LogConfig logConfig;
    private final LogDetail logDetail;
    private final SessionFilter sessionFilter;

    /**
     * Create a default
     * */
    public CsrfConfig() {
        this(null, DEFAULT_CSRF_HEADER_NAME_FIELD,
            DEFAULT_CSRF_PARAMETER_NAME_FIELD, DEFAULT_CSRF_TOKEN_FIELD_NAME,
            DEFAULT_CSRF_PRIORITIZATION, null, null, null, false);
    }

    public CsrfConfig(String csrfTokenEndpoint, SessionFilter sessionFilter) {
        this(csrfTokenEndpoint, DEFAULT_CSRF_HEADER_NAME_FIELD,
            DEFAULT_CSRF_PARAMETER_NAME_FIELD, DEFAULT_CSRF_TOKEN_FIELD_NAME,
            DEFAULT_CSRF_PRIORITIZATION, sessionFilter);
    }

    public CsrfConfig(String csrfTokenEndpoint, String csrfHeaderNameField,
                      String csrfParameterNameField, String csrfTokenFieldName,
                      CsrfPrioritization csrfPrioritization, SessionFilter sessionFilter) {
        this(csrfTokenEndpoint, csrfHeaderNameField, csrfParameterNameField,
            csrfTokenFieldName, csrfPrioritization, null, null, sessionFilter, true);
    }

    private CsrfConfig(String csrfTokenEndpoint, String csrfHeaderNameField,
                      String csrfParameterNameField, String csrfTokenFieldName,
                      CsrfPrioritization csrfPrioritization, LogConfig logConfig,
                      LogDetail logDetail, SessionFilter sessionFilter, boolean isUserConfigured) {

        this.csrfTokenEndpoint = StringUtils.trimToNull(csrfTokenEndpoint);
        this.csrfHeaderNameField = StringUtils.trimToNull(csrfHeaderNameField);
        this.csrfParameterNameField = StringUtils.trimToNull(csrfParameterNameField);
        this.csrfTokenFieldName = StringUtils.trimToNull(csrfTokenFieldName);
        this.csrfPrioritization = csrfPrioritization;
        this.logConfig = logConfig;
        this.logDetail = logDetail;
        this.sessionFilter = sessionFilter;
        this.isUserConfigured = isUserConfigured;
    }

    @Override
    public boolean isUserConfigured() {
        return isUserConfigured;
    }

    public boolean isCsrfEnabled() {
        return csrfTokenEndpoint != null;
    }

    public CsrfConfig defaultCsrfEndpoint() {
        return csrfTokenEndpoint(DEFAULT_CSRF_ENDPOINT);
    }

    public CsrfConfig csrfTokenEndpoint(String csrfTokenEndpoint) {
        return new CsrfConfig(csrfTokenEndpoint, csrfHeaderNameField,
            csrfParameterNameField, csrfTokenFieldName, csrfPrioritization,
            logConfig, logDetail, sessionFilter, true);
    }

    public CsrfConfig sessionFilter(SessionFilter sessionFilter) {
        return new CsrfConfig(csrfTokenEndpoint, csrfHeaderNameField,
            csrfParameterNameField, csrfTokenFieldName, csrfPrioritization,
            logConfig, logDetail, sessionFilter, true);
    }

    public CsrfConfig with() {
        return this;
    }

    public boolean isLoggingEnabled() {
        return logConfig != null && logDetail != null;
    }

    public CsrfConfig loggingEnabled() {
        return loggingEnabled(LogDetail.ALL);
    }

    public CsrfConfig loggingEnabled(LogDetail logDetail) {
        return loggingEnabled(logDetail, new LogConfig());
    }

    public CsrfConfig loggingEnabled(LogConfig logConfig) {
        return loggingEnabled(LogDetail.ALL, logConfig);
    }

    public CsrfConfig loggingEnabled(LogDetail logDetail, LogConfig logConfig) {
        return new CsrfConfig(csrfTokenEndpoint, csrfHeaderNameField,
            csrfParameterNameField, csrfTokenFieldName, csrfPrioritization,
            logConfig, logDetail, sessionFilter, true);
    }

    public boolean isCsrfPrioritization(CsrfPrioritization csrfPrioritization) {
        return this.csrfPrioritization == csrfPrioritization;
    }
}
