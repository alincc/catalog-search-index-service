package no.nb.microservices.catalogsearchindex.core.services;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

import org.springframework.data.domain.Pageable;

/**
 * @author ronnymikalsen
 */
public interface ISearchService {
    SearchAggregated search(String query, Pageable pageRequest);
}
