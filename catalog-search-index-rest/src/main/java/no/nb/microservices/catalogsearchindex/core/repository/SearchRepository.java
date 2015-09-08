package no.nb.microservices.catalogsearchindex.core.repository;

import org.springframework.data.domain.Pageable;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

public interface SearchRepository {
    
    SearchAggregated search(String searchString, String[] aggs, Pageable pageRequest);
}
