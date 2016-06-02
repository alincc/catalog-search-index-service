package no.nb.microservices.catalogsearchindex.rest.controller;

import no.nb.microservices.catalogsearchindex.BucketValue;
import no.nb.microservices.catalogsearchindex.SimpleAggregation;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleAggregationFactory {

    public SimpleAggregation create(Aggregation aggregation) {
        if(aggregation instanceof Terms) {
            return new SimpleAggregation(aggregation.getName(), getTermsBuckets((Terms)aggregation));
        } else if (aggregation instanceof GeoHashGrid) {
            return new SimpleAggregation(aggregation.getName(), getGeoHashGridBuckets((GeoHashGrid)aggregation));
        }
        return new SimpleAggregation(aggregation.getName());
    }

    private List<BucketValue> getTermsBuckets(Terms aggregation) {
        return aggregation.getBuckets().stream()
                .map(entry -> new BucketValue(entry.getKey(), entry.getDocCount()))
                .collect(Collectors.toList());
    }

    private List<BucketValue> getGeoHashGridBuckets(GeoHashGrid aggregation) {
        return aggregation.getBuckets()
                .stream()
                .map(entry -> new BucketValue(entry.getKeyAsGeoPoint().toString(), entry.getDocCount()))
                .collect(Collectors.toList());
    }
}
