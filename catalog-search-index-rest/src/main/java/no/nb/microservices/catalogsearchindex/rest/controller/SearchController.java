package no.nb.microservices.catalogsearchindex.rest.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

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

    @ApiOperation(value = "Search", notes = "Search in NBQL", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful response") })
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String search(@RequestParam String q) {
        return q;
    }
}
