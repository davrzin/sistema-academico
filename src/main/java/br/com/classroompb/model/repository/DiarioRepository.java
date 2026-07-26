package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Responsavel pela persistencia de diarios em arquivo JSON.
 */
public class DiarioRepository {

  private static final String DIRETORIO_DIARIOS = PersistenciaPaths.DIARIOS.toString();

  private ObjectMapper objectMapper;
  private final String diretorioDiarios;

  /**
   * Cria o repositorio de diarios com diretorio padrao.
   *
   * @param objectMapper mapeador JSON.
   */
  public DiarioRepository(ObjectMapper objectMapper) {
    this(objectMapper, DIRETORIO_DIARIOS);
  }

  /**
   * Cria o repositorio de diarios com diretorio informado.
   *
   * @param objectMapper mapeador JSON.
   * @param diretorioDiarios diretorio dos diarios.
   */
  public DiarioRepository(ObjectMapper objectMapper, String diretorioDiarios) {
    this.objectMapper = objectMapper;
    this.diretorioDiarios = diretorioDiarios;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String getDiretorioDiarios() {
    return diretorioDiarios;
  }

  /**
   * Salva um diario no arquivo JSON.
   *
   * @param diario diario a ser salvo.
   */
  public void salvarDiario(Diario diario) {
    if (diario == null) {
      throw new IllegalArgumentException("Diário não pode ser nulo.");
    }

    List<Diario> diarios = listarDiarios();
    diarios.add(diario);
    salvarListaDiarios(diarios);
  }

  /**
   * Atualiza um diario no arquivo JSON.
   *
   * @param diarioAtualizado diario com dados atualizados.
   * @return verdadeiro se o diario foi atualizado.
   */
  public boolean atualizarDiario(Diario diarioAtualizado) {
    if (diarioAtualizado == null) {
      throw new IllegalArgumentException("Diário não pode ser nulo.");
    }

    List<Diario> diarios = listarDiarios();

    for (int i = 0; i < diarios.size(); i++) {
      Diario diario = diarios.get(i);

      if (diario.getCodigo() != null
          && diario.getCodigo().equalsIgnoreCase(diarioAtualizado.getCodigo())) {
        diarios.set(i, diarioAtualizado);
        salvarListaDiarios(diarios);
        return true;
      }
    }

    return false;
  }

  /**
   * Remove um diario pelo codigo.
   *
   * @param codigo codigo do diario.
   * @return verdadeiro se o diario foi removido.
   */
  public boolean removerDiarioPorCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      return false;
    }

    List<Diario> diarios = listarDiarios();
    Iterator<Diario> iterator = diarios.iterator();

    while (iterator.hasNext()) {
      Diario diario = iterator.next();

      if (diario.getCodigo() != null && diario.getCodigo().equalsIgnoreCase(codigo.trim())) {
        iterator.remove();
        salvarListaDiarios(diarios);
        return true;
      }
    }

    return false;
  }

  /**
   * Lista todos os diarios cadastrados.
   *
   * @return lista de diarios cadastrados.
   */
  public List<Diario> listarDiarios() {
    File arquivo = new File(getCaminhoArquivo());
    RepositoryJsonFiles.garantirArquivoLista(arquivo);

    try {
      return lerDiarios(arquivo, Diario.class);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler diários.", e);
    }
  }

  /**
   * Busca um diario pelo codigo.
   *
   * @param codigo codigo do diario.
   * @return diario encontrado.
   */
  public Diario buscarDiarioPorCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      return null;
    }

    for (Diario diario : listarDiarios()) {
      if (diario.getCodigo() != null
          && diario.getCodigo().trim().equalsIgnoreCase(codigo.trim())) {
        return diario;
      }
    }

    return null;
  }

  /**
   * Busca diarios pelo codigo da turma associada.
   *
   * @param codigoTurma codigo da turma.
   * @return lista de diarios da turma.
   */
  public List<Diario> buscarDiariosPorTurma(String codigoTurma) {
    validarCodigoTurma(codigoTurma);

    List<Diario> diariosDaTurma = new ArrayList<>();

    for (Diario diario : listarDiarios()) {
      if (diario.getCodigoTurma() != null
          && diario.getCodigoTurma().equalsIgnoreCase(codigoTurma.trim())) {
        diariosDaTurma.add(diario);
      }
    }

    return diariosDaTurma;
  }

  /**
   * Busca diarios pela matricula do professor responsavel.
   *
   * @param matriculaProfessor matricula do professor.
   * @return lista de diarios do professor.
   */
  public List<Diario> buscarDiariosPorMatriculaDeProfessor(String matriculaProfessor) {
    List<Diario> diariosDoProfessor = new ArrayList<>();

    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      return diariosDoProfessor;
    }

    for (Diario diario : listarDiarios()) {
      if (diario.getMatriculaProfessor() != null
          && diario.getMatriculaProfessor().equalsIgnoreCase(matriculaProfessor.trim())) {
        diariosDoProfessor.add(diario);
      }
    }

    return diariosDoProfessor;
  }

  private void validarCodigoTurma(String codigoTurma) {
    if (codigoTurma == null || codigoTurma.isBlank()) {
      throw new EntradaInvalidaException("Código de turma não pode ser vazio.");
    }
  }

  private void salvarListaDiarios(List<Diario> diarios) {
    String caminhoArquivo = getCaminhoArquivo();

    try {
      objectMapper.writeValue(new File(caminhoArquivo), diarios);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar diários.", e);
    }
  }

  private String getCaminhoArquivo() {
    File diretorio = new File(diretorioDiarios);

    if (!diretorio.exists()) {
      diretorio.mkdirs();
    }

    return new File(diretorio, "diarios.json").getPath();
  }

  private List<Diario> lerDiarios(File arquivo, Class<Diario> tipo) throws IOException {
    return new ArrayList<>(
        objectMapper.readValue(
            arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
  }
}
