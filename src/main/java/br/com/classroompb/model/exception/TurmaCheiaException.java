package br.com.classroompb.model.exception;

/**
 * Excecao lancada quando a turma esta cheia.
 */
public class TurmaCheiaException extends RuntimeException {
  /**
   * Cria a excecao com mensagem.
   *
   * @param message mensagem da excecao.
   */
  public TurmaCheiaException(String message) {
    super(message);
  }

  /**
   * Cria a excecao com mensagem padrao.
   */
  public TurmaCheiaException() {
    super("A turma na qual você deseja entrar está cheia");
  }
}
