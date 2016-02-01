package no.nb.microservices.catalogsearchindex.core.model;

import no.nb.microservices.catalogsearchindex.NBSearchType;
import org.springframework.data.domain.Pageable;

public class SearchCriteria {

    private String searchString;
    private String[] aggregations;
    private Pageable pageRequest;
    private NBSearchType searchType;
    private GeoSearch geoSearch;
    private boolean explain;

    public SearchCriteria(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String[] getAggregations() {
        return aggregations;
    }

    public void setAggregations(String[] aggregations) {
        this.aggregations = aggregations;
    }

    public Pageable getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(Pageable pageRequest) {
        this.pageRequest = pageRequest;
    }

    public NBSearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(NBSearchType searchType) {
        this.searchType = searchType;
    }

    public GeoSearch getGeoSearch() {
        return geoSearch;
    }

    public void setGeoSearch(GeoSearch geoSearch) {
        this.geoSearch = geoSearch;
    }

    public boolean isExplain() {
        return explain;
    }

    public void setExplain(boolean explain) {
        this.explain = explain;
    }
}
