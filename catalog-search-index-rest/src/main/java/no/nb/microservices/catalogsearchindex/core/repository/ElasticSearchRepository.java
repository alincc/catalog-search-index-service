package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ElasticSearchRepository implements SearchRepository {

    private static final String SCHEMA_NAME = "expressionrecords";
    private static final String TYPE_NAME = "expressionrecord";
    private final Client client;

    @Autowired
    public ElasticSearchRepository(Client client) {
        this.client = client;
    }

    @Override
    public SearchAggregated search(String searchString, String[] aggregations, Pageable pageRequest) {
        SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(searchString, aggregations, pageRequest);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        Page<Item> page = extractSearchResult(searchResponse, pageRequest);
        return new SearchAggregated(page, searchResponse.getAggregations());
    }

    private Page<Item> extractSearchResult(SearchResponse searchResponse, Pageable pageRequest) {
        List<Item> content = new ArrayList<>();
        for (SearchHit searchHitFields : searchResponse.getHits()) {
            content.add(new Item(searchHitFields.getId()));
        }
        return new PageImpl<>(content, pageRequest, searchResponse.getHits().getTotalHits());
    }

    private SearchRequestBuilder getSearchRequestBuilder(String searchString, String[] aggregations, Pageable pageRequest) {
        QueryBuilder queryBuilder = QueryBuilders.simpleQueryStringQuery(searchString);

        SearchRequestBuilder searchRequestBuilder = client
                .prepareSearch(SCHEMA_NAME)
                .setTypes(TYPE_NAME)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder)
                .setFrom(pageRequest.getPageNumber())
                .setSize(pageRequest.getPageSize());

        if(aggregations != null) {
            for (String aggregation : aggregations) {
                searchRequestBuilder.addAggregation(AggregationBuilders.terms(aggregation).field(aggregation));
            }
        }
        return searchRequestBuilder;
    }

}
