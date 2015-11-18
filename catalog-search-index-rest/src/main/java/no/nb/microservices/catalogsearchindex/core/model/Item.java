package no.nb.microservices.catalogsearchindex.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Item {

    private String id;
    private List<String> freetextMetadatas;
    private List<String> freetextHits;
    private String location;

    public Item() {
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
}
