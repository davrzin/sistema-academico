package br.com.classroompb.ui.menu;

import java.util.Scanner;

import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.ui.tela.DisciplinaTela;
import br.com.classroompb.ui.tela.TurmaTela;

public class MenuAluno {

    private final Scanner scanner = new Scanner(System.in);
    private Aluno usuarioLogado;
    private final DisciplinaTela disciplinaTela = new DisciplinaTela(scanner);
    private final TurmaTela turmaTela = new TurmaTela(scanner);

    public MenuAluno(Aluno usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public Aluno getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Aluno usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar() {
        int opcao;

        do {
            System.out.println("""
            ╔════════════════════════════════════╗
            ║             MENU ALUNO             ║
            ╠════════════════════════════════════╣
            ║ 1 - Listar disciplinas             ║
            ║ 2 - Listar turmas                  ║
            ║ 3 - Listar matrícula               ║
            ║ 4 - Listar boletim                 ║
            ║ 5 - Listar histórico acadêmico     ║
            ║ 6 - Matricular em turma            ║
            ║ 7 - Cancelar matrícula             ║
            ║ 0 - Voltar                         ║
            ╚════════════════════════════════════╝
            """);

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    disciplinaTela.listarDisciplinas();
                    break;

                case 2:
                    turmaTela.listarTurmas();
                    break;

                case 3:
                    System.out.println("Funcionalidade de listar matrícula ainda não implementada.");
                    break;

                case 4:
                    System.out.println("Funcionalidade de listar boletim ainda não implementada.");
                    break;

                case 5:
                    System.out.println("Funcionalidade de listar histórico acadêmico ainda não implementada.");
                    break;

                case 6:
                    turmaTela.cadastrarNovoAluno(usuarioLogado);
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
