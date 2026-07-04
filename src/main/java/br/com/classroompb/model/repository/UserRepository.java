package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsavel pela persistencia de usuarios em arquivo JSON.
 */
public class UserRepository {

  private static final String DIRETORIO_USUARIOS_PADRAO = PersistenciaPaths.USUARIOS.toString();

  private ObjectMapper objectMapper;
  private final String diretorioUsuarios;

  /**
   * Cria o repositorio de usuarios com diretorio padrao.
   *
   * @param objectMapper mapeador JSON.
   */
  public UserRepository(ObjectMapper objectMapper) {
    this(objectMapper, DIRETORIO_USUARIOS_PADRAO);
  }

  /**
   * Cria o repositorio de usuarios com diretorio informado.
   *
   * @param objectMapper mapeador JSON.
   * @param diretorioUsuarios diretorio dos usuarios.
   */
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

  /**
   * Salva um usuario no arquivo JSON.
   *
   * @param usuario usuario a ser salvo.
   */
  public void salvarUsuario(Usuario usuario) {
    if (usuario == null) {
      throw new IllegalArgumentException("Usuário não pode ser nulo.");
    }

    TipoUsuario tipoUsuario = usuario.getTipoUsuario();
    List<Usuario> usuarios = listar(tipoUsuario);

    usuarios.add(usuario);
    salvarListaUsuarios(tipoUsuario, usuarios);
  }

  /**
   * Atualiza um usuario no arquivo JSON.
   *
   * @param usuarioAtualizado usuario com dados atualizados.
   */
  public void atualizarUsuario(Usuario usuarioAtualizado) {
    if (usuarioAtualizado == null) {
      throw new IllegalArgumentException("Usuário não pode ser nulo.");
    }

    TipoUsuario tipoUsuario = usuarioAtualizado.getTipoUsuario();
    List<Usuario> usuarios = listar(tipoUsuario);

    for (int i = 0; i < usuarios.size(); i++) {
      Usuario usuario = usuarios.get(i);

      if (matriculasSaoIguais(usuario.getMatricula(), usuarioAtualizado.getMatricula())) {
        usuarios.set(i, usuarioAtualizado);
        salvarListaUsuarios(tipoUsuario, usuarios);
        return;
      }
    }

    throw new UsuarioNaoEncontradoException(
        "Usuário com matrícula " + usuarioAtualizado.getMatricula() + " não foi encontrado.");
  }

  /**
   * Remove um usuario pela matricula e tipo.
   *
   * @param matricula matricula do usuario.
   * @param tipoUsuario tipo do usuario.
   * @return usuario removido.
   */
  public Usuario removerPorMatricula(String matricula, TipoUsuario tipoUsuario) {
    validarMatriculaInformada(matricula);

    List<Usuario> usuarios = listar(tipoUsuario);

    for (int i = 0; i < usuarios.size(); i++) {
      Usuario usuario = usuarios.get(i);

      if (matriculasSaoIguais(usuario.getMatricula(), matricula)) {
        Usuario usuarioRemovido = usuarios.remove(i);
        salvarListaUsuarios(tipoUsuario, usuarios);
        return usuarioRemovido;
      }
    }

    throw new UsuarioNaoEncontradoException(
        "Usuário com matrícula " + matricula + " não foi encontrado.");
  }

  /**
   * Encontra um usuario por email e senha.
   *
   * @param email email do usuario.
   * @param senha senha do usuario.
   * @return usuario encontrado.
   */
  public Usuario encontrarUsuario(String email, String senha) {
    List<Usuario> listaUsuarios = this.listar();

    for (Usuario usuario : listaUsuarios) {
      if (usuario.getEmail().equals(email) && usuario.getSenha().equals(senha)) {
        return usuario;
      }
    }

    throw new UsuarioNaoEncontradoException();
  }

  /**
   * Busca um usuario pela matricula.
   *
   * @param matricula matricula do usuario.
   * @return usuario encontrado.
   */
  public Usuario buscarPorMatricula(String matricula) {
    validarMatriculaInformada(matricula);

    List<Usuario> listaUsuarios = this.listar();

    for (Usuario usuario : listaUsuarios) {
      if (matriculasSaoIguais(usuario.getMatricula(), matricula)) {
        return usuario;
      }
    }

    throw new UsuarioNaoEncontradoException(
        "Usuário com matrícula " + matricula + " não foi encontrado.");
  }

  /**
   * Busca um usuario pela matricula e tipo.
   *
   * @param matricula matricula do usuario.
   * @param tipoUsuario tipo do usuario.
   * @return usuario encontrado.
   */
  public Usuario buscarPorMatricula(String matricula, TipoUsuario tipoUsuario) {
    validarMatriculaInformada(matricula);

    List<Usuario> listaUsuarios = this.listar(tipoUsuario);

    for (Usuario usuario : listaUsuarios) {
      if (matriculasSaoIguais(usuario.getMatricula(), matricula)) {
        return usuario;
      }
    }

    throw new UsuarioNaoEncontradoException(
        "Usuário com matrícula " + matricula + " não foi encontrado.");
  }

  /**
   * Lista todos os usuarios cadastrados.
   *
   * @return lista de usuarios cadastrados.
   */
  public List<Usuario> listar() {
    File diretorio = new File(diretorioUsuarios);

    if (!diretorio.exists()) {
      diretorio.mkdirs();
    }

    if (!diretorio.isDirectory()) {
      throw new PersistenciaException(
          "Diretorio de usuarios invalido.",
          new IOException("Caminho nao e diretorio: " + diretorio.getPath()));
    }

    for (TipoUsuario tipoUsuario : TipoUsuario.values()) {
      RepositoryJsonFiles.garantirArquivoLista(new File(getCaminhoArquivo(tipoUsuario)));
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

  /**
   * Lista usuarios pelo tipo informado.
   *
   * @param tipoUsuario tipo do usuario.
   * @return lista de usuarios do tipo informado.
   */
  public List<Usuario> listar(TipoUsuario tipoUsuario) {
    File arquivo = new File(getCaminhoArquivo(tipoUsuario));
    RepositoryJsonFiles.garantirArquivoLista(arquivo);

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

  private void salvarListaUsuarios(TipoUsuario tipoUsuario, List<Usuario> usuarios) {
    String caminhoArquivo = getCaminhoArquivo(tipoUsuario);

    try {
      objectMapper.writeValue(new File(caminhoArquivo), usuarios);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar usuários.", e);
    }
  }

  private void validarMatriculaInformada(String matricula) {
    if (matricula == null || matricula.trim().isEmpty()) {
      throw new IllegalArgumentException("Matrícula não pode ser vazia.");
    }
  }

  private boolean matriculasSaoIguais(String matriculaUsuario, String matriculaBuscada) {
    return matriculaUsuario != null
        && matriculaUsuario.trim().equalsIgnoreCase(matriculaBuscada.trim());
  }

  private <T extends Usuario> List<Usuario> lerUsuarios(File arquivo, Class<T> tipo)
      throws IOException {
    return new ArrayList<>(
        objectMapper.readValue(
            arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
  }

  private String getCaminhoArquivo(TipoUsuario tipoUsuario) {
    File diretorio = new File(diretorioUsuarios);

    if (!diretorio.exists()) {
      diretorio.mkdirs();
    }

    return new File(diretorio, tipoUsuario.name().toLowerCase() + ".json").getPath();
  }
}
