package no.nb.microservices.catalogsearchindex;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

public class AggregationResource extends ResourceSupport {

    private final String name;
    private List<FacetValueResource> facetValues;
    
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