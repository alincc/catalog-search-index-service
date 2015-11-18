package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.core.model.GeoSearch;
import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public SearchAggregated search(SearchCriteria searchCriteria) {
        SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(searchCriteria);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        Page<Item> page = extractSearchResult(searchResponse, searchCriteria.getPageRequest());
        return new SearchAggregated(page, searchResponse.getAggregations());
    }
    
    @Override
    public SearchAggregated searchWithin(String id, String searchString, Pageable pageable) {
        SearchRequestBuilder search = buildFreetextQuery(id, searchString, pageable);
        SearchResponse searchResponse = search.execute().actionGet();
        Page<Item> page = extractSearchResult(searchResponse, pageable);
        return new SearchAggregated(page, null);
    }

    private SearchRequestBuilder buildFreetextQuery(String id, String searchString, Pageable pageRequest) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder = queryBuilder.must(
                QueryBuilders.queryStringQuery(searchString).field("freetext"));
        queryBuilder = queryBuilder
                .must(QueryBuilders.idsQuery().addIds(id));
        SearchSourceBuilder searchBuilder = SearchSourceBuilder.searchSource()
                .query(queryBuilder);
        searchBuilder = searchBuilder.highlight(SearchSourceBuilder.highlight()
                .field("freetext", 1, 10000, 0).preTags("")
                .postTags(""));
        searchBuilder = searchBuilder.field("freetext_metadata");
        
        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(
                pageRequest);
        searchRequestBuilder.internalBuilder(searchBuilder);
        return searchRequestBuilder;
    }

    private Page<Item> extractSearchResult(SearchResponse searchResponse, Pageable pageRequest) {
        List<Item> content = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits()) {
            Item item = new Item(searchHit.getId());
            if (searchHit.getFields().containsKey("location")) {
                item.setLocation(searchHit.getFields().get("location").getValue().toString());
            }
            List<Text> fragments = getFreetextHits(searchHit);
            for(Text fragment : fragments) {
                item.addFreetextFragment(fragment.string());
            }
            
            List<String> freetextMetadatas = getFreetextMetadata(searchHit);
            for (String freetextMetadata : freetextMetadatas) {
                item.addFreetextMetadata(freetextMetadata);
            }
            content.add(item);
        }
        return new PageImpl<>(content, pageRequest, searchResponse.getHits().getTotalHits());
    }

    private List<Text> getFreetextHits(SearchHit searchHitFields) {
        if (searchHitFields != null && searchHitFields.getHighlightFields() != null && searchHitFields.getHighlightFields().containsKey("freetext")) {
            HighlightField highlightField = searchHitFields.getHighlightFields().get("freetext");
            return Arrays.asList(highlightField.getFragments());
        } else {
            return Collections.emptyList();
        }
    }
    
    private List<String> getFreetextMetadata(SearchHit searchHitFields) {
        ArrayList<String> metadatas = new ArrayList<String>();
        SearchHitField field = searchHitFields.field("freetext_metadata");
        if (field != null && field.getValue() != null && field.getValue() instanceof String) {
            for(String metadata : ((String)field.getValue()).split(" ")) {
                metadatas.add(metadata);
            }
        }
        return metadatas;
    }

    private SearchRequestBuilder getSearchRequestBuilder(SearchCriteria searchCriteria) {
        Pageable pageRequest = searchCriteria.getPageRequest();

        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(pageRequest);

        QueryStringQueryBuilder query = getQueryStringQueryBuilder(searchCriteria);

        FilterBuilder filterBuilder = null;
        GeoSearch geoSearch = searchCriteria.getGeoSearch();
        if(geoSearch != null) {
            searchRequestBuilder.addAggregation(AggregationBuilders.geohashGrid("locations").field("location").precision(geoSearch.getPrecision()));
            if(geoSearch.getTopRight() != null && geoSearch.getBottomLeft() != null) {
                filterBuilder = FilterBuilders.geoBoundingBoxFilter("location")
                        .topRight(geoSearch.getTopRight())
                        .bottomLeft(geoSearch.getBottomLeft());
            }
        }

        FilteredQueryBuilder filteredQueryBuilder = new FilteredQueryBuilder(query, filterBuilder);
        searchRequestBuilder.setQuery(filteredQueryBuilder);
        searchRequestBuilder.addField("location");
        
        String[] aggregations = searchCriteria.getAggregations();
        if(aggregations != null) {
            for (String aggregation : aggregations) {
                searchRequestBuilder.addAggregation(AggregationBuilders.terms(aggregation).field(aggregation));
            }
        }
        return searchRequestBuilder;
    }

    private SearchRequestBuilder createSearchRequestBuilder(
            Pageable pageRequest) {
        return client
                .prepareSearch(SCHEMA_NAME)
                .setTypes(TYPE_NAME)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(pageRequest.getPageNumber())
                .setSize(pageRequest.getPageSize());
    }

    private QueryStringQueryBuilder getQueryStringQueryBuilder(SearchCriteria searchCriteria) {
        QueryStringQueryBuilder query = new QueryStringQueryBuilder(searchCriteria.getSearchString());

        switch (searchCriteria.getSearchType()) {
            case FIELD_RESTRICTED_SEARCH:
                query.field("title", 6);
                query.field("name", 4);
                query.field("description", 4);
                query.field("hosttitle");
                query.field("otherid");
                query.field("subject");
                query.field("isbn");
                query.field("series");
                query.field("note");
                query.field("ismn");
                break;
            case TEXT_SEARCH:
                query.field("freetext");
                break;
        }
        return query;
    }
}
