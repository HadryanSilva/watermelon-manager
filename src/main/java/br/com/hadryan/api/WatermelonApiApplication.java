package br.com.hadryan.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class WatermelonApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatermelonApiApplication.class, args);
	}

}
