package no.nb.microservices.catalogsearchindex.rest.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.List;

import no.nb.microservices.catalogsearchindex.core.content.service.IContentService;
import no.nb.microservices.catalogsearchindex.core.model.ContentFragment;
import no.nb.microservices.catalogsearchindex.core.model.ContentSearch;
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
import no.nb.microservices.catalogsearchindex.core.index.service.ISearchService;
import no.nb.microservices.catalogsearchindex.searchwithin.ContentSearchResource;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {

    @Mock
    private ISearchService searchService;

    @Mock
    private IContentService contentService;

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
                .willReturn(new SearchAggregated(null, null));

        given(contentService.getContent(anyObject()))
                .willReturn(contentSearch());

        ResponseEntity<ContentSearchResource> searchWithin = controller
                .contentSearch("id", "q", getPageable());

        assertThat("Should hava a self reference", searchWithin.getBody().getId().getHref(), is("http://localhost/catalog/v1/id/search?q=q"));
        assertThat("Should have fragments", searchWithin.getBody().getFragments().size(), is(1));
    }

    private ContentSearch contentSearch() {
        ContentSearch contentSearch = new ContentSearch();
        List<ContentFragment> fragments = new ArrayList<>();
        fragments.add(new ContentFragment(1766,2647,226,45,"URN:NBN:no-nb_digibok_2014070158006_C3","TEATER","",""));
        contentSearch.setFragments(fragments);
        return contentSearch;
    }

    private Pageable getPageable() {
        Pageable pageable = new PageRequest(0, 1);
        return pageable;
    }

}
