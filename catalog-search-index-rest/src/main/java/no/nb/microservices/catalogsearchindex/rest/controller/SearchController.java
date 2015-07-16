package no.nb.microservices.catalogsearchindex.rest.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import no.nb.htrace.annotation.Traceable;
import no.nb.microservices.catalogsearchindex.SearchResource;
import no.nb.microservices.catalogsearchindex.core.model.SearchAggregated;
import no.nb.microservices.catalogsearchindex.core.services.ISearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ronnymikalsen
 */
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
            @RequestParam(value = "q") String query,
            @PageableDefault Pageable pageRequest) {
        SearchAggregated result = searchService.search(query, pageRequest);
        SearchResource resource = new SearchResultResourceAssembler()
                .toResource(result);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
