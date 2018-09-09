package nexucis.elasticsearch.client;

import nexucis.elasticsearch.client.namespace.DocumentNamespace;
import nexucis.elasticsearch.data.annotation.AnnotationManager;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.Closeable;
import java.io.IOException;

public class Client implements Closeable {

    private RestHighLevelClient restHighLevelClient;

    private DocumentNamespace document;

    private AnnotationManager annotationManager;

    public Client(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
        this.annotationManager = new AnnotationManager();
    }

    public DocumentNamespace document() {

        if (this.document == null) {
            this.document = new DocumentNamespace(this.restHighLevelClient, this.annotationManager);
        }

        return this.document;
    }

    @Override
    public void close() throws IOException {
        this.restHighLevelClient.close();
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return this.restHighLevelClient;
    }
}
