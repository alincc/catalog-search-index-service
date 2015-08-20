package no.nb.microservices.catalogsearchindex.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "expressionrecords", type = "expressionrecord", shards = 5, replicas = 0)
public class Item {

    @Id
    private String id;
    private String ddc;
    private String ddc1;
    private String ddc2;
    private String ddc3;
    private String mediatype;

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

    public String getDdc() {
        return ddc;
    }

    public void setDdc(String ddc) {
        this.ddc = ddc;
    }

    public String getDdc1() {
        return ddc1;
    }

    public void setDdc1(String ddc1) {
        this.ddc1 = ddc1;
    }

    public String getDdc2() {
        return ddc2;
    }

    public void setDdc2(String ddc2) {
        this.ddc2 = ddc2;
    }

    public String getDdc3() {
        return ddc3;
    }

    public void setDdc3(String ddc3) {
        this.ddc3 = ddc3;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }
}
