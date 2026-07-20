package br.com.classroompb.model.entities.usuario;

import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;

/**
 * Representa um usuario base do sistema.
 */
public abstract class Usuario {

  private String nome;
  private String email;
  private String matricula;
  private String senha;
  private TipoUsuario tipoUsuario;

  /**
   * Cria um usuario vazio para subclasses.
   */
  protected Usuario() {}

  /**
   * Cria um usuario sem matricula.
   *
   * @param nome nome do usuario.
   * @param email email do usuario.
   * @param senha senha do usuario.
   * @param tipoUsuario tipo do usuario.
   */
  public Usuario(String nome, String email, String senha, TipoUsuario tipoUsuario) {
    setNome(nome);
    setEmail(email);
    setSenha(senha);
    setTipoUsuario(tipoUsuario);
  }

  /**
   * Cria um usuario com matricula.
   *
   * @param nome nome do usuario.
   * @param email email do usuario.
   * @param matricula matricula do usuario.
   * @param senha senha do usuario.
   * @param tipoUsuario tipo do usuario.
   */
  public Usuario(
      String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
    setNome(nome);
    setEmail(email);
    setMatricula(matricula);
    setSenha(senha);
    setTipoUsuario(tipoUsuario);
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
  public final void setNome(String nome) {
    validarNome(nome);
    this.nome = nome;
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
  public final void setEmail(String email) {
    validarEmail(email);
    this.email = email;
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
  public final void setMatricula(String matricula) {
    validarMatricula(matricula);
    this.matricula = matricula;
  }

  /**
   * Retorna a senha do usuario.
   *
   * @return senha do usuario.
   */
  public String getSenha() {
    return senha;
  }

  /**
   * Define a senha do usuario.
   *
   * @param senha senha do usuario.
   */
  public final void setSenha(String senha) {
    validarSenha(senha);
    this.senha = senha;
  }

  /**
   * Retorna o tipo do usuario.
   *
   * @return tipo do usuario.
   */
  public TipoUsuario getTipoUsuario() {
    return tipoUsuario;
  }

  /**
   * Define o tipo do usuario.
   *
   * @param tipoUsuario tipo do usuario.
   */
  public final void setTipoUsuario(TipoUsuario tipoUsuario) {
    validarTipoUsuario(tipoUsuario);
    this.tipoUsuario = tipoUsuario;
  }

  /**
   * Valida os dados basicos do usuario.
   */
  public void validarDadosBasicos() {
    validarNome(nome);
    validarEmail(email);
    validarSenha(senha);
    validarTipoUsuario(tipoUsuario);
  }

  /**
   * Valida os dados do usuario com matricula.
   */
  public void validarDadosComMatricula() {
    validarDadosBasicos();
    validarMatricula(matricula);
  }

  private void validarNome(String nome) {
    if (nome == null || nome.isBlank()) {
      throw new EntradaInvalidaException("Nome do usuário não pode ser vazio.");
    }
  }

  private void validarEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new EntradaInvalidaException("E-mail do usuário não pode ser vazio.");
    }

    if (!email.contains("@") || !email.contains(".")) {
      throw new EntradaInvalidaException("E-mail inválido.");
    }
  }

  private void validarMatricula(String matricula) {
    if (matricula == null || matricula.isBlank()) {
      throw new EntradaInvalidaException("Matrícula do usuário não pode ser vazia.");
    }
  }

  private void validarSenha(String senha) {
    if (senha == null || senha.isBlank()) {
      throw new EntradaInvalidaException("Senha do usuário não pode ser vazia.");
    }
  }

  private void validarTipoUsuario(TipoUsuario tipoUsuario) {
    if (tipoUsuario == null) {
      throw new EntradaInvalidaException("Tipo de usuário inválido.");
    }
  }
}
