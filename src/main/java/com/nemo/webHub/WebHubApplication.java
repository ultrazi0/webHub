package com.nemo.webHub;

import nu.pattern.OpenCV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebHubApplication {

	static {
		OpenCV.loadLocally();
	}

	public static void main(String[] args) {

		SpringApplication.run(WebHubApplication.class, args);


	}

}
