package no.nb.microservices.catalogsearchindex.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "expressionrecords", type = "expressionrecord", shards = 5, replicas = 0)
public class Item {

    @Id
    private String id;

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
}
