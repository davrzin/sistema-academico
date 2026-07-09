package br.com.classroompb.model.entities.gestaoacademica;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o relatorio de alunos matriculados em uma turma.
 */
public class RelatorioAlunosTurma {

  private String codigoTurma;
  private String nomeDisciplina;
  private String periodoLetivo;
  private String nomeProfessor;
  private int limiteVagas;
  private int totalMatriculados;
  private List<ItemRelatorioAlunoTurma> alunos = new ArrayList<>();

  /**
   * Cria um relatorio vazio.
   */
  public RelatorioAlunosTurma() {}

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
   * Retorna o nome do professor.
   *
   * @return nome do professor.
   */
  public String getNomeProfessor() {
    return nomeProfessor;
  }

  /**
   * Define o nome do professor.
   *
   * @param nomeProfessor nome do professor.
   */
  public void setNomeProfessor(String nomeProfessor) {
    this.nomeProfessor = nomeProfessor;
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
   * Retorna o total de matriculados.
   *
   * @return total de matriculados.
   */
  public int getTotalMatriculados() {
    return totalMatriculados;
  }

  /**
   * Define o total de matriculados.
   *
   * @param totalMatriculados total de matriculados.
   */
  public void setTotalMatriculados(int totalMatriculados) {
    this.totalMatriculados = totalMatriculados;
  }

  /**
   * Retorna os alunos do relatorio.
   *
   * @return alunos do relatorio.
   */
  public List<ItemRelatorioAlunoTurma> getAlunos() {
    return alunos;
  }

  /**
   * Define os alunos do relatorio.
   *
   * @param alunos alunos do relatorio.
   */
  public void setAlunos(List<ItemRelatorioAlunoTurma> alunos) {
    if (alunos == null) {
      this.alunos = new ArrayList<>();
      return;
    }

    this.alunos = new ArrayList<>(alunos);
  }
}
