package no.nb.microservices.catalogsearchindex.core.services;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;
import org.springframework.data.domain.Pageable;

public interface ISearchService {
    SearchAggregated search(SearchCriteria searchCriteria);
    SearchAggregated contentSearch(String id, String q, Pageable pageRequest);
}
