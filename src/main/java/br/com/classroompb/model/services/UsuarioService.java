package br.com.classroompb.model.services;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
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

    public void cadastrarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        validarEmailDisponivel(usuario.getEmail());

        String matricula = gerarMatricula(usuario.getTipoUsuario());
        usuario.setMatricula(matricula);

        usuario.validarDadosComMatricula();
        repository.salvarUsuario(usuario);
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new EntradaInvalidaException("Usuário não pode ser null.");
        }

        usuario.validarDadosBasicos();
    }

    private void validarEmailDisponivel(String email) {
        List<Usuario> usuarios = repository.listar();

        for (Usuario usuario : usuarios) {
            if (usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(email)) {
                throw new EntradaInvalidaException("Já existe um usuário cadastrado com esse e-mail.");
            }
        }
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
        String prefixo = switch (tipoUsuario) {
            case ALUNO -> "al";
            case ADMINISTRADOR -> "ad";
            case PROFESSOR -> "pr";
            case COORDENADOR -> "co";
        };

        int contador = repository.listar(tipoUsuario).size();
        String matricula;

        do {
            matricula = prefixo + String.format("%02d", contador);
            contador++;
        } while (existeMatricula(matricula, tipoUsuario));

        return matricula;
    }

    private boolean existeMatricula(String matricula, TipoUsuario tipoUsuario) {
        List<Usuario> usuarios = repository.listar(tipoUsuario);

        for (Usuario usuario : usuarios) {
            if (usuario.getMatricula() != null && usuario.getMatricula().equalsIgnoreCase(matricula)) {
                return true;
            }
        }

        return false;
    }

    public List<Usuario> listarUsuarios() {
        return repository.listar();
    }   
}
