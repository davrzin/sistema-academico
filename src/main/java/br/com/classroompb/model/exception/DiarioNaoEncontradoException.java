package br.com.classroompb.model.exception;

/**
 * Excecao lancada quando o diario nao e encontrado.
 */
public class DiarioNaoEncontradoException extends RuntimeException {
  /**
   * Cria a excecao com mensagem.
   *
   * @param message mensagem da excecao.
   */
  public DiarioNaoEncontradoException(String message) {
    super(message);
  }

  /**
   * Cria a excecao com mensagem padrao.
   */
  public DiarioNaoEncontradoException() {
    super("Diário não encontrado");
  }
}
