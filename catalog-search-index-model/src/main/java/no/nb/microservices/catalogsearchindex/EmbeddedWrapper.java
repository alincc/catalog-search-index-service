package no.nb.microservices.catalogsearchindex;

import java.util.ArrayList;
import java.util.List;

public class EmbeddedWrapper {
    private List<ItemResource> items = new ArrayList<>();
    private List<SimpleAggregation> aggregations = new ArrayList<>();

    public EmbeddedWrapper() {
    }

    public EmbeddedWrapper(List<ItemResource> items, List<SimpleAggregation> aggregations) {
        this.items = items;
        this.aggregations = aggregations;
    }

    public List<ItemResource> getItems() {
        return items;
    }

    public void setItems(List<ItemResource> items) {
        this.items = items;
    }

    public List<SimpleAggregation> getAggregations() {
        return aggregations;
    }

    public void setAggregations(List<SimpleAggregation> aggregations) {
        this.aggregations = aggregations;
    }
}
