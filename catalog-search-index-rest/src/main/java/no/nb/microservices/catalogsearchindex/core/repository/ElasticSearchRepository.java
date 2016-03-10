package no.nb.microservices.catalogsearchindex.core.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nb.microservices.catalogsearchindex.NBSearchType;
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
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

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
    public SearchAggregated contentSearch(String id, String searchString, Pageable pageable) {
        SearchRequestBuilder search = buildFreetextQuery(id, searchString, pageable);
        SearchResponse searchResponse = search.execute().actionGet();
        Page<Item> page = extractSearchResult(searchResponse, pageable);

        return new SearchAggregated(page, null);
    }

    private SearchRequestBuilder getSearchRequestBuilder(SearchCriteria searchCriteria) {
        Pageable pageRequest = searchCriteria.getPageRequest();
        List<FilterBuilder> filters = new LinkedList<>();

        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(pageRequest);

        QueryStringQueryBuilder query = getQueryStringQueryBuilder(searchCriteria);

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(query);

        geoSearch(searchCriteria, filters, searchRequestBuilder);
        searchRequestBuilder.setExplain(searchCriteria.isExplain());
        BoolFilterBuilder filterBuilder = searchFilters(searchCriteria, filters);

        searchFields(filterBuilder, searchRequestBuilder, boolQueryBuilder);
        aggregations(searchCriteria, searchRequestBuilder);
        sort(searchCriteria, searchRequestBuilder);
        boost(searchCriteria, query);
        should(searchCriteria, boolQueryBuilder);

        return searchRequestBuilder;
    }

    private Page<Item> extractSearchResult(SearchResponse searchResponse, Pageable pageRequest) {
        List<Item> content = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (SearchHit searchHit : searchResponse.getHits()) {
            Item item = new Item(searchHit.getId());
            if (searchHit.getFields().containsKey("urn")) {
                item.setUrn(searchHit.getFields().get("urn").getValue().toString());
            }
            if (searchHit.getFields().containsKey("firstIndexTime")) {
                item.setFirstIndexTime(searchHit.getFields().get("firstIndexTime").getValue().toString());
            }
            if (searchHit.getFields().containsKey("location")) {
                item.setLocation(searchHit.getFields().get("location").getValue().toString());
            }
            if (searchHit.getFields().containsKey("pageCount")) {
                item.setPageCount(searchHit.getFields().get("pageCount").getValue().toString());
            }
            if (searchHit.getFields().containsKey("contentClasses")) {
                item.setContentClasses(searchHit.getFields().get("contentClasses").getValues()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList()));
            }
            if (searchHit.getFields().containsKey("metadataClasses")) {
                item.setMetadataClasses(searchHit.getFields().get("metadataClasses").getValues()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList()));
            }
            if (searchHit.getFields().containsKey("digital")) {
                String digital = searchHit.getFields().get("digital").getValue().toString();
                item.setDigital(digital.equalsIgnoreCase("ja") ? true : false);
            }
            if (searchHit.getFields().containsKey("title")) {
                item.setTitle(searchHit.getFields().get("title").getValue().toString());
            }
            if (searchHit.getFields().containsKey("mediatype")) {
                item.setMediaTypes(searchHit.getFields().get("mediatype").getValues()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList()));
            }
            if (searchHit.getFields().containsKey("thumbnailurn")) {
                item.setThumbnailUrn(searchHit.getFields().get("thumbnailurn").getValue().toString());
            }
            if (searchHit.getFields().containsKey("creator")) {
                item.setCreators(searchHit.getFields().get("creator").getValues()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList()));
            }
            if (searchHit.getFields().containsKey("dateissued")) {
                item.setDateIssued(searchHit.getFields().get("dateissued").getValue().toString());
            }

            List<Text> fragments = getFreetextHits(searchHit);
            for(Text fragment : fragments) {
                item.addFreetextFragment(fragment.string());
            }

            List<String> freetextMetadatas = getFreetextMetadata(searchHit);
            for (String freetextMetadata : freetextMetadatas) {
                item.addFreetextMetadata(freetextMetadata);
            }
            if (searchHit.getExplanation() != null) {
                JsonNode jsonNode = mapper.convertValue(searchHit.getExplanation(), JsonNode.class);
                item.setExplain(jsonNode);
            }
            content.add(item);
        }
        return new PageImpl<>(content, pageRequest, searchResponse.getHits().getTotalHits());
    }

    private SearchRequestBuilder buildFreetextQuery(String id, String searchString, Pageable pageRequest) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder = queryBuilder.must(
                QueryBuilders.queryStringQuery(searchString).field("freetext"));

        queryBuilder = queryBuilder
                .must(new QueryStringQueryBuilder("urn:\""+id+"\""));

        SearchSourceBuilder searchBuilder = SearchSourceBuilder.searchSource()
                .query(queryBuilder);

        searchBuilder = searchBuilder.highlight(SearchSourceBuilder.highlight()
                .field("freetext", 200, 10000, 0).preTags("")
                .postTags(""));

        searchBuilder = searchBuilder.field("freetext_metadata");

        SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(
                pageRequest);
        searchRequestBuilder.internalBuilder(searchBuilder);
        return searchRequestBuilder;
    }

    private SearchRequestBuilder createSearchRequestBuilder(
            Pageable pageRequest) {
        return client
                .prepareSearch(SCHEMA_NAME)
                .setTypes(TYPE_NAME)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(pageRequest.getPageNumber() * pageRequest.getPageSize())
                .setSize(pageRequest.getPageSize());
    }

    private QueryStringQueryBuilder getQueryStringQueryBuilder(SearchCriteria searchCriteria) {
        QueryStringQueryBuilder query = new QueryStringQueryBuilder(searchCriteria.getSearchString());

        if(searchCriteria.getSearchType() == NBSearchType.FIELD_RESTRICTED_SEARCH) {
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
        } else if(searchCriteria.getSearchType() == NBSearchType.TEXT_SEARCH) {
            query.field("freetext");
        }
        return query;
    }

    private void geoSearch(SearchCriteria searchCriteria, List<FilterBuilder> filters, SearchRequestBuilder searchRequestBuilder) {
        GeoSearch geoSearch = searchCriteria.getGeoSearch();
        if(geoSearch != null) {
            searchRequestBuilder.addAggregation(AggregationBuilders.geohashGrid("locations").field("location").precision(geoSearch.getPrecision()));
            if(geoSearch.getTopRight() != null && geoSearch.getBottomLeft() != null) {
                GeoBoundingBoxFilterBuilder geoBoundingBoxFilterBuilder = FilterBuilders.geoBoundingBoxFilter("location")
                        .topRight(geoSearch.getTopRight())
                        .bottomLeft(geoSearch.getBottomLeft());

                filters.add(geoBoundingBoxFilterBuilder);
            }
        }
    }

    private BoolFilterBuilder searchFilters(SearchCriteria searchCriteria, List<FilterBuilder> filters) {
        BoolFilterBuilder filterBuilder = null;
        if (searchCriteria.getFilters() != null && searchCriteria.getFilters().length > 0) {
            for (String filter : searchCriteria.getFilters()) {
                String[] values = filter.split(":");
                filters.add(FilterBuilders.termFilter(values[0].toLowerCase(), values[1].toLowerCase()));
            }
        }

        if (filters.size() > 0) {
            filterBuilder = FilterBuilders.boolFilter().must(filters.toArray(new FilterBuilder[filters.size()]));
        }
        return filterBuilder;
    }

    private void searchFields(BoolFilterBuilder filterBuilder, SearchRequestBuilder searchRequestBuilder, BoolQueryBuilder boolQueryBuilder) {
        FilteredQueryBuilder filteredQueryBuilder = new FilteredQueryBuilder(boolQueryBuilder, filterBuilder);
        searchRequestBuilder.setQuery(filteredQueryBuilder);
        searchRequestBuilder.addField("location");
        searchRequestBuilder.addField("firstIndexTime");
        searchRequestBuilder.addField("pageCount");
        searchRequestBuilder.addField("contentClasses");
        searchRequestBuilder.addField("metadataClasses");
        searchRequestBuilder.addField("digital");
        searchRequestBuilder.addField("title");
        searchRequestBuilder.addField("mediatype");
        searchRequestBuilder.addField("thumbnailurn");
        searchRequestBuilder.addField("creator");
        searchRequestBuilder.addField("urn");
        searchRequestBuilder.addField("dateissued");
    }

    private void aggregations(SearchCriteria searchCriteria,
            SearchRequestBuilder searchRequestBuilder) {
        String[] aggregations = searchCriteria.getAggregations();
        if(aggregations != null) {
            for (String aggregation : aggregations) {
                searchRequestBuilder.addAggregation(AggregationBuilders.terms(aggregation).field(aggregation));
            }
        }
    }

    private void sort(SearchCriteria searchCriteria,
                      SearchRequestBuilder searchRequestBuilder) {
        if (searchCriteria.getPageRequest().getSort() != null) {
            for (Sort.Order order : searchCriteria.getPageRequest().getSort()) {
                if (order.getProperty().equalsIgnoreCase("date")) {
                    FieldSortBuilder fieldSortBuilder = new FieldSortBuilder("year");
                    fieldSortBuilder.missing("_last");
                    SortOrder sortOrder = SortOrder.ASC;
                    if (!order.isAscending()) {
                        sortOrder = SortOrder.DESC;
                    }
                    searchRequestBuilder.addSort(fieldSortBuilder);

                    fieldSortBuilder = new FieldSortBuilder("month");
                    fieldSortBuilder.missing("_last");
                    fieldSortBuilder.order(sortOrder);
                    searchRequestBuilder.addSort(fieldSortBuilder);

                    fieldSortBuilder = new FieldSortBuilder("day");
                    fieldSortBuilder.missing("_last");
                    fieldSortBuilder.order(sortOrder);
                    searchRequestBuilder.addSort(fieldSortBuilder);
                } else {
                    String sortfield = order.getProperty();
                    if (order.getProperty().equalsIgnoreCase("title")) {
                        sortfield = "titlesort";
                    }
                    if (sortfield.equalsIgnoreCase("otherid")) {
                        sortfield = "otheridsort";
                    }

                    FieldSortBuilder fieldSortBuilder = new FieldSortBuilder(sortfield);
                    SortOrder sortOrder = SortOrder.ASC;
                    if (!order.isAscending()) {
                        sortOrder = SortOrder.DESC;
                    }
                    fieldSortBuilder.order(sortOrder);
                    searchRequestBuilder.addSort(fieldSortBuilder);
                }
            }
        }
    }

    private void boost(SearchCriteria searchCriteria, QueryStringQueryBuilder query) {

        searchCriteria.getBoostMap().forEach((k,v) -> {
            query.field(k, v);
        });

    }

    private void should(SearchCriteria searchCriteria, BoolQueryBuilder boolQueryBuilder) {
        if (searchCriteria.getShould().length > 0) {
            if (searchCriteria.getShould().length == 2 && searchCriteria.getShould()[0].indexOf(",") == -1) {
                boolQueryBuilder.should(QueryBuilders.termQuery(searchCriteria.getShould()[0], searchCriteria.getShould()[1]));
            } else {
                for (String should : searchCriteria.getShould()) {
                    String[] split = should.split(",");
                    boolQueryBuilder.should(QueryBuilders.termQuery(split[0], split[1]));
                }
            }
        }
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
        ArrayList<String> metadatas = new ArrayList<>();
        SearchHitField field = searchHitFields.field("freetext_metadata");
        if (field != null && field.getValue() != null && field.getValue() instanceof String) {
            for(String metadata : ((String)field.getValue()).split(" ")) {
                metadatas.add(metadata);
            }
        }
        return metadatas;
    }

}
