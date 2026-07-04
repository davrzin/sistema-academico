package br.com.classroompb.model.exception;

/**
 * Excecao lancada quando um periodo letivo ja existe.
 */
public class PeriodoLetivoExistenteException extends RuntimeException {
  /**
   * Cria a excecao com mensagem.
   *
   * @param message mensagem da excecao.
   */
  public PeriodoLetivoExistenteException(String message) {
    super(message);
  }

  /**
   * Cria a excecao com mensagem padrao.
   */
  public PeriodoLetivoExistenteException() {
    this("Já existe um período letivo com essas informações.");
  }
}
