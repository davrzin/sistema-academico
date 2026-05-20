package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.Administrador;
import br.com.classroompb.model.entities.Aluno;
import br.com.classroompb.model.entities.Coordenador;
import br.com.classroompb.model.entities.Professor;
import br.com.classroompb.model.entities.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Path;

public class UsuarioService {

    private final String PASTA_USUARIO = System.getProperty("user.home");

    private final Path DIRETORIO_USUARIOS = Path.of(PASTA_USUARIO, "usuarios");

    private final UserRepository repository = new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString());

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

        repository.salvarUsuario(usuario);

        return usuario;
    }

    public Usuario fazerLoginUsuario(String email, String senha){

        Usuario usuario = null;
        try{
           usuario = repository.encontrarUsuario(email, senha);
        }
        catch(UsuarioNaoEncontradoException e){
            System.out.println(e.getMessage());
        }

        return usuario;
    }
}
