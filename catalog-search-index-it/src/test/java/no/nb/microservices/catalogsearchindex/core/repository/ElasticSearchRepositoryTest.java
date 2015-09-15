package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import org.elasticsearch.client.Client;
import org.junit.*;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by alfredw on 9/10/15.
 */
public class ElasticSearchRepositoryTest {

    private Client client;
    private SearchRepository searchRepository;
    private static EmbeddedElasticsearch embeddedElasticsearch;

    @BeforeClass
    public static void init() throws IOException {
        embeddedElasticsearch = EmbeddedElasticsearch.getInstance();
    }

    @Before
    public void setup () {
        client = embeddedElasticsearch.getClient();
        searchRepository = new ElasticSearchRepository(client);
    }

    @Test
    public void searchWithQueryString() {
        SearchAggregated searchAggregated = searchRepository.search("-huasui", new String[]{}, new PageRequest(0, 10));
        assertEquals(3, searchAggregated.getPage().getContent().size());
    }

    @Test
    public void searchWithAggregations() {
        SearchAggregated searchAggregated = searchRepository.search("-huasui", new String[]{"mediatype"}, new PageRequest(0, 10));
        assertNotNull(searchAggregated.getAggregations());
    }

    @After
    public void tearDown() throws IOException {
        client.close();
    }

    @AfterClass
    public static void shutdown() throws IOException {
        embeddedElasticsearch.shutdown();
    }
}
