package no.nb.microservices.catalogsearchindex.searchwithin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "_links", "freetextMetadatas", "fragments" })
public class ContentSearchResource extends ResourceSupport {
    @JsonProperty("page")
    private PageMetadata metadata;
    private List<String> freetextMetadatas;
    private List<String> fragments;

    public ContentSearchResource() {
        super();
    }

    public ContentSearchResource(PageMetadata metadata) {
        super();
        this.metadata = metadata;
    }

    public PageMetadata getMetadata() {
        return metadata;
    }
    public List<String> getFreetextMetadatas() {
        if (freetextMetadatas == null) {
            return Collections.emptyList();
        } else {
            return freetextMetadatas;
        }
    }

    public void setFreetextMetadatas(List<String> freetextMetadatas) {
        this.freetextMetadatas = freetextMetadatas;
    }

    public void addFreetextMetdatas(String metadata) {
        if (freetextMetadatas == null) {
            freetextMetadatas = new ArrayList<>();
        }
        freetextMetadatas.add(metadata);
    }
    
    public List<String> getFragments() {
        if (fragments == null) {
            return Collections.emptyList();
        } else {
            return fragments;
        }
    }

    public void setFragments(List<String> fragments) {
        this.fragments = fragments;
    }
    
    public void addFragment(String fragment) {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        fragments.add(fragment);
    }
    

}
