package com.nemo.webHub;

import nu.pattern.OpenCV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebHubApplication {

	public static void main(String[] args) {

		OpenCV.loadLocally();

		SpringApplication.run(WebHubApplication.class, args);


	}

}
