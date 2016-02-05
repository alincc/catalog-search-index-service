package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.NBSearchType;
import no.nb.microservices.catalogsearchindex.core.model.GeoSearch;
import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.junit.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class ElasticSearchRepositoryTest {

    private Client client;
    private SearchRepository searchRepository;
    private static EmbeddedElasticsearch embeddedElasticsearch;
    private SearchCriteria searchCriteria;

    @BeforeClass
    public static void init() throws IOException {
        embeddedElasticsearch = EmbeddedElasticsearch.getInstance();
    }

    @Before
    public void setup () {
        client = embeddedElasticsearch.getClient();
        searchRepository = new ElasticSearchRepository(client);

        searchCriteria = new SearchCriteria("-huasui");
        searchCriteria.setPageRequest(new PageRequest(0, 10));
        searchCriteria.setSearchType(NBSearchType.FULL_TEXT_SEARCH);
    }

    @Test
    public void searchWithQueryString() {
        SearchAggregated searchAggregated = searchRepository.search(searchCriteria);

        assertEquals(3, searchAggregated.getPage().getContent().size());
    }

    @Test
    public void searchWithAggregations() {
        searchCriteria.setAggregations(new String[]{"mediatype"});

        SearchAggregated searchAggregated = searchRepository.search(searchCriteria);

        assertNotNull(searchAggregated.getAggregations().get("mediatype"));
    }

    @Test
    public void searchWithFilters() {
        searchCriteria.setFilters(new String[]{"mediatype:Aviser"});
        SearchAggregated searchAggregated1 = searchRepository.search(searchCriteria);

        searchCriteria.setFilters(new String[]{"mediatype:Bøker"});
        SearchAggregated searchAggregated2 = searchRepository.search(searchCriteria);

        searchCriteria.setFilters(new String[]{"mediatype:Bøker", "mediatype:Aviser"});
        SearchAggregated searchAggregated3 = searchRepository.search(searchCriteria);

        assertEquals("Search 1 should get hits on 0 items", 0, searchAggregated1.getPage().getContent().size());
        assertEquals("Search 2 should get hits on 3 items", 3, searchAggregated2.getPage().getContent().size());
        for (Item item : searchAggregated2.getPage().getContent()) {
            assertTrue("Each item should have mediatype \"Bøker\"", item.getMediaTypes().get(0).equalsIgnoreCase("Bøker"));
        }
        assertEquals("Search 3 should get hits on 0 items", 0, searchAggregated3.getPage().getContent().size());
    }

    @Test
    public void searchInFreeTextOnly() {
        searchCriteria.setSearchString("teater");
        searchCriteria.setSearchType(NBSearchType.TEXT_SEARCH);

        SearchAggregated search = searchRepository.search(searchCriteria);

        assertThat(search.getPage().getContent(), hasSize(1));
        assertEquals(search.getPage().getContent().get(0).getId(), "0b8501b8e2b822c8ec13558de82aaef9");
    }

    @Test
    public void searchInMetadataOnly() {
        searchCriteria.setSearchString("2009");
        searchCriteria.setSearchType(NBSearchType.FIELD_RESTRICTED_SEARCH);

        SearchAggregated search = searchRepository.search(searchCriteria);

        assertThat(search.getPage().getContent(), hasSize(1));
        assertEquals(search.getPage().getContent().get(0).getId(), "92eb4d381bf7004de77337800654f610");
        assertEquals("2015-06-10T10:17:21.607Z", search.getPage().getContent().get(0).getFirstIndexTime());
    }

    @Test
    public void geoSearchNoZoom() {
        searchCriteria.setGeoSearch(new GeoSearch());

        SearchAggregated search = searchRepository.search(searchCriteria);

        assertThatLocationAggregationHasSize(search, 3);
    }

    @Test
    public void geoSearchWithZoomOnNordlandAndLowPrecision() {
        GeoSearch geoSearch = createGeoSearchWithZoomOnNordland(3);
        searchCriteria.setGeoSearch(geoSearch);

        SearchAggregated search = searchRepository.search(searchCriteria);

        assertThatLocationAggregationHasSize(search, 1);
    }

    @Test
    public void geoSearchWithZoomOnNordlandAndHighPrecision() {
        GeoSearch geoSearch = createGeoSearchWithZoomOnNordland(8);
        searchCriteria.setGeoSearch(geoSearch);

        SearchAggregated search = searchRepository.search(searchCriteria);

        assertThatLocationAggregationHasSize(search, 2);
    }

    @Test
    public void sortAscending() {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC,"title");
        Sort sort = new Sort(order);
        PageRequest pageRequest = new PageRequest(0,10, sort);
        searchCriteria.setPageRequest(pageRequest);

        SearchAggregated search = searchRepository.search(searchCriteria);
        assertThat(search.getPage().getContent().get(0).getTitle(), is("Nøtteknekkeren"));
    }

    @Test
    public void sortDescending() {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC,"title");
        Sort sort = new Sort(order);
        PageRequest pageRequest = new PageRequest(0,10, sort);
        searchCriteria.setPageRequest(pageRequest);

        SearchAggregated search = searchRepository.search(searchCriteria);
        assertThat(search.getPage().getContent().get(0).getTitle(), is("Så rart : Inger Hagerup"));
    }

    @Test
    public void searchWithBoost() {
        searchCriteria.setBoost(new String[]{"title,10", "name,4"});
        
        SearchAggregated search = searchRepository.search(searchCriteria);
        
        assertThat(search, notNullValue());
    }

    private GeoSearch createGeoSearchWithZoomOnNordland(int precision) {
        GeoSearch geoSearch = new GeoSearch();
        geoSearch.setTopRight(new GeoPoint(68.47, 18.09));
        geoSearch.setBottomLeft(new GeoPoint(65.14, 10.37));
        geoSearch.setPrecision(precision);
        return geoSearch;
    }

    private void assertThatLocationAggregationHasSize(SearchAggregated search, int numberOfBuckets) {
        Aggregation locations = search.getAggregations().get("locations");
        assertNotNull(locations);
        assertThat(((GeoHashGrid) locations).getBuckets(), hasSize(numberOfBuckets));
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
