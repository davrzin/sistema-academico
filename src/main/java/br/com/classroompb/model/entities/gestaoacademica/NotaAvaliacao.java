package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

/**
 * Representa a nota obtida por um aluno em uma avaliacao especifica.
 */
public class NotaAvaliacao {

  private String codigoAvaliacao;
  private String matriculaAluno;
  private double valorNota;

  /**
   * Construtor padrão.
   */
  public NotaAvaliacao() {}

  /**
   * Cria uma nota de avaliação.
   *
   * @param codigoAvaliacao código da avaliação relacionada
   * @param matriculaAluno matrícula do aluno avaliado
   * @param valorNota valor da nota obtida
   */
  public NotaAvaliacao(String codigoAvaliacao, String matriculaAluno, double valorNota) {
    setCodigoAvaliacao(codigoAvaliacao);
    setMatriculaAluno(matriculaAluno);
    setValorNota(valorNota);
  }

  public String getCodigoAvaliacao() {
    return codigoAvaliacao;
  }

  /**
   * Define o código da avaliação relacionada.
   *
   * @param codigoAvaliacao código da avaliação
   */
  public void setCodigoAvaliacao(String codigoAvaliacao) {
    if (codigoAvaliacao == null || codigoAvaliacao.isBlank()) {
      throw new EntradaInvalidaException("Código da avaliação não pode ser vazio.");
    }
    this.codigoAvaliacao = codigoAvaliacao;
  }

  public String getMatriculaAluno() {
    return matriculaAluno;
  }

  /**
   * Define a matrícula do aluno avaliado.
   *
   * @param matriculaAluno matrícula do aluno
   */
  public void setMatriculaAluno(String matriculaAluno) {
    if (matriculaAluno == null || matriculaAluno.isBlank()) {
      throw new EntradaInvalidaException("Matrícula do aluno não pode ser vazia.");
    }
    this.matriculaAluno = matriculaAluno;
  }

  public double getValorNota() {
    return valorNota;
  }

  /**
   * Define o valor da nota obtida.
   *
   * @param valorNota valor da nota, não pode ser negativo
   */
  public void setValorNota(double valorNota) {
    if (valorNota < 0) {
      throw new EntradaInvalidaException("A nota não pode ser negativa.");
    }
    this.valorNota = valorNota;
  }
}
