package itj.seosong.Exceptions;

/** Recurso não existe → HTTP 404. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
