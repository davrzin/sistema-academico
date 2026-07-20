package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsavel pela persistencia de disciplinas em arquivo JSON.
 */
public class DisciplinaRepository {

  private static final String DIRETORIO_DISCIPLINAS = PersistenciaPaths.DISCIPLINAS.toString();

  private ObjectMapper objectMapper;
  private final String diretorioDisciplinas;

  /**
   * Cria o repositorio de disciplinas com diretorio padrao.
   *
   * @param objectMapper mapeador JSON.
   */
  public DisciplinaRepository(ObjectMapper objectMapper) {
    this(objectMapper, DIRETORIO_DISCIPLINAS);
  }

  /**
   * Cria o repositorio de disciplinas com diretorio informado.
   *
   * @param objectMapper mapeador JSON.
   * @param diretorioDisciplinas diretorio das disciplinas.
   */
  public DisciplinaRepository(ObjectMapper objectMapper, String diretorioDisciplinas) {
    this.objectMapper = objectMapper;
    this.diretorioDisciplinas = diretorioDisciplinas;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String getDiretorioDisciplinas() {
    return diretorioDisciplinas;
  }

  /**
   * Salva uma disciplina no arquivo JSON.
   *
   * @param disciplina disciplina a ser salva.
   */
  public void salvarDisciplina(Disciplina disciplina) {
    if (disciplina == null) {
      throw new IllegalArgumentException("Disciplina não pode ser nula.");
    }

    String caminhoArquivo = this.getCaminhoArquivo();
    List<Disciplina> disciplinas = this.listarDisciplinas();
    disciplinas.add(disciplina);

    try {
      this.objectMapper.writeValue(new File(caminhoArquivo), disciplinas);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar disciplina.", e);
    }
  }

  /**
   * Lista todas as disciplinas cadastradas.
   *
   * @return lista de disciplinas cadastradas.
   */
  public List<Disciplina> listarDisciplinas() {
    File arquivo = new File(this.getCaminhoArquivo());
    RepositoryJsonFiles.garantirArquivoLista(arquivo);

    try {
      return this.lerDisciplinas(arquivo, Disciplina.class);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler disciplinas.", e);
    }
  }

  /**
   * Busca uma disciplina pelo codigo.
   *
   * @param codigo codigo da disciplina.
   * @return disciplina encontrada.
   */
  public Disciplina buscarPorCodigo(String codigo) {
    List<Disciplina> disciplinas = this.listarDisciplinas();

    for (Disciplina disciplina : disciplinas) {
      if (disciplina.getCodigo().equalsIgnoreCase(codigo)) {
        return disciplina;
      }
    }

    return null;
  }

  /**
   * Busca uma disciplina pelo nome.
   *
   * @param nome nome da disciplina.
   * @return disciplina encontrada.
   */
  public Disciplina buscarPorNome(String nome) {
    List<Disciplina> disciplinas = this.listarDisciplinas();

    for (Disciplina disciplina : disciplinas) {
      if (disciplina.getNome().equalsIgnoreCase(nome)) {
        return disciplina;
      }
    }

    return null;
  }

  /**
   * Busca disciplinas pelo codigo do curso.
   *
   * @param codigoCurso codigo do curso.
   * @return lista de disciplinas do curso.
   */
  public List<Disciplina> buscarPorCurso(String codigoCurso) {
    List<Disciplina> disciplinas = this.listarDisciplinas();
    List<Disciplina> disciplinasCurso = new ArrayList<>();

    for (Disciplina disciplina : disciplinas) {
      if (disciplina.getCodigoCurso().equalsIgnoreCase(codigoCurso)) {
        disciplinasCurso.add(disciplina);
      }
    }

    return disciplinasCurso;
  }

  private String getCaminhoArquivo() {
    File diretorio = new File(diretorioDisciplinas);

    if (!diretorio.exists()) {
      diretorio.mkdirs();
    }

    return new File(diretorio, "disciplinas.json").getPath();
  }

  private List<Disciplina> lerDisciplinas(File arquivo, Class<Disciplina> tipo)
      throws IOException {
    return new ArrayList<>(
        objectMapper.readValue(
            arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
  }
}
