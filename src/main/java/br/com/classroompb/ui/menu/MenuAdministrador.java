package br.com.classroompb.ui.menu;

import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.ui.tela.CursoTela;
import br.com.classroompb.ui.tela.UsuarioTela;
import java.util.Scanner;

/** Menu de funcionalidades do administrador. */
public class MenuAdministrador {

  private final Scanner scanner = new Scanner(System.in);
  private final Administrador usuarioLogado;
  private final UsuarioTela usuarioTela = new UsuarioTela(scanner);
  private final CursoTela cursoTela = new CursoTela(scanner);

  /** Cria o menu para o administrador logado. */
  public MenuAdministrador(Administrador usuarioLogado) {
    this.usuarioLogado = usuarioLogado;
  }

  /** Inicia a navegacao pelo menu do administrador. */
  public void iniciar() {
    int opcao;

    do {
      imprimirMenu();

      opcao = lerOpcao();

      switch (opcao) {
        case 1:
          usuarioTela.cadastrarUsuario();
          break;

        case 2:
          usuarioTela.listarUsuarios(usuarioLogado);
          break;

        case 3:
          usuarioTela.buscarUsuarioPorMatricula(usuarioLogado);
          break;

        case 4:
          usuarioTela.atualizarUsuario(usuarioLogado);
          break;

        case 5:
          usuarioTela.removerUsuario(usuarioLogado);
          break;

        case 6:
          cursoTela.cadastrarCurso();
          break;

        case 7:
          cursoTela.listarCursos();
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
    System.out.println("║        MENU ADMINISTRADOR         ║");
    System.out.println("╠═══════════════════════════════════╣");
    System.out.println("║ 1 - Cadastrar usuário             ║");
    System.out.println("║ 2 - Listar usuários               ║");
    System.out.println("║ 3 - Buscar usuário por matrícula  ║");
    System.out.println("║ 4 - Atualizar usuário             ║");
    System.out.println("║ 5 - Remover usuário               ║");
    System.out.println("║ 6 - Cadastrar curso               ║");
    System.out.println("║ 7 - Listar cursos                 ║");
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
