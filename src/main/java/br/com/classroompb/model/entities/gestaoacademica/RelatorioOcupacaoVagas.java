package br.com.classroompb.model.entities.gestaoacademica;

/**
 * Representa o relatorio de ocupacao de vagas de uma turma.
 */
public class RelatorioOcupacaoVagas {

  private String codigoTurma;
  private String nomeDisciplina;
  private String periodoLetivo;
  private int limiteVagas;
  private int vagasOcupadas;
  private int vagasDisponiveis;
  private double percentualOcupacao;

  /**
   * Cria um relatorio vazio.
   */
  public RelatorioOcupacaoVagas() {}

  /**
   * Retorna o codigo da turma.
   *
   * @return codigo da turma.
   */
  public String getCodigoTurma() {
    return codigoTurma;
  }

  /**
   * Define o codigo da turma.
   *
   * @param codigoTurma codigo da turma.
   */
  public void setCodigoTurma(String codigoTurma) {
    this.codigoTurma = codigoTurma;
  }

  /**
   * Retorna o nome da disciplina.
   *
   * @return nome da disciplina.
   */
  public String getNomeDisciplina() {
    return nomeDisciplina;
  }

  /**
   * Define o nome da disciplina.
   *
   * @param nomeDisciplina nome da disciplina.
   */
  public void setNomeDisciplina(String nomeDisciplina) {
    this.nomeDisciplina = nomeDisciplina;
  }

  /**
   * Retorna o periodo letivo.
   *
   * @return periodo letivo.
   */
  public String getPeriodoLetivo() {
    return periodoLetivo;
  }

  /**
   * Define o periodo letivo.
   *
   * @param periodoLetivo periodo letivo.
   */
  public void setPeriodoLetivo(String periodoLetivo) {
    this.periodoLetivo = periodoLetivo;
  }

  /**
   * Retorna o limite de vagas.
   *
   * @return limite de vagas.
   */
  public int getLimiteVagas() {
    return limiteVagas;
  }

  /**
   * Define o limite de vagas.
   *
   * @param limiteVagas limite de vagas.
   */
  public void setLimiteVagas(int limiteVagas) {
    this.limiteVagas = limiteVagas;
  }

  /**
   * Retorna a quantidade de vagas ocupadas.
   *
   * @return quantidade de vagas ocupadas.
   */
  public int getVagasOcupadas() {
    return vagasOcupadas;
  }

  /**
   * Define a quantidade de vagas ocupadas.
   *
   * @param vagasOcupadas quantidade de vagas ocupadas.
   */
  public void setVagasOcupadas(int vagasOcupadas) {
    this.vagasOcupadas = vagasOcupadas;
  }

  /**
   * Retorna a quantidade de vagas disponiveis.
   *
   * @return quantidade de vagas disponiveis.
   */
  public int getVagasDisponiveis() {
    return vagasDisponiveis;
  }

  /**
   * Define a quantidade de vagas disponiveis.
   *
   * @param vagasDisponiveis quantidade de vagas disponiveis.
   */
  public void setVagasDisponiveis(int vagasDisponiveis) {
    this.vagasDisponiveis = vagasDisponiveis;
  }

  /**
   * Retorna o percentual de ocupacao.
   *
   * @return percentual de ocupacao.
   */
  public double getPercentualOcupacao() {
    return percentualOcupacao;
  }

  /**
   * Define o percentual de ocupacao.
   *
   * @param percentualOcupacao percentual de ocupacao.
   */
  public void setPercentualOcupacao(double percentualOcupacao) {
    this.percentualOcupacao = percentualOcupacao;
  }
}
