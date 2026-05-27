package br.com.classroompb.model.services;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.Usuario.Administrador;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.entities.Usuario.Coordenador;
import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.UserRepository;

public class UsuarioService {

    private static final Path DIRETORIO_USUARIOS = Path.of(System.getProperty("user.dir"), "usuarios");

    private final UserRepository repository;

    public UsuarioService() {
        this.repository = new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString());
    }

    public UsuarioService(UserRepository repository) {
        this.repository = repository;
    }

    public Usuario cadastrarUsuario(String nome, String email, String senha, TipoUsuario tipoUsuario) {
        if (tipoUsuario == null) {
            throw new IllegalArgumentException("Tipo de usuário inválido.");
        }
        
        String matricula = gerarMatricula(tipoUsuario);

        Usuario usuario;

        switch (tipoUsuario) {
            case ALUNO:
                usuario = new Aluno(nome, email, matricula, senha, tipoUsuario);
                break;

            case ADMINISTRADOR:
                usuario = new Administrador(nome, email, matricula, senha, tipoUsuario);
                break;

            case COORDENADOR:
                usuario = new Coordenador(nome, email, matricula, senha, tipoUsuario);
                break;

            case PROFESSOR:
                usuario = new Professor(nome, email, matricula, senha, tipoUsuario);
                break;

            default:
                throw new IllegalArgumentException("Tipo de usuário inválido.");
        }

        repository.salvarUsuario(usuario);

        return usuario;
    }

    public Usuario fazerLoginUsuario(String email, String senha) {
        try {
            return repository.encontrarUsuario(email, senha);
        } catch (UsuarioNaoEncontradoException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Usuario buscarUsuarioPorMatricula(Usuario usuarioLogado, String matricula) {
        Usuario usuarioEncontrado = repository.buscarPorMatricula(matricula);

        PermissaoService.validarPermissaoBuscaPorMatricula(usuarioLogado, usuarioEncontrado);

        return usuarioEncontrado;
    }

    private String gerarMatricula(TipoUsuario tipoUsuario) {
    
        String prefixo = "";

        switch (tipoUsuario) {
            case ALUNO:
                prefixo = "al";
                break;

            case ADMINISTRADOR:
                prefixo = "ad";
                break;

            case PROFESSOR:
                prefixo = "pr";
                break;

            case COORDENADOR:
                prefixo = "co";
                break;
        }

        UserRepository userRepository = new UserRepository(new ObjectMapper());
        List<Usuario> usuarios = userRepository.listar(tipoUsuario);

        long quantidade = usuarios.stream()
                .filter(u -> u.getTipoUsuario() == tipoUsuario)
                .count();

        return prefixo + String.format("%02d", quantidade);
    }
}