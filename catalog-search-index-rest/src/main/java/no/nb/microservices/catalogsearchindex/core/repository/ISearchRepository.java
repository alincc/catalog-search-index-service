package no.nb.microservices.catalogsearchindex.core.repository;

import org.springframework.data.elasticsearch.core.query.SearchQuery;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

public interface ISearchRepository {
    SearchAggregated search(SearchQuery searchQuery);
}
