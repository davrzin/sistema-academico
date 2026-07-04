package br.com.classroompb.application;

import br.com.classroompb.ui.menu.MenuPrincipal;

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

    MenuPrincipal menuPrincipal = new MenuPrincipal();
    menuPrincipal.iniciar();
  }
}
