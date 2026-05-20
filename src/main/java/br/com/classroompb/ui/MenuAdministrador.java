package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.Administrador;

public class MenuAdministrador {

    Scanner scanner = new Scanner(System.in);
    private Administrador usuarioLogado;

    public MenuAdministrador(Administrador usuarioLogado){
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar(){

        int opcao;

        do{
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
            ║ 0 - Voltar                        ║
            ╚═══════════════════════════════════╝
        \s""");

            System.out.print("Digite uma opção: ");

            opcao = scanner.nextInt();

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

                case 0:
                    System.out.println("Voltando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while(opcao != 0);
    }
}
