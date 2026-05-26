package br.com.classroompb.model.entities.Usuario;

import br.com.classroompb.model.enums.TipoUsuario;

public class Aluno extends Usuario {

    public Aluno() {
        super();
    }

    public Aluno(String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
        super(nome, email, matricula, senha, tipoUsuario);
    }

}
