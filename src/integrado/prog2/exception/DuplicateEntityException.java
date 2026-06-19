package integrado.prog2.exception;

public class DuplicateEntityException extends BusinessException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
