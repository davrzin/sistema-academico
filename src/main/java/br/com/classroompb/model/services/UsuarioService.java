package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Servico responsavel pelas operacoes de usuario.
 */
public class UsuarioService {

  private static final Path DIRETORIO_USUARIOS = PersistenciaPaths.USUARIOS;
  private static final Path DIRETORIO_CURSOS = PersistenciaPaths.CURSOS;

  private final UserRepository repository;
  private final CursoRepository cursoRepository;

  /**
   * Cria o servico de usuarios com repositorio padrao.
   */
  public UsuarioService() {
    this.repository = new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString());
    this.cursoRepository = new CursoRepository(new ObjectMapper(), DIRETORIO_CURSOS.toString());
  }

  /**
   * Cria o servico de usuarios com repositorio informado.
   *
   * @param repository repositorio de usuarios.
   */
  public UsuarioService(UserRepository repository) {
    this.repository = repository;
    this.cursoRepository = new CursoRepository(new ObjectMapper(), DIRETORIO_CURSOS.toString());
  }

  /**
   * Cadastra um usuario.
   *
   * @param usuario usuario a ser cadastrado.
   */
  public void cadastrarUsuario(Usuario usuario) {
    validarUsuario(usuario);
    validarEmailDisponivel(usuario.getEmail(), null);

    String matricula = gerarMatricula(usuario.getTipoUsuario());
    usuario.setMatricula(matricula);

    usuario.validarDadosComMatricula();
    repository.salvarUsuario(usuario);
    sincronizarCursoDoCoordenador(usuario);
  }

  /**
   * Atualiza um usuario.
   *
   * @param usuarioLogado usuario logado.
   * @param matricula matricula do usuario.
   * @param novoNome nome atualizado.
   * @param novoEmail email atualizado.
   * @param novaSenha senha atualizada.
   * @return usuario atualizado.
   */
  public Usuario atualizarUsuario(
      Usuario usuarioLogado,
      String matricula,
      String novoNome,
      String novoEmail,
      String novaSenha) {
    Usuario usuarioEncontrado = buscarUsuarioPorMatricula(usuarioLogado, matricula);

    if (novoNome != null && !novoNome.isBlank()) {
      usuarioEncontrado.setNome(novoNome);
    }

    if (novoEmail != null && !novoEmail.isBlank()) {
      validarEmailDisponivel(novoEmail, usuarioEncontrado.getMatricula());
      usuarioEncontrado.setEmail(novoEmail);
    }

    if (novaSenha != null && !novaSenha.isBlank()) {
      usuarioEncontrado.setSenha(novaSenha);
    }

    usuarioEncontrado.validarDadosComMatricula();
    repository.atualizarUsuario(usuarioEncontrado);

    return usuarioEncontrado;
  }

  /**
   * Remove um usuario pela matricula.
   *
   * @param usuarioLogado usuario logado.
   * @param matricula matricula do usuario.
   * @return usuario removido.
   */
  public Usuario removerUsuarioPorMatricula(Usuario usuarioLogado, String matricula) {
    Usuario usuarioEncontrado = buscarUsuarioPorMatricula(usuarioLogado, matricula);

    repository.removerPorMatricula(
        usuarioEncontrado.getMatricula(), usuarioEncontrado.getTipoUsuario());

    return usuarioEncontrado;
  }

  private void validarUsuario(Usuario usuario) {
    if (usuario == null) {
      throw new EntradaInvalidaException("Usuário não pode ser null.");
    }

    usuario.validarDadosBasicos();
    validarVinculoCurso(usuario);
  }

  private void validarVinculoCurso(Usuario usuario) {
    if (usuario instanceof Aluno aluno) {
      validarCursoObrigatorio(aluno.getCodigoCurso());
      return;
    }

    if (usuario instanceof Professor professor) {
      validarCursoObrigatorio(professor.getCodigoCurso());
      return;
    }

    if (usuario instanceof Coordenador coordenador) {
      validarCursoOpcional(coordenador.getCodigoCurso());
    }
  }

  private void validarCursoObrigatorio(String codigoCurso) {
    if (codigoCurso == null || codigoCurso.isBlank()) {
      throw new EntradaInvalidaException("Codigo do curso nao pode ser vazio.");
    }

    validarCursoExistente(codigoCurso);
  }

  private void validarCursoOpcional(String codigoCurso) {
    if (codigoCurso == null || codigoCurso.isBlank()) {
      return;
    }

    Curso curso = validarCursoExistente(codigoCurso);
    validarCursoSemCoordenador(curso);
  }

  private Curso validarCursoExistente(String codigoCurso) {
    Curso curso = cursoRepository.buscarPorCodigo(codigoCurso.trim());

    if (curso == null) {
      throw new EntradaInvalidaException("Curso informado nao encontrado.");
    }

    return curso;
  }

  private void validarCursoSemCoordenador(Curso curso) {
    if (curso.getMatriculaCoordenador() != null && !curso.getMatriculaCoordenador().isBlank()) {
      throw new EntradaInvalidaException("Curso ja possui coordenador.");
    }
  }

  private void sincronizarCursoDoCoordenador(Usuario usuario) {
    if (!(usuario instanceof Coordenador coordenador)) {
      return;
    }

    String codigoCurso = coordenador.getCodigoCurso();

    if (codigoCurso == null || codigoCurso.isBlank()) {
      return;
    }

    Curso curso = cursoRepository.buscarPorCodigo(codigoCurso.trim());
    curso.setMatriculaCoordenador(coordenador.getMatricula());
    cursoRepository.atualizarCurso(curso);
  }

  private void validarEmailDisponivel(String email, String matriculaIgnorada) {
    List<Usuario> usuarios = repository.listar();

    for (Usuario usuario : usuarios) {
      boolean mesmoEmail = usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(email);

      boolean mesmoUsuario =
          matriculaIgnorada != null
              && usuario.getMatricula() != null
              && usuario.getMatricula().equalsIgnoreCase(matriculaIgnorada);

      if (mesmoEmail && !mesmoUsuario) {
        throw new EntradaInvalidaException("Já existe um usuário cadastrado com esse e-mail.");
      }
    }
  }

  /**
   * Realiza login de usuario.
   *
   * @param email email do usuario.
   * @param senha senha do usuario.
   * @return usuario autenticado.
   */
  public Usuario fazerLoginUsuario(String email, String senha) {
    try {
      return repository.encontrarUsuario(email, senha);
    } catch (UsuarioNaoEncontradoException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  /**
   * Busca um usuario pela matricula.
   *
   * @param usuarioLogado usuario logado.
   * @param matricula matricula do usuario.
   * @return usuario encontrado.
   */
  public Usuario buscarUsuarioPorMatricula(Usuario usuarioLogado, String matricula) {
    Usuario usuarioEncontrado = repository.buscarPorMatricula(matricula);

    PermissaoService.validarPermissaoBuscaPorMatricula(usuarioLogado, usuarioEncontrado);
    validarUsuarioDoCursoDoCoordenador(usuarioLogado, usuarioEncontrado);

    return usuarioEncontrado;
  }

  /**
   * Busca um aluno pela matricula.
   *
   * @param matricula matricula do aluno.
   * @return aluno encontrado.
   */
  public Aluno buscarAlunoPorMatricula(String matricula) {

    Usuario usuario = repository.buscarPorMatricula(matricula);

    if (!(usuario instanceof Aluno aluno)) {
      throw new EntradaInvalidaException("Usuário informado não é um aluno.");
    }

    return aluno;
  }

  /**
   * Lista todos os coordenadores cadastrados.
   *
   * @return lista de coordenadores.
   */
  public List<Coordenador> listarCoordenadores() {
    List<Coordenador> coordenadores = new ArrayList<>();

    for (Usuario usuario : repository.listar(TipoUsuario.COORDENADOR)) {
      if (usuario instanceof Coordenador coordenador) {
        coordenadores.add(coordenador);
      }
    }

    return coordenadores;
  }

  private String gerarMatricula(TipoUsuario tipoUsuario) {
    String prefixo;

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
      default:
        throw new EntradaInvalidaException("Tipo de usuario invalido.");
    }

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

  /**
   * Lista usuarios conforme permissao do usuario logado.
   *
   * @param usuarioLogado usuario logado.
   * @return lista de usuarios visiveis.
   */
  public List<Usuario> listarUsuarios(Usuario usuarioLogado) {
    if (usuarioLogado instanceof Coordenador coordenador) {
      return listarAlunosProfessoresPorCursoCoordenador(coordenador);
    }

    PermissaoService.validarPermissao(usuarioLogado, TipoUsuario.ADMINISTRADOR);
    return repository.listar();
  }

  private List<Usuario> listarAlunosProfessoresPorCursoCoordenador(Coordenador coordenador) {
    validarCoordenadorComCurso(coordenador);

    List<Usuario> usuariosCurso = new ArrayList<>();

    for (Usuario usuario : repository.listar(TipoUsuario.ALUNO)) {
      if (usuario instanceof Aluno aluno
          && pertenceAoCurso(aluno.getCodigoCurso(), coordenador.getCodigoCurso())) {
        usuariosCurso.add(aluno);
      }
    }

    for (Usuario usuario : repository.listar(TipoUsuario.PROFESSOR)) {
      if (usuario instanceof Professor professor
          && pertenceAoCurso(professor.getCodigoCurso(), coordenador.getCodigoCurso())) {
        usuariosCurso.add(professor);
      }
    }

    return usuariosCurso;
  }

  private void validarUsuarioDoCursoDoCoordenador(
      Usuario usuarioLogado, Usuario usuarioEncontrado) {
    if (!(usuarioLogado instanceof Coordenador coordenador)) {
      return;
    }

    validarCoordenadorComCurso(coordenador);

    if (usuarioEncontrado instanceof Aluno aluno) {
      if (!pertenceAoCurso(aluno.getCodigoCurso(), coordenador.getCodigoCurso())) {
        throw new EntradaInvalidaException("Coordenador nao pode visualizar aluno de outro curso.");
      }
      return;
    }

    if (usuarioEncontrado instanceof Professor professor) {
      if (!pertenceAoCurso(professor.getCodigoCurso(), coordenador.getCodigoCurso())) {
        throw new EntradaInvalidaException(
            "Coordenador nao pode visualizar professor de outro curso.");
      }
    }
  }

  private void validarCoordenadorComCurso(Coordenador coordenador) {
    if (coordenador.getCodigoCurso() == null || coordenador.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Coordenador nao esta vinculado a nenhum curso.");
    }
  }

  private boolean pertenceAoCurso(String codigoCursoUsuario, String codigoCursoCoordenador) {
    return codigoCursoUsuario != null
        && codigoCursoCoordenador != null
        && codigoCursoUsuario.trim().equalsIgnoreCase(codigoCursoCoordenador.trim());
  }
}
