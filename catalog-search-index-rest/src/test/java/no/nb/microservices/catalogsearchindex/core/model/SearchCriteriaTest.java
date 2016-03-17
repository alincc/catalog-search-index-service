package no.nb.microservices.catalogsearchindex.core.model;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;

public class SearchCriteriaTest {

    private SearchCriteria searchCriteria;
    
    @Before
    public void setUp() {
        searchCriteria = new SearchCriteria("");
    }
    
    @Test
    public void testBoostMap() {
        searchCriteria.setBoost(new String[]{"name,10", "title,4"});
        
        Map<String, Float> boostMap = searchCriteria.getBoostMap();
        
        assertThat(boostMap, allOf(IsMapContaining.hasEntry("name", 10.0f),
                IsMapContaining.hasEntry("title", 4.0f)));
    }

}
