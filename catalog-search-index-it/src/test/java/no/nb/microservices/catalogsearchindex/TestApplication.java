package no.nb.microservices.catalogsearchindex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {Application.class})
public class TestApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
