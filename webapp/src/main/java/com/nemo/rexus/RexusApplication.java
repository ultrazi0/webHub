package com.nemo.rexus;

import nu.pattern.OpenCV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class RexusApplication {

	static {
		OpenCV.loadLocally();
	}

	public static void main(String[] args) {

		SpringApplication.run(RexusApplication.class, args);


	}

}
