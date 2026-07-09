package br.com.classroompb.ui.menu;

import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.ui.tela.DisciplinaTela;
import br.com.classroompb.ui.tela.HistoricoAcademicoTela;
import br.com.classroompb.ui.tela.TurmaTela;
import br.com.classroompb.ui.tela.UsuarioTela;
import java.util.Scanner;

/** Menu de funcionalidades do aluno. */
public class MenuAluno {

  private final Scanner scanner = new Scanner(System.in);
  private Aluno usuarioLogado;
  private final DisciplinaTela disciplinaTela = new DisciplinaTela(scanner);
  private final HistoricoAcademicoTela historicoAcademicoTela =
      new HistoricoAcademicoTela(scanner);
  private final TurmaTela turmaTela = new TurmaTela(scanner);
  private final UsuarioTela usuarioTela = new UsuarioTela(scanner);

  /** Cria o menu para o aluno logado. */
  public MenuAluno(Aluno usuarioLogado) {
    this.usuarioLogado = usuarioLogado;
  }

  /** Retorna o aluno logado. */
  public Aluno getUsuarioLogado() {
    return usuarioLogado;
  }

  /** Atualiza o aluno logado. */
  public void setUsuarioLogado(Aluno usuarioLogado) {
    this.usuarioLogado = usuarioLogado;
  }

  /** Inicia a navegacao pelo menu do aluno. */
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
          disciplinaTela.listarDisciplinas(usuarioLogado);
          break;

        case 2:
          turmaTela.listarTurmas(usuarioLogado);
          break;

        case 3:
          turmaTela.listarMatriculasAluno(usuarioLogado);
          break;

        case 4:
          usuarioTela.exibirBoletinAluno(usuarioLogado);
          break;

        case 5:
          historicoAcademicoTela.listarHistoricoAluno(usuarioLogado);
          break;

        case 6:
          turmaTela.cadastrarNovoAluno(usuarioLogado);
          break;

        case 7:
          turmaTela.cancelarTurmaAluno(usuarioLogado);
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
    System.out.println("╔════════════════════════════════════╗");
    System.out.println("║             MENU ALUNO             ║");
    System.out.println("╠════════════════════════════════════╣");
    System.out.println("║ 1 - Listar disciplinas             ║");
    System.out.println("║ 2 - Listar turmas                  ║");
    System.out.println("║ 3 - Listar matrícula               ║");
    System.out.println("║ 4 - Listar boletim                 ║");
    System.out.println("║ 5 - Listar histórico acadêmico     ║");
    System.out.println("║ 6 - Matricular em turma            ║");
    System.out.println("║ 7 - Cancelar matrícula             ║");
    System.out.println("║ 0 - Voltar                         ║");
    System.out.println("╚════════════════════════════════════╝");
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
