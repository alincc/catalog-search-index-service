package no.nb.microservices.catalogsearchindex.rest.controller;

import no.nb.microservices.catalogsearchindex.SearchResource;
import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;

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

public class SearchResultResourceAssembler implements ResourceAssembler<SearchAggregated, SearchResource>{

	private ItemResourceAssembler assembler = new ItemResourceAssembler();
	private final HateoasPageableHandlerMethodArgumentResolver pageableResolver = new HateoasPageableHandlerMethodArgumentResolver();
	
	@Override
    public SearchResource toResource(SearchAggregated result) {
		PageMetadata metadata = asPageMetadata(result.getPage()); 

		
		SearchResource resources = new SearchResource(metadata);
		for(Item item : result.getPage().getContent()) {
			resources.getEmbedded().getItems().add(assembler.toResource(item));
		}
		
		return addPaginationLinks(resources, result.getPage());
    }
	
	private SearchResource addPaginationLinks(SearchResource resources, Page<?> page) {
        
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
