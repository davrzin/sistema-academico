package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.services.UsuarioService;

public class MenuProfessor {

    private Scanner scanner = new Scanner(System.in);
    private Professor usuarioLogado;
    private UsuarioService usuarioService = new UsuarioService();

    public MenuProfessor(Professor usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public Professor getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Professor usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar() {
        int opcao;

        do {
            System.out.println("""
            ╔═══════════════════════════════════╗
            ║          MENU PROFESSOR           ║
            ╠═══════════════════════════════════╣
            ║ 1 - Consultar turmas              ║
            ║ 2 - Consultar alunos              ║
            ║ 3 - Lançar notas                  ║
            ║ 4 - Lançar frequência             ║
            ║ 5 - Consultar diário              ║
            ║ 6 - Buscar aluno por matrícula    ║
            ║ 0 - Voltar                        ║
            ╚═══════════════════════════════════╝
        \s""");

            System.out.print("Digite uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:

                    break;

                case 2:

                    break;

                case 3:

                    break;

                case 4:

                    break;

                case 5:

                    break;

                case 6:
                    buscarAlunoPorMatricula();
                    break;

                case 0:
                    System.out.println("Voltando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    private void buscarAlunoPorMatricula() {
        System.out.print("Digite a matrícula do aluno: ");
        String matricula = scanner.nextLine();

        try {
            Usuario usuarioEncontrado = usuarioService.buscarUsuarioPorMatricula(usuarioLogado, matricula);
            exibirUsuario(usuarioEncontrado);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void exibirUsuario(Usuario usuario) {
        System.out.println("\nAluno encontrado:");
        System.out.println("Nome: " + usuario.getNome());
        System.out.println("E-mail: " + usuario.getEmail());
        System.out.println("Matrícula: " + usuario.getMatricula());
        System.out.println("Tipo: " + usuario.getTipoUsuario());
        System.out.println();
    }
}
