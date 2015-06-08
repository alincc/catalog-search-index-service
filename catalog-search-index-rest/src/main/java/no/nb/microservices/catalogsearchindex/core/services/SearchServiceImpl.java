package no.nb.microservices.catalogsearchindex.core.services;

import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.repository.ISearchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author ronnymikalsen
 */
@Service
public class SearchServiceImpl implements ISearchService {

    private final ISearchRepository searchRepository;

    @Autowired
    public SearchServiceImpl(final ISearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public SearchAggregated search(String query, Pageable pageRequest) {
        Page<Item> page = searchRepository.search(query, pageRequest);
        return new SearchAggregated(page);
    }

}
