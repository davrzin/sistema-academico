package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.Administrador;
import br.com.classroompb.model.entities.Aluno;
import br.com.classroompb.model.entities.Coordenador;
import br.com.classroompb.model.entities.Professor;
import br.com.classroompb.model.entities.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.repository.UserRepository;

public class UsuarioService {

    public Usuario cadastrarUsuario(String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario){

        Usuario usuario = null;

        switch (tipoUsuario) {
            case ALUNO:
                usuario = new Aluno(nome, email, matricula, senha, tipoUsuario);
                break;
            case ADMINISTRADOR:
                usuario = new Administrador(nome, email, matricula, senha, tipoUsuario);
                break;
            case COORDENADOR:
                usuario = new Coordenador(nome, email, matricula, senha, tipoUsuario);
            case PROFESSOR:
                usuario = new Professor(nome, email, matricula, senha, tipoUsuario);
            default:
                break;
        }

        //Chamar UserRepository

        return usuario;
    }

//    public Usuario fazerLoginUsuario(String email, String senha){
//
//        //CHAMAR USERREPOSITORY
//
//
//
//
//    }
}
