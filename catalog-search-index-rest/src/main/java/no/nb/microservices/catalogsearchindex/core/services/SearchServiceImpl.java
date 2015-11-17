package no.nb.microservices.catalogsearchindex.core.services;

import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.repository.SearchRepository;

@Service
public class SearchServiceImpl implements ISearchService {

    private final SearchRepository searchRepository;

    @Autowired
    public SearchServiceImpl(final SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public SearchAggregated search(SearchCriteria searchCriteria) {
        return searchRepository.search(searchCriteria);
    }

    @Override
    public SearchAggregated searchWithin(String id, String q,
            Pageable pageable) {
        return searchRepository.searchWithin(id, q, pageable);
    }

}
