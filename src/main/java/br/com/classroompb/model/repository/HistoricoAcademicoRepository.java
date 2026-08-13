package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.ItemHistoricoAcademico;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Responsavel pela persistencia dos resultados consolidados do historico academico. */
public class HistoricoAcademicoRepository {

  private static final String DIRETORIO_HISTORICOS = PersistenciaPaths.HISTORICOS.toString();
  private final ObjectMapper objectMapper;
  private final String diretorioHistoricos;

  /**
   * Cria o repositorio com o diretorio padrao.
   *
   * @param objectMapper mapeador JSON.
   */
  public HistoricoAcademicoRepository(ObjectMapper objectMapper) {
    this(objectMapper, DIRETORIO_HISTORICOS);
  }

  /**
   * Cria o repositorio com um diretorio informado.
   *
   * @param objectMapper mapeador JSON.
   * @param diretorioHistoricos diretorio dos historicos.
   */
  public HistoricoAcademicoRepository(ObjectMapper objectMapper, String diretorioHistoricos) {
    if (objectMapper == null) {
      throw new EntradaInvalidaException("Mapeador JSON não pode ser nulo.");
    }
    if (diretorioHistoricos == null || diretorioHistoricos.isBlank()) {
      throw new EntradaInvalidaException("Diretório de históricos não pode ser vazio.");
    }
    this.objectMapper = objectMapper;
    this.diretorioHistoricos = diretorioHistoricos;
  }

  /**
   * Lista todos os registros persistidos.
   *
   * @return registros do historico.
   */
  public List<ItemHistoricoAcademico> listarHistoricos() {
    File arquivo = getArquivoHistoricos();
    RepositoryJsonFiles.garantirArquivoLista(arquivo);

    try {
      return new ArrayList<>(
          objectMapper.readValue(
              arquivo,
              objectMapper
                  .getTypeFactory()
                  .constructCollectionType(List.class, ItemHistoricoAcademico.class)));
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler históricos acadêmicos.", e);
    }
  }

  /**
   * Busca os registros de um aluno.
   *
   * @param matriculaAluno matricula do aluno.
   * @return registros do aluno.
   */
  public List<ItemHistoricoAcademico> buscarPorAluno(String matriculaAluno) {
    validarCampoChave(matriculaAluno, "Matrícula do aluno não pode ser vazia.");
    String matriculaNormalizada = matriculaAluno.trim();

    return listarHistoricos().stream()
        .filter(
            item ->
                item.getMatriculaAluno() != null
                    && item.getMatriculaAluno().equalsIgnoreCase(matriculaNormalizada))
        .toList();
  }

  /**
   * Busca o registro identificado por aluno e turma.
   *
   * @param matriculaAluno matricula do aluno.
   * @param codigoTurma codigo da turma.
   * @return registro encontrado ou {@code null}.
   */
  public ItemHistoricoAcademico buscarPorAlunoTurma(
      String matriculaAluno, String codigoTurma) {
    validarChave(matriculaAluno, codigoTurma);
    String matriculaNormalizada = matriculaAluno.trim();
    String turmaNormalizada = codigoTurma.trim();

    for (ItemHistoricoAcademico item : listarHistoricos()) {
      if (mesmaChave(item, matriculaNormalizada, turmaNormalizada)) {
        return item;
      }
    }
    return null;
  }

  /**
   * Inclui ou atualiza um registro pela chave logica de aluno e turma.
   *
   * @param item registro consolidado.
   */
  public void salvarOuAtualizar(ItemHistoricoAcademico item) {
    if (item == null) {
      throw new EntradaInvalidaException("Item do histórico não pode ser nulo.");
    }
    validarChave(item.getMatriculaAluno(), item.getCodigoTurma());

    List<ItemHistoricoAcademico> historicos = listarHistoricos();
    String matriculaNormalizada = item.getMatriculaAluno().trim();
    String turmaNormalizada = item.getCodigoTurma().trim();
    item.setMatriculaAluno(matriculaNormalizada);
    item.setCodigoTurma(turmaNormalizada);
    boolean atualizou = false;

    for (int i = 0; i < historicos.size(); i++) {
      if (mesmaChave(historicos.get(i), matriculaNormalizada, turmaNormalizada)) {
        historicos.set(i, item);
        atualizou = true;
        break;
      }
    }
    if (!atualizou) {
      historicos.add(item);
    }
    salvarLista(historicos);
  }

  private boolean mesmaChave(
      ItemHistoricoAcademico item, String matriculaAluno, String codigoTurma) {
    return item != null
        && item.getMatriculaAluno() != null
        && item.getCodigoTurma() != null
        && item.getMatriculaAluno().trim().equalsIgnoreCase(matriculaAluno)
        && item.getCodigoTurma().trim().equalsIgnoreCase(codigoTurma);
  }

  private void validarChave(String matriculaAluno, String codigoTurma) {
    validarCampoChave(matriculaAluno, "Matrícula do aluno não pode ser vazia.");
    validarCampoChave(codigoTurma, "Código da turma não pode ser vazio.");
  }

  private void validarCampoChave(String valor, String mensagem) {
    if (valor == null || valor.isBlank()) {
      throw new EntradaInvalidaException(mensagem);
    }
  }

  private void salvarLista(List<ItemHistoricoAcademico> historicos) {
    File arquivo = getArquivoHistoricos();
    RepositoryJsonFiles.garantirArquivoLista(arquivo);
    try {
      objectMapper.writeValue(arquivo, historicos);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar históricos acadêmicos.", e);
    }
  }

  private File getArquivoHistoricos() {
    return new File(diretorioHistoricos, "historicos.json");
  }
}
