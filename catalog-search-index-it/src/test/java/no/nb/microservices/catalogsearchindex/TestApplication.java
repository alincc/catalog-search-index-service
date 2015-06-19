package no.nb.microservices.catalogsearchindex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(exclude = {Application.class, ElasticsearchAutoConfiguration.class})
@ComponentScan(excludeFilters = {
    @ComponentScan.Filter(value = Application.class, type = FilterType.ASSIGNABLE_TYPE),
})
public class TestApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
