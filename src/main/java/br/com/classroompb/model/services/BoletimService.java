package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import br.com.classroompb.model.repository.TurmaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;

/**
 * Servico responsavel pelas operacoes de boletim.
 */
public class BoletimService {

  private static final Path DIRETORIO_BOLETINS = PersistenciaPaths.BOLETINS;
  private static final Path DIRETORIO_TURMAS = PersistenciaPaths.TURMAS;
  private final BoletimRepository repository;
  private final TurmaRepository turmaRepository;

  /**
   * Cria o servico de boletins com repositorio padrao.
   */
  public BoletimService() {
    this.repository = new BoletimRepository(new ObjectMapper(), DIRETORIO_BOLETINS.toString());
    this.turmaRepository = new TurmaRepository(new ObjectMapper(), DIRETORIO_TURMAS.toString());
  }

  /**
   * Cria o servico de boletins com repositorio informado.
   *
   * @param repository repositorio de boletins.
   */
  public BoletimService(BoletimRepository repository) {
    this.repository = repository;
    this.turmaRepository = new TurmaRepository(new ObjectMapper(), DIRETORIO_TURMAS.toString());
  }

  /**
   * Cria o servico de boletins com repositorios informados.
   *
   * @param repository repositorio de boletins.
   * @param turmaRepository repositorio de turmas.
   */
  public BoletimService(BoletimRepository repository, TurmaRepository turmaRepository) {
    this.repository = repository;
    this.turmaRepository = turmaRepository;
  }

  /**
   * Retorna o repositorio de boletins usado pelo servico.
   *
   * @return repositorio de boletins.
   */
  public BoletimRepository getRepository() {
    return repository;
  }

  /**
   * Cria um boletim.
   *
   * @param boletim boletim a ser criado.
   * @return boletim criado.
   */
  public Boletim criarBoletim(Boletim boletim) {
    validarBoletim(boletim);

    String codigo = gerarCodigoBoletim();
    boletim.setIdBoletim(codigo);

    repository.salvarBoletim(boletim);

    return boletim;
  }

  /**
   * Cria um boletim se ainda nao existir.
   *
   * @param matriculaAluno matricula do aluno.
   * @param codigoTurma codigo da turma.
   * @return boletim existente ou criado.
   */
  public Boletim criarBoletimSeNaoExistir(String matriculaAluno, String codigoTurma) {
    validarMatriculaAluno(matriculaAluno);
    validarCodigoTurma(codigoTurma);

    Boletim boletimExistente = buscarBoletimPorAlunoETurma(matriculaAluno, codigoTurma);

    if (boletimExistente != null) {
      return boletimExistente;
    }

    return criarBoletim(new Boletim(matriculaAluno, codigoTurma));
  }

  /**
   * Busca um boletim por aluno e turma.
   *
   * @param matriculaAluno matricula do aluno.
   * @param codigoTurma codigo da turma.
   * @return boletim encontrado.
   */
  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  public Boletim buscarBoletimPorAlunoETurma(String matriculaAluno, String codigoTurma) {
    validarMatriculaAluno(matriculaAluno);
    validarCodigoTurma(codigoTurma);

    for (Boletim boletim : repository.buscarBoletinsPorAluno(matriculaAluno)) {
      if (boletim.getCodigoTurma() != null
          && boletim.getCodigoTurma().equalsIgnoreCase(codigoTurma.trim())) {
        return boletim;
      }
    }

    return null;
  }

  /**
   * Busca boletins pela matricula do aluno.
   *
   * @param matriculaAluno matricula do aluno.
   * @return lista de boletins do aluno.
   */
  public List<Boletim> buscarBoletinsPorAluno(String matriculaAluno) {
    if (matriculaAluno == null || matriculaAluno.isBlank()) {
      throw new EntradaInvalidaException("Matrícula do aluno não pode ser null.");
    }

    return repository.buscarBoletinsPorAluno(matriculaAluno);
  }

  /**
   * Busca boletins pelo codigo da turma.
   *
   * @param codigoTurma codigo da turma.
   * @return boletins da turma.
   */
  public List<Boletim> buscarBoletinsPorTurma(String codigoTurma) {
    validarCodigoTurma(codigoTurma);
    return repository.buscarBoletinsPorTurma(codigoTurma);
  }

