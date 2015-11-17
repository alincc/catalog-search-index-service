package no.nb.microservices.catalogsearchindex.core.repository;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;

public interface SearchRepository {
    
    SearchAggregated search(SearchCriteria searchCriteria);
}
