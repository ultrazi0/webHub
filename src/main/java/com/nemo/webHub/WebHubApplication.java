package com.nemo.webHub;

import com.nemo.webHub.Commands.Aim.DetectQRCode;
import nu.pattern.OpenCV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
public class WebHubApplication {

	public static void main(String[] args) {

		OpenCV.loadLocally();

		SpringApplication.run(WebHubApplication.class, args);


	}

}
