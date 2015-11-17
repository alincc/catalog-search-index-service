package no.nb.microservices.catalogsearchindex.config;

import no.nb.microservices.catalogsearchindex.core.repository.EmbeddedElasticsearch;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ElasticSearchTestConfig implements DisposableBean {

    private EmbeddedElasticsearch embeddedElasticsearch;

    @Bean
    public Client esClient() throws IOException {
        embeddedElasticsearch = EmbeddedElasticsearch.getInstance();
        return embeddedElasticsearch.getClient();
    }
	
    @Override
    public void destroy() throws Exception {
        embeddedElasticsearch.shutdown();
    }
}
