package no.nb.microservices.catalogsearchindex.core.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Item {

    private String id;
    private String urn;
    private List<String> freetextMetadatas;
    private List<String> freetextHits;
    private String location;
    private String firstIndexTime;
    private String pageCount;
    private List<String> contentClasses;
    private List<String> metadataClasses;
    private boolean isDigital;
    private String title;
    private List<String> mediaTypes;
    private String thumbnailUrn;
    private JsonNode explain;
    private List<String> creators;

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

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
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

    public List<String> getMediaTypes() {
        if (mediaTypes == null) {
            return new ArrayList<>();
        } else {
            return mediaTypes;
        }
    }

    public void setMediaTypes(List<String> mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public String getThumbnailUrn() {
        return thumbnailUrn;
    }

    public void setThumbnailUrn(String thumbnailUrn) {
        this.thumbnailUrn = thumbnailUrn;
    }

    public JsonNode getExplain() {
        return explain;
    }

    public void setExplain(JsonNode explain) {
        this.explain = explain;
    }

	public List<String> getCreators() {
		return creators;
	}

	public void setCreators(List<String> creators) {
		this.creators = creators;
	}
	
	@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(title).
            append(creators).
            append(mediaTypes).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof Item))
            return false;
        if (obj == this)
            return true;

        Item rhs = (Item) obj;
        return new EqualsBuilder().
            append(title, rhs.title).
            append(creators, rhs.creators).
            append(mediaTypes, rhs.mediaTypes).
            isEquals();
    }	
}