  /**
   * Lanca ou retifica notas em um boletim.
   *
   * @param matriculaAluno matricula do aluno.
   * @param codigoTurma codigo da turma.
   * @param primeiraNota primeira nota.
   * @param segundaNota segunda nota.
   * @param matriculaProfessor matricula do professor.
   */
  public void lancarNotas(
      String codigoTurma,
      String matriculaAluno,
      float primeiraNota,
      float segundaNota,
      String matriculaProfessor) {
    Boletim boletim = buscarBoletimParaLancamento(codigoTurma, matriculaAluno, matriculaProfessor);

    boletim.setPrimeiraNota(primeiraNota);
    boletim.setSegundaNota(segundaNota);
    repository.atualizarBoletins(boletim);
  }

  /**
   * Lanca ou altera apenas a primeira nota em um boletim.
   *
   * @param codigoTurma codigo da turma.
   * @param matriculaAluno matricula do aluno.
   * @param primeiraNota primeira nota.
   * @param matriculaProfessor matricula do professor.
   */
  public void lancarPrimeiraNota(
      String codigoTurma, String matriculaAluno, float primeiraNota, String matriculaProfessor) {
    Boletim boletim = buscarBoletimParaLancamento(codigoTurma, matriculaAluno, matriculaProfessor);

    boletim.setPrimeiraNota(primeiraNota);
    repository.atualizarBoletins(boletim);
  }

  /**
   * Lanca ou altera apenas a segunda nota em um boletim.
   *
   * @param codigoTurma codigo da turma.
   * @param matriculaAluno matricula do aluno.
   * @param segundaNota segunda nota.
   * @param matriculaProfessor matricula do professor.
   */
  public void lancarSegundaNota(
      String codigoTurma, String matriculaAluno, float segundaNota, String matriculaProfessor) {
    Boletim boletim = buscarBoletimParaLancamento(codigoTurma, matriculaAluno, matriculaProfessor);

    boletim.setSegundaNota(segundaNota);
    repository.atualizarBoletins(boletim);
  }

  private Boletim buscarBoletimParaLancamento(
      String codigoTurma, String matriculaAluno, String matriculaProfessor) {
    Turma turma = validarTurmaPertenceAoProfessor(codigoTurma, matriculaProfessor);
    validarAlunoMatriculadoNaTurma(turma, matriculaAluno);

    Boletim boletim = buscarBoletimPorAlunoETurma(matriculaAluno, codigoTurma);

    if (boletim == null) {
      throw new EntradaInvalidaException("Boletim do aluno nao encontrado para esta turma.");
    }

    return boletim;
  }

  private void validarBoletim(Boletim boletim) {
    if (boletim == null || boletim.getClass() != Boletim.class) {
      throw new EntradaInvalidaException("Boletim não pode ser null.");
    }
  }

  private void validarMatriculaAluno(String matriculaAluno) {
    if (matriculaAluno == null || matriculaAluno.isBlank()) {
      throw new EntradaInvalidaException("Matricula do aluno nao pode ser vazia.");
    }
  }

  private void validarCodigoTurma(String codigoTurma) {
    if (codigoTurma == null || codigoTurma.isBlank()) {
      throw new EntradaInvalidaException("Codigo da turma nao pode ser vazio.");
    }
  }

  private void validarMatriculaProfessor(String matriculaProfessor) {
    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      throw new EntradaInvalidaException("Matricula do professor logado nao pode ser vazia.");
    }
  }

  private Turma validarTurmaPertenceAoProfessor(String codigoTurma, String matriculaProfessor) {
    validarCodigoTurma(codigoTurma);
    validarMatriculaProfessor(matriculaProfessor);

    Turma turma = turmaRepository.buscarTurmaPorCodigo(codigoTurma);

    if (turma == null) {
      throw new EntradaInvalidaException("Turma nao encontrada.");
    }

    if (turma.getMatriculaProfessor() == null
        || !turma.getMatriculaProfessor().equalsIgnoreCase(matriculaProfessor.trim())) {
      throw new EntradaInvalidaException(
          "Professor nao pode lancar notas em turma de outro professor.");
    }

    return turma;
  }

  private void validarAlunoMatriculadoNaTurma(Turma turma, String matriculaAluno) {
    validarMatriculaAluno(matriculaAluno);

    if (turma.getMatriculados() == null || !turma.getMatriculados().contains(matriculaAluno)) {
      throw new EntradaInvalidaException("Aluno nao esta matriculado nesta turma.");
    }
  }

  private String gerarCodigoBoletim() {
    int contador = repository.listarBoletins().size();
    String codigo;

    do {
      codigo = "bol" + String.format("%02d", contador);
      contador++;
    } while (repository.buscarBoletimPorCodigo(codigo) != null);

    return codigo;
  }
}
