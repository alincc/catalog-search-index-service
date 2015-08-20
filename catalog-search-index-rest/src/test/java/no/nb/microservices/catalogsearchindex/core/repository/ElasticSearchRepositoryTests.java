package no.nb.microservices.catalogsearchindex.core.repository;

import static org.elasticsearch.index.query.QueryBuilders.queryString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchRepositoryTests {
	
	@Mock
	private ElasticsearchTemplate templateMock;
	
	@Test
	public void repositoryUsesTemplateToDoSearch() {
		ElasticSearchRepository elasticSearchRepository = new ElasticSearchRepository(templateMock);
		SearchQuery searchQuery = new NativeSearchQuery(queryString("query"));
		when(templateMock.query(eq(searchQuery), any(SearchResultsExtractor.class))).thenReturn(null);
		
		elasticSearchRepository.search(searchQuery);
		
		Mockito.verify(templateMock).query(eq(searchQuery), any(SearchResultsExtractor.class));
	}
}