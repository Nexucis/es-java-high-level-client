package nexucis.elasticsearch.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T getObjectFromString(String s, Class<T> clazz) throws IOException {
        return mapper.readValue(s, clazz);
    }
}
