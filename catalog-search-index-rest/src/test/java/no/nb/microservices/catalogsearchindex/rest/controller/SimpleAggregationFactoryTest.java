package no.nb.microservices.catalogsearchindex.rest.controller;

import no.nb.microservices.catalogsearchindex.SimpleAggregation;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleAggregationFactoryTest {

    @Test
    public void whenTermAggregationThenReturnSimpleAggregation() throws Exception {
        Aggregation aggregation = createMockedTermsAggregation();
        SimpleAggregationFactory aggregationFactory = new SimpleAggregationFactory();
        SimpleAggregation simpleAggregation = aggregationFactory.create(aggregation);
        assertThat(simpleAggregation.getName(), is("mediatype"));
    }

    @Test
    public void whenGeoAggregationThenReturnSimpleAggregation() throws Exception {
        Aggregation aggregation = createMockedGeoHashGridAggregation();
        SimpleAggregationFactory aggregationFactory = new SimpleAggregationFactory();
        SimpleAggregation simpleAggregation = aggregationFactory.create(aggregation);
        assertThat(simpleAggregation.getName(), is("locations"));
    }

    private Terms createMockedTermsAggregation() {
        List<Terms.Bucket> buckets = new ArrayList<>();
        Terms.Bucket mockBucket = mock(Terms.Bucket.class);
        when(mockBucket.getKey()).thenReturn("aviser");
        when(mockBucket.getDocCount()).thenReturn(1L);
        buckets.add(mockBucket);

        Terms mockAggregation = mock(Terms.class);
        when(mockAggregation.getName()).thenReturn("mediatype");
        when(mockAggregation.getBuckets()).thenReturn(buckets);
        return mockAggregation;
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
