package no.nb.microservices.catalogsearchindex.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
        assertNotNull("The page element should not be null", searchResultResource.getMetadata());
        assertEquals("The number should be 2", 2, searchResultResource.getMetadata().getNumber());
        assertEquals("The size should be 10", 10, searchResultResource.getMetadata().getSize());
        assertEquals("The total elements should be 1000", 1000, searchResultResource.getMetadata().getTotalElements());
        assertEquals("The total pages should be 100", 100, searchResultResource.getMetadata().getTotalPages());
    }
    
    @Test
    public void whenOnAnyPageReturnValueShouldHaveASelfLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(0);
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertEquals("Should have a self-referential link element", "self", searchResultResource.getId().getRel());
    }

    @Test
    public void whenOnFirstPageThenReturnValueShouldNotHaveAPreviousLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(0);
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNull(searchResultResource.getLink(Link.REL_PREVIOUS));
    }

    @Test
    public void whenOnLastPageThenReturnValueShouldNotHaveANextLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(100);
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNull(searchResultResource.getLink(Link.REL_NEXT));
    }

    @Test
    public void whenNotOnFirstPageThenReturnValueShouldHaveAFirstLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(2);
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNotNull(searchResultResource.getLink(Link.REL_FIRST));
    }

    @Test
    public void whenNotOnLastPageThenReturnValueShouldHaveALastLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(10);
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNotNull(searchResultResource.getLink(Link.REL_LAST));
    }

    @Test
    public void whenItNotOnLastPageThenReturnValueShouldHaveALastLinkElement() {
        SearchAggregated searchAggregated = createSearchAggregated(10); 
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNotNull(searchResultResource.getLink(Link.REL_LAST));
    }

    @Test
    public void whenSearchResultHasItemsThenReturnValueShouldHaveItemsElement() {
        SearchAggregated searchAggregated = createSearchAggregated(0);
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertEquals("Items should have 2 items", 2, searchResultResource.getEmbedded().getItems().size());
    }
    
    @Test
    public void whenSearchResultHasAggregation() throws Exception {
        SearchAggregated searchAggregated = createSearchAggregated(0);
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertEquals(1, searchResultResource.getEmbedded().getAggregations().size());
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
