package no.nb.microservices.catalogsearchindex;

import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchResource extends ResourceSupport {

    @JsonProperty("page")
	private PageMetadata metadata;
    @JsonProperty("_embedded")
    private EmbeddedWrapper wrapper = new EmbeddedWrapper(); 

    @JsonCreator
    public SearchResource() {
    }
    
    public SearchResource(PageMetadata metadata) {
        super();
        this.metadata = metadata;
    }

    public PageMetadata getMetadata() {
        return metadata;
    }

    public EmbeddedWrapper getEmbedded() {
        return wrapper;
    }

    public void setEmbedded(EmbeddedWrapper wrapper) {
        this.wrapper = wrapper;
    }
}
