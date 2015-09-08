package no.nb.microservices.catalogsearchindex.core.repository;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

@Repository
public class ElasticSearchRepository implements SearchRepository {

    private final ElasticsearchOperations template;

    @Autowired
    public ElasticSearchRepository(final ElasticsearchOperations template) {
        this.template = template;
    }

    @Override
    public SearchAggregated search(String searchString, String[] aggregations,
            Pageable pageRequest) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(queryString(searchString).defaultOperator(Operator.AND))
                .withPageable(pageRequest);
        
        if(aggregations != null) {
            for(String agg : aggregations) {
                searchQueryBuilder.addAggregation(AggregationBuilders.terms(agg).field(agg));
            }
        }

        return template.query(searchQueryBuilder.build(), new SearchResultsExtractor());
    }

}
