package com.project.sfoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SfocApplication {

	public static void main(String[] args) {
		SpringApplication.run(SfocApplication.class, args);
	}

}
