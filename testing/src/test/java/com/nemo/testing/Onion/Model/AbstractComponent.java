package com.nemo.testing.Onion.Model;

import com.nemo.testing.Onion.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AbstractComponent serves as a base class providing shared functionalities
 * and dependencies to be used by derived components within the application.
 */
@Component
public abstract class AbstractComponent {

    @Autowired
    protected DriverService driverService;

}
