package com.nemo.testing.Onion.Model;

import com.nemo.testing.Onion.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbstractComponent {

    @Autowired
    protected DriverService driverService;

}
