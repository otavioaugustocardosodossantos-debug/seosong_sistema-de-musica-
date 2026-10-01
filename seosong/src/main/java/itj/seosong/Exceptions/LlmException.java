package itj.seosong.Exceptions;

/** Falha ao falar com a IA (chave ausente, timeout, erro da API...) → HTTP 502. */
public class LlmException extends RuntimeException {
    public LlmException(String message) {
        super(message);
    }

    public LlmException(String message, Throwable cause) {
        super(message, cause);
    }
}
