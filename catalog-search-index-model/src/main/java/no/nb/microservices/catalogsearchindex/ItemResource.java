package no.nb.microservices.catalogsearchindex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemResource extends ResourceSupport {

    private String itemId;
    private Location location;
    private String firstIndexTime;
    private Integer pageCount;
    private List<String> contentClasses;
    private List<String> metadataClasses;
    private boolean isDigital;
    private String title;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getFirstIndexTime() {
        return firstIndexTime;
    }

    public void setFirstIndexTime(String firstIndexTime) {
        this.firstIndexTime = firstIndexTime;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public List<String> getContentClasses() {
        if (contentClasses == null) {
            return new ArrayList<>();
        } else {
            return contentClasses;
        }
    }

    public void setContentClasses(List<String> contentClasses) {
        this.contentClasses = contentClasses;
    }

    public List<String> getMetadataClasses() {
        if (metadataClasses == null) {
            return new ArrayList<>();
        } else {
            return metadataClasses;
        }
    }

    public void setMetadataClasses(List<String> metadataClasses) {
        this.metadataClasses = metadataClasses;
    }

    public boolean isDigital() {
        return isDigital;
    }

    public void setDigital(boolean isDigital) {
        this.isDigital = isDigital;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
