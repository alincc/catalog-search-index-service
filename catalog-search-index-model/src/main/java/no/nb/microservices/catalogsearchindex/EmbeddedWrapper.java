package no.nb.microservices.catalogsearchindex;

import java.util.ArrayList;
import java.util.List;

public class EmbeddedWrapper {
    private List<ItemResource> items = new ArrayList<>();
    private List<AggregationResource> aggregations = new ArrayList<>();

    public List<ItemResource> getItems() {
        return items;
    }

    public void setItems(List<ItemResource> items) {
        this.items = items;
    }

    public List<AggregationResource> getAggregations() {
        return aggregations;
    }

    public void setAggregations(List<AggregationResource> aggregations) {
        this.aggregations = aggregations;
    }
}
