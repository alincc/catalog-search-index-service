package no.nb.microservices.catalogsearchindex.core.repository;

import org.springframework.data.domain.Pageable;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;

public interface SearchRepository {
    
    SearchAggregated search(SearchCriteria searchCriteria);

    SearchAggregated searchWithin(String id, String searchString, Pageable pageRequest);
}
