package no.nb.microservices.catalogsearchindex.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.elasticsearch.search.aggregations.Aggregation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import no.nb.microservices.catalogsearchindex.AggregationResource;

public class AggregationResourceAssemblerTest {

    @Before
    public void init() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/search?q=Junit&aggs=ddc1");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(attributes);
    }
    
    @After
    public void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
    }
    
    @Test
    public void testToResource() {
        AggregationResourceAssembler assembler = new AggregationResourceAssembler();
        Aggregation mockAggregation = mock(Aggregation.class);
        when(mockAggregation.getName()).thenReturn("ddc1");
        
        AggregationResource resource = assembler.toResource(mockAggregation);
        
        assertEquals("ddc1", resource.getName());
    }
}
