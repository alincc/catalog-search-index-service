package no.nb.microservices.catalogsearchindex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "catalog")
public class ApplicationConfig {

    private List<String> esHosts = new ArrayList<>();

    public List<String> getEsHosts() {
        return esHosts;
    }
}
