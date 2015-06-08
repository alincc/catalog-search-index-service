package no.nb.microservices.catalogsearchindex.core.model;

import org.springframework.data.domain.Page;

public class SearchAggregated {

    private Page<Item> page;

    public SearchAggregated(Page<Item> page) {
        this.page = page;
    }

    public Page<Item> getPage() {
        return page;
    }
}
