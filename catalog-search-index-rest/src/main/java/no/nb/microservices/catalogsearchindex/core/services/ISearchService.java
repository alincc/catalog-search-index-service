package no.nb.microservices.catalogsearchindex.core.services;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;

/**
 * @author ronnymikalsen
 */
public interface ISearchService {
    SearchAggregated search(SearchCriteria searchCriteria);
}
