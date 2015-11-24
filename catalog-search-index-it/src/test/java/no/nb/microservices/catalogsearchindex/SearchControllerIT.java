package no.nb.microservices.catalogsearchindex;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import no.nb.microservices.catalogsearchindex.config.ElasticSearchTestConfig;
import no.nb.microservices.catalogsearchindex.searchwithin.SearchWithinResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestApplication.class, ElasticSearchTestConfig.class})
@WebIntegrationTest("server.port: 0")
public class SearchControllerIT {

	@Value("${local.server.port}")
	int port;	

	@Test
	public void testSimpleSearch() throws Exception {
	    ResponseEntity<SearchResource> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/v1/search?q=*", SearchResource.class);
	    
	    SearchResource searchResource = entity.getBody();
	    
	    assertNotNull(searchResource.getEmbedded().getItems());
	}
	
	@Test
    public void searchWithSearchStringAndAggregation() throws Exception {
        ResponseEntity<SearchResource> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/v1/search?q=*&aggs=ddc1", SearchResource.class);
        
        SearchResource searchResource = entity.getBody();
        
        assertNotNull(searchResource.getEmbedded().getAggregations());
	}

	@Test
	public void searchInFreeTextOnly() {
        ResponseEntity<SearchResource> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/v1/search?q=teater&searchType=TEXT_SEARCH&md=false", SearchResource.class);

        SearchResource searchResource = entity.getBody();

        assertThat(searchResource.getEmbedded().getItems(), hasSize(1));
    }

	@Test
	public void geoSearch() {
		ResponseEntity<SearchResource> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port + "/v1/search?q=*&topRight=85.0,180.0&bottomLeft=-85.0,-180.0", SearchResource.class);

		SearchResource searchResource = entity.getBody();

        assertThat(searchResource.getEmbedded().getItems(), hasSize(3));
        assertThat(searchResource.getEmbedded().getAggregations(), hasSize(1));
        assertEquals("locations", searchResource.getEmbedded().getAggregations().get(0).getName());
	}
	
	@Test
    public void testSearchWithin() {
        ResponseEntity<SearchWithinResource> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/v1/0b8501b8e2b822c8ec13558de82aaef9/search?q=teater", SearchWithinResource.class);

        SearchWithinResource searchResource = entity.getBody();

        assertThat("Should have freetext metadada", searchResource.getFreetextMetadatas(), hasSize(1));
        assertThat("Should have fragments",searchResource.getFragments(), hasSize(1));
    }	
}