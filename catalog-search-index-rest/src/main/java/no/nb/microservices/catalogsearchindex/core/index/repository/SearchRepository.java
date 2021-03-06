package no.nb.microservices.catalogsearchindex.core.index.repository;

import org.springframework.data.domain.Pageable;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;

public interface SearchRepository {
    
    SearchAggregated search(SearchCriteria searchCriteria);

    SearchAggregated contentSearch(String id, String searchString, Pageable pageRequest);
}
