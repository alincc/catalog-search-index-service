package no.nb.microservices.catalogsearchindex.rest.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nb.microservices.catalogsearchindex.SearchResource;
import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

public class SearchResultResourceAssemblerTest {

	private SearchResultResourceAssembler searchResultResourceAssembler;

    @Before
	public void init() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/catalog/v1/search?q=Junit");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(attributes);
        
        searchResultResourceAssembler = new SearchResultResourceAssembler();
	}
	
	@After
    public void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
    }
	
	@Test
    public void whenOnAnyPageReturnValueShouldHaveAPageElement() {
        SearchAggregated searchAggregated = createSearchAggregated(2);

        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        
        assertThat("The page element should not be null", searchResultResource.getMetadata(), notNullValue());
        assertThat("The number should be 2", searchResultResource.getMetadata().getNumber(), is(2));
        assertThat("The size should be 10", searchResultResource.getMetadata().getSize(), is(10));
        assertThat("The total elements should be 1000", searchResultResource.getMetadata().getTotalElements(), is(1000));
        assertThat("The total pages should be 100", searchResultResource.getMetadata().getTotalPages(), is(100));
    }
    
    @Test
    public void whenOnAnyPageReturnValueShouldHaveASelfLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(0);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        
        assertThat(searchResultResource.getId().getRel(), is("self"));
    }

    @Test
    public void whenOnFirstPageThenReturnValueShouldNotHaveAPreviousLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(0);
       
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        
        assertThat(searchResultResource.getLink(Link.REL_PREVIOUS), nullValue());
    }

    @Test
    public void whenOnLastPageThenReturnValueShouldNotHaveANextLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(100);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        
        assertThat(searchResultResource.getLink(Link.REL_NEXT), nullValue());
    }

    @Test
    public void whenNotOnFirstPageThenReturnValueShouldHaveAFirstLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(2);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        
        assertThat(searchResultResource.getLink(Link.REL_FIRST), notNullValue());
    }

    @Test
    public void whenNotOnLastPageThenReturnValueShouldHaveALastLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(10);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        
        assertThat(searchResultResource.getLink(Link.REL_LAST), notNullValue());
    }

    @Test
    public void whenItNotOnLastPageThenReturnValueShouldHaveALastLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(10); 
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        
        assertThat(searchResultResource.getLink(Link.REL_LAST), notNullValue());
    }

    @Test
    public void whenSearchResultHasItemsThenReturnValueShouldHaveItemsElement() {
        SearchAggregated searchAggregated = createSearchAggregated(0);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        
        assertThat("Items should have 2 items", searchResultResource.getEmbedded().getItems().size(), is(2));
    }
    
    @Test
    public void whenSearchResultHasAggregation() throws Exception {
        SearchAggregated searchAggregated = createSearchAggregated(0);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        
        assertThat(searchResultResource.getEmbedded().getAggregations().size(), is(1));
    }

    @Test
    public void whenSearchResultHasExplain() {
        ArrayList<Item> items = new ArrayList<>();
        Item id1 = createItem("id1");
        ObjectMapper mapper = new ObjectMapper();
        String json = "{}";
        id1.setExplain(mapper.convertValue(json, JsonNode.class));
        items.add(id1);
        Page<Item> page = new PageImpl<Item>(items, new PageRequest(0, 10) , 10);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        searchAggregated.setAggregations(createMockedAggregations());
        
        SearchResource searchResource = searchResultResourceAssembler.toResource(searchAggregated);

        assertThat("SearchResource should have explain",searchResource.getEmbedded().getItems().get(0).getExplain(), notNullValue());
    }

    private SearchAggregated createSearchAggregated(int currentPage) {
        ArrayList<Item> items = new ArrayList<>();
        items.add(createItem("id1"));
        items.add(createItem("id2"));
        Page<Item> page = new PageImpl<Item>(items, new PageRequest(currentPage, 10) , 1000);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        searchAggregated.setAggregations(createMockedAggregations());
        return searchAggregated;
    }

    private Aggregations createMockedAggregations() {
        Aggregations mockedAggregations = mock(Aggregations.class);
        StringTerms mockedAggregation = mock(StringTerms.class);
        when(mockedAggregation.getName()).thenReturn("aggregationName");
        Bucket mockedBucket = mock(Bucket.class);
        when(mockedBucket.getKey()).thenReturn("bucket1");
        when(mockedBucket.getDocCount()).thenReturn(15L);
        List<Aggregation> mockedAggregationList = Arrays.asList(mockedAggregation);
        when(mockedAggregations.iterator()).thenReturn(mockedAggregationList.iterator());
        return mockedAggregations;
    }
    
    private Item createItem(String id) {
        Item item = new Item();
        item.setId(id);
        return item;
    }
}
