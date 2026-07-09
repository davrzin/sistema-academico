package br.com.classroompb.model.entities.gestaoacademica;

/**
 * Representa um aluno listado no relatorio de alunos por turma.
 */
public class ItemRelatorioAlunoTurma {

  private String matricula;
  private String nome;
  private String email;

  /**
   * Cria um item vazio de relatorio.
   */
  public ItemRelatorioAlunoTurma() {}

  /**
   * Retorna a matricula do aluno.
   *
   * @return matricula do aluno.
   */
  public String getMatricula() {
    return matricula;
  }

  /**
   * Define a matricula do aluno.
   *
   * @param matricula matricula do aluno.
   */
  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  /**
   * Retorna o nome do aluno.
   *
   * @return nome do aluno.
   */
  public String getNome() {
    return nome;
  }

  /**
   * Define o nome do aluno.
   *
   * @param nome nome do aluno.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * Retorna o email do aluno.
   *
   * @return email do aluno.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Define o email do aluno.
   *
   * @param email email do aluno.
   */
  public void setEmail(String email) {
    this.email = email;
  }
}
