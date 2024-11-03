package com.nemo.testing.core.API;

import io.restassured.filter.Filter;
import io.restassured.filter.OrderedFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

/**
 * Extension of {@link Request} that logs request and response data.
 * It exists for test purposes only - it is not the idea that you use it anywhere,
 * unless you want to look at what exactly is happening.
 * {@link Request} already logs everything needed if test fails!
 *
 * <p><u>Once again:</u> this is <b>NOT</b> for general use!</p>
 *
 * @see Request
 * */
public final class LoggedRequest extends Request {

    public LoggedRequest() {
        this(LogDetail.ALL);
    }

    public LoggedRequest(LogDetail logDetail) {
        this(logDetail, logDetail);
    }

    public LoggedRequest(LogDetail requestLogDetail, LogDetail responseLogDetail) {
        super();
        this.filter(requestloggingFilter(requestLogDetail));
        this.filter(responseLoggingFilter(responseLogDetail));
    }

    private static Filter requestloggingFilter(LogDetail logDetail) {
        return OrderedRequestLoggingFilter.with(logDetail);
    }

    private static Filter responseLoggingFilter(LogDetail logDetail) {
        return OrderedResponseLoggingFilter.with(logDetail);
    }

    private static final class OrderedRequestLoggingFilter extends RequestLoggingFilter implements OrderedFilter {
        @Override
        public int getOrder() {
            return LOWEST_PRECEDENCE;
        }

        private OrderedRequestLoggingFilter(LogDetail logDetail) {
            super(logDetail);
        }

        private static OrderedRequestLoggingFilter with(LogDetail logDetail) {
            return new OrderedRequestLoggingFilter(logDetail);
        }

    }

    private static final class OrderedResponseLoggingFilter extends ResponseLoggingFilter implements OrderedFilter {
        @Override
        public int getOrder() {
            return LOWEST_PRECEDENCE;
        }

        private OrderedResponseLoggingFilter(LogDetail logDetail) {
            super(logDetail);
        }

        private static OrderedResponseLoggingFilter with(LogDetail logDetail) {
            return new OrderedResponseLoggingFilter(logDetail);
        }

    }
}
