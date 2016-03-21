package no.nb.microservices.catalogsearchindex.searchwithin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "_links", "fragments" })
public class ContentSearchResource extends ResourceSupport {
    private List<Fragment> fragments;

    public ContentSearchResource() {

    }

    public ContentSearchResource(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }
}
