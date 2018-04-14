package nexucis.elasticsearch.data.exception;

public class IllegalEntityException extends RuntimeException {

    public IllegalEntityException(String msg) {
        super(msg);
    }

    public IllegalEntityException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
