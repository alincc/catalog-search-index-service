package no.nb.microservices.catalogsearchindex.config;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import no.nb.microservices.catalogsearchindex.core.model.Item;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Profile("unit-test")
@Configuration
@EnableElasticsearchRepositories(basePackages = "no.nb.microservices.catalogsearchindex.core.repository")
public class ElasticSearchTestConfig {

	private final static String config = "no/nb/microservices/catalogsearchindex/";
	
	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		Settings settings = ImmutableSettings.settingsBuilder()
//                .put("http.enabled", "false")
//                .put("path.data", "target/es-data")
                .build();
		
		Node node = nodeBuilder()
					.local(true)
					.settings(settings)
					.node();
		
		ElasticsearchTemplate elasticsearchTemplate = new ElasticsearchTemplate(node.client());
		
		elasticsearchTemplate.deleteIndex(Item.class);
		elasticsearchTemplate.createIndex("expressionrecords", ElasticsearchTemplate.readFileFromClasspath(config + "settings.json"));
		elasticsearchTemplate.putMapping("expressionrecords", "expressionrecord", ElasticsearchTemplate.readFileFromClasspath(config + "mapping.json"));

		return elasticsearchTemplate;
	}
	
	@Bean
	public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
		Resource sourceData = new ClassPathResource(config + "test-data.json");

	    Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
	    factory.setResources(new Resource[] { sourceData });
	    return factory;
	}
}
