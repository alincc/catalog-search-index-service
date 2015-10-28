package no.nb.microservices.catalogsearchindex.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by alfredw on 9/14/15.
 */
@Configuration
public class ElasticsearchConfig {

    @Autowired
    private ApplicationConfig config;

    @Bean
    public Client esClient() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "Test Cluster").build();
        TransportClient transportClient = new TransportClient(settings);
        for (String host : config.getEsHosts()) {
            transportClient.addTransportAddress(new InetSocketTransportAddress(host, 9300));
        }

        return transportClient;
    }
}
