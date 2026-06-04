package br.com.classroompb.model.exception;

public class TurmaCheiaException extends RuntimeException {
    public TurmaCheiaException(String message) {
        super(message);
    }

    public TurmaCheiaException(){
        super("A turma na qual você deseja entrar está cheia");
    }
}
