package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsavel pela persistencia de aulas em arquivo JSON.
 */
public class AulaRepository {
  private static final String DIRETORIO_AULAS = PersistenciaPaths.AULAS.toString();

  private ObjectMapper objectMapper;
  private final String diretorioAulas;

  /**
   * Cria o repositorio de aulas com diretorio padrao.
   *
   * @param objectMapper mapeador JSON.
   */
  public AulaRepository(ObjectMapper objectMapper) {
    this(objectMapper, DIRETORIO_AULAS);
  }

  /**
   * Cria o repositorio de aulas com diretorio informado.
   *
   * @param objectMapper mapeador JSON.
   * @param diretorioAulas diretorio das aulas.
   */
  public AulaRepository(ObjectMapper objectMapper, String diretorioAulas) {
    this.objectMapper = objectMapper;
    this.diretorioAulas = diretorioAulas;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String getDiretorioAulas() {
    return diretorioAulas;
  }

  /**
   * Salva uma aula no arquivo JSON.
   *
   * @param aula aula a ser salva.
   * @return verdadeiro se a aula foi salva.
   */
  public boolean salvarAula(Aula aula) {
    if (aula == null) {
      throw new IllegalArgumentException("Aula não pode ser nula.");
    }

    List<Aula> aulas = listarAulas();
    aulas.add(aula);
    salvarListaAulas(aulas);

    return true;
  }

  /**
   * Atualiza uma aula no arquivo JSON.
   *
   * @param aulaAtualizada aula com dados atualizados.
   * @return verdadeiro se a aula foi atualizada.
   */
  public boolean atualizarAula(Aula aulaAtualizada) {
    if (aulaAtualizada == null) {
      throw new IllegalArgumentException("Aula não pode ser nula.");
    }

    List<Aula> aulas = listarAulas();

    for (int i = 0; i < aulas.size(); i++) {
      Aula aula = aulas.get(i);

      if (aula.getId() != null && aula.getId().equalsIgnoreCase(aulaAtualizada.getId())) {
        aulas.set(i, aulaAtualizada);
        salvarListaAulas(aulas);
        return true;
      }
    }

    return false;
  }

  private void salvarListaAulas(List<Aula> aulas) {
    String caminhoArquivo = getCaminhoArquivo();

    try {
      objectMapper.writeValue(new File(caminhoArquivo), aulas);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar aulas.", e);
    }
  }

  /**
   * Lista todas as aulas cadastradas.
   *
   * @return lista de aulas cadastradas.
   */
  public List<Aula> listarAulas() {
    File arquivo = new File(getCaminhoArquivo());
    RepositoryJsonFiles.garantirArquivoLista(arquivo);

    try {
      return lerAulas(arquivo, Aula.class);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler aulas.", e);
    }
  }

  /**
   * Busca uma aula pelo identificador.
   *
   * @param id identificador da aula.
   * @return aula encontrada.
   * @throws EntradaInvalidaException quando a aula nao e encontrada.
   */
  public Aula buscarAulaPorId(String id) throws EntradaInvalidaException {
    if (id == null || id.isBlank()) {
      throw new EntradaInvalidaException("Código de aula não pode ser vazio.");
    }

    for (Aula aula : listarAulas()) {
      if (aula.getId() != null && aula.getId().trim().equalsIgnoreCase(id.trim())) {
        return aula;
      }
    }

    return null;
  }

  private String getCaminhoArquivo() {
    File diretorio = new File(diretorioAulas);

    if (!diretorio.exists()) {
      diretorio.mkdirs();
    }

    return new File(diretorio, "aulas.json").getPath();
  }

  private <T extends Aula> List<Aula> lerAulas(File arquivo, Class<T> tipo) throws IOException {
    return new ArrayList<>(
        objectMapper.readValue(
            arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
  }
}
