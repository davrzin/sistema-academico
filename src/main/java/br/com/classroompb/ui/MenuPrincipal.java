package br.com.classroompb.ui;

import br.com.classroompb.model.services.UsuarioService;

import java.util.Scanner;

public class MenuPrincipal {

    private Scanner scanner = new Scanner(System.in);

    public void iniciar(){

        UsuarioService usuarioService = new UsuarioService();
        int opcao;

        do{
            System.out.println("""
            ╔══════════════════════════════╗
            ║      SISTEMA ACADÊMICO       ║
            ╠══════════════════════════════╣
            ║ 1 - Login                    ║
            ║ 2 - Cadastro de Aluno        ║
            ║ 0 - Sair                     ║
            ╚══════════════════════════════╝
            """);

            System.out.print("Digite uma opção: ");

            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    System.out.println("Email: ");
                    String emailUsuario = scanner.nextLine();

                    System.out.println("Senha: ");
                    String senhaUsuario = scanner.nextLine();

                    //usuarioService.fazerLoginUsuario(emailUsuario, senhaUsuario);

                    break;
                case 2:
                    System.out.println();
                    break;
                case 0:
                    System.out.println("Fechando o Sistema");
                default:
                    System.out.println("Opção inválida");
            }

        } while(opcao != 0);


    }

}
