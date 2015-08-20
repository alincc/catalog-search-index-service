package no.nb.microservices.catalogsearchindex.core.repository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ResultsExtractor;

import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

public class SearchResultsExtractorTest {

    private ResultsExtractor<SearchAggregated> searchResultsExtractor;

    @Before
    public void setup() {
        searchResultsExtractor = new SearchResultsExtractor();
    }
    
    @Test
    public void searchResponseWithHits() throws Exception {
        SearchResponse mockedSearchResponse = getMockedSearchResponse();
        SearchAggregated searchAggregated = searchResultsExtractor.extract(mockedSearchResponse);
        assertThat(searchAggregated.getPage(), new ContainsItemsWithIds(Arrays.asList("id1")));
    }
    
    @Test
    public void searchResponseWithHitsAndAggregations() throws Exception {
        SearchResponse mockedSearchResponse = getMockedSearchResponse();
        SearchAggregated searchAggregated = searchResultsExtractor.extract(mockedSearchResponse);
        assertNotNull(searchAggregated.getAggregations());
    }

    private SearchResponse getMockedSearchResponse() {
        SearchResponse mockedSearchResponse = mock(SearchResponse.class);
        SearchHits mockedSearchHits = getMockedSearchHits();
        when(mockedSearchResponse.getHits()).thenReturn(mockedSearchHits);
        
        Aggregations mockedAggregations = mock(Aggregations.class);
        when(mockedSearchResponse.getAggregations()).thenReturn(mockedAggregations);
        return mockedSearchResponse;
    }

    private SearchHits getMockedSearchHits() {
        SearchHits mockedSearchHits = mock(SearchHits.class);
        List<SearchHit> mockedSearchHitList = new ArrayList<>();
        
        SearchHit mockedSearchHit1 = mock(SearchHit.class);
        when(mockedSearchHit1.getId()).thenReturn("id1");
        mockedSearchHitList.add(mockedSearchHit1);
        
        Iterator<SearchHit> mockedIterator = mockedSearchHitList.iterator();
        when(mockedSearchHits.iterator()).thenReturn(mockedIterator);
        
        return mockedSearchHits;
    }
    
    class ContainsItemsWithIds extends BaseMatcher<Page<Item>> {
        private List<String> itemIds;
        
        public ContainsItemsWithIds(List<String> itemIds) {
            this.itemIds = itemIds;
        }

        @Override
        public boolean matches(Object item) {
            if(item instanceof Page) {
                return hasSameContent(item);
            }
            return false;
        }

        private boolean hasSameContent(Object item) {
            Page<?> page = (Page<?>)item;
            Iterator<?> iterator = page.iterator();
            if(!pageContainsIds(iterator))
                return false;
            if(itemIds.size() != page.getNumberOfElements())
                return false;
            return true;
        }

        private boolean pageContainsIds(Iterator<?> iterator) {
            while(iterator.hasNext()) {
                Object nextObject = iterator.next();
                if(nextObject instanceof Item) {
                    Item expressionRecord = (Item)nextObject;
                    if(!itemIds.contains(expressionRecord.getId())) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("A page that contains items with given ids");
        }
    }
}
