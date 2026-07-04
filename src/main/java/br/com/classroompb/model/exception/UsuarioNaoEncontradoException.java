package br.com.classroompb.model.exception;

/**
 * Excecao lancada quando o usuario nao e encontrado.
 */
public class UsuarioNaoEncontradoException extends RuntimeException {
  /**
   * Cria a excecao com mensagem.
   *
   * @param message mensagem da excecao.
   */
  public UsuarioNaoEncontradoException(String message) {
    super(message);
  }

  /**
   * Cria a excecao com mensagem padrao.
   */
  public UsuarioNaoEncontradoException() {
    this("Usuário não foi encontrado");
  }
}
