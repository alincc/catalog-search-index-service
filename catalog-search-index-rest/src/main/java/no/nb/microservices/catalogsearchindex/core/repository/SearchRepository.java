package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import org.springframework.data.domain.Pageable;

public interface SearchRepository {
    
    SearchAggregated search(String searchString, String[] aggregations, Pageable pageRequest, boolean freeText, boolean metadata);
}
