package nexucis.elasticsearch.client;

import nexucis.elasticsearch.client.namespace.DocumentNamespace;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.Closeable;
import java.io.IOException;

public class Client implements Closeable {

    private RestHighLevelClient restHighLevelClient;

    private DocumentNamespace document;

    public Client(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public DocumentNamespace document() {

        if (this.document == null) {
            this.document = new DocumentNamespace(this.restHighLevelClient);
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
