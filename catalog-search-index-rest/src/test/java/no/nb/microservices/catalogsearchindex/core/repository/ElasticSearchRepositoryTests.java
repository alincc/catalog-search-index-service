package no.nb.microservices.catalogsearchindex.core.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import no.nb.microservices.catalogsearchindex.core.model.Item;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.FacetedPageImpl;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchRepositoryTests {
	
	@Mock
	private ElasticsearchTemplate templateMock;
	
	@Test
	public void testSearch() {
		ElasticSearchRepository elasticSearchRepository = new ElasticSearchRepository(templateMock);
		
		String query = "Junit";
		Pageable pageRequest = new PageRequest(0, 10);

		FacetedPage<Item> p2 = new FacetedPageImpl<Item>(Arrays.asList(new Item("1"), new Item("2"), new Item("3")));
		when(templateMock.queryForPage(argThat(new CompareSearchQuery()), eq(Item.class))).thenReturn(p2);
		
		Page<Item> result = elasticSearchRepository.search(query, pageRequest);
		assertNotNull(result);
		assertEquals("Should be 3 items", 3, result.getContent().size());
		
		Mockito.verify(templateMock).queryForPage(argThat(new CompareSearchQuery()), eq(Item.class));
	}
	
	class CompareSearchQuery extends ArgumentMatcher<SearchQuery> {

		@Override
        public boolean matches(Object argument) {
	        if(argument instanceof SearchQuery) {
	        	return true;
	        }else {
	        	return false;
	        }
        }
	}
}
