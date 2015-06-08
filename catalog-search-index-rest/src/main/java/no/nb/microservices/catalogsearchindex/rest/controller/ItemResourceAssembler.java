package no.nb.microservices.catalogsearchindex.rest.controller;

import no.nb.microservices.catalogsearchindex.ItemResource;
import no.nb.microservices.catalogsearchindex.core.model.Item;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ItemResourceAssembler extends
        ResourceAssemblerSupport<Item, ItemResource> {

    public ItemResourceAssembler() {
        super(SearchController.class, ItemResource.class);
    }

    @Override
    public ItemResource toResource(Item item) {
        ItemResource resource = createResourceWithId(item.getId(), item);
        resource.setItemId(item.getId());
        return resource;
    }

}
