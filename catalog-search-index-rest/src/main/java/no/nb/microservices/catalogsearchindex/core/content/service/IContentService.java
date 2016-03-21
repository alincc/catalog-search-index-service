package no.nb.microservices.catalogsearchindex.core.content.service;

import no.nb.microservices.catalogsearchindex.core.model.ContentSearch;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

public interface IContentService {
    ContentSearch getContent(SearchAggregated searchAggregated);
}
