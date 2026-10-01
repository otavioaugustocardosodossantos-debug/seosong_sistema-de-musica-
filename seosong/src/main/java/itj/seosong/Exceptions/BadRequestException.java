package itj.seosong.Exceptions;

/** Dados enviados pelo cliente são inválidos → HTTP 400. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
