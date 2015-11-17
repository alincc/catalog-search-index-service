package no.nb.microservices.catalogsearchindex.core.services;

import org.springframework.data.domain.Pageable;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;

public interface ISearchService {
    SearchAggregated search(SearchCriteria searchCriteria);

    SearchAggregated searchWithin(String id, String q, Pageable pageRequest);
}
