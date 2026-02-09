package com.nbu.CSCB532;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.nbu.CSCB532"})
public class LogisticCompanyApplication {
	public static void main(String[] args) {
		SpringApplication.run(LogisticCompanyApplication.class, args);
	}
}

