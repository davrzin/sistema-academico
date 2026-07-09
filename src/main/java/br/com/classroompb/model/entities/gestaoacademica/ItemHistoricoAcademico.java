package br.com.classroompb.model.entities.gestaoacademica;

/**
 * Representa uma linha do historico academico de um aluno.
 */
public class ItemHistoricoAcademico {

  private String matriculaAluno;
  private String nomeAluno;
  private String periodoLetivo;
  private String codigoTurma;
  private String codigoDisciplina;
  private String nomeDisciplina;
  private String nomeProfessor;
  private Double notaFinal;
  private Double frequencia;
  private String situacao;

  /**
   * Cria um item vazio de historico academico.
   */
  public ItemHistoricoAcademico() {}

  public String getMatriculaAluno() {
    return matriculaAluno;
  }

  public void setMatriculaAluno(String matriculaAluno) {
    this.matriculaAluno = matriculaAluno;
  }

  public String getNomeAluno() {
    return nomeAluno;
  }

  public void setNomeAluno(String nomeAluno) {
    this.nomeAluno = nomeAluno;
  }

  public String getPeriodoLetivo() {
    return periodoLetivo;
  }

  public void setPeriodoLetivo(String periodoLetivo) {
    this.periodoLetivo = periodoLetivo;
  }

  public String getCodigoTurma() {
    return codigoTurma;
  }

  public void setCodigoTurma(String codigoTurma) {
    this.codigoTurma = codigoTurma;
  }

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

  public String getNomeProfessor() {
    return nomeProfessor;
  }

  public void setNomeProfessor(String nomeProfessor) {
    this.nomeProfessor = nomeProfessor;
  }

  public Double getNotaFinal() {
    return notaFinal;
  }

  public void setNotaFinal(Double notaFinal) {
    this.notaFinal = notaFinal;
  }

  public Double getFrequencia() {
    return frequencia;
  }

  public void setFrequencia(Double frequencia) {
    this.frequencia = frequencia;
  }

  public String getSituacao() {
    return situacao;
  }

  public void setSituacao(String situacao) {
    this.situacao = situacao;
  }
}
