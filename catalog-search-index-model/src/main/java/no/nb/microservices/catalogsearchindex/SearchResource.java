package no.nb.microservices.catalogsearchindex;

import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResource extends ResourceSupport {

    @JsonProperty("page")
    private PageMetadata metadata;
    private String scrollId;

    private EmbeddedWrapper wrapper = new EmbeddedWrapper();

    @JsonCreator
    public SearchResource() {
    }

    public SearchResource(PageMetadata metadata, EmbeddedWrapper embeddedWrapper) {
        super();
        this.metadata = metadata;
        this.wrapper = embeddedWrapper;
    }

    public PageMetadata getMetadata() {
        return metadata;
    }

    @JsonProperty("_embedded")
    public EmbeddedWrapper getEmbedded() {
        return wrapper;
    }

    public void setEmbedded(EmbeddedWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }
}
