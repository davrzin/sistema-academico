package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Responsavel pela persistencia de turmas em arquivo JSON.
 */
public class TurmaRepository {

  private static final String DIRETORIO_TURMAS = PersistenciaPaths.TURMAS.toString();

  private ObjectMapper objectMapper;
  private final String diretorioTurmas;

  /**
   * Cria o repositorio de turmas com diretorio padrao.
   *
   * @param objectMapper mapeador JSON.
   */
  public TurmaRepository(ObjectMapper objectMapper) {
    this(objectMapper, DIRETORIO_TURMAS);
  }

  /**
   * Cria o repositorio de turmas com diretorio informado.
   *
   * @param objectMapper mapeador JSON.
   * @param diretorioTurmas diretorio das turmas.
   */
  public TurmaRepository(ObjectMapper objectMapper, String diretorioTurmas) {
    this.objectMapper = objectMapper;
    this.diretorioTurmas = diretorioTurmas;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String getDiretorioTurmas() {
    return diretorioTurmas;
  }

  /**
   * Salva uma turma no arquivo JSON.
   *
   * @param turma turma a ser salva.
   */
  public void salvarTurma(Turma turma) {
    if (turma == null) {
      throw new IllegalArgumentException("Turma não pode ser nula.");
    }

    List<Turma> turmas = listarTurmas();
    turmas.add(turma);
    salvarListaTurmas(turmas);
  }

  /**
   * Atualiza uma turma no arquivo JSON.
   *
   * @param turmaAtualizada turma com dados atualizados.
   * @return verdadeiro se a turma foi atualizada.
   */
  public boolean atualizarTurma(Turma turmaAtualizada) {
    if (turmaAtualizada == null) {
      throw new IllegalArgumentException("Turma não pode ser nula.");
    }

    List<Turma> turmas = listarTurmas();

    for (int i = 0; i < turmas.size(); i++) {
      Turma turma = turmas.get(i);

      if (turma.getCodigo() != null
          && turma.getCodigo().equalsIgnoreCase(turmaAtualizada.getCodigo())) {
        turmas.set(i, turmaAtualizada);
        salvarListaTurmas(turmas);
        return true;
      }
    }

    return false;
  }

  /**
   * Remove uma turma pelo codigo.
   *
   * @param codigo codigo da turma.
   * @return verdadeiro se a turma foi removida.
   */
  public boolean removerTurmaPorCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      return false;
    }

    List<Turma> turmas = listarTurmas();
    Iterator<Turma> iterator = turmas.iterator();

    while (iterator.hasNext()) {
      Turma turma = iterator.next();

      if (turma.getCodigo() != null && turma.getCodigo().equalsIgnoreCase(codigo.trim())) {
        iterator.remove();
        salvarListaTurmas(turmas);
        return true;
      }
    }

    return false;
  }

  /**
   * Lista todas as turmas cadastradas.
   *
   * @return lista de turmas cadastradas.
   */
  public List<Turma> listarTurmas() {
    File arquivo = new File(getCaminhoArquivo());
    RepositoryJsonFiles.garantirArquivoLista(arquivo);

    try {
      return lerTurmas(arquivo, Turma.class);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler turmas.", e);
    }
  }

  /**
   * Busca uma turma pelo codigo.
   *
   * @param codigo codigo da turma.
   * @return turma encontrada.
   */
  public Turma buscarTurmaPorCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      return null;
    }

    for (Turma turma : listarTurmas()) {
      if (turma.getCodigo() != null && turma.getCodigo().trim().equalsIgnoreCase(codigo.trim())) {
        return turma;
      }
    }

    return null;
  }

  /**
   * Busca turmas pela matricula do professor.
   *
   * @param matriculaProfessor matricula do professor.
   * @return lista de turmas do professor.
   */
  public List<Turma> buscarTurmaPorMatriculaDeProfessor(String matriculaProfessor) {
    List<Turma> turmasDoProfessor = new ArrayList<>();

    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      return turmasDoProfessor;
    }

    for (Turma turma : listarTurmas()) {
      if (turma.getMatriculaProfessor() != null
          && turma.getMatriculaProfessor().equalsIgnoreCase(matriculaProfessor.trim())) {
        turmasDoProfessor.add(turma);
      }
    }

    return turmasDoProfessor;
  }

  /**
   * Busca turmas pelo periodo letivo.
   *
   * @param periodoLetivo periodo letivo.
   * @return lista de turmas do periodo.
   */
  public List<Turma> buscarTurmaPorPeriodoLetivo(String periodoLetivo) {
    List<Turma> turmasDoPeriodo = new ArrayList<>();

    if (periodoLetivo == null || periodoLetivo.isBlank()) {
      return turmasDoPeriodo;
    }

    for (Turma turma : listarTurmas()) {
      if (turma.getPeriodoLetivo() != null
          && turma.getPeriodoLetivo().equalsIgnoreCase(periodoLetivo.trim())) {
        turmasDoPeriodo.add(turma);
      }
    }

    return turmasDoPeriodo;
  }

  /**
   * Busca as aulas de uma turma.
   *
   * @param codigoTurma codigo da turma.
   * @return lista de aulas da turma.
   */
  public List<String> buscarAulasDeTurma(String codigoTurma) {
    validarCodigoTurma(codigoTurma);

    Turma turma = buscarTurmaPorCodigo(codigoTurma);

    if (turma == null) {
      return new ArrayList<>();
    }

    return turma.getAulas();
  }

  private void validarCodigoTurma(String codigoTurma) {
    if (codigoTurma == null || codigoTurma.isBlank()) {
      throw new EntradaInvalidaException("Código de turma não pode ser null.");
    }
  }

  private void salvarListaTurmas(List<Turma> turmas) {
    String caminhoArquivo = getCaminhoArquivo();

    try {
      objectMapper.writeValue(new File(caminhoArquivo), turmas);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar turmas.", e);
    }
  }

  private String getCaminhoArquivo() {
    File diretorio = new File(diretorioTurmas);

    if (!diretorio.exists()) {
      diretorio.mkdirs();
    }

    return new File(diretorio, "turmas.json").getPath();
  }

  private List<Turma> lerTurmas(File arquivo, Class<Turma> tipo) throws IOException {
    return new ArrayList<>(
        objectMapper.readValue(
            arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
  }
}
