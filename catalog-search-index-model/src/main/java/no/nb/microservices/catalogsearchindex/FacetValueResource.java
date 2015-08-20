package no.nb.microservices.catalogsearchindex;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FacetValueResource {
    private final String key;
    private final long count;

    @JsonCreator
    public FacetValueResource(@JsonProperty("key") String key, @JsonProperty("count") long count) {
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public long getCount() {
        return count;
    }
}
