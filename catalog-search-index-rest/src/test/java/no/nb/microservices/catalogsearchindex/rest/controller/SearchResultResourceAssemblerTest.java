package no.nb.microservices.catalogsearchindex.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import no.nb.microservices.catalogsearchindex.SearchResource;
import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

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

public class SearchResultResourceAssemblerTest {

	@Before
	public void init() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/search?q=Junit");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(attributes);
	}
	
	@After
    public void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
    }
	
	@Test
    public void whenOnAnyPageReturnValueShouldHaveAPageElement() {
        SearchResultResourceAssembler searchResultResourceAssembler = new SearchResultResourceAssembler();
        
        Page<Item> page = new PageImpl<Item>(new ArrayList<Item>(), new PageRequest(2, 10) , 1000);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNotNull("The page element should not be null", searchResultResource.getMetadata());
        assertEquals("The number should be 2", 2, searchResultResource.getMetadata().getNumber());
        assertEquals("The size should be 10", 10, searchResultResource.getMetadata().getSize());
        assertEquals("The total elements should be 1000", 1000, searchResultResource.getMetadata().getTotalElements());
        assertEquals("The total pages should be 100", 100, searchResultResource.getMetadata().getTotalPages());
        
    }
    
    @Test
    public void whenOnAnyPageReturnValueShouldHaveASelfLinkElement() {
        SearchResultResourceAssembler searchResultResourceAssembler = new SearchResultResourceAssembler();

        Page<Item> page = new PageImpl<Item>(new ArrayList<Item>(), new PageRequest(0, 10) , 1000);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertEquals("Should have a self-referential link element", "self", searchResultResource.getId().getRel());
        
    }

    @Test
    public void whenOnFirstPageThenReturnValueShouldNotHaveAPreviousLinkElement() {
        SearchResultResourceAssembler searchResultResourceAssembler = new SearchResultResourceAssembler();

        Page<Item> page = new PageImpl<Item>(new ArrayList<Item>(), new PageRequest(0, 10) , 1000);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNull(searchResultResource.getLink(Link.REL_PREVIOUS));
        
    }

    @Test
    public void whenOnLastPageThenReturnValueShouldNotHaveANextLinkElement() {
        SearchResultResourceAssembler searchResultResourceAssembler = new SearchResultResourceAssembler();

        Page<Item> page = new PageImpl<Item>(new ArrayList<Item>(), new PageRequest(100, 10) , 1000);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNull(searchResultResource.getLink(Link.REL_NEXT));
        
    }

    @Test
    public void whenNotOnFirstPageThenReturnValueShouldHaveAFirstLinkElement() {
        SearchResultResourceAssembler searchResultResourceAssembler = new SearchResultResourceAssembler();

        Page<Item> page = new PageImpl<Item>(new ArrayList<Item>(), new PageRequest(2, 10) , 1000);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNotNull(searchResultResource.getLink(Link.REL_FIRST));
        
    }

    @Test
    public void whenNotOnLastPageThenReturnValueShouldHaveALastLinkElement() {
        SearchResultResourceAssembler searchResultResourceAssembler = new SearchResultResourceAssembler();

        Page<Item> page = new PageImpl<Item>(new ArrayList<Item>(), new PageRequest(10, 10) , 1000);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNotNull(searchResultResource.getLink(Link.REL_LAST));
        
    }

    @Test
    public void whenItNotOnLastPageThenReturnValueShouldHaveALastLinkElement() {
        SearchResultResourceAssembler searchResultResourceAssembler = new SearchResultResourceAssembler();

        Page<Item> page = new PageImpl<Item>(new ArrayList<Item>(), new PageRequest(10, 10) , 1000);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertNotNull(searchResultResource.getLink(Link.REL_LAST));
        
    }

    @Test
    public void whenSearchResultHasItemsThenReturnValueShouldHaveItemsElement() {
        SearchResultResourceAssembler searchResultResourceAssembler = new SearchResultResourceAssembler();

        ArrayList<Item> items = new ArrayList<>();
        items.add(createItem("id1"));
        items.add(createItem("id2"));
        
        Page<Item> page = new PageImpl<Item>(items, new PageRequest(0, 10) , 1000);
        SearchAggregated searchAggregated = new SearchAggregated(page);
        
        SearchResource searchResultResource = searchResultResourceAssembler.toResource(searchAggregated);
        assertEquals("Items should have 2 items", 2, searchResultResource.getEmbedded().getItems().size());
        
    }
    
    private Item createItem(String id) {
        Item item = new Item();
        item.setId(id);
        return item;
    }
}
