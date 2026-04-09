package com.jmt.franchises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jmt.franchises")
public class FranchisesApplication {

	public static void main(String[] args) {
		SpringApplication.run(FranchisesApplication.class, args);
	}

}
