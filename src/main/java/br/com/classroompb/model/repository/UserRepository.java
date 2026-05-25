package br.com.classroompb.model.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.Usuario;
import br.com.classroompb.model.entities.Aluno;
import br.com.classroompb.model.entities.Administrador;
import br.com.classroompb.model.entities.Coordenador;
import br.com.classroompb.model.entities.Professor;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.PersistenciaException;

public class UserRepository {

    private static final String DIRETORIO_USUARIOS_PADRAO = "usuarios";

    private ObjectMapper objectMapper;
    private final String diretorioUsuarios;

    public UserRepository(ObjectMapper objectMapper) {
        this(objectMapper, DIRETORIO_USUARIOS_PADRAO);
    }

    public UserRepository(ObjectMapper objectMapper, String diretorioUsuarios) {
        this.objectMapper = objectMapper;
        this.diretorioUsuarios = diretorioUsuarios;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void salvarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }

        TipoUsuario tipoUsuario = usuario.getTipoUsuario();
        String caminhoArquivo = getCaminhoArquivo(tipoUsuario);

        List<Usuario> usuarios = listar(tipoUsuario);
        usuarios.add(usuario);

        try {
            objectMapper.writeValue(new File(caminhoArquivo), usuarios);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao adicionar usuários.", e);
        }
    }

    public Usuario encontrarUsuario(String email, String senha) {
        List<Usuario> listaUsuarios = this.listar();

        // Iterando sobre todos os usuários, pois no login ainda não sabemos qual é o tipo do usuário.
        for (Usuario usuario : listaUsuarios) {
            if (usuario.getEmail().equals(email) && usuario.getSenha().equals(senha)) {
                return usuario;
            }
        }

        throw new UsuarioNaoEncontradoException();
    }

    public Usuario buscarPorMatricula(String matricula) {
        validarMatriculaInformada(matricula);

        List<Usuario> listaUsuarios = this.listar();

        for (Usuario usuario : listaUsuarios) {
            if (matriculasSaoIguais(usuario.getMatricula(), matricula)) {
                return usuario;
            }
        }

        throw new UsuarioNaoEncontradoException("Usuário com matrícula " + matricula + " não foi encontrado.");
    }

    public Usuario buscarPorMatricula(String matricula, TipoUsuario tipoUsuario) {
        validarMatriculaInformada(matricula);

        List<Usuario> listaUsuarios = this.listar(tipoUsuario);

        for (Usuario usuario : listaUsuarios) {
            if (matriculasSaoIguais(usuario.getMatricula(), matricula)) {
                return usuario;
            }
        }

        throw new UsuarioNaoEncontradoException("Usuário com matrícula " + matricula + " não foi encontrado.");
    }

    public List<Usuario> listar() {
        File diretorio = new File(diretorioUsuarios);

        if (!diretorio.exists() || !diretorio.isDirectory()) {
            return new ArrayList<>();
        }

        File[] arquivos = diretorio.listFiles((dir, nome) -> nome.toLowerCase().endsWith(".json"));
        List<Usuario> usuarios = new ArrayList<>();

        if (arquivos == null) {
            return usuarios;
        }

        try {
            for (File arquivo : arquivos) {
                String nomeArquivo = arquivo.getName().toLowerCase();

                if (nomeArquivo.equals(TipoUsuario.ALUNO.name().toLowerCase() + ".json")) {
                    usuarios.addAll(lerUsuarios(arquivo, Aluno.class));
                } else if (nomeArquivo.equals(TipoUsuario.ADMINISTRADOR.name().toLowerCase() + ".json")) {
                    usuarios.addAll(lerUsuarios(arquivo, Administrador.class));
                } else if (nomeArquivo.equals(TipoUsuario.COORDENADOR.name().toLowerCase() + ".json")) {
                    usuarios.addAll(lerUsuarios(arquivo, Coordenador.class));
                } else if (nomeArquivo.equals(TipoUsuario.PROFESSOR.name().toLowerCase() + ".json")) {
                    usuarios.addAll(lerUsuarios(arquivo, Professor.class));
                }
            }
            return usuarios;
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler usuários.", e);
        }
    }

    public List<Usuario> listar(TipoUsuario tipoUsuario) {
        File arquivo = new File(getCaminhoArquivo(tipoUsuario));

        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try {
            return switch (tipoUsuario) {
                case ALUNO -> new ArrayList<>(lerUsuarios(arquivo, Aluno.class));
                case ADMINISTRADOR -> new ArrayList<>(lerUsuarios(arquivo, Administrador.class));
                case COORDENADOR -> new ArrayList<>(lerUsuarios(arquivo, Coordenador.class));
                case PROFESSOR -> new ArrayList<>(lerUsuarios(arquivo, Professor.class));
            };
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler usuários.", e);
        }
    }

    private void validarMatriculaInformada(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("Matrícula não pode ser vazia.");
        }
    }

    private boolean matriculasSaoIguais(String matriculaUsuario, String matriculaBuscada) {
        return matriculaUsuario != null && matriculaUsuario.trim().equalsIgnoreCase(matriculaBuscada.trim());
    }

    private <T extends Usuario> List<Usuario> lerUsuarios(File arquivo, Class<T> tipo) throws IOException {
        return new ArrayList<>(objectMapper.readValue(arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
    }

    private String getCaminhoArquivo(TipoUsuario tipoUsuario) {
        File diretorio = new File(diretorioUsuarios);

        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        return new File(diretorio, tipoUsuario.name().toLowerCase() + ".json").getPath();
    }
}
