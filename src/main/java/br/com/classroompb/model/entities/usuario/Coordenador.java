package br.com.classroompb.model.entities.usuario;

import br.com.classroompb.model.enums.TipoUsuario;

/**
 * Representa um usuario coordenador do sistema.
 */
public class Coordenador extends Usuario {

  private String codigoCurso;

  /**
   * Cria um coordenador vazio.
   */
  public Coordenador() {
    super();
  }

  /**
   * Cria um coordenador sem matricula.
   *
   * @param nome nome do coordenador.
   * @param email email do coordenador.
   * @param senha senha do coordenador.
   */
  public Coordenador(String nome, String email, String senha) {
    super(nome, email, senha, TipoUsuario.COORDENADOR);
  }

  /**
   * Cria um coordenador com matricula.
   *
   * @param nome nome do coordenador.
   * @param email email do coordenador.
   * @param matricula matricula do coordenador.
   * @param senha senha do coordenador.
   */
  public Coordenador(String nome, String email, String matricula, String senha) {
    super(nome, email, matricula, senha, TipoUsuario.COORDENADOR);
  }

  /**
   * Cria um coordenador com curso.
   *
   * @param nome nome do coordenador.
   * @param email email do coordenador.
   * @param matricula matricula do coordenador.
   * @param senha senha do coordenador.
   * @param codigoCurso codigo do curso.
   */
  public Coordenador(
      String nome, String email, String matricula, String senha, String codigoCurso) {
    super(nome, email, matricula, senha, TipoUsuario.COORDENADOR);
    setCodigoCurso(codigoCurso);
  }

  /**
   * Cria um coordenador com tipo informado.
   *
   * @param nome nome do coordenador.
   * @param email email do coordenador.
   * @param matricula matricula do coordenador.
   * @param senha senha do coordenador.
   * @param tipoUsuario tipo do usuario.
   */
  public Coordenador(
      String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
    super(nome, email, matricula, senha, TipoUsuario.COORDENADOR);
  }

  /**
   * Cria um coordenador completo.
   *
   * @param nome nome do coordenador.
   * @param email email do coordenador.
   * @param matricula matricula do coordenador.
   * @param senha senha do coordenador.
   * @param tipoUsuario tipo do usuario.
   * @param codigoCurso codigo do curso.
   */
  public Coordenador(
      String nome,
      String email,
      String matricula,
      String senha,
      TipoUsuario tipoUsuario,
      String codigoCurso) {
    super(nome, email, matricula, senha, TipoUsuario.COORDENADOR);
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
