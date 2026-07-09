package br.com.classroompb.ui.menu;

import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.ui.tela.DisciplinaTela;
import br.com.classroompb.ui.tela.HistoricoAcademicoTela;
import br.com.classroompb.ui.tela.PeriodoLetivoTela;
import br.com.classroompb.ui.tela.TurmaTela;
import br.com.classroompb.ui.tela.UsuarioTela;
import java.util.Scanner;

/** Menu de funcionalidades do coordenador. */
public class MenuCoordenador {

  private final Coordenador usuarioLogado;
  private final Scanner scanner = new Scanner(System.in);
  private final DisciplinaTela disciplinaTela = new DisciplinaTela(scanner);
  private final HistoricoAcademicoTela historicoAcademicoTela =
      new HistoricoAcademicoTela(scanner);
  private final PeriodoLetivoTela periodoLetivoTela = new PeriodoLetivoTela(scanner);
  private final TurmaTela turmaTela = new TurmaTela(scanner);
  private final UsuarioTela usuarioTela = new UsuarioTela(scanner);

  /** Cria o menu para o coordenador logado. */
  public MenuCoordenador(Coordenador usuarioLogado) {
    this.usuarioLogado = usuarioLogado;
  }

  /** Inicia a navegacao pelo menu do coordenador. */
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
          disciplinaTela.cadastrarDisciplina(usuarioLogado);
          break;

        case 2:
          disciplinaTela.listarDisciplinas(usuarioLogado);
          break;

        case 3:
          periodoLetivoTela.cadastrarPeriodoLetivo();
          break;

        case 4:
          periodoLetivoTela.listarPeriodosLetivos();
          break;

        case 5:
          periodoLetivoTela.ativarPeriodoLetivo();
          break;

        case 6:
          periodoLetivoTela.encerrarPeriodoLetivo();
          break;

        case 7:
          turmaTela.cadastrarTurma(usuarioLogado);
          break;

        case 8:
          turmaTela.listarTurmas(usuarioLogado);
          break;

        case 9:
          turmaTela.atualizarTurma(usuarioLogado);
          break;

        case 10:
          turmaTela.cancelarTurma(usuarioLogado);
          break;

        case 11:
          usuarioTela.buscarUsuarioPorMatricula(usuarioLogado);
          break;

        case 12:
          turmaTela.mostrarListaEsperaTurmas(usuarioLogado);
          break;

        case 13:
          historicoAcademicoTela.consultarHistoricoAluno(usuarioLogado);
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
    System.out.println("╔═════════════════════════════════════╗");
    System.out.println("║          MENU COORDENADOR           ║");
    System.out.println("╠═════════════════════════════════════╣");
    System.out.println("║ 1 - Cadastrar disciplina            ║");
    System.out.println("║ 2 - Listar disciplinas              ║");
    System.out.println("║ 3 - Cadastrar período letivo        ║");
    System.out.println("║ 4 - Listar períodos letivos         ║");
    System.out.println("║ 5 - Ativar período letivo           ║");
    System.out.println("║ 6 - Encerrar período letivo         ║");
    System.out.println("║ 7 - Cadastrar turma                 ║");
    System.out.println("║ 8 - Listar turmas                   ║");
    System.out.println("║ 9 - Atualizar turma                 ║");
    System.out.println("║ 10 - Cancelar turma                 ║");
    System.out.println("║ 11 - Buscar aluno/professor         ║");
    System.out.println("║ 12 - Ver lista de espera            ║");
    System.out.println("║ 13 - Consultar histórico acadêmico  ║");
    System.out.println("║ 0 - Voltar                          ║");
    System.out.println("╚═════════════════════════════════════╝");
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
