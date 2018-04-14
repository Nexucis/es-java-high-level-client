package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.data.annotation.Id;
import nexucis.elasticsearch.data.exception.IllegalEntityException;
import nexucis.elasticsearch.utils.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AbstractNamespace {

    private static final Map<Class<?>, Map<Class<? extends Annotation>, Object>> ANNOTATION_CACHE = new HashMap<>();

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
            throw new IllegalEntityException("the given entity doesn't have the Document annotation");
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

    protected String getIndex(Document document) {
        return StringUtils.isNotEmpty(document.alias()) ? document.alias() : document.index();
    }

    protected <T> void setId(String value, Class<T> clazz) {
        Field fieldId = this.getFieldID(clazz);
        try {
            fieldId.set(clazz, value);
        } catch (IllegalAccessException e) {
            throw new IllegalEntityException("the given entity doesn't have an accessor to set the Id, please provide one", e);
        }
    }

    protected <T> String getId(Class<T> clazz) {
        Field fieldId = this.getFieldID(clazz);
        try {
            return (String) fieldId.get(clazz);
        } catch (IllegalAccessException e) {
            throw new IllegalEntityException("the given entity doesn't have an accessor to get the Id, please provide one", e);
        }
    }

    private <T> Field getFieldID(Class<T> clazz) {
        if (AbstractNamespace.ANNOTATION_CACHE.containsKey(clazz)
                && AbstractNamespace.ANNOTATION_CACHE.get(clazz).containsKey(Id.class)) {
            return (Field) AbstractNamespace.ANNOTATION_CACHE.get(clazz).get(Id.class);
        }

        Field[] fields = clazz.getFields();
        int i = 0;
        Field fieldId = null;

        while (i < fields.length && fieldId == null) {
            Field field = fields[i];
            if (field.getAnnotation(Id.class) != null) {
                fieldId = field;
            }
        }

        if (fieldId == null) {
            throw new IllegalArgumentException("the given entity doesn't have a field that carry the ID annotation");
        }

        AbstractNamespace.ANNOTATION_CACHE.get(clazz).put(Id.class, fieldId);

        return fieldId;
    }
}
