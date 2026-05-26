package br.com.classroompb.model.entities.Usuario;

import br.com.classroompb.model.enums.TipoUsuario;

public class Administrador extends Usuario {

    public Administrador() {
        super();
    }

    public Administrador(String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
        super(nome, email, matricula, senha, tipoUsuario);
    }

}
