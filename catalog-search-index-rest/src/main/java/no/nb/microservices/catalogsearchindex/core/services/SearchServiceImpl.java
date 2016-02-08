package no.nb.microservices.catalogsearchindex.core.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;
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
    	if (searchCriteria.isGrouping()) {
    		return groupedSearch(searchCriteria);
    	} else {
    		return searchRepository.search(searchCriteria);
    	}
    }

    @Override
    public SearchAggregated searchWithin(String id, String q,
            Pageable pageable) {
        return searchRepository.searchWithin(id, q, pageable);
    }

    
    private SearchAggregated groupedSearch(SearchCriteria searchCriteria) {
        Set<Integer> hashValues = new HashSet<>();
        
        Pageable pageRequest = searchCriteria.getPageRequest();
        int pageSize = pageRequest.getPageSize();
        
        PageRequest pg = new PageRequest(pageRequest.getPageNumber(), 
                calculatePageSize(pageSize), 
                searchCriteria.getPageRequest().getSort());
        searchCriteria.setPageRequest(pg);

        SearchAggregated result = searchRepository.search(searchCriteria);
        List<Item> items = new ArrayList<>(result.getPage().getContent());
        
        List<Item> content = new ArrayList<>();
        
        do {

        	for (Item item : items) {
        		if (!hashValues.contains(item.hashCode())) {
        			content.add(item);
        			hashValues.add(item.hashCode());
        		}
        		
        		if (content.size() == pageSize) {
        			break;
        		}
        	}
        	if(result.getPage().hasNext()) {
        		searchCriteria.setPageRequest(result.getPage().nextPageable());
        		result = searchRepository.search(searchCriteria);
        		items = new ArrayList<>(result.getPage().getContent());
        	}
        } while(content.size() != pageSize && result.getPage().hasNext());

        Page<Item> page = new PageImpl<>(content, pageRequest, result.getPage().getTotalElements());
        SearchAggregated groupedSearchAggregated = new SearchAggregated(page, result.getAggregations());
        groupedSearchAggregated.setScrollId(UUID.randomUUID().toString());
        return groupedSearchAggregated;
    }

    private int calculatePageSize(int pageSize) {
        int extendedPageSize = (int) Math.ceil(pageSize*1.5);
        if (extendedPageSize-pageSize > 50) {
        	extendedPageSize = 50;
        } else if (extendedPageSize-pageSize < 10) {
        	extendedPageSize = 10;
        }
        return extendedPageSize;
    }

}
