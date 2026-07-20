package br.com.classroompb.model.entities.usuario;

import br.com.classroompb.model.enums.TipoUsuario;

/**
 * Representa um usuario professor do sistema.
 */
public final class Professor extends Usuario {

  private String codigoCurso;

  /**
   * Cria um professor vazio.
   */
  public Professor() {
    super();
  }

  /**
   * Cria um professor sem matricula.
   *
   * @param nome nome do professor.
   * @param email email do professor.
   * @param senha senha do professor.
   */
  public Professor(String nome, String email, String senha) {
    super(nome, email, senha, TipoUsuario.PROFESSOR);
  }

  /**
   * Cria um professor com matricula.
   *
   * @param nome nome do professor.
   * @param email email do professor.
   * @param matricula matricula do professor.
   * @param senha senha do professor.
   */
  public Professor(String nome, String email, String matricula, String senha) {
    super(nome, email, matricula, senha, TipoUsuario.PROFESSOR);
  }

  /**
   * Cria um professor com curso.
   *
   * @param nome nome do professor.
   * @param email email do professor.
   * @param matricula matricula do professor.
   * @param senha senha do professor.
   * @param codigoCurso codigo do curso.
   */
  public Professor(String nome, String email, String matricula, String senha, String codigoCurso) {
    super(nome, email, matricula, senha, TipoUsuario.PROFESSOR);
    setCodigoCurso(codigoCurso);
  }

  /**
   * Cria um professor com tipo informado.
   *
   * @param nome nome do professor.
   * @param email email do professor.
   * @param matricula matricula do professor.
   * @param senha senha do professor.
   * @param tipoUsuario tipo do usuario.
   */
  public Professor(
      String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
    super(nome, email, matricula, senha, TipoUsuario.PROFESSOR);
  }

  /**
   * Cria um professor completo.
   *
   * @param nome nome do professor.
   * @param email email do professor.
   * @param matricula matricula do professor.
   * @param senha senha do professor.
   * @param tipoUsuario tipo do usuario.
   * @param codigoCurso codigo do curso.
   */
  public Professor(
      String nome,
      String email,
      String matricula,
      String senha,
      TipoUsuario tipoUsuario,
      String codigoCurso) {
    super(nome, email, matricula, senha, TipoUsuario.PROFESSOR);
    setCodigoCurso(codigoCurso);
  }

  /**
   * Retorna o codigo do curso.
   *
   * @return codigo do curso.
   */
  public String getCodigoCurso() {
    return codigoCurso;
  }

  /**
   * Define o codigo do curso.
   *
   * @param codigoCurso codigo do curso.
   */
  public void setCodigoCurso(String codigoCurso) {
    this.codigoCurso = codigoCurso;
  }
}
