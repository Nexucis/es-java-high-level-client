package nexucis.elasticsearch.client.namespace;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class IndicesNamespace extends AbstractNamespace {

    public IndicesNamespace(RestHighLevelClient client) {
        super(client);
    }

    public void deleteAll() throws IOException {
        this.client.indices().delete(new DeleteIndexRequest("_all"));
    }
}
