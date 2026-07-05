package br.com.classroompb.ui.tela;

import java.util.Scanner;

/**
 * Leituras comuns de entrada no terminal.
 */
public final class EntradaTela {

  private EntradaTela() {}

  /**
   * Sinaliza que o usuario cancelou o fluxo atual.
   */
  public static class EntradaCanceladaException extends RuntimeException {
    /**
     * Cria a excecao de cancelamento de entrada.
     */
    public EntradaCanceladaException() {
      super("Entrada cancelada.");
    }
  }

  /**
   * Le um texto obrigatorio.
   *
   * @param scanner leitor de entrada.
   * @param prompt texto exibido antes da leitura.
   * @param nomeCampo nome do campo para mensagem de erro.
   * @return texto informado.
   */
  public static String lerTextoObrigatorio(
      Scanner scanner, String prompt, String nomeCampo) {
    while (true) {
      System.out.print(prompt);
      String valor = scanner.nextLine();

      if (valor != null && !valor.isBlank()) {
        return valor;
      }

      System.out.println(nomeCampo + " obrigatorio. Informe novamente.");
      System.out.println();
    }
  }

  /**
   * Le um texto obrigatorio ou cancela quando o usuario digita 0.
   *
   * @param scanner leitor de entrada.
   * @param prompt texto exibido antes da leitura.
   * @param nomeCampo nome do campo para mensagem de erro.
   * @return texto informado.
   */
  public static String lerTextoObrigatorioOuCancelar(
      Scanner scanner, String prompt, String nomeCampo) {
    while (true) {
      System.out.print(prompt);
      String valor = scanner.nextLine();

      if (usuarioCancelou(valor)) {
        throw new EntradaCanceladaException();
      }

      if (valor != null && !valor.isBlank()) {
        return valor;
      }

      System.out.println(nomeCampo + " obrigatorio. Informe novamente.");
      System.out.println();
    }
  }

  /**
   * Le um inteiro maior que zero.
   *
   * @param scanner leitor de entrada.
   * @param prompt texto exibido antes da leitura.
   * @return inteiro informado.
   */
  public static int lerInteiroPositivo(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      String valor = scanner.nextLine();

      try {
        int numero = Integer.parseInt(valor);
        if (numero > 0) {
          return numero;
        }
        System.out.println("Valor invalido. Informe um numero maior que zero.");
      } catch (NumberFormatException e) {
        System.out.println("Valor invalido. Informe um numero valido.");
      }

      System.out.println();
    }
  }

  /**
   * Le um inteiro maior que zero ou cancela quando o usuario digita 0.
   *
   * @param scanner leitor de entrada.
   * @param prompt texto exibido antes da leitura.
   * @return inteiro informado.
   */
  public static int lerInteiroPositivoOuCancelar(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      String valor = scanner.nextLine();

      if (usuarioCancelou(valor)) {
        throw new EntradaCanceladaException();
      }

      try {
        int numero = Integer.parseInt(valor);
        if (numero > 0) {
          return numero;
        }
        System.out.println("Valor invalido. Informe um numero maior que zero.");
      } catch (NumberFormatException e) {
        System.out.println("Valor invalido. Informe um numero valido.");
      }

      System.out.println();
    }
  }

  /**
   * Le uma opcao numerica dentro do intervalo informado.
   *
   * @param scanner leitor de entrada.
   * @param prompt texto exibido antes da leitura.
   * @param minimo menor opcao aceita.
   * @param maximo maior opcao aceita.
   * @return opcao escolhida.
   */
  public static int lerOpcao(Scanner scanner, String prompt, int minimo, int maximo) {
    while (true) {
      System.out.print(prompt);
      String entrada = scanner.nextLine();

      try {
        int opcao = Integer.parseInt(entrada);
        if (opcao >= minimo && opcao <= maximo) {
          return opcao;
        }
      } catch (NumberFormatException e) {
        // Trata abaixo como opcao invalida.
      }

      System.out.println("Opcao invalida. Escolha uma opcao da lista.");
      System.out.println();
    }
  }

  /**
   * Le uma opcao numerica de 0 ate o maximo informado.
   *
   * @param scanner leitor de entrada.
   * @param prompt texto exibido antes da leitura.
   * @param maximo maior opcao aceita.
   * @return opcao escolhida, incluindo 0 para voltar ou cancelar.
   */
  public static int lerOpcaoOuCancelar(Scanner scanner, String prompt, int maximo) {
    return lerOpcao(scanner, prompt, 0, maximo);
  }

  private static boolean usuarioCancelou(String valor) {
    return valor != null && "0".equals(valor.trim());
  }
}
