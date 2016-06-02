package no.nb.microservices.catalogsearchindex;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class SimpleAggregation {

    private String name;
    private List<BucketValue> buckets;
    
    @JsonCreator
    public SimpleAggregation() {
    }
    
    public SimpleAggregation(String name) {
        this.name = name;
    }

    public SimpleAggregation(String name, List<BucketValue> buckets) {
        this.name = name;
        this.buckets = buckets;
    }

    public String getName() {
        return name;
    }

    public List<BucketValue> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<BucketValue> buckets) {
        this.buckets = buckets;
    }
}