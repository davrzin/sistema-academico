package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Avaliacao;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.NotaAvaliacao;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AvaliacaoRepository;
import br.com.classroompb.model.repository.DiarioRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class AvaliacaoService {

  private final AvaliacaoRepository avaliacaoRepository;
  private final DiarioRepository diarioRepository;

  public AvaliacaoService() {
    this.avaliacaoRepository =
        new AvaliacaoRepository(new ObjectMapper(), PersistenciaPaths.DIARIOS.toString());
    this.diarioRepository =
        new DiarioRepository(new ObjectMapper(), PersistenciaPaths.DIARIOS.toString());
  }

  public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, DiarioRepository diarioRepository) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.diarioRepository = diarioRepository;
  }

  public void cadastrarAvaliacao(Avaliacao avaliacao) {
    validarDiarioAtivo(avaliacao.getCodigoDiario());

    if (avaliacao.getNotaMaxima() <= 0 || avaliacao.getNotaMaxima() > 10.0) {
      throw new EntradaInvalidaException("Nota máxima inválida (deve ser entre 0.1 e 10.0).");
    }

    int total = avaliacaoRepository.listarAvaliacoes().size();
    avaliacao.setCodigo("avl" + String.format("%02d", total));
    avaliacaoRepository.salvarAvaliacao(avaliacao);
  }

  public List<Avaliacao> listarAvaliacoesPorDiario(String codigoDiario) {
    return avaliacaoRepository.buscarPorDiario(codigoDiario);
  }

  public void lancarNota(String codigoAvaliacao, String matriculaAluno, double nota) {
    Avaliacao av = buscarAvaliacaoPorCodigo(codigoAvaliacao);
    validarDiarioAtivo(av.getCodigoDiario());

    if (nota < 0 || nota > av.getNotaMaxima()) {
      throw new EntradaInvalidaException(
          "A nota deve estar entre 0.0 e a nota máxima (" + av.getNotaMaxima() + ").");
    }

    avaliacaoRepository.salvarNota(new NotaAvaliacao(codigoAvaliacao, matriculaAluno, nota));
  }

  public Double buscarNotaDoAluno(String codigoAvaliacao, String matriculaAluno) {
    return avaliacaoRepository.buscarNotaDoAluno(codigoAvaliacao, matriculaAluno);
  }

  private Avaliacao buscarAvaliacaoPorCodigo(String codigo) {
    for (Avaliacao a : avaliacaoRepository.listarAvaliacoes()) {
      if (a.getCodigo().equalsIgnoreCase(codigo)) return a;
    }
    throw new EntradaInvalidaException("Avaliação não encontrada.");
  }

  private void validarDiarioAtivo(String codigoDiario) {
    Diario d = diarioRepository.buscarDiarioPorCodigo(codigoDiario);
    if (d == null) {
      throw new EntradaInvalidaException("Diário não encontrado.");
    }
    if (d.getSituacao() != SituacaoDiario.ATIVO) {
      throw new EntradaInvalidaException("Não é possível alterar avaliações em diário fechado/cancelado.");
    }
  }
}