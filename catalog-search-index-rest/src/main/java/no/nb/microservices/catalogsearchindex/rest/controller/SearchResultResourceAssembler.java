package no.nb.microservices.catalogsearchindex.rest.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.UriTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import no.nb.microservices.catalogsearchindex.AggregationResource;
import no.nb.microservices.catalogsearchindex.SearchResource;
import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;


public class SearchResultResourceAssembler implements ResourceAssembler<SearchAggregated, SearchResource> {

    private AggregationResourceAssembler aggregationResourceAssembler = new AggregationResourceAssembler();
    private ItemResourceAssembler assembler = new ItemResourceAssembler();
    private final HateoasPageableHandlerMethodArgumentResolver pageableResolver = new HateoasPageableHandlerMethodArgumentResolver();

    @Override
    public SearchResource toResource(SearchAggregated result) {
        PageMetadata metadata = asPageMetadata(result.getPage());

        SearchResource resources = new SearchResource(metadata);
        resources.setScrollId(result.getScrollId());
        for (Item item : result.getPage().getContent()) {
            resources.getEmbedded().getItems().add(assembler.toResource(item));
        }

        resources.getEmbedded().setAggregations(getAggregationResources(result));

        return addPaginationLinks(resources, result);
    }

    private List<AggregationResource> getAggregationResources(SearchAggregated result) {
        List<AggregationResource> aggregationResources = new ArrayList<>();
        if (result.getAggregations() != null) {
            Aggregations aggregations = result.getAggregations();
            Iterator<Aggregation> iterator = aggregations.iterator();
            
            while (iterator.hasNext()) {
                aggregationResources.add(aggregationResourceAssembler.toResource(iterator.next()));
            }
            
        }
        return aggregationResources;
    }

    private SearchResource addPaginationLinks(SearchResource resources, SearchAggregated result) {

    	if (result.getScrollId() != null) {
    		resources.add(linkTo(methodOn(SearchController.class).search(result.getScrollId())).withRel(Link.REL_NEXT));
    	} else {
	    	Page<?> page = result.getPage();
	    	
	        UriTemplate base = new UriTemplate(ServletUriComponentsBuilder.fromCurrentRequest().build().toString());
	
	        if (page.hasPrevious()) {
	            resources.add(createLink(base, new PageRequest(0, page.getSize(), page.getSort()), Link.REL_FIRST));
	        }
	
	        if (page.hasPrevious()) {
	            resources.add(createLink(base, page.previousPageable(), Link.REL_PREVIOUS));
	        }
	
	        resources.add(createLink(base, null, Link.REL_SELF));
	
	        if (page.hasNext()) {
	            resources.add(createLink(base, page.nextPageable(), Link.REL_NEXT));
	        }
	
	        if (page.hasNext()) {
	
	            int lastIndex = page.getTotalPages() == 0 ? 0 : page.getTotalPages() - 1;
	
	            resources.add(createLink(base, new PageRequest(lastIndex, page.getSize(), page.getSort()), Link.REL_LAST));
	        }
    	}

        return resources;
    }

    private Link createLink(UriTemplate base, Pageable pageable, String rel) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(base.expand());
        pageableResolver.enhance(builder, null, pageable);

        return new Link(new UriTemplate(builder.build().toString()), rel);
    }

    private static <T> PageMetadata asPageMetadata(Page<T> page) {

        return new PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }
}
