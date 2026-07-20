package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsavel pela persistencia de cursos em arquivo JSON.
 */
public class CursoRepository {

  private static final String DIRETORIO_CURSOS = PersistenciaPaths.CURSOS.toString();

  private ObjectMapper objectMapper;
  private final String diretorioCursos;

  /**
   * Cria o repositorio de cursos com diretorio padrao.
   *
   * @param objectMapper mapeador JSON.
   */
  public CursoRepository(ObjectMapper objectMapper) {
    this(objectMapper, DIRETORIO_CURSOS);
  }

  /**
   * Cria o repositorio de cursos com diretorio informado.
   *
   * @param objectMapper mapeador JSON.
   * @param diretorioCursos diretorio dos cursos.
   */
  public CursoRepository(ObjectMapper objectMapper, String diretorioCursos) {
    this.objectMapper = objectMapper;
    this.diretorioCursos = diretorioCursos;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String getDiretorioCursos() {
    return diretorioCursos;
  }

  /**
   * Salva um curso no arquivo JSON.
   *
   * @param curso curso a ser salvo.
   * @return verdadeiro se o curso foi salvo.
   */
  public boolean salvarCurso(Curso curso) {
    if (curso == null) {
      throw new IllegalArgumentException("Curso não pode ser nulo.");
    }

    String caminhoArquivo = this.getCaminhoArquivo();

    List<Curso> cursos = this.listarCursos();

    cursos.add(curso);

    try {
      this.objectMapper.writeValue(new File(caminhoArquivo), cursos);

      return true;
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar curso.", e);
    }
  }

  /**
   * Atualiza um curso no arquivo JSON.
   *
   * @param cursoAtualizado curso com dados atualizados.
   * @return verdadeiro se o curso foi atualizado.
   */
  public boolean atualizarCurso(Curso cursoAtualizado) {
    if (cursoAtualizado == null) {
      throw new IllegalArgumentException("Curso nao pode ser nulo.");
    }

    List<Curso> cursos = listarCursos();

    for (int i = 0; i < cursos.size(); i++) {
      Curso curso = cursos.get(i);

      if (curso.getCodigo() != null
          && curso.getCodigo().equalsIgnoreCase(cursoAtualizado.getCodigo())) {
        cursos.set(i, cursoAtualizado);

        try {
          objectMapper.writeValue(new File(getCaminhoArquivo()), cursos);
          return true;
        } catch (IOException e) {
          throw new PersistenciaException("Erro ao atualizar curso.", e);
        }
      }
    }

    return false;
  }

  /**
   * Remove um curso pelo codigo.
   *
   * @param codigo codigo do curso.
   * @return verdadeiro quando o curso foi removido.
   */
  public boolean removerPorCodigo(String codigo) {
    List<Curso> cursos = listarCursos();
    boolean removido =
        cursos.removeIf(
            curso -> curso.getCodigo() != null && curso.getCodigo().equalsIgnoreCase(codigo));

    if (!removido) {
      return false;
    }

    try {
      objectMapper.writeValue(new File(getCaminhoArquivo()), cursos);
      return true;
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao remover curso.", e);
    }
  }

  /**
   * Lista todos os cursos cadastrados.
   *
   * @return lista de cursos cadastrados.
   */
  public List<Curso> listarCursos() {

    File arquivo = new File(getCaminhoArquivo());
    RepositoryJsonFiles.garantirArquivoLista(arquivo);

    try {
      return lerCursos(arquivo, Curso.class);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler cursos.", e);
    }
  }

  /**
   * Busca um curso pelo codigo.
   *
   * @param codigo codigo do curso.
   * @return curso encontrado.
   */
  public Curso buscarPorCodigo(String codigo) {
    List<Curso> cursos = this.listarCursos();
    for (Curso curso : cursos) {
      if (curso.getCodigo().equalsIgnoreCase(codigo)) {
        return curso;
      }
    }
    return null;
  }

  /**
   * Busca um curso pelo nome.
   *
   * @param nome nome do curso.
   * @return curso encontrado.
   */
  public Curso buscarPorNome(String nome) {
    List<Curso> cursos = this.listarCursos();
    for (Curso curso : cursos) {
      if (curso.getNome().equalsIgnoreCase(nome)) {
        return curso;
      }
    }
    return null;
  }

  private String getCaminhoArquivo() {
    File diretorio = new File(diretorioCursos);
    if (!diretorio.exists()) {
      diretorio.mkdirs();
    }
    return new File(diretorio, "cursos.json").getPath();
  }

  private List<Curso> lerCursos(File arquivo, Class<Curso> tipo) throws IOException {
    return new ArrayList<>(
        objectMapper.readValue(
            arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
  }
}
