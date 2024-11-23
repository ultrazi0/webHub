package com.nemo.testing.APrivateInvestigator.Model;

import com.nemo.testing.core.API.Request;
import com.nemo.testing.core.Persistence.UniqueAttributes.AbstractUniqueAttributes;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import io.restassured.config.FailureConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.internal.LogRequestAndResponseOnFailListener;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Extension of {@link Request} that provides additional functionality useful for test requests,
 * i.e. requests that are being tested. This class automatically provides the log as an attachment
 * to the current step should the validation fail.
 * */
public class TestRequest extends Request {

    @Setter
    private CurrentStep currentStep;
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Getter
    @Setter
    private Class<?> entityType;
    @Getter
    @Setter
    private AbstractUniqueAttributes uniqueAttributes;

    public TestRequest() {
        this.config(RestAssuredConfig.config()
            .logConfig(
                new LogConfig(new PrintStream(byteArrayOutputStream), true)
                    .enableLoggingOfRequestAndResponseIfValidationFails())
            .failureConfig(
                new FailureConfig(List.of(new LogRequestAndResponseToAttachmentsOnFailListener())))
        );
    }

    /**
     * Whether an entity created is created as a result of this request. This is needed for automatic cleanup
     * */
    public boolean needsEntityRegister() {
        return entityType != null && uniqueAttributes != null;
    }

    private class LogRequestAndResponseToAttachmentsOnFailListener extends LogRequestAndResponseOnFailListener {

        @Override
        public void onFailure(RequestSpecification requestSpecification,
                              ResponseSpecification responseSpecification, Response response) {

            super.onFailure(requestSpecification, responseSpecification, response);

            currentStep.addAttachment(Attachment
                .fromText(reduceTabs(byteArrayOutputStream.toString()), MediaType.textUtf8("txt")));

        }

        private static String reduceTabs(String input) {
            return input.replaceAll("\t\t", "\t");
        }
    }

}
