package br.com.classroompb.model.exception;

/**
 * Excecao lancada quando ocorre falha de persistencia.
 */
public class PersistenciaException extends RuntimeException {

  /**
   * Cria a excecao com mensagem e causa.
   *
   * @param message mensagem da excecao.
   * @param causa causa da excecao.
   */
  public PersistenciaException(String message, Throwable causa) {
    super(message, causa);
  }
}
