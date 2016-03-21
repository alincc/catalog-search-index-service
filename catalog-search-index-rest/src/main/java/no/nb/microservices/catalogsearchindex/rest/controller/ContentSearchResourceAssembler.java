package no.nb.microservices.catalogsearchindex.rest.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nb.microservices.catalogsearchindex.core.model.ContentFragment;
import no.nb.microservices.catalogsearchindex.core.model.ContentSearch;
import no.nb.microservices.catalogsearchindex.searchwithin.Fragment;
import no.nb.microservices.catalogsearchindex.searchwithin.Position;
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


public class ContentSearchResourceAssembler extends ResourceAssemblerSupport<ContentSearch, ContentSearchResource> {
    private final HateoasPageableHandlerMethodArgumentResolver pageableResolver = new HateoasPageableHandlerMethodArgumentResolver();
    
    public ContentSearchResourceAssembler() {
        super(SearchController.class, ContentSearchResource.class);
    }
    
    @Override
    public ContentSearchResource toResource(ContentSearch contentSearch) {
        ContentSearchResource resource = new ContentSearchResource();
        List<Fragment> fragments = new ArrayList<>();
        for (ContentFragment contentFragment : contentSearch.getFragments()) {
            Position position = new Position(contentFragment.getX(),contentFragment.getY(), contentFragment.getW(), contentFragment.getH());
            Fragment fragment = new Fragment(contentFragment.getText(), contentFragment.getBefore(), contentFragment.getAfter(), contentFragment.getPageid(), position);
            fragments.add(fragment);
        }
        resource.setFragments(fragments);

        resource.add(createSelfLink());

        return resource;
    }

    private Link createSelfLink() {
        UriTemplate base = new UriTemplate(ServletUriComponentsBuilder.fromCurrentRequest().build().toString());
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(base.expand());
        return new Link(new UriTemplate(builder.build().toString()), Link.REL_SELF);
    }
}
