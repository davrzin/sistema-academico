package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.exception.EntradaInvalidaException;
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
  private final VinculoCursoCoordenadorService vinculoService;

  /**
   * Cria o servico de cursos com repositorio padrao.
   */
  public CursoService() {
    this(
        new CursoRepository(new ObjectMapper(), DIRETORIO_CURSOS.toString()),
        new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString()));
  }

  /**
   * Cria o servico de cursos com repositorio informado.
   *
   * @param cursoRepository repositorio de cursos.
   */
  public CursoService(CursoRepository cursoRepository) {
    this(
        cursoRepository,
        new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString()));
  }

  /**
   * Cria o servico de cursos com repositorios informados.
   *
   * @param cursoRepository repositorio de cursos.
   * @param userRepository repositorio de usuarios.
   */
  public CursoService(CursoRepository cursoRepository, UserRepository userRepository) {
    this.repository = cursoRepository;
    this.vinculoService =
        new VinculoCursoCoordenadorService(cursoRepository, userRepository);
  }

  /**
   * Cadastra um curso.
   *
   * @param curso curso a ser cadastrado.
   */
  public void cadastrarCurso(Curso curso) {
    cadastrarCurso(curso, null);
  }

  /**
   * Cadastra um curso e vincula um coordenador livre.
   *
   * @param curso curso a ser cadastrado.
   * @param matricula matricula do coordenador.
   */
  public void cadastrarCurso(Curso curso, String matricula) {
    validarCurso(curso);

    String codigo = gerarCodigoCurso();
    curso.setCodigo(codigo);

    validarExistenciaCurso(curso.getCodigo(), curso.getNome());
    boolean possuiCoordenador = matricula != null && !matricula.isBlank();
    if (possuiCoordenador) {
      vinculoService.validarCoordenadorDisponivel(matricula);
    }

    repository.salvarCurso(curso);
    if (possuiCoordenador) {
      try {
        vinculoService.vincular(matricula, curso.getCodigo());
      } catch (RuntimeException e) {
        try {
          repository.removerPorCodigo(curso.getCodigo());
        } catch (RuntimeException rollbackException) {
          e.addSuppressed(rollbackException);
        }
        throw e;
      }
    }
  }

  /**
   * Busca o coordenador responsavel por um curso.
   *
   * @param codigoCurso codigo do curso.
   * @return coordenador responsavel ou null.
   */
  public Coordenador buscarCoordenadorPorCurso(String codigoCurso) {
    return vinculoService.buscarCoordenadorPorCurso(codigoCurso);
  }

  /**
   * Lista coordenadores disponiveis para um curso.
   *
   * @return coordenadores sem curso.
   */
  public List<Coordenador> listarCoordenadoresSemCurso() {
    return vinculoService.listarCoordenadoresSemCurso();
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

  /**
   * Lista todos os cursos cadastrados.
   *
   * @return lista de cursos cadastrados.
   */
  public List<Curso> listarCursos() {
    return repository.listarCursos();
  }
}
