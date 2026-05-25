package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.Administrador;
import br.com.classroompb.model.entities.Usuario;
import br.com.classroompb.model.services.UsuarioService;

public class MenuAdministrador {

    private Scanner scanner = new Scanner(System.in);
    private Administrador usuarioLogado;
    private UsuarioService usuarioService = new UsuarioService();

    public MenuAdministrador(Administrador usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public Administrador getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Administrador usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar() {
        int opcao;

        do {
            System.out.println("""
            ╔═══════════════════════════════════╗
            ║        MENU ADMINISTRADOR         ║
            ╠═══════════════════════════════════╣
            ║ 1 - Cadastrar usuário             ║
            ║ 2 - Listar usuários               ║
            ║ 3 - Cadastrar curso               ║
            ║ 4 - Listar cursos                 ║
            ║ 5 - Remover usuário               ║
            ║ 6 - Atualizar usuário             ║
            ║ 7 - Buscar por matrícula          ║
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

                    break;

                case 7:
                    buscarUsuarioPorMatricula();
                    break;

                case 0:
                    System.out.println("Voltando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    private void buscarUsuarioPorMatricula() {
        System.out.print("Digite a matrícula do aluno, professor ou coordenador: ");
        String matricula = scanner.nextLine();

        try {
            Usuario usuarioEncontrado = usuarioService.buscarUsuarioPorMatricula(usuarioLogado, matricula);
            exibirUsuario(usuarioEncontrado);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void exibirUsuario(Usuario usuario) {
        System.out.println("\nUsuário encontrado:");
        System.out.println("Nome: " + usuario.getNome());
        System.out.println("E-mail: " + usuario.getEmail());
        System.out.println("Matrícula: " + usuario.getMatricula());
        System.out.println("Tipo: " + usuario.getTipoUsuario());
        System.out.println();
    }
}
