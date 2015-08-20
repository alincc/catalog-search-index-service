package no.nb.microservices.catalogsearchindex.core.services;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.repository.ISearchRepository;

/**
 * @author ronnymikalsen
 */
@Service
public class SearchServiceImpl implements ISearchService {

    private final ISearchRepository searchRepository;

    @Autowired
    public SearchServiceImpl(final ISearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public SearchAggregated search(String searchString, String[] aggs, Pageable pageRequest) {

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder().withPageable(pageRequest);

        searchQueryBuilder.withQuery(queryString(searchString));
        
        if(aggs != null) {
            for(String agg : aggs) {
                searchQueryBuilder.addAggregation(AggregationBuilders.terms(agg).field(agg));
            }
        }

        return searchRepository.search(searchQueryBuilder.build());
    }

}
