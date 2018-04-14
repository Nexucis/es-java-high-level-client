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

    public static String capitalizeFirstChar(String s) {
        if (s == null) {
            return null;
        }

        if ("".equals(s)) {
            return s;
        }

        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
