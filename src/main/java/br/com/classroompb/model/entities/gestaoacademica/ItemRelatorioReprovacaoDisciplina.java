package br.com.classroompb.model.entities.gestaoacademica;

/**
 * Representa uma disciplina no relatorio de reprovacao.
 */
public class ItemRelatorioReprovacaoDisciplina {

  private String codigoDisciplina;
  private String nomeDisciplina;
  private int totalResultadosFinais;
  private int totalReprovadosPorNota;
  private int totalReprovadosPorFalta;
  private int totalReprovados;
  private double percentualReprovacao;

  /**
   * Cria um item vazio de relatorio.
   */
  public ItemRelatorioReprovacaoDisciplina() {}

  public String getCodigoDisciplina() {
    return codigoDisciplina;
  }

  public void setCodigoDisciplina(String codigoDisciplina) {
    this.codigoDisciplina = codigoDisciplina;
  }

  public String getNomeDisciplina() {
    return nomeDisciplina;
  }

  public void setNomeDisciplina(String nomeDisciplina) {
    this.nomeDisciplina = nomeDisciplina;
  }

  public int getTotalResultadosFinais() {
    return totalResultadosFinais;
  }

  public void setTotalResultadosFinais(int totalResultadosFinais) {
    this.totalResultadosFinais = totalResultadosFinais;
  }

  public int getTotalReprovadosPorNota() {
    return totalReprovadosPorNota;
  }

  public void setTotalReprovadosPorNota(int totalReprovadosPorNota) {
    this.totalReprovadosPorNota = totalReprovadosPorNota;
  }

  public int getTotalReprovadosPorFalta() {
    return totalReprovadosPorFalta;
  }

  public void setTotalReprovadosPorFalta(int totalReprovadosPorFalta) {
    this.totalReprovadosPorFalta = totalReprovadosPorFalta;
  }

  public int getTotalReprovados() {
    return totalReprovados;
  }

  public void setTotalReprovados(int totalReprovados) {
    this.totalReprovados = totalReprovados;
  }

  public double getPercentualReprovacao() {
    return percentualReprovacao;
  }

  public void setPercentualReprovacao(double percentualReprovacao) {
    this.percentualReprovacao = percentualReprovacao;
  }
}
