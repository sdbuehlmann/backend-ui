package ch.donkeycode.examples.persons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ch.donkeycode")
public class BackendUIExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendUIExampleApplication.class, args);
	}

}
