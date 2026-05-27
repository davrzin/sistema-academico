package br.com.classroompb.model.exception;

public class ExistePeriodoAtivoException extends RuntimeException {
    public ExistePeriodoAtivoException(String message) {
        super(message);
    }

    public ExistePeriodoAtivoException(){
        this("Já existe um período ativo no momento.");
    }
}
