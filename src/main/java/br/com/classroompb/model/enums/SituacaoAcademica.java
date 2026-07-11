package br.com.classroompb.model.enums;

/** Situacoes academicas apresentadas no boletim e no historico. */
public enum SituacaoAcademica {
  EM_ANDAMENTO("Em andamento"),
  APROVADO("Aprovado"),
  REPROVADO_POR_NOTA("Reprovado por nota"),
  REPROVADO_POR_FALTA("Reprovado por falta"),
  EM_RECUPERACAO("Em recuperação");

  private final String descricao;

  SituacaoAcademica(String descricao) {
    this.descricao = descricao;
  }

  /** Retorna o texto exibido ao usuario. */
  public String getDescricao() {
    return descricao;
  }
}
