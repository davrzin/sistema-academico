package br.com.classroompb.model.exception;

/**
 * Excecao lancada quando ja existe um periodo ativo.
 */
public class ExistePeriodoAtivoException extends RuntimeException {
  /**
   * Cria a excecao com mensagem.
   *
   * @param message mensagem da excecao.
   */
  public ExistePeriodoAtivoException(String message) {
    super(message);
  }

  /**
   * Cria a excecao com mensagem padrao.
   */
  public ExistePeriodoAtivoException() {
    this("Já existe um período ativo no momento.");
  }
}
