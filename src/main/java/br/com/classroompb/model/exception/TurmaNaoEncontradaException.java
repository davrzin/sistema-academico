package br.com.classroompb.model.exception;

/**
 * Excecao lancada quando a turma nao e encontrada.
 */
public class TurmaNaoEncontradaException extends RuntimeException {
  /**
   * Cria a excecao com mensagem.
   *
   * @param message mensagem da excecao.
   */
  public TurmaNaoEncontradaException(String message) {
    super(message);
  }

  /**
   * Cria a excecao com mensagem padrao.
   */
  public TurmaNaoEncontradaException() {
    super("Turma não encontrada");
  }
}
