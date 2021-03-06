package no.nb.microservices.catalogsearchindex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import no.nb.htrace.annotation.EnableTracing;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import no.nb.metrics.annotation.EnableMetrics;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@RefreshScope
@EnableTracing
@EnableFeignClients
@EnableDiscoveryClient
@EnableMetrics
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
