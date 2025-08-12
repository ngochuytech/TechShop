package com.project.techstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TechstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechstoreApplication.class, args);
	}

}
