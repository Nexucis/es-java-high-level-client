package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.utils.JsonUtils;
import nexucis.elasticsearch.utils.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class DocumentNamespace extends AbstractNamespace {

    public DocumentNamespace(RestHighLevelClient client) {
        super(client);
    }

    public <T> T get(String id, Class<T> clazz) throws IOException {
        Document document = this.getDocument(clazz);
        GetRequest getRequest = new GetRequest(StringUtils.isNotEmpty(document.alias()) ? document.alias() : document.index(), document.type(), id);

        GetResponse response = client.get(getRequest);

        T entity = JsonUtils.getObjectFromString(response.getSourceAsString(), clazz);

        return entity;
    }

}
