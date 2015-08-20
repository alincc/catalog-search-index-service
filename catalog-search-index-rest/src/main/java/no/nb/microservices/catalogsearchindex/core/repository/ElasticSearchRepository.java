package no.nb.microservices.catalogsearchindex.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

@Repository
public class ElasticSearchRepository implements ISearchRepository {

    private final ElasticsearchOperations template;

    @Autowired
    public ElasticSearchRepository(final ElasticsearchOperations template) {
        this.template = template;
    }

    @Override
    public SearchAggregated search(SearchQuery searchQuery) {
        return template.query(searchQuery, new SearchResultsExtractor());
    }

}
