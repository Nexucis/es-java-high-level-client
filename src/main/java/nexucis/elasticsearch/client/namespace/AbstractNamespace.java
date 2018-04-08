package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.data.exception.IllegalEntityException;
import nexucis.elasticsearch.utils.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;

public class AbstractNamespace {

    protected RestHighLevelClient client;

    public AbstractNamespace(RestHighLevelClient client) {
        this.client = client;
    }

    protected <T> Document getDocument(Class<T> clazz) {
        Document document = clazz.getAnnotation(Document.class);
        if (document == null) {
            throw new IllegalEntityException("the given entity doesn't have the Document exception");
        }

        if (StringUtils.isEmpty(document.alias()) || StringUtils.isEmpty(document.index())) {
            throw new IllegalArgumentException("the given entity doesn't fill the alias or index field");
        }

        if (StringUtils.isEmpty(document.type())) {
            throw new IllegalArgumentException("the given entity doesn't fill the type field");
        }

        return document;
    }
}
