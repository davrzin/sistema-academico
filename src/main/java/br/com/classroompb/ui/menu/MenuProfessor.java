package br.com.classroompb.ui.menu;

import java.util.Scanner;

import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.ui.tela.TurmaTela;
import br.com.classroompb.ui.tela.UsuarioTela;

public class MenuProfessor {

    private final Scanner scanner = new Scanner(System.in);
    private final Professor usuarioLogado;
    private final TurmaTela turmaTela = new TurmaTela(scanner);
    private final UsuarioTela usuarioTela = new UsuarioTela(scanner);

    public MenuProfessor(Professor usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar() {
        int opcao;

        do {
            System.out.println("""
            ╔═══════════════════════════════════╗
            ║          MENU PROFESSOR           ║
            ╠═══════════════════════════════════╣
            ║ 1 - Listar minhas turmas          ║
            ║ 2 - Listar alunos                 ║
            ║ 3 - Buscar aluno por matrícula    ║
            ║ 4 - Lançar notas                  ║
            ║ 5 - Lançar frequência             ║
            ║ 6 - Listar diário                 ║
            ║ 0 - Voltar                        ║
            ╚═══════════════════════════════════╝
            """);

            opcao = lerOpcao();

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
                    System.out.println("Funcionalidade de lançar notas ainda não implementada.");
                    break;

                case 5:
                    turmaTela.listarMinhasTurmas(usuarioLogado);
                    turmaTela.adicionarFrequencia();
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

    private int lerOpcao() {
        System.out.print("Digite uma opção: ");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
