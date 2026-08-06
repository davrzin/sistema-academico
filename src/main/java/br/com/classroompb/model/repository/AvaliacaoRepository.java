package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Avaliacao;
import br.com.classroompb.model.entities.gestaoacademica.NotaAvaliacao;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoRepository {

  private ObjectMapper objectMapper;
  private final String diretorio;

  public AvaliacaoRepository(ObjectMapper objectMapper, String diretorio) {
    this.objectMapper = objectMapper;
    this.diretorio = diretorio;
  }

  public void salvarAvaliacao(Avaliacao avaliacao) {
    List<Avaliacao> lista = listarAvaliacoes();
    lista.add(avaliacao);
    salvarListaAvaliacoes(lista);
  }

  public List<Avaliacao> listarAvaliacoes() {
    File arquivo = new File(getCaminhoArquivo("avaliacoes.json"));
    RepositoryJsonFiles.garantirArquivoLista(arquivo);
    try {
      return new ArrayList<>(
          objectMapper.readValue(
              arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, Avaliacao.class)));
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler avaliações.", e);
    }
  }

  public List<Avaliacao> buscarPorDiario(String codigoDiario) {
    List<Avaliacao> result = new ArrayList<>();
    for (Avaliacao a : listarAvaliacoes()) {
      if (a.getCodigoDiario() != null && a.getCodigoDiario().equalsIgnoreCase(codigoDiario.trim())) {
        result.add(a);
      }
    }
    return result;
  }

  public void salvarNota(NotaAvaliacao nota) {
    List<NotaAvaliacao> notas = listarNotas();
    notas.removeIf(
        n ->
            n.getCodigoAvaliacao().equalsIgnoreCase(nota.getCodigoAvaliacao())
                && n.getMatriculaAluno().equalsIgnoreCase(nota.getMatriculaAluno()));
    notas.add(nota);
    salvarListaNotas(notas);
  }

  public List<NotaAvaliacao> listarNotas() {
    File arquivo = new File(getCaminhoArquivo("notas_avaliacoes.json"));
    RepositoryJsonFiles.garantirArquivoLista(arquivo);
    try {
      return new ArrayList<>(
          objectMapper.readValue(
              arquivo,
              objectMapper.getTypeFactory().constructCollectionType(List.class, NotaAvaliacao.class)));
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler notas de avaliações.", e);
    }
  }

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
    if (!dir.exists()) dir.mkdirs();
    return new File(dir, nome).getPath();
  }
}