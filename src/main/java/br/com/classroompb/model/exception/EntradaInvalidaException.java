package br.com.classroompb.model.exception;

/**
 * Excecao lancada quando uma entrada invalida e informada.
 */
public class EntradaInvalidaException extends RuntimeException {
  /**
   * Cria a excecao com mensagem.
   *
   * @param message mensagem da excecao.
   */
  public EntradaInvalidaException(String message) {
    super(message);
  }
}
