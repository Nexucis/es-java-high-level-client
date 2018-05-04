package nexucis.elasticsearch.client;

import nexucis.elasticsearch.client.namespace.DocumentNamespace;
import nexucis.elasticsearch.client.namespace.IndicesNamespace;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.Closeable;
import java.io.IOException;

public class Client implements Closeable {

    private RestHighLevelClient restHighLevelClient;

    private DocumentNamespace document;

    private IndicesNamespace indices;

    public Client(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public DocumentNamespace document() {

        if (this.document == null) {
            this.document = new DocumentNamespace(this.restHighLevelClient);
        }

        return this.document;
    }

    public IndicesNamespace indices() {
        if (this.indices == null) {
            this.indices = new IndicesNamespace(this.restHighLevelClient);
        }

        return this.indices;
    }

    @Override
    public void close() throws IOException {
        this.restHighLevelClient.close();
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return this.restHighLevelClient;
    }
}
