package nexucis.elasticsearch.utils;

public class StringUtils {

    private StringUtils() {

    }

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    public static boolean isNotEmpty(String s) {
        return !StringUtils.isEmpty(s);
    }
}
