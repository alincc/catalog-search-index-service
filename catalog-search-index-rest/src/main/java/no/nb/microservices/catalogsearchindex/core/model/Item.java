package no.nb.microservices.catalogsearchindex.core.model;

public class Item {

    private String id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
