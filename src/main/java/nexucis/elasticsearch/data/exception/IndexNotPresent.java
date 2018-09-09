package nexucis.elasticsearch.data.exception;

public class IndexNotPresent extends RuntimeException {

    public IndexNotPresent(String alias) {
        super("index linked to the alias " + alias + "is not found");
    }
}
