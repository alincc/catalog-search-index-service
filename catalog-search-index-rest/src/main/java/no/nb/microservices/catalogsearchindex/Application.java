package no.nb.microservices.catalogsearchindex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;

/**
 * @author ronnymikalsen
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableOAuth2Resource
@RefreshScope
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
