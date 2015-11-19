package no.nb.microservices.catalogsearchindex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.ResourceSupport;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemResource extends ResourceSupport {

    private String itemId;
    private Location location;
    private String firstIndexTime;
    private int pageCount;

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

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
