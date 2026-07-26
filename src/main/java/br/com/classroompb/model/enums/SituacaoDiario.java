package br.com.classroompb.model.enums;

/** Situacoes possiveis de um diario de turma. */
public enum SituacaoDiario {
  ATIVO("Ativo"),
  ENCERRADO("Encerrado"),
  CANCELADO("Cancelado");

  private final String descricao;

  SituacaoDiario(String descricao) {
    this.descricao = descricao;
  }

  /** Retorna o texto exibido ao usuario. */
  public String getDescricao() {
    return descricao;
  }
}
