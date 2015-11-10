package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import org.elasticsearch.client.Client;
import org.junit.*;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
        SearchAggregated searchAggregated = searchRepository.search("-huasui", new String[]{}, new PageRequest(0, 10), true, true);
        assertEquals(3, searchAggregated.getPage().getContent().size());
    }

    @Test
    public void searchWithAggregations() {
        SearchAggregated searchAggregated = searchRepository.search("-huasui", new String[]{"mediatype"}, new PageRequest(0, 10), true, true);
        assertNotNull(searchAggregated.getAggregations());
    }

    @Test
    public void searchInFreeTextOnly() {
        SearchAggregated search = searchRepository.search("teater", new String[]{}, new PageRequest(0, 10), true, false);
        assertThat(search.getPage().getContent(), hasSize(1));
        assertEquals(search.getPage().getContent().get(0).getId(), "0b8501b8e2b822c8ec13558de82aaef9");
    }

    @Test
    public void searchInMetadataOnly() {
        SearchAggregated search = searchRepository.search("2009", new String[]{}, new PageRequest(0, 10), false, true);
        assertThat(search.getPage().getContent(), hasSize(1));
        assertEquals(search.getPage().getContent().get(0).getId(), "92eb4d381bf7004de77337800654f610");
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
