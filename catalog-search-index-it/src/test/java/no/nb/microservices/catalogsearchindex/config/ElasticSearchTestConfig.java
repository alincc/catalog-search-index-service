package no.nb.microservices.catalogsearchindex.config;

import java.io.File;

import no.nb.microservices.catalogsearchindex.core.model.Item;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
@EnableElasticsearchRepositories(basePackages = "no.nb.microservices.catalogsearchindex.core.repository")
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticSearchTestConfig implements DisposableBean{

	private final static String config = "no/nb/microservices/catalogsearchindex/";
	
	private NodeClient client;
	
	@Bean
    public ElasticsearchTemplate elasticsearchTemplate() {
        ElasticsearchTemplate elasticsearchTemplate = new ElasticsearchTemplate(esClient());
        elasticsearchTemplate.deleteIndex(Item.class);
        elasticsearchTemplate.createIndex(Item.class);
        return elasticsearchTemplate;
    }
	
	@Bean
    public Client esClient() {
            NodeBuilder nodeBuilder = new NodeBuilder();
            nodeBuilder
                    .local(false)
            ;
            nodeBuilder.settings()
                    .put("path.data", "target/es-data")
//                    .put("http.enabled", true)
            ;
            this.client = (NodeClient)nodeBuilder.node().client();
            return this.client;
    }
	
	@Bean
	public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
		Resource sourceData = new ClassPathResource(config + "test-data.json");

	    Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
	    factory.setResources(new Resource[] { sourceData });
	    return factory;
	}

    @Override
    public void destroy() throws Exception {
        if(this.client != null) {
            this.client.close();
        }
        
        FileUtils.deleteDirectory(new File("target/es-data"));
    }
}
