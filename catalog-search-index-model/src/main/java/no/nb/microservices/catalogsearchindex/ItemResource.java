package no.nb.microservices.catalogsearchindex;

import org.springframework.hateoas.ResourceSupport;

public class ItemResource extends ResourceSupport {

    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
