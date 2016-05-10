package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.NBSearchType;
import no.nb.microservices.catalogsearchindex.core.index.repository.ElasticSearchRepository;
import no.nb.microservices.catalogsearchindex.core.index.repository.SearchRepository;
import no.nb.microservices.catalogsearchindex.core.model.GeoSearch;
import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregator;
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

    @AfterClass
    public static void shutdown() throws IOException {
        embeddedElasticsearch.shutdown();
    }

    @Before
    public void setup () {
        client = embeddedElasticsearch.getClient();
        searchRepository = new ElasticSearchRepository(client);

        searchCriteria = new SearchCriteria("-huasui");
        searchCriteria.setPageRequest(new PageRequest(0, 10));
        searchCriteria.setSearchType(NBSearchType.FULL_TEXT_SEARCH);
    }

    @After
    public void tearDown() throws IOException {
        client.close();
    }

    @Test
    public void testSimpleSearch() {
        SearchAggregated result = searchRepository.search(searchCriteria);

        assertThat(result.getPage().getContent().size(), is(3));
    }

    @Test
    public void testSearchWithAggregations() {
        searchCriteria.setAggregations(new String[]{"mediatype"});

        SearchAggregated result = searchRepository.search(searchCriteria);

        assertThat(result.getAggregations().get("mediatype"), notNullValue());
    }

    @Test
    public void testSearchWithSingleFilter() {
        searchCriteria.setFilters(new String[]{"mediatype:Bøker"});
        
        SearchAggregated result = searchRepository.search(searchCriteria);

        assertEquals("Should get hits on 3 items", 3, result.getPage().getContent().size());
        for (Item item : result.getPage().getContent()) {
            assertThat("Each item should have mediatype \"Bøker\"", item.getMediaTypes().get(0).equalsIgnoreCase("Bøker"), is(true));
        }
    }

    @Test
    public void testSearchWithMultipleFilters() {
        searchCriteria.setFilters(new String[]{"mediatype:Bøker", "mediatype:Aviser"});
        
        SearchAggregated result = searchRepository.search(searchCriteria);

        assertThat("Search 3 should get hits on 0 items", result.getPage().getContent().size(), is(0));
    }

    @Test
    public void testSearchInFreeTextOnly() {
        searchCriteria.setSearchString("teater");
        searchCriteria.setSearchType(NBSearchType.TEXT_SEARCH);

        SearchAggregated search = searchRepository.search(searchCriteria);

        assertThat(search.getPage().getContent(), hasSize(1));
        assertThat(search.getPage().getContent().get(0).getId(), is("0b8501b8e2b822c8ec13558de82aaef9"));
    }

    @Test
    public void testSearchInMetadataOnly() {
        searchCriteria.setSearchString("2009");
        searchCriteria.setSearchType(NBSearchType.FIELD_RESTRICTED_SEARCH);

        SearchAggregated search = searchRepository.search(searchCriteria);

        assertThat(search.getPage().getContent(), hasSize(1));
        assertEquals(search.getPage().getContent().get(0).getId(), "92eb4d381bf7004de77337800654f610");
        assertEquals("2015-06-10T10:17:21.607Z", search.getPage().getContent().get(0).getFirstIndexTime());
    }

    @Test
    public void testGeoSearchNoZoom() {
        searchCriteria.setGeoSearch(new GeoSearch());

        SearchAggregated result = searchRepository.search(searchCriteria);

        assertThatLocationAggregationHasSize(result, 3);
    }

    @Test
    public void testGeoSearchWithZoomOnNordlandAndLowPrecision() {
        GeoSearch geoSearch = createGeoSearchWithZoomOnNordland(3);
        searchCriteria.setGeoSearch(geoSearch);

        SearchAggregated result = searchRepository.search(searchCriteria);

        assertThatLocationAggregationHasSize(result, 1);
    }

    @Test
    public void testGeoSearchWithZoomOnNordlandAndHighPrecision() {
        GeoSearch geoSearch = createGeoSearchWithZoomOnNordland(8);
        searchCriteria.setGeoSearch(geoSearch);

        SearchAggregated result = searchRepository.search(searchCriteria);

        assertThatLocationAggregationHasSize(result, 2);
    }

    @Test
    public void testSearchWithSortAscending() {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC,"title");
        Sort sort = new Sort(order);
        PageRequest pageRequest = new PageRequest(0,10, sort);
        searchCriteria.setPageRequest(pageRequest);

        SearchAggregated result = searchRepository.search(searchCriteria);
        
        assertThat(result.getPage().getContent().get(0).getTitle(), is("Nøtteknekkeren"));
    }

    @Test
    public void testSearchWithSortDescending() {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC,"title");
        Sort sort = new Sort(order);
        PageRequest pageRequest = new PageRequest(0,10, sort);
        searchCriteria.setPageRequest(pageRequest);

        SearchAggregated result = searchRepository.search(searchCriteria);
        
        assertThat(result.getPage().getContent().get(0).getTitle(), is("Så rart : Inger Hagerup"));
    }

    @Test
    public void testSearchWithBoost() {
        searchCriteria.setBoost(new String[]{"title,10", "name,4"});
        
        SearchAggregated result = searchRepository.search(searchCriteria);
        
        assertThat(result, notNullValue());
    }

    @Test
    public void testSearchWithShould() {
        searchCriteria.setShould(new String[]{"title,peter"});

        SearchAggregated result = searchRepository.search(searchCriteria);
        
        assertThat(result, notNullValue());
    }

    @Test
    public void whenFilteringOnTitleThenFilterOnTitleUntouched() throws Exception {
        searchCriteria.setFilters(new String[]{"title:Peter Pan for barn ; Peter Pan for vaksne"});

        SearchAggregated result = searchRepository.search(searchCriteria);

        assertThat(result.getPage().getTotalElements(), is(1L));
    }

    @Test
    public void whenAggOnTitleThenAggOnTitleUntouched() throws Exception {
        searchCriteria.setAggregations(new String[]{"title"});

        SearchAggregated result = searchRepository.search(searchCriteria);

        assertThat(result.getAggregations().asMap().get("title").getName(), is("title"));
        assertThat(((Terms)result.getAggregations().asMap().get("title")).getBuckets().size(), is(3));
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

}
