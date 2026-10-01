package itj.seosong.Exceptions;

/** Operação não pode ser feita no estado atual (ex.: excluir artista com músicas) → HTTP 409. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
