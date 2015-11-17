package no.nb.microservices.catalogsearchindex.core.model;

import org.springframework.data.domain.Pageable;

public class SearchCriteria {
    private String searchString;
    private String[] aggregations;
    private Pageable pageRequest;
    private boolean searchInMetadata;
    private boolean searchInFreeText;
    private GeoSearch geoSearch;

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

    public boolean isSearchInMetadata() {
        return searchInMetadata;
    }

    public void setSearchInMetadata(boolean searchInMetadata) {
        this.searchInMetadata = searchInMetadata;
    }

    public boolean isSearchInFreeText() {
        return searchInFreeText;
    }

    public void setSearchInFreeText(boolean searchInFreeText) {
        this.searchInFreeText = searchInFreeText;
    }

    public GeoSearch getGeoSearch() {
        return geoSearch;
    }

    public void setGeoSearch(GeoSearch geoSearch) {
        this.geoSearch = geoSearch;
    }
}
