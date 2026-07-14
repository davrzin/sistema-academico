package br.com.classroompb.application;

import br.com.classroompb.ui.menu.MenuPrincipal;
import java.util.Scanner;

/**
 * Classe de entrada da aplicacao.
 */
public class Program {
  /**
   * Inicia a aplicacao.
   *
   * @param args argumentos de linha de comando.
   */
  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);
    MenuPrincipal menuPrincipal = new MenuPrincipal(scanner);
    menuPrincipal.iniciar();
  }
}
