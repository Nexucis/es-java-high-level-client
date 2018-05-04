package nexucis.elasticsearch.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtils(){

    }

    public static <T> T getObjectFromString(String s, Class<T> clazz) throws IOException {
        return mapper.readValue(s, clazz);
    }

    public static <T> String getJsonFromObject(T entity) throws JsonProcessingException {
        return mapper.writeValueAsString(entity);
    }
}
