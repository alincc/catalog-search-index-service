package no.nb.microservices.catalogsearchindex.rest.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.services.ISearchService;
import no.nb.microservices.catalogsearchindex.searchwithin.ContentSearchResource;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {

    @Mock
    private ISearchService searchService;

    @InjectMocks
    private SearchController controller;

    @Before
    public void init() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/catalog/v1/id/search?q=q");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @After
    public void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
    }
    
    @Test
    public void testSearchWithin() {
        given(searchService.contentSearch(anyString(), anyString(), anyObject()))
                .willReturn(searchWithin());

        ResponseEntity<ContentSearchResource> searchWithin = controller
                .contentSearch("id", "q", getPageable());

        assertThat("Should hava a self reference", searchWithin.getBody().getId().getHref(), is("http://localhost/catalog/v1/id/search?q=q"));
        assertThat("Should have fragments", searchWithin.getBody().getFragments().size(), is(2));
        assertThat("Should have freetext metadata", searchWithin.getBody().getFreetextMetadatas().size(), is(1));
    }

    private SearchAggregated searchWithin() {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.addFreetextFragment("K?RE#414P4F110w");
        item.addFreetextFragment("PUBLIKUM#415?4F1?0x");
        item.addFreetextMetadata("DIV1#1000g7m?");
        items.add(item);
        Pageable pageable = getPageable();
        Page page = new PageImpl<>(items, pageable, 1);
        return new SearchAggregated(page);
    }

    private Pageable getPageable() {
        Pageable pageable = new PageRequest(0, 1);
        return pageable;
    }

}
