package no.nb.microservices.catalogsearchindex.core.model;

import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.data.domain.Page;

public class SearchAggregated {

    private Page<Item> page;
    private Aggregations aggregations;
    private String scrollId;

    public SearchAggregated(Page<Item> page) {
        this.page = page;
    }

    public SearchAggregated(Page<Item> page, Aggregations aggregations) {
        this.page = page;
        this.aggregations = aggregations;
    }

    public Page<Item> getPage() {
        return page;
    }

    public Aggregations getAggregations() {
        return aggregations;
    }

    public void setAggregations(Aggregations aggregations) {
        this.aggregations = aggregations;
    }

	public String getScrollId() {
		return scrollId;
	}

	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}
}