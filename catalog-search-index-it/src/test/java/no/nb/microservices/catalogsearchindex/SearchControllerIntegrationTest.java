package no.nb.microservices.catalogsearchindex;

import no.nb.microservices.catalogsearchindex.config.ElasticSearchTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestApplication.class, ElasticSearchTestConfig.class})
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

	@Test
	public void searchInFreeTextOnly() {
        ResponseEntity<SearchResource> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/search?q=teater&ft=true&md=false", SearchResource.class);

        SearchResource searchResource = entity.getBody();

        assertThat(searchResource.getEmbedded().getItems(), hasSize(1));
    }
}