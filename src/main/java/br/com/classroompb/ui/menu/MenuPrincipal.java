package br.com.classroompb.ui.menu;

import java.util.Scanner;

import br.com.classroompb.ui.tela.LoginTela;
import br.com.classroompb.ui.tela.UsuarioTela;

public class MenuPrincipal {

    private final Scanner scanner = new Scanner(System.in);
    private final LoginTela loginTela = new LoginTela(scanner);
    private final UsuarioTela usuarioTela = new UsuarioTela(scanner);

    public void iniciar() {
        int opcao;

        do {
            System.out.println("""
            ╔══════════════════════════════╗
            ║      SISTEMA ACADÊMICO       ║
            ╠══════════════════════════════╣
            ║ 1 - Login                    ║
            ║ 2 - Cadastrar usuário        ║
            ║ 0 - Sair                     ║
            ╚══════════════════════════════╝
            """);

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    loginTela.login();
                    break;

                case 2:
                    usuarioTela.cadastrarUsuario();
                    break;

                case 0:
                    System.out.println("Fechando o sistema.");
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
