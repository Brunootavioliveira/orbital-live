package br.com.solarsystem.solar_system_nasa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SolarSystemNasaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolarSystemNasaApplication.class, args);
	}

}
