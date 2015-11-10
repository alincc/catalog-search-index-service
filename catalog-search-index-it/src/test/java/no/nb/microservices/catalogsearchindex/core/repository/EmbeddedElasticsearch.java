package no.nb.microservices.catalogsearchindex.core.repository;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created by alfredw on 9/14/15.
 */
public class EmbeddedElasticsearch {

    private static final String ES_DATA_DIRECTORY = "target/es-data";
    private static final String SCHEMA_NAME = "expressionrecords";
    private static final String TYPE_NAME = "expressionrecord";
    private static EmbeddedElasticsearch embeddedElasticsearch;
    private Node node;
    private Client client;

    public static EmbeddedElasticsearch getInstance() throws IOException {
        if (embeddedElasticsearch == null) {
            synchronized (EmbeddedElasticsearch.class) {
                embeddedElasticsearch = new EmbeddedElasticsearch();
            }
        }
        return embeddedElasticsearch;
    }

    private EmbeddedElasticsearch() throws IOException {
        initializeNode();
        initializeIndex();
        initializeSettings();
        initializeMappings();
        initializeData();
    }

    private void initializeNode() {
        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", false)
                .put("path.data", ES_DATA_DIRECTORY);

        node = nodeBuilder()
                .settings(settings)
                .local(true)
                .node();

        client = node.client();
    }

    private void initializeIndex() {
        Settings indexSettings = ImmutableSettings.settingsBuilder()
                .put("number_of_shards", 1)
                .put("number_of_replicas", 0)
                .build();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(SCHEMA_NAME, indexSettings);
        client.admin().indices().create(createIndexRequest).actionGet();
        client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
    }

    private void initializeSettings() throws IOException {
        String file = getClass().getClassLoader().getResource("no/nb/microservices/catalogsearchindex/settings.json").getFile();
        if(file != null) {
            client.admin().indices().prepareClose(SCHEMA_NAME).execute().actionGet();
            String settings = new String(Files.readAllBytes(Paths.get(file)));
            client.admin().indices().prepareUpdateSettings().setSettings(settings).execute().actionGet();
            client.admin().indices().prepareOpen(SCHEMA_NAME).execute().actionGet();
        }
    }

    private void initializeMappings() throws IOException {
        String file = getClass().getClassLoader().getResource("no/nb/microservices/catalogsearchindex/mapping.json").getFile();
        if(file != null) {
            String mapping = new String(Files.readAllBytes(Paths.get(file)));
            client.admin().indices()
                    .preparePutMapping(SCHEMA_NAME).setSource(mapping)
                    .setType(TYPE_NAME).execute().actionGet();
        }
    }

    private void initializeData() throws IOException {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(client.prepareIndex(SCHEMA_NAME, TYPE_NAME, "0b8501b8e2b822c8ec13558de82aaef9").setSource(getDataSource("0b8501b8e2b822c8ec13558de82aaef9")));
        bulkRequest.add(client.prepareIndex(SCHEMA_NAME, TYPE_NAME, "41a7fb4e94aab9a88be23745a1504a92").setSource(getDataSource("41a7fb4e94aab9a88be23745a1504a92")));
        bulkRequest.add(client.prepareIndex(SCHEMA_NAME, TYPE_NAME, "92eb4d381bf7004de77337800654f610").setSource(getDataSource("92eb4d381bf7004de77337800654f610")));
        bulkRequest.execute().actionGet();

        RefreshRequest refreshRequest = new RefreshRequest().indices(SCHEMA_NAME);
        client.admin().indices().refresh(refreshRequest).actionGet();
    }

    private String getDataSource(String sesamid) {
        try {
            String file = getClass().getClassLoader().getResource("no/nb/microservices/catalogsearchindex/esdata/" + sesamid + ".json").getFile();
            if(file != null) {
                return new String(Files.readAllBytes(Paths.get(file)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public Client getClient() {
        return node.client();
    }

    public void shutdown() throws IOException {
        client.close();
        node.close();
        FileUtils.deleteDirectory(new File(ES_DATA_DIRECTORY));
        embeddedElasticsearch = null;
    }
}
