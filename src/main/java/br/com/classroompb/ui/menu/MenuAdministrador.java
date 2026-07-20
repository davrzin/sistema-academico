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
        case 1 -> usuarioTela.cadastrarUsuario();
        case 2 -> usuarioTela.listarUsuarios(usuarioLogado);
        case 3 -> usuarioTela.buscarUsuarioPorMatricula(usuarioLogado);
        case 4 -> usuarioTela.atualizarUsuario(usuarioLogado);
        case 5 -> usuarioTela.removerUsuario(usuarioLogado);
        case 6 -> cursoTela.cadastrarCurso();
        case 7 -> cursoTela.listarCursos();
        case 8 -> relatorioUsuarioTela.gerarRelatorioGeralUsuarios(usuarioLogado);
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

    } while (opcao != 0);
  }

  private void imprimirMenu() {
    System.out.println("╔═══════════════════════════════════╗");
    System.out.println("║        MENU ADMINISTRADOR         ║");
    System.out.println("╠═══════════════════════════════════╣");
    System.out.println("║ 1 - Cadastrar usuário             ║");
    System.out.println("║ 2 - Listar usuários               ║");
    System.out.println("║ 3 - Buscar usuário por matrícula  ║");
    System.out.println("║ 4 - Atualizar usuário             ║");
    System.out.println("║ 5 - Remover usuário               ║");
    System.out.println("║ 6 - Cadastrar curso               ║");
    System.out.println("║ 7 - Listar cursos                 ║");
    System.out.println("║ 8 - Relatório geral de usuários   ║");
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
