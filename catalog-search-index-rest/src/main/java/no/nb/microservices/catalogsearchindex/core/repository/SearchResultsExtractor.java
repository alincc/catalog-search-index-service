package no.nb.microservices.catalogsearchindex.core.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.core.ResultsExtractor;

import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

public class SearchResultsExtractor implements ResultsExtractor<SearchAggregated>{

    @Override
    public SearchAggregated extract(SearchResponse response) {
        Page<Item> page = new PageImpl<>(extractContent(response));
        SearchAggregated searchAggregated = new SearchAggregated(page);
        searchAggregated.setAggregations(response.getAggregations());
        return searchAggregated;
    }

    private List<Item> extractContent(SearchResponse response) {
        SearchHits hits = response.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        List<Item> content = new ArrayList<>();
        
        while(iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            content.add(new Item(searchHit.getId()));
        }
        
        return content;
    }

}
