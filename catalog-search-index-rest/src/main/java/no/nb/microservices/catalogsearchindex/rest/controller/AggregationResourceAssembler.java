package no.nb.microservices.catalogsearchindex.rest.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import no.nb.microservices.catalogsearchindex.AggregationResource;
import no.nb.microservices.catalogsearchindex.FacetValueResource;

public class AggregationResourceAssembler extends ResourceAssemblerSupport<Aggregation, AggregationResource> {

    public AggregationResourceAssembler() {
        super(SearchController.class, AggregationResource.class);
    }

    @Override
    public AggregationResource toResource(Aggregation aggregation) {
        AggregationResource aggregationResource = new AggregationResource(aggregation.getName());
        
        if(aggregation instanceof Terms) {
            Collection<Bucket> buckets = ((Terms) aggregation).getBuckets();
            List<FacetValueResource> facetValues = new ArrayList<>();
            for (Terms.Bucket entry : buckets) {
                facetValues.add(new FacetValueResource(entry.getKey(), entry.getDocCount()));
            }

            aggregationResource.setFacetValues(facetValues);
        } else if (aggregation instanceof GeoHashGrid) {
            Collection<GeoHashGrid.Bucket> buckets = ((GeoHashGrid) aggregation).getBuckets();
            List<FacetValueResource> facetValues = new ArrayList<>();
            for (GeoHashGrid.Bucket entry : buckets) {
                facetValues.add(new FacetValueResource(entry.getKeyAsGeoPoint().toString(), entry.getDocCount()));
            }

            aggregationResource.setFacetValues(facetValues);
        }
        return aggregationResource;
    }
}