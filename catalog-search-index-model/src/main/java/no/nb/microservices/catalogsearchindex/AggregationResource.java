package no.nb.microservices.catalogsearchindex;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;

public class AggregationResource extends ResourceSupport {

    private String name;
    private List<FacetValueResource> facetValues;
    
    @JsonCreator
    public AggregationResource() {
    }
    
    public AggregationResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<FacetValueResource> getFacetValues() {
        return facetValues;
    }

    public void setFacetValues(List<FacetValueResource> facetValues) {
        this.facetValues = facetValues;
    }
}