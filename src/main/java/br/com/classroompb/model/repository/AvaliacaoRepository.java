package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Avaliacao;
import br.com.classroompb.model.entities.gestaoacademica.NotaAvaliacao;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório responsável por persistir e consultar avaliações e notas de avaliações.
 */
public class AvaliacaoRepository {

  private ObjectMapper objectMapper;
  private final String diretorio;

  /**
   * Cria o repositório de avaliações.
   *
   * @param objectMapper mapeador JSON utilizado para leitura e escrita
   * @param diretorio diretório onde os arquivos de dados são armazenados
   */
  public AvaliacaoRepository(ObjectMapper objectMapper, String diretorio) {
    this.objectMapper = objectMapper;
    this.diretorio = diretorio;
  }

  /**
   * Salva uma nova avaliação.
   *
   * @param avaliacao avaliação a ser salva
   */
  public void salvarAvaliacao(Avaliacao avaliacao) {
    List<Avaliacao> lista = listarAvaliacoes();
    lista.add(avaliacao);
    salvarListaAvaliacoes(lista);
  }

  /**
   * Lista todas as avaliações cadastradas.
   *
   * @return lista de avaliações
   */
  public List<Avaliacao> listarAvaliacoes() {
    File arquivo = new File(getCaminhoArquivo("avaliacoes.json"));
    RepositoryJsonFiles.garantirArquivoLista(arquivo);
    try {
      return new ArrayList<>(
          objectMapper.readValue(
              arquivo,
              objectMapper.getTypeFactory().constructCollectionType(List.class, Avaliacao.class)));
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler avaliações.", e);
    }
  }

  /**
   * Busca as avaliações associadas a um diário.
   *
   * @param codigoDiario código do diário
   * @return lista de avaliações do diário informado
   */
  public List<Avaliacao> buscarPorDiario(String codigoDiario) {
    List<Avaliacao> result = new ArrayList<>();
    for (Avaliacao a : listarAvaliacoes()) {
      boolean pertenceAoDiario =
          a.getCodigoDiario() != null && a.getCodigoDiario().equalsIgnoreCase(codigoDiario.trim());
      if (pertenceAoDiario) {
        result.add(a);
      }
    }
    return result;
  }

  /**
   * Salva ou atualiza a nota de um aluno em uma avaliação.
   *
   * @param nota nota a ser salva
   */
  public void salvarNota(NotaAvaliacao nota) {
    List<NotaAvaliacao> notas = listarNotas();
    notas.removeIf(
        n ->
            n.getCodigoAvaliacao().equalsIgnoreCase(nota.getCodigoAvaliacao())
                && n.getMatriculaAluno().equalsIgnoreCase(nota.getMatriculaAluno()));
    notas.add(nota);
    salvarListaNotas(notas);
  }

  /**
   * Lista todas as notas de avaliações cadastradas.
   *
   * @return lista de notas de avaliações
   */
  public List<NotaAvaliacao> listarNotas() {
    File arquivo = new File(getCaminhoArquivo("notas_avaliacoes.json"));
    RepositoryJsonFiles.garantirArquivoLista(arquivo);
    try {
      return new ArrayList<>(
          objectMapper.readValue(
              arquivo,
              objectMapper
                  .getTypeFactory()
                  .constructCollectionType(List.class, NotaAvaliacao.class)));
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler notas de avaliações.", e);
    }
  }

  /**
   * Busca a nota de um aluno em uma avaliação específica.
   *
   * @param codigoAvaliacao código da avaliação
   * @param matriculaAluno matrícula do aluno
   * @return valor da nota, ou {@code null} caso não exista
   */
  public Double buscarNotaDoAluno(String codigoAvaliacao, String matriculaAluno) {
    for (NotaAvaliacao n : listarNotas()) {
      if (n.getCodigoAvaliacao().equalsIgnoreCase(codigoAvaliacao)
          && n.getMatriculaAluno().equalsIgnoreCase(matriculaAluno)) {
        return n.getValorNota();
      }
    }
    return null;
  }

  private void salvarListaAvaliacoes(List<Avaliacao> lista) {
    try {
      objectMapper.writeValue(new File(getCaminhoArquivo("avaliacoes.json")), lista);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar avaliações.", e);
    }
  }

  private void salvarListaNotas(List<NotaAvaliacao> lista) {
    try {
      objectMapper.writeValue(new File(getCaminhoArquivo("notas_avaliacoes.json")), lista);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar notas de avaliações.", e);
    }
  }

  private String getCaminhoArquivo(String nome) {
    File dir = new File(diretorio);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return new File(dir, nome).getPath();
  }
}