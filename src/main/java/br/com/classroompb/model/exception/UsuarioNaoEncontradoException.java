package br.com.classroompb.model.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }

    public UsuarioNaoEncontradoException(){
        this("Usuário não foi encontrado");
    }
}
