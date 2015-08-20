package no.nb.microservices.catalogsearchindex.core.model;

import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.data.domain.Page;

public class SearchAggregated {

    private Page<Item> page;
    private Aggregations aggregations;

    public SearchAggregated(Page<Item> page) {
        this.page = page;
    }
    
    public Page<Item> getPage() {
        return page;
    }

    public Aggregations getAggregations() {
        return aggregations;
    }

    public void setAggregations(Aggregations aggregations) {
        this.aggregations = aggregations;
    }
}