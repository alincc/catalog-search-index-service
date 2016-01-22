package no.nb.microservices.catalogsearchindex.rest.controller;

import no.nb.microservices.catalogsearchindex.AggregationResource;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AggregationResourceAssemblerTest {

    private AggregationResourceAssembler assembler;

    @Before
    public void init() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/catalog/v1/search?q=Junit&aggs=ddc1");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(attributes);
        assembler = new AggregationResourceAssembler();
    }
    
    @After
    public void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
    }
    
    @Test
    public void testToResource() {
        Terms mockAggregation = createMockedTermsAggregation();

        AggregationResource resource = assembler.toResource(mockAggregation);
        
        assertEquals("ddc1", resource.getName());
        assertThat(resource.getFacetValues(), hasSize(1));
    }

    private Terms createMockedTermsAggregation() {
        List<Terms.Bucket> buckets = new ArrayList<>();
        Terms.Bucket mockBucket = mock(Terms.Bucket.class);
        when(mockBucket.getKey()).thenReturn("bucket");
        when(mockBucket.getDocCount()).thenReturn(1L);
        buckets.add(mockBucket);

        Terms mockAggregation = mock(Terms.class);
        when(mockAggregation.getName()).thenReturn("ddc1");
        when(mockAggregation.getBuckets()).thenReturn(buckets);
        return mockAggregation;
    }

    @Test
    public void testToResourceWithGeoHashGridAggregation() {
        GeoHashGrid mockAggregation = createMockedGeoHashGridAggregation();

        AggregationResource resource = assembler.toResource(mockAggregation);

        assertEquals("locations", resource.getName());
        assertThat(resource.getFacetValues(), hasSize(1));
    }

    private GeoHashGrid createMockedGeoHashGridAggregation() {
        List<GeoHashGrid.Bucket> buckets = new ArrayList<>();
        GeoHashGrid.Bucket mockBucket = mock(GeoHashGrid.Bucket.class);
        when(mockBucket.getKeyAsGeoPoint()).thenReturn(new GeoPoint());
        when(mockBucket.getDocCount()).thenReturn(1L);
        buckets.add(mockBucket);

        GeoHashGrid mockAggregation = mock(GeoHashGrid.class);
        when(mockAggregation.getName()).thenReturn("locations");
        when(mockAggregation.getBuckets()).thenReturn(buckets);
        return mockAggregation;
    }
}
