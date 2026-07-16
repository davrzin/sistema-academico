package br.com.classroompb.model.entities.gestaoacademica;

/**
 * Representa um usuario listado no relatorio geral de usuarios.
 */
public class ItemRelatorioUsuario {

  private String tipoUsuario;
  private String nome;
  private String matricula;
  private String email;
  private String codigoCurso;

  /**
   * Cria um item vazio de relatorio.
   */
  public ItemRelatorioUsuario() {}

  /**
   * Retorna o tipo do usuario.
   *
   * @return tipo do usuario.
   */
  public String getTipoUsuario() {
    return tipoUsuario;
  }

  /**
   * Define o tipo do usuario.
   *
   * @param tipoUsuario tipo do usuario.
   */
  public void setTipoUsuario(String tipoUsuario) {
    this.tipoUsuario = tipoUsuario;
  }

  /**
   * Retorna o nome do usuario.
   *
   * @return nome do usuario.
   */
  public String getNome() {
    return nome;
  }

  /**
   * Define o nome do usuario.
   *
   * @param nome nome do usuario.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * Retorna a matricula do usuario.
   *
   * @return matricula do usuario.
   */
  public String getMatricula() {
    return matricula;
  }

  /**
   * Define a matricula do usuario.
   *
   * @param matricula matricula do usuario.
   */
  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  /**
   * Retorna o email do usuario.
   *
   * @return email do usuario.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Define o email do usuario.
   *
   * @param email email do usuario.
   */
  public void setEmail(String email) {
    this.email = email;
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
