package no.nb.microservices.catalogsearchindex.rest.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import no.nb.microservices.catalogsearchindex.core.model.Item;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.searchwithin.ContentSearchResource;


public class ContentSearchResourceAssembler extends ResourceAssemblerSupport<SearchAggregated, ContentSearchResource> {
    private final HateoasPageableHandlerMethodArgumentResolver pageableResolver = new HateoasPageableHandlerMethodArgumentResolver();
    
    public ContentSearchResourceAssembler() {
        super(SearchController.class, ContentSearchResource.class);
    }
    
    @Override
    public ContentSearchResource toResource(SearchAggregated result) {
        ContentSearchResource resource = new ContentSearchResource(asPageMetadata(result.getPage()));

        getFreetextFragments(result)
                .forEach(resource::addFragment);

        getFreetextMetadata(result)
                .forEach(resource::addFreetextMetdatas);

        return addPaginationLinks(resource, result.getPage());
    }

    private static <T> PageMetadata asPageMetadata(Page<T> page) {
        return new PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }

    private List<String> getFreetextFragments(SearchAggregated search) {
        List<Item> items = search.getPage().getContent();
        if (items != null && items.size() == 1) {
            return items.get(0).getFreetextHits();
        } else {
            return Collections.emptyList();
        }
    }

    private List<String> getFreetextMetadata(SearchAggregated search) {
        List<Item> items = search.getPage().getContent();
        if (items != null && items.size() == 1) {
            return items.get(0).getFreetextMetadatas();
        } else {
            return Collections.emptyList();
        }
    }
    
    private ContentSearchResource addPaginationLinks(ContentSearchResource resources, Page<?> page) {

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
}
