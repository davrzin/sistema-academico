package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;

/**
 * Servico responsavel pelas operacoes de disciplina.
 */
public class DisciplinaService {

  private static final Path DIRETORIO_DISCIPLINAS = PersistenciaPaths.DISCIPLINAS;
  private static final Path DIRETORIO_CURSOS = PersistenciaPaths.CURSOS;

  private final DisciplinaRepository repository;
  private final CursoRepository cursoRepository;

  /**
   * Cria o servico de disciplinas com dependencias padrao.
   */
  public DisciplinaService() {
    this.repository =
        new DisciplinaRepository(new ObjectMapper(), DIRETORIO_DISCIPLINAS.toString());
    this.cursoRepository = new CursoRepository(new ObjectMapper(), DIRETORIO_CURSOS.toString());
  }

  /**
   * Cria o servico de disciplinas com dependencias informadas.
   *
   * @param repository repositorio de disciplinas.
   * @param cursoRepository repositorio de cursos.
   */
  public DisciplinaService(DisciplinaRepository repository, CursoRepository cursoRepository) {
    this.repository = repository;
    this.cursoRepository = cursoRepository;
  }

  /**
   * Cadastra uma disciplina.
   *
   * @param disciplina disciplina a ser cadastrada.
   */
  public void cadastrarDisciplina(Disciplina disciplina) {
    validarDisciplina(disciplina);

    String codigo = gerarCodigoDisciplina();
    disciplina.setCodigo(codigo);

    validarExistenciaDisciplina(disciplina.getCodigo(), disciplina.getNome());
    validarCurso(disciplina);
    validarPreRequisitos(disciplina);

    repository.salvarDisciplina(disciplina);
  }

  private void validarDisciplina(Disciplina disciplina) {
    if (disciplina == null) {
      throw new EntradaInvalidaException("Disciplina não pode ser null.");
    }

    disciplina.validarDadosBasicos();
  }

  private void validarExistenciaDisciplina(String codigo, String nome) {
    Disciplina disciplinaCodigo = repository.buscarPorCodigo(codigo);
    Disciplina disciplinaNome = repository.buscarPorNome(nome);

    if (disciplinaCodigo != null) {
      throw new EntradaInvalidaException("Já existe uma disciplina cadastrada com esse código.");
    }

    if (disciplinaNome != null) {
      throw new EntradaInvalidaException("Já existe uma disciplina cadastrada com esse nome.");
    }
  }

  private void validarCurso(Disciplina disciplina) {
    Curso curso = cursoRepository.buscarPorCodigo(disciplina.getCodigoCurso());

    if (curso == null) {
      throw new EntradaInvalidaException("Curso não encontrado.");
    }

    if (disciplina.getPeriodo() > curso.getQuantidadePeriodos()) {
      throw new EntradaInvalidaException(
          "O período da disciplina não pode ser maior que a quantidade de períodos do curso.");
    }
  }

  private void validarPreRequisitos(Disciplina disciplinaCadastrada) {
    List<String> preRequisitos = disciplinaCadastrada.getPreRequisitos();

    if (preRequisitos == null || preRequisitos.isEmpty()) {
      return;
    }

    for (String codigoDisciplina : preRequisitos) {
      Disciplina disciplina = repository.buscarPorCodigo(codigoDisciplina);

      if (disciplina == null) {
        throw new EntradaInvalidaException(
            "Pré-requisito " + codigoDisciplina + " não encontrado.");
      }

      if (!disciplina.getCodigoCurso().equalsIgnoreCase(disciplinaCadastrada.getCodigoCurso())) {
        throw new EntradaInvalidaException(
            "Pre-requisito deve pertencer ao mesmo curso da disciplina.");
      }
    }
  }

  private String gerarCodigoDisciplina() {
    int contador = repository.listarDisciplinas().size();
    String codigo;

    do {
      codigo = "dis" + String.format("%02d", contador);
      contador++;
    } while (repository.buscarPorCodigo(codigo) != null);

    return codigo;
  }

  /**
   * Lista todas as disciplinas cadastradas.
   *
   * @return lista de disciplinas cadastradas.
   */
  public List<Disciplina> listarDisciplinas() {
    return repository.listarDisciplinas();
  }

  /**
   * Lista disciplinas pelo codigo do curso.
   *
   * @param codigoCurso codigo do curso.
   * @return lista de disciplinas do curso.
   */
  public List<Disciplina> listarDisciplinasPorCurso(String codigoCurso) {
    return repository.buscarPorCurso(codigoCurso);
  }
}
