package no.nb.microservices.catalogsearchindex.rest.controller;

import no.nb.microservices.catalogsearchindex.ItemResource;
import no.nb.microservices.catalogsearchindex.Location;
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
        if (item.getLocation() != null) {
            String[] loc = item.getLocation().split(",");
            resource.setLocation(new Location(new Double(loc[0]),new Double(loc[1])));
        }
        resource.setItemId(item.getId());
        return resource;
    }

}
