package no.nb.microservices.catalogsearchindex.core.repository;

import static org.elasticsearch.index.query.QueryBuilders.queryString;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchRepositoryTests {
	
    
	@Mock
	private ElasticsearchTemplate templateMock;

	@InjectMocks
	private ElasticSearchRepository elasticSearchRepository;
	
	@Test
    public void searchWithQueryString() {
        Pageable pageRequest = new PageRequest(0, 10);
        String searchString = "queryString";
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryString(searchString).defaultOperator(Operator.AND))
                .withPageable(pageRequest)
                .build();

        IsSameSearchQuery isSameSearchQuery = new IsSameSearchQuery(searchQuery);
        when(templateMock.query(eq(searchQuery), any(SearchResultsExtractor.class))).thenReturn(null);
        
        elasticSearchRepository.search(searchString, null, pageRequest);
        verify(templateMock).query(argThat(isSameSearchQuery), any(SearchResultsExtractor.class));
    }

    @Test
    public void searchWithQueryStringAndOneAggregation() throws Exception {
        Pageable pageRequest = new PageRequest(0, 10);
        String searchString = "queryString";
        String[] aggs = {"ddc1"};
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryString(searchString).defaultOperator(Operator.AND))
                .withPageable(pageRequest)
                .addAggregation(terms(aggs[0]).field(aggs[0]))
                .build();

        IsSameSearchQuery isSameSearchQuery = new IsSameSearchQuery(searchQuery);
        when(templateMock.query(eq(searchQuery), any(SearchResultsExtractor.class))).thenReturn(null);
        
        elasticSearchRepository.search(searchString, aggs, pageRequest);
        verify(templateMock).query(argThat(isSameSearchQuery), any(SearchResultsExtractor.class));
    }

    class IsSameSearchQuery extends ArgumentMatcher<SearchQuery> {

        private final SearchQuery expectedSearchQuery;
        
        public IsSameSearchQuery(SearchQuery expectedSearchQuery) {
            this.expectedSearchQuery = expectedSearchQuery;
        }
        
        @Override
        public boolean matches(Object argument) {
            if(argument instanceof SearchQuery){
                isSameSearchQuery(argument);
                return true;
            }
            return false;
        }

        private void isSameSearchQuery(Object argument) {
            SearchQuery actualSearchQuery = (SearchQuery)argument;
            hasAggregations(actualSearchQuery);
            isSameQuery(actualSearchQuery);
        }

        private void hasAggregations(SearchQuery actualSearchQuery) {
            if(expectedSearchQuery.getAggregations() != null) {
                assertNotNull(actualSearchQuery.getAggregations());
            }
        }

        private void isSameQuery(SearchQuery actualSearchQuery) {
            assertEquals(expectedSearchQuery.getQuery().toString(), actualSearchQuery.getQuery().toString());
        }
        
    }

}