package br.com.classroompb.ui.menu;

import br.com.classroompb.ui.tela.LoginTela;
import br.com.classroompb.ui.tela.UsuarioTela;
import java.util.Scanner;

/** Menu inicial do sistema academico. */
public class MenuPrincipal {

  private final Scanner scanner = new Scanner(System.in);
  private final LoginTela loginTela = new LoginTela(scanner);
  private final UsuarioTela usuarioTela = new UsuarioTela(scanner);

  /** Inicia a navegacao pelo menu principal. */
  public void iniciar() {
    int opcao;
    boolean primeiraExibicao = true;

    do {
      if (!primeiraExibicao) {
        System.out.println();
      }

      imprimirMenu();
      primeiraExibicao = false;

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1:
          loginTela.login();
          break;

        case 2:
          usuarioTela.cadastrarUsuario();
          break;

        case 0:
          System.out.println("Fechando o sistema.");
          break;

        default:
          System.out.println("Opção inválida.");
      }

    } while (opcao != 0);
  }

  private void imprimirMenu() {
    System.out.println("╔══════════════════════════════╗");
    System.out.println("║      SISTEMA ACADÊMICO       ║");
    System.out.println("╠══════════════════════════════╣");
    System.out.println("║ 1 - Login                    ║");
    System.out.println("║ 2 - Cadastrar usuário        ║");
    System.out.println("║ 0 - Sair                     ║");
    System.out.println("╚══════════════════════════════╝");
  }

  private int lerOpcao() {
    System.out.print("Digite uma opção: ");

    try {
      return Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      return -1;
    }
  }
}
