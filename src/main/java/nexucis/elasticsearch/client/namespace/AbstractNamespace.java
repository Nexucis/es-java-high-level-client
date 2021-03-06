package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.data.annotation.Id;
import nexucis.elasticsearch.data.exception.IllegalEntityException;
import nexucis.elasticsearch.utils.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

        if (StringUtils.isEmpty(document.alias()) && StringUtils.isEmpty(document.index())) {
            throw new IllegalEntityException("the given entity doesn't fill the alias or index field");
        }

        if (StringUtils.isEmpty(document.type())) {
            throw new IllegalEntityException("the given entity doesn't fill the type field");
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

    protected <T> void setId(String value, T entity) {
        Field fieldId = this.getFieldID(entity.getClass());

        if (fieldId.isAccessible()) {
            try {
                fieldId.set(entity.getClass(), value);
                return;
            } catch (IllegalAccessException e) {
                throw new IllegalEntityException("For strange reason the field is not accessible, and it should be", e);
            }
        }

        try {
            this.getFieldMutator(fieldId, entity.getClass()).invoke(entity, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalEntityException("the given entity doesn't have a mutator to set the Id, please provide one", e);
        }
    }

    protected <T> String getId(T entity) {
        Field fieldId = this.getFieldID(entity.getClass());

        if (fieldId.isAccessible()) {
            try {
                return (String) fieldId.get(entity.getClass());
            } catch (IllegalAccessException e) {
                throw new IllegalEntityException("For strange reason the field is not accessible, and it should be", e);
            }
        }

        try {
            Object result = this.getFieldAccessor(fieldId, entity.getClass()).invoke(entity);

            return result == null ? null : result.toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalEntityException("the given entity doesn't have an accessor to get the Id, please provide one", e);
        }
    }

    private <T> Method getFieldAccessor(Field field, Class<T> clazz) {
        String getterName = "get" + StringUtils.capitalizeFirstChar(field.getName());
        return this.getFieldMethodByString(field, getterName, clazz);

    }

    private <T> Method getFieldMutator(Field field, Class<T> clazz) {
        String setterName = "set" + StringUtils.capitalizeFirstChar(field.getName());
        return this.getFieldMethodByString(field, setterName, clazz);
    }

    private <T> Method getFieldMethodByString(Field field, String methodName, Class<T> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        int i = 0;
        Method fieldAccessor = null;

        while (i < methods.length && fieldAccessor == null) {
            Method m = methods[i];
            if (m.getName().equals(methodName)) {
                fieldAccessor = m;
            }
            i++;
        }

        if (fieldAccessor == null) {
            throw new IllegalEntityException("the given entity doesn't have the method " + methodName + " for the field " + field.getName());
        }

        return fieldAccessor;
    }

    private <T> Field getFieldID(Class<T> clazz) {
        if (AbstractNamespace.ANNOTATION_CACHE.containsKey(clazz)
                && AbstractNamespace.ANNOTATION_CACHE.get(clazz).containsKey(Id.class)) {
            return (Field) AbstractNamespace.ANNOTATION_CACHE.get(clazz).get(Id.class);
        }

        Field[] fields = clazz.getDeclaredFields();
        int i = 0;
        Field fieldId = null;

        while (i < fields.length && fieldId == null) {
            Field field = fields[i];
            if (field.getAnnotation(Id.class) != null) {
                fieldId = field;
            }
            i++;
        }

        if (fieldId == null) {
            throw new IllegalEntityException("the given entity doesn't have a field that carry the ID annotation");
        }

        AbstractNamespace.ANNOTATION_CACHE.get(clazz).put(Id.class, fieldId);

        return fieldId;
    }
}
