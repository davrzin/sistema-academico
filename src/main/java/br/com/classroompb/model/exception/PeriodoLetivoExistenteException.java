package br.com.classroompb.model.exception;

public class PeriodoLetivoExistenteException extends RuntimeException {
    public PeriodoLetivoExistenteException(String message) {
        super(message);
    }

    public PeriodoLetivoExistenteException(){
        this("Já existe um período letivo com essas informações.");
    }

}
