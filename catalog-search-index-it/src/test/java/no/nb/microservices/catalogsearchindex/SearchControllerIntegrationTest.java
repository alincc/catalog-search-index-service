package no.nb.microservices.catalogsearchindex;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestApplication.class, EsTestConfig.class})
@WebIntegrationTest("server.port: 0")
public class SearchControllerIntegrationTest {

	@Value("${local.server.port}")
	int port;	

	@Test
	public void testSimpleSearch() throws Exception {
	    ResponseEntity<SearchResource> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/search?q=*", SearchResource.class);
	    
	    SearchResource searchResource = entity.getBody();
	    
	    assertNotNull(searchResource.getEmbedded().getItems());
	}
	
	@Test
    public void searchWithSearchStringAndAggregation() throws Exception {
        ResponseEntity<SearchResource> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/search?q=*&aggs=ddc1", SearchResource.class);
        
        SearchResource searchResource = entity.getBody();
        
        assertNotNull(searchResource.getEmbedded().getAggregations());
    }
}

class EsTestConfig {
    private final static String config = "no/nb/microservices/catalogsearchindex/";

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
        Resource sourceData = new ClassPathResource(config + "test-data.json");
    
        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(new Resource[] { sourceData });
        return factory;
    }
}

