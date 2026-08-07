package br.com.classroompb.ui.menu;

import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.ui.tela.DiarioTela;
import br.com.classroompb.ui.tela.DisciplinaTela;
import br.com.classroompb.ui.tela.FrequenciaTela;
import br.com.classroompb.ui.tela.HistoricoAcademicoTela;
import br.com.classroompb.ui.tela.PeriodoLetivoTela;
import br.com.classroompb.ui.tela.RelatorioAcademicoTela;
import br.com.classroompb.ui.tela.TurmaTela;
import br.com.classroompb.ui.tela.UsuarioTela;
import java.util.Scanner;

/** Menu de funcionalidades do coordenador. */
public class MenuCoordenador {

  private final Coordenador usuarioLogado;
  private final Scanner scanner;
  private final DiarioTela diarioTela;
  private final DisciplinaTela disciplinaTela;
  private final FrequenciaTela frequenciaTela;
  private final HistoricoAcademicoTela historicoAcademicoTela;
  private final PeriodoLetivoTela periodoLetivoTela;
  private final RelatorioAcademicoTela relatorioAcademicoTela;
  private final TurmaTela turmaTela;
  private final UsuarioTela usuarioTela;

  /** Cria o menu para o coordenador logado. */
  public MenuCoordenador(Coordenador usuarioLogado, Scanner scanner) {
    this.usuarioLogado = usuarioLogado;
    this.scanner = scanner;
    this.diarioTela = new DiarioTela(scanner);
    this.disciplinaTela = new DisciplinaTela(scanner);
    this.frequenciaTela = new FrequenciaTela(scanner);
    this.historicoAcademicoTela = new HistoricoAcademicoTela(scanner);
    this.periodoLetivoTela = new PeriodoLetivoTela(scanner);
    this.relatorioAcademicoTela = new RelatorioAcademicoTela();
    this.turmaTela = new TurmaTela(scanner);
    this.usuarioTela = new UsuarioTela(scanner);
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
        case 1 -> menuUsuarios();
        case 2 -> menuDisciplinas();
        case 3 -> menuPeriodosLetivos();
        case 4 -> menuTurmas();
        case 5 -> menuRelatorios();
        case 6 -> menuDiarios();
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

    } while (opcao != 0);
  }

  private void menuUsuarios() {
    int opcao;
    do {
      System.out.println("╔══════════════════════════════════════════════╗");
      System.out.println("║                   USUÁRIOS                   ║");
      System.out.println("╠══════════════════════════════════════════════╣");
      System.out.println("║ 1 - Buscar aluno/professor                   ║");
      System.out.println("║ 2 - Consultar histórico acadêmico            ║");
      System.out.println("║ 0 - Voltar                                   ║");
      System.out.println("╚══════════════════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> usuarioTela.buscarUsuarioPorMatricula(usuarioLogado);
        case 2 -> historicoAcademicoTela.consultarHistoricoAluno(usuarioLogado);
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void menuDisciplinas() {
    int opcao;
    do {
      System.out.println("╔══════════════════════════════════════════════╗");
      System.out.println("║                 DISCIPLINAS                  ║");
      System.out.println("╠══════════════════════════════════════════════╣");
      System.out.println("║ 1 - Cadastrar disciplina                     ║");
      System.out.println("║ 2 - Listar disciplinas                       ║");
      System.out.println("║ 0 - Voltar                                   ║");
      System.out.println("╚══════════════════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> disciplinaTela.cadastrarDisciplina(usuarioLogado);
        case 2 -> disciplinaTela.listarDisciplinas(usuarioLogado);
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void menuPeriodosLetivos() {
    int opcao;
    do {
      System.out.println("╔══════════════════════════════════════════════╗");
      System.out.println("║               PERÍODOS LETIVOS               ║");
      System.out.println("╠══════════════════════════════════════════════╣");
      System.out.println("║ 1 - Cadastrar período letivo                 ║");
      System.out.println("║ 2 - Listar períodos letivos                  ║");
      System.out.println("║ 3 - Ativar período letivo                    ║");
      System.out.println("║ 4 - Encerrar período letivo                  ║");
      System.out.println("║ 0 - Voltar                                   ║");
      System.out.println("╚══════════════════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> periodoLetivoTela.cadastrarPeriodoLetivo();
        case 2 -> periodoLetivoTela.listarPeriodosLetivos();
        case 3 -> periodoLetivoTela.ativarPeriodoLetivo();
        case 4 -> periodoLetivoTela.encerrarPeriodoLetivo();
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void menuTurmas() {
    int opcao;
    do {
      System.out.println("╔══════════════════════════════════════════════╗");
      System.out.println("║                    TURMAS                    ║");
      System.out.println("╠══════════════════════════════════════════════╣");
      System.out.println("║ 1 - Cadastrar turma                          ║");
      System.out.println("║ 2 - Listar turmas                            ║");
      System.out.println("║ 3 - Atualizar turma                          ║");
      System.out.println("║ 4 - Cancelar turma                           ║");
      System.out.println("║ 5 - Ver lista de espera                      ║");
      System.out.println("║ 6 - Consultar frequência de turma            ║");
      System.out.println("║ 7 - Consultar frequência por aula            ║");
      System.out.println("║ 8 - Consolidar resultados da turma           ║");
      System.out.println("║ 0 - Voltar                                   ║");
      System.out.println("╚══════════════════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> turmaTela.cadastrarTurma(usuarioLogado);
        case 2 -> turmaTela.listarTurmas(usuarioLogado);
        case 3 -> turmaTela.atualizarTurma(usuarioLogado);
        case 4 -> turmaTela.cancelarTurma(usuarioLogado);
        case 5 -> turmaTela.mostrarListaEsperaTurmas(usuarioLogado);
        case 6 -> frequenciaTela.consultarFrequenciaTurma(usuarioLogado);
        case 7 -> frequenciaTela.consultarFrequenciaPorAula(usuarioLogado);
        case 8 -> turmaTela.consolidarResultadosTurma(usuarioLogado);
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
      System.out.println("╔══════════════════════════════════════════════╗");
      System.out.println("║                  RELATÓRIOS                  ║");
      System.out.println("╠══════════════════════════════════════════════╣");
      System.out.println("║ 1 - Relatório de alunos por turma            ║");
      System.out.println("║ 2 - Relatório de ocupação de vagas           ║");
      System.out.println("║ 3 - Relatório de reprovação por disciplina   ║");
      System.out.println("║ 0 - Voltar                                   ║");
      System.out.println("╚══════════════════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> relatorioAcademicoTela.gerarRelatorioAlunosPorTurma(usuarioLogado);
        case 2 -> relatorioAcademicoTela.gerarRelatorioOcupacaoVagas(usuarioLogado);
        case 3 -> relatorioAcademicoTela.gerarRelatorioReprovacaoPorDisciplina(usuarioLogado);
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void menuDiarios() {
    int opcao;
    do {
      System.out.println("╔══════════════════════════════════════════════╗");
      System.out.println("║                    DIÁRIOS                   ║");
      System.out.println("╠══════════════════════════════════════════════╣");
      System.out.println("║ 1 - Cadastrar diário de turma                ║");
      System.out.println("║ 2 - Listar diários                           ║");
      System.out.println("║ 3 - Listar diários de uma turma              ║");
      System.out.println("║ 4 - Atualizar diário                         ║");
      System.out.println("║ 5 - Cancelar diário                          ║");
      System.out.println("║ 0 - Voltar                                   ║");
      System.out.println("╚══════════════════════════════════════════════╝");

      opcao = lerOpcao();
      System.out.println();

      switch (opcao) {
        case 1 -> diarioTela.cadastrarDiario(usuarioLogado);
        case 2 -> diarioTela.listarDiarios(usuarioLogado);
        case 3 -> diarioTela.listarDiariosPorTurma(usuarioLogado);
        case 4 -> diarioTela.atualizarDiario(usuarioLogado);
        case 5 -> diarioTela.cancelarDiario(usuarioLogado);
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }

      if (opcao != 0) {
        System.out.println();
      }
    } while (opcao != 0);
  }

  private void imprimirMenu() {
    System.out.println("╔══════════════════════════════════════════════╗");
    System.out.println("║              MENU COORDENADOR                ║");
    System.out.println("╠══════════════════════════════════════════════╣");
    System.out.println("║ 1 - Usuários                                 ║");
    System.out.println("║ 2 - Disciplinas                              ║");
    System.out.println("║ 3 - Períodos letivos                         ║");
    System.out.println("║ 4 - Turmas                                   ║");
    System.out.println("║ 5 - Relatórios                               ║");
    System.out.println("║ 6 - Diários                                  ║");
    System.out.println("║ 0 - Voltar                                   ║");
    System.out.println("╚══════════════════════════════════════════════╝");
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
