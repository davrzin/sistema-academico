package br.com.classroompb.ui.menu;

import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.ui.tela.CursoTela;
import br.com.classroompb.ui.tela.RelatorioUsuarioTela;
import br.com.classroompb.ui.tela.UsuarioTela;
import java.util.Scanner;

/** Menu de funcionalidades do administrador. */
public class MenuAdministrador {

  private final Scanner scanner;
  private final Administrador usuarioLogado;
  private final UsuarioTela usuarioTela;
  private final CursoTela cursoTela;
  private final RelatorioUsuarioTela relatorioUsuarioTela;

  /** Cria o menu para o administrador logado. */
  public MenuAdministrador(Administrador usuarioLogado, Scanner scanner) {
    this.usuarioLogado = usuarioLogado;
    this.scanner = scanner;
    this.usuarioTela = new UsuarioTela(scanner);
    this.cursoTela = new CursoTela(scanner);
    this.relatorioUsuarioTela = new RelatorioUsuarioTela();
  }

  /** Inicia a navegacao pelo menu do administrador. */
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
        case 1 -> menuUsuarios();
        case 2 -> menuCursos();
        case 3 -> menuRelatorios();
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

    } while (opcao != 0);
  }

  private void menuUsuarios() {
    int opcao;
    do {
      System.out.println("╔═══════════════════════════════════╗");
      System.out.println("║              USUÁRIOS             ║");
      System.out.println("╠═══════════════════════════════════╣");
      System.out.println("║ 1 - Cadastrar usuário             ║");
      System.out.println("║ 2 - Listar usuários               ║");
      System.out.println("║ 3 - Buscar usuário por matrícula  ║");
      System.out.println("║ 4 - Atualizar usuário             ║");
      System.out.println("║ 5 - Remover usuário               ║");
      System.out.println("║ 0 - Voltar                        ║");
      System.out.println("╚═══════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> usuarioTela.cadastrarUsuario();
        case 2 -> usuarioTela.listarUsuarios(usuarioLogado);
        case 3 -> usuarioTela.buscarUsuarioPorMatricula(usuarioLogado);
        case 4 -> usuarioTela.atualizarUsuario(usuarioLogado);
        case 5 -> usuarioTela.removerUsuario(usuarioLogado);
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void menuCursos() {
    int opcao;
    do {
      System.out.println("╔═══════════════════════════════════╗");
      System.out.println("║               CURSOS              ║");
      System.out.println("╠═══════════════════════════════════╣");
      System.out.println("║ 1 - Cadastrar curso               ║");
      System.out.println("║ 2 - Listar cursos                 ║");
      System.out.println("║ 0 - Voltar                        ║");
      System.out.println("╚═══════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> cursoTela.cadastrarCurso();
        case 2 -> cursoTela.listarCursos();
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void menuRelatorios() {
    int opcao;
    do {
      System.out.println("╔═══════════════════════════════════╗");
      System.out.println("║             RELATÓRIOS            ║");
      System.out.println("╠═══════════════════════════════════╣");
      System.out.println("║ 1 - Relatório geral de usuários   ║");
      System.out.println("║ 0 - Voltar                        ║");
      System.out.println("╚═══════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> relatorioUsuarioTela.gerarRelatorioGeralUsuarios(usuarioLogado);
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void imprimirMenu() {
    System.out.println("╔═══════════════════════════════════╗");
    System.out.println("║        MENU ADMINISTRADOR         ║");
    System.out.println("╠═══════════════════════════════════╣");
    System.out.println("║ 1 - Usuários                      ║");
    System.out.println("║ 2 - Cursos                        ║");
    System.out.println("║ 3 - Relatórios                    ║");
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
