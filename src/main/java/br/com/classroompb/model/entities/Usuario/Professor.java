package br.com.classroompb.model.entities.Usuario;

import br.com.classroompb.model.enums.TipoUsuario;

public class Professor extends Usuario {

    public Professor() {
        super();
    }

    public Professor(String nome, String email, String senha) {
        super(nome, email, senha, TipoUsuario.PROFESSOR);
    }

    public Professor(String nome, String email, String matricula, String senha) {
        super(nome, email, matricula, senha, TipoUsuario.PROFESSOR);
    }

    public Professor(String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
        super(nome, email, matricula, senha, TipoUsuario.PROFESSOR);
    }
}
