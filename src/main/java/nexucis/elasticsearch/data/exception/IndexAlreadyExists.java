package nexucis.elasticsearch.data.exception;

public class IndexAlreadyExists extends RuntimeException {

    public IndexAlreadyExists(String index) {
        super("Index " + index + "already exists");
    }
}
