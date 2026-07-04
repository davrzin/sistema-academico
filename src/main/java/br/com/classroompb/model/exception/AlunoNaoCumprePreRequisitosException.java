package br.com.classroompb.model.exception;

/**
 * Excecao lancada quando o aluno nao cumpre os pre-requisitos.
 */
public class AlunoNaoCumprePreRequisitosException extends RuntimeException {
  /**
   * Cria a excecao com mensagem.
   *
   * @param message mensagem da excecao.
   */
  public AlunoNaoCumprePreRequisitosException(String message) {
    super(message);
  }
}
