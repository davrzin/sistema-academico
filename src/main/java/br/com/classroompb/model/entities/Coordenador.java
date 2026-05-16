package br.com.classroompb.model.entities;

import br.com.classroompb.model.enums.TipoUsuario;

public class Coordenador extends Usuario {

    public Coordenador() {
        super();
    }

    public Coordenador(String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
        super(nome, email, matricula, senha, tipoUsuario);
    }

}
