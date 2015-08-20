package no.nb.microservices.catalogsearchindex.core.services;

import org.springframework.data.domain.Pageable;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

/**
 * @author ronnymikalsen
 */
public interface ISearchService {
    SearchAggregated search(String searchString, String[] aggs, Pageable pageRequest);
}
