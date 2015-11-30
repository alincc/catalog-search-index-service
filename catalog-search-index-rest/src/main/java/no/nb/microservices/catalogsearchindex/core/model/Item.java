package no.nb.microservices.catalogsearchindex.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Item {

    private String id;
    private List<String> freetextMetadatas;
    private List<String> freetextHits;
    private String location;
    private String firstIndexTime;
    private String pageCount;
    private List<String> contentClasses;
    private List<String> metadataClasses;
    private boolean isDigital;
    private String title;

    public Item() {
        super();
    }

    public Item(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getFreetextMetadatas() {
        if (freetextMetadatas == null) {
            return Collections.emptyList();
        } else {
            return freetextMetadatas;
        }
    }

    public void addFreetextMetadata(String freetextMetadata) {
        if (this.freetextMetadatas == null) {
            this.freetextMetadatas = new ArrayList<>();
        }
        this.freetextMetadatas.add(freetextMetadata);
    }

    public List<String> getFreetextHits() {
        if (freetextHits == null) {
            return Collections.emptyList();
        } else {
            return freetextHits;
        }
    }

    public void addFreetextFragment(String freetextHit) {
        if (this.freetextHits == null) {
            this.freetextHits = new ArrayList<>();
        }
        this.freetextHits.add(freetextHit);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFirstIndexTime() {
        return firstIndexTime;
    }

    public void setFirstIndexTime(String firstIndexTime) {
        this.firstIndexTime = firstIndexTime;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
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
