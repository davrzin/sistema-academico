package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;

/**
 * Servico responsavel pelas operacoes de curso.
 */
public class CursoService {

  private static final Path DIRETORIO_CURSOS = PersistenciaPaths.CURSOS;
  private static final Path DIRETORIO_USUARIOS = PersistenciaPaths.USUARIOS;
  private final CursoRepository repository;
  private final UserRepository userRepository;

  /**
   * Cria o servico de cursos com repositorio padrao.
   */
  public CursoService() {
    this.repository = new CursoRepository(new ObjectMapper(), DIRETORIO_CURSOS.toString());
    this.userRepository = new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString());
  }

  /**
   * Cria o servico de cursos com repositorio informado.
   *
   * @param cursoRepository repositorio de cursos.
   */
  public CursoService(CursoRepository cursoRepository) {
    this.repository = cursoRepository;
    this.userRepository = new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString());
  }

  /**
   * Cadastra um curso.
   *
   * @param curso curso a ser cadastrado.
   */
  public void cadastrarCurso(Curso curso) {
    validarCurso(curso);

    String codigo = gerarCodigoCurso();
    curso.setCodigo(codigo);

    validarExistenciaCurso(curso.getCodigo(), curso.getNome());
    Coordenador coordenador = validarCoordenador(curso);

    repository.salvarCurso(curso);
    sincronizarCoordenadorDoCurso(curso, coordenador);
  }

  private void validarCurso(Curso curso) {
    if (curso == null) {
      throw new EntradaInvalidaException("Curso não pode ser null.");
    }

    curso.validarDadosBasicos();
  }

  private void validarExistenciaCurso(String codigo, String nome) {
    Curso cursoCodigo = repository.buscarPorCodigo(codigo);
    Curso cursoNome = repository.buscarPorNome(nome);

    if (cursoCodigo != null) {
      throw new EntradaInvalidaException("Já existe um curso cadastrado com esse código.");
    }

    if (cursoNome != null) {
      throw new EntradaInvalidaException("Já existe um curso cadastrado com esse nome.");
    }
  }

  private String gerarCodigoCurso() {
    int contador = repository.listarCursos().size();
    String codigo;

    do {
      codigo = "cur" + String.format("%02d", contador);
      contador++;
    } while (repository.buscarPorCodigo(codigo) != null);

    return codigo;
  }

  private Coordenador validarCoordenador(Curso curso) {
    String matriculaCoordenador = curso.getMatriculaCoordenador();

    if (matriculaCoordenador == null || matriculaCoordenador.isBlank()) {
      return null;
    }

    try {
      Coordenador coordenador =
          (Coordenador)
              userRepository.buscarPorMatricula(
                  matriculaCoordenador.trim(), TipoUsuario.COORDENADOR);
      validarCoordenadorSemCurso(coordenador);
      curso.setMatriculaCoordenador(coordenador.getMatricula());
      return coordenador;
    } catch (UsuarioNaoEncontradoException e) {
      throw new EntradaInvalidaException(
          "Coordenador informado nao encontrado ou nao e coordenador.");
    }
  }

  private void validarCoordenadorSemCurso(Coordenador coordenador) {
    if (coordenador.getCodigoCurso() != null && !coordenador.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Coordenador ja esta vinculado a outro curso.");
    }
  }

  private void sincronizarCoordenadorDoCurso(Curso curso, Coordenador coordenador) {
    if (coordenador == null) {
      return;
    }

    coordenador.setCodigoCurso(curso.getCodigo());
    userRepository.atualizarUsuario(coordenador);
  }

  /**
   * Lista todos os cursos cadastrados.
   *
   * @return lista de cursos cadastrados.
   */
  public List<Curso> listarCursos() {
    return repository.listarCursos();
  }
}
