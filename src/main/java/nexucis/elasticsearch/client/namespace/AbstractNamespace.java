package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.data.exception.IllegalEntityException;
import nexucis.elasticsearch.utils.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class AbstractNamespace {

    private static final Map<Class<?>, Map<Class<? extends Annotation>, Annotation>> ANNOTATION_CACHE = new HashMap<>();

    protected RestHighLevelClient client;

    public AbstractNamespace(RestHighLevelClient client) {
        this.client = client;
    }

    protected <T> Document getDocument(Class<T> clazz) {

        if (AbstractNamespace.ANNOTATION_CACHE.containsKey(clazz)
                && AbstractNamespace.ANNOTATION_CACHE.get(clazz).containsKey(Document.class)) {
            return (Document) AbstractNamespace.ANNOTATION_CACHE.get(clazz).get(Document.class);
        }

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

        if (!AbstractNamespace.ANNOTATION_CACHE.containsKey(clazz)) {
            AbstractNamespace.ANNOTATION_CACHE.put(clazz, new HashMap<>());
        }

        AbstractNamespace.ANNOTATION_CACHE.get(clazz).put(Document.class, document);

        return document;
    }
}
