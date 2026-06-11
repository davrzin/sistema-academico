package br.com.classroompb.model.exception;

public class TurmaNaoEncontradaException extends RuntimeException {
    public TurmaNaoEncontradaException(String message) {
        super(message);
    }

    public TurmaNaoEncontradaException(){
        super("Turma não encontrada");
    }

}
