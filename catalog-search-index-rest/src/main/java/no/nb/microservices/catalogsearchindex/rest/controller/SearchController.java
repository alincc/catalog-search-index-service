package no.nb.microservices.catalogsearchindex.rest.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import no.nb.htrace.annotation.Traceable;
import no.nb.microservices.catalogsearchindex.NBSearchType;
import no.nb.microservices.catalogsearchindex.SearchResource;
import no.nb.microservices.catalogsearchindex.core.model.GeoSearch;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.model.SearchCriteria;
import no.nb.microservices.catalogsearchindex.core.services.ISearchService;
import no.nb.microservices.catalogsearchindex.searchwithin.ContentSearchResource;

import org.apache.commons.lang3.NotImplementedException;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog/v1")
@Api(value = "/catalog/v1", description = "Search index API")
public class SearchController {

    private final ISearchService searchService;

    @Autowired
    public SearchController(final ISearchService searchService) {
        this.searchService = searchService;
    }
    
    @InitBinder
    public void sortBinderInit(WebDataBinder binder) {
        binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(null));
    }

    @ApiOperation(value = "Search", notes = "Search in NBQL", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful response") })
    @Traceable(description="search")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<SearchResource> search(
            @RequestParam(value = "q") String searchString,
            @RequestParam(value = "aggs", required = false) String[] aggregations,
            @RequestParam(value = "boost", required = false) String[] boost,
            @RequestParam(value = "searchType", required = false, defaultValue = "FULL_TEXT_SEARCH") NBSearchType searchType,
            @RequestParam(value = "topRight", required = false) double[] topRight,
            @RequestParam(value = "bottomLeft", required = false) double[] bottomLeft,
            @RequestParam(value = "precision", required = false, defaultValue = "5") int precision,
            @RequestParam(value = "explain", required = false) boolean explain,
            @RequestParam(value = "filter", required = false) String[] filters,
            @RequestParam(value = "should", required = false) String[] should,
            @RequestParam(value = "grouping", required = false) boolean grouping,
            @PageableDefault Pageable pageRequest) {

        SearchCriteria searchCriteria = new SearchCriteria(searchString);
        searchCriteria.setAggregations(aggregations);
        searchCriteria.setSearchType(searchType);
        searchCriteria.setExplain(explain);
        searchCriteria.setFilters(filters);
        searchCriteria.setBoost(boost);
        searchCriteria.setGrouping(grouping);
        searchCriteria.setShould(should);
        searchCriteria.setPrecision(precision);
        searchCriteria.setTopRight(topRight);
        searchCriteria.setBottomLeft(bottomLeft);
        searchCriteria.setPageRequest(pageRequest);

        SearchAggregated result = searchService.search(searchCriteria);
        SearchResource resource = new SearchResultResourceAssembler()
                .toResource(result);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @RequestMapping(value = "/search/scroll", method = RequestMethod.GET)
    public ResponseEntity<SearchResource> search(
    		@RequestParam(value = "scrollId") String scrollId) {
    	throw new NotImplementedException("Scroll not implemented");
    }

    @Traceable(description="/contentsearch")
    @RequestMapping(value = "/{id}/search", method = RequestMethod.GET)
    public ResponseEntity<ContentSearchResource> contentSearch(@PathVariable(value = "id") String id, 
            @RequestParam(value = "q") String q,
            @PageableDefault Pageable pageRequest) {
        SearchAggregated result = searchService.contentSearch(id, q, pageRequest);
        ContentSearchResource resource = new ContentSearchResourceAssembler()
                .toResource(result);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
