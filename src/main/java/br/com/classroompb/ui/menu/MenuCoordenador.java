package br.com.classroompb.ui.menu;

import java.util.Scanner;

import br.com.classroompb.model.entities.Usuario.Coordenador;
import br.com.classroompb.ui.tela.DisciplinaTela;
import br.com.classroompb.ui.tela.PeriodoLetivoTela;
import br.com.classroompb.ui.tela.TurmaTela;
import br.com.classroompb.ui.tela.UsuarioTela;

public class MenuCoordenador {

    private final Coordenador usuarioLogado;
    private final Scanner scanner = new Scanner(System.in);
    private final DisciplinaTela disciplinaTela = new DisciplinaTela(scanner);
    private final PeriodoLetivoTela periodoLetivoTela = new PeriodoLetivoTela(scanner);
    private final TurmaTela turmaTela = new TurmaTela(scanner);
    private final UsuarioTela usuarioTela = new UsuarioTela(scanner);

    public MenuCoordenador(Coordenador usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar() {
        int opcao;

        do {
            System.out.println("""
            ╔═══════════════════════════════════╗
            ║         MENU COORDENADOR          ║
            ╠═══════════════════════════════════╣
            ║ 1 - Cadastrar disciplina          ║
            ║ 2 - Listar disciplinas            ║
            ║ 3 - Cadastrar período letivo      ║
            ║ 4 - Listar períodos letivos       ║
            ║ 5 - Ativar período letivo         ║
            ║ 6 - Encerrar período letivo       ║
            ║ 7 - Cadastrar turma               ║
            ║ 8 - Listar turmas                 ║
            ║ 9 - Atualizar turma               ║
            ║ 10 - Cancelar turma               ║
            ║ 11 - Buscar aluno/professor       ║
            ║ 0 - Voltar                        ║
            ╚═══════════════════════════════════╝
            """);

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    disciplinaTela.cadastrarDisciplina();
                    break;

                case 2:
                    disciplinaTela.listarDisciplinas();
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
                    turmaTela.cadastrarTurma();
                    break;

                case 8:
                    turmaTela.listarTurmas();
                    break;

                case 9:
                    turmaTela.atualizarTurma();
                    break;

                case 10:
                    turmaTela.cancelarTurma();
                    break;

                case 11:
                    usuarioTela.buscarUsuarioPorMatricula(usuarioLogado);
                    break;

                case 0:
                    System.out.println("Voltando...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
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
