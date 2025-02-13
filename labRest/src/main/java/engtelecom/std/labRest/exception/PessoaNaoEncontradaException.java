package engtelecom.std.labRest.exception;

public class PessoaNaoEncontradaException extends RuntimeException {
    public PessoaNaoEncontradaException(Long id) {
        super("Naõ encontrei com o id" + id);
    }
}