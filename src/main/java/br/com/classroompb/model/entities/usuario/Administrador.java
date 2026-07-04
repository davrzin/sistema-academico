package br.com.classroompb.model.entities.usuario;

import br.com.classroompb.model.enums.TipoUsuario;

/**
 * Representa um usuario administrador do sistema.
 */
public class Administrador extends Usuario {

  /**
   * Cria um administrador vazio.
   */
  public Administrador() {
    super();
  }

  /**
   * Cria um administrador sem matricula.
   *
   * @param nome nome do administrador.
   * @param email email do administrador.
   * @param senha senha do administrador.
   */
  public Administrador(String nome, String email, String senha) {
    super(nome, email, senha, TipoUsuario.ADMINISTRADOR);
  }

  /**
   * Cria um administrador com matricula.
   *
   * @param nome nome do administrador.
   * @param email email do administrador.
   * @param matricula matricula do administrador.
   * @param senha senha do administrador.
   */
  public Administrador(String nome, String email, String matricula, String senha) {
    super(nome, email, matricula, senha, TipoUsuario.ADMINISTRADOR);
  }

  /**
   * Cria um administrador com tipo informado.
   *
   * @param nome nome do administrador.
   * @param email email do administrador.
   * @param matricula matricula do administrador.
   * @param senha senha do administrador.
   * @param tipoUsuario tipo do usuario.
   */
  public Administrador(
      String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
    super(nome, email, matricula, senha, TipoUsuario.ADMINISTRADOR);
  }
}
