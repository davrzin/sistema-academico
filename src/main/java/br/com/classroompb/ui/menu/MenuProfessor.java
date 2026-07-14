package br.com.classroompb.ui.menu;

import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.ui.tela.TurmaTela;
import br.com.classroompb.ui.tela.UsuarioTela;
import java.util.Scanner;

/** Menu de funcionalidades do professor. */
public class MenuProfessor {

  private final Scanner scanner;
  private final Professor usuarioLogado;
  private final TurmaTela turmaTela;
  private final UsuarioTela usuarioTela;

  /** Cria o menu para o professor logado. */
  public MenuProfessor(Professor usuarioLogado, Scanner scanner) {
    this.usuarioLogado = usuarioLogado;
    this.scanner = scanner;
    this.turmaTela = new TurmaTela(scanner);
    this.usuarioTela = new UsuarioTela(scanner);
  }

  /** Inicia a navegacao pelo menu do professor. */
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
          turmaTela.listarMinhasTurmas(usuarioLogado);
          break;

        case 2:
          usuarioTela.listarUsuarios(usuarioLogado);
          break;

        case 3:
          usuarioTela.buscarUsuarioPorMatricula(usuarioLogado);
          break;

        case 4:
          turmaTela.adicionarNotas(usuarioLogado);
          break;

        case 5:
          turmaTela.adicionarFrequencia(usuarioLogado);
          break;

        case 6:
          System.out.println("Funcionalidade de listar diário ainda não implementada.");
          break;

        case 0:
          System.out.println("Voltando...");
          break;

        default:
          System.out.println("Opção inválida.");
      }

    } while (opcao != 0);
  }

  private void imprimirMenu() {
    System.out.println("╔═══════════════════════════════════╗");
    System.out.println("║          MENU PROFESSOR           ║");
    System.out.println("╠═══════════════════════════════════╣");
    System.out.println("║ 1 - Listar minhas turmas          ║");
    System.out.println("║ 2 - Listar alunos                 ║");
    System.out.println("║ 3 - Buscar aluno por matrícula    ║");
    System.out.println("║ 4 - Lançar notas                  ║");
    System.out.println("║ 5 - Lançar frequência             ║");
    System.out.println("║ 6 - Listar diário                 ║");
    System.out.println("║ 0 - Voltar                        ║");
    System.out.println("╚═══════════════════════════════════╝");
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
