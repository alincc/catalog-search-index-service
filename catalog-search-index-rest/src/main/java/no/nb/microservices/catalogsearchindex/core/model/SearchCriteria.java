package no.nb.microservices.catalogsearchindex.core.model;

import no.nb.microservices.catalogsearchindex.NBSearchType;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Pageable;

public class SearchCriteria {
    private String searchString;
    private String[] aggregations;
    private Pageable pageRequest;
    private NBSearchType searchType;
    private GeoSearch geoSearch;
    private boolean explain;
    private String[] filters;
    private String[] boost;
    private String[] should;
    private boolean grouping;

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

    public String[] getFilters() {
        return filters;
    }

    public void setFilters(String[] filters) {
        this.filters = filters;
    }

    public String[] getBoost() {
        if (boost == null) {
            this.boost = new String[0];
        }
        return boost;
    }

    public void setBoost(String[] boost) {
        this.boost = boost;
    }
    
    public Map<String, Float> getBoostMap() {
        Map<String, Float> boostMap = new HashMap<>();
        for(String boost : this.getBoost()) {
            String[] boostFields = boost.split(",");
            if (boostFields.length == 2 && NumberUtils.isDigits(boostFields[1])) {
                boostMap.put(boostFields[0], Float.valueOf(boostFields[1]));
            }
        }
        return boostMap;
    }

    public String[] getShould() {
        if (should == null) {
            this.should = new String[0];
        }
        return should;
    }

    public void setShould(String[] should) {
        this.should = should;
    }

	public boolean isGrouping() {
		return grouping;
	}

	public void setGrouping(boolean grouping) {
		this.grouping = grouping;
	}
}
