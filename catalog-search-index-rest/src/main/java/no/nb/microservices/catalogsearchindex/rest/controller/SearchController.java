package no.nb.microservices.catalogsearchindex.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import no.nb.htrace.annotation.Traceable;
import no.nb.microservices.catalogsearchindex.SearchResource;
import no.nb.microservices.catalogsearchindex.core.model.GeoSearch;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;
import no.nb.microservices.catalogsearchindex.core.services.ISearchService;
import no.nb.microservices.catalogsearchindex.searchwithin.SearchWithinResource;

import org.elasticsearch.common.geo.GeoPoint;

@RestController
@Api(value = "/", description = "Search index API")
public class SearchController {

    private final ISearchService searchService;

    @Autowired
    public SearchController(final ISearchService searchService) {
        this.searchService = searchService;
    }

    @ApiOperation(value = "Search", notes = "Search in NBQL", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful response") })
    @Traceable(description="search")
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public ResponseEntity<SearchResource> search(
            @RequestParam(value = "q") String searchString,
            @RequestParam(value = "aggs", required = false) String[] aggregations,
            @PageableDefault Pageable pageRequest,
            @RequestParam(value = "ft", required = false, defaultValue = "true") boolean searchInFreeText,
            @RequestParam(value = "md", required = false, defaultValue = "true") boolean searchInMetadata,
            @RequestParam(value = "topRight", required = false) double[] topRight,
            @RequestParam(value = "bottomLeft", required = false) double[] bottomLeft,
            @RequestParam(value = "precision", required = false, defaultValue = "5") int precision) {

        SearchCriteria searchCriteria = new SearchCriteria(searchString);
        searchCriteria.setAggregations(aggregations);
        searchCriteria.setPageRequest(pageRequest);
        searchCriteria.setSearchInFreeText(searchInFreeText);
        searchCriteria.setSearchInMetadata(searchInMetadata);

        if(topRight != null && bottomLeft != null) {
            GeoSearch geoSearch = new GeoSearch();
            if(topRight.length == 2 && bottomLeft.length == 2) {
                geoSearch.setTopRight(new GeoPoint(topRight[0], topRight[1]));
                geoSearch.setBottomLeft(new GeoPoint(bottomLeft[0], bottomLeft[1]));
            }
            geoSearch.setPrecision(precision);
            searchCriteria.setGeoSearch(geoSearch);
        }

        SearchAggregated result = searchService.search(searchCriteria);

        SearchResource resource = new SearchResultResourceAssembler()
                .toResource(result);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Traceable(description="searchWithin")
    @RequestMapping(value = "/{id}/search", method = RequestMethod.GET)
    public ResponseEntity<SearchWithinResource> searchWithin(@PathVariable(value = "id") String id, 
            @RequestParam(value = "q") String q,
            @PageableDefault Pageable pageRequest) {
        SearchAggregated result = searchService.searchWithin(id, q, pageRequest);
        SearchWithinResource resource = new SearchWithinResourceAssembler()
                .toResource(result);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
