package no.nb.microservices.catalogsearchindex.rest.controller;

import java.util.Arrays;
import java.util.List;

import no.nb.microservices.catalogsearchindex.ItemResource;

public class ItemResourceBuilder {
    private List<String> mediaTypes;
    private List<String> contentClasses;
    private boolean isDigital;

    public ItemResourceBuilder withMediaTypes(String... mediaTypes) {
        this.mediaTypes = Arrays.asList(mediaTypes);
        return this;
    }

    public ItemResourceBuilder withContentClasses(String... contentClasses) {
        this.contentClasses = Arrays.asList(contentClasses);
        return this;
    }

    public ItemResourceBuilder isDigital() {
        this.isDigital = true;
        return this;
    }

    public ItemResource build() {
        ItemResource itemResource = new ItemResource();
        itemResource.setMediaTypes(mediaTypes);
        itemResource.setContentClasses(contentClasses);
        itemResource.setDigital(isDigital);
        return itemResource;
    }

}
