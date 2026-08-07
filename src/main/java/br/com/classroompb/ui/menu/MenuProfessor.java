package br.com.classroompb.ui.menu;

import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.ui.tela.AvaliacaoTela;
import br.com.classroompb.ui.tela.DiarioTela;
import br.com.classroompb.ui.tela.FrequenciaTela;
import br.com.classroompb.ui.tela.TurmaTela;
import br.com.classroompb.ui.tela.UsuarioTela;
import java.util.Scanner;

/** Menu de funcionalidades do professor. */
public class MenuProfessor {

  private final Scanner scanner;
  private final Professor usuarioLogado;
  private final AvaliacaoTela avaliacaoTela;
  private final DiarioTela diarioTela;
  private final FrequenciaTela frequenciaTela;
  private final TurmaTela turmaTela;
  private final UsuarioTela usuarioTela;

  /** Cria o menu para o professor logado. */
  public MenuProfessor(Professor usuarioLogado, Scanner scanner) {
    this.usuarioLogado = usuarioLogado;
    this.scanner = scanner;
    this.avaliacaoTela = new AvaliacaoTela(scanner);
    this.diarioTela = new DiarioTela(scanner);
    this.frequenciaTela = new FrequenciaTela(scanner);
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
        case 1 -> menuTurmas();
        case 2 -> menuAlunos();
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

    } while (opcao != 0);
  }

  private void menuTurmas() {
    int opcao;
    do {
      System.out.println("╔═══════════════════════════════════╗");
      System.out.println("║               TURMAS              ║");
      System.out.println("╠═══════════════════════════════════╣");
      System.out.println("║ 1 - Listar minhas turmas          ║");
      System.out.println("║ 2 - Avaliações e notas            ║");
      System.out.println("║ 3 - Lançar frequência             ║");
      System.out.println("║ 4 - Listar diário                 ║");
      System.out.println("║ 5 - Consultar frequência          ║");
      System.out.println("║ 6 - Consultar frequência por aula ║");
      System.out.println("║ 7 - Consultar notas por avaliação ║");
      System.out.println("║ 8 - Encerrar diário                ║");
      System.out.println("║ 0 - Voltar                        ║");
      System.out.println("╚═══════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> turmaTela.listarMinhasTurmas(usuarioLogado);
        case 2 -> menuAvaliacoesENotas();
        case 3 -> turmaTela.adicionarFrequencia(usuarioLogado);
        case 4 -> diarioTela.listarDiariosDoProfessor(usuarioLogado);
        case 5 -> frequenciaTela.consultarFrequenciaTurma(usuarioLogado);
        case 6 -> frequenciaTela.consultarFrequenciaPorAula(usuarioLogado);
        case 7 -> avaliacaoTela.consultarNotasProfessor(usuarioLogado);
        case 8 -> diarioTela.encerrarDiario(usuarioLogado);
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void menuAvaliacoesENotas() {
    int opcao;
    do {
      System.out.println("=== AVALIAÇÕES E NOTAS ===");
      System.out.println("1 - Cadastrar avaliação");
      System.out.println("2 - Lançar ou alterar nota por avaliação");
      System.out.println("0 - Voltar");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> avaliacaoTela.cadastrarAvaliacao(usuarioLogado);
        case 2 -> avaliacaoTela.lancarNotaPorAluno(usuarioLogado);
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void menuAlunos() {
    int opcao;
    do {
      System.out.println("╔═══════════════════════════════════╗");
      System.out.println("║               ALUNOS              ║");
      System.out.println("╠═══════════════════════════════════╣");
      System.out.println("║ 1 - Listar alunos                 ║");
      System.out.println("║ 2 - Buscar aluno por matrícula    ║");
      System.out.println("║ 0 - Voltar                        ║");
      System.out.println("╚═══════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> usuarioTela.listarUsuarios(usuarioLogado);
        case 2 -> usuarioTela.buscarUsuarioPorMatricula(usuarioLogado);
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
    System.out.println("║          MENU PROFESSOR           ║");
    System.out.println("╠═══════════════════════════════════╣");
    System.out.println("║ 1 - Turmas                        ║");
    System.out.println("║ 2 - Alunos                        ║");
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
