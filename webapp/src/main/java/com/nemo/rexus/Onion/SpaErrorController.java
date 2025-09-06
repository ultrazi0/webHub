package com.nemo.rexus.Onion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpaErrorController extends BasicErrorController {

    public SpaErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes, serverProperties.getError());
    }

    /// For HTML requests, if URL is not recognized by the backend, let the frontend deal with it
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("forward:/index.html");
    }

}
