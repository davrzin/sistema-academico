package br.com.classroompb.ui;

import br.com.classroompb.model.entities.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
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

                    Usuario usuario = usuarioService.fazerLoginUsuario(emailUsuario, senhaUsuario);

                    if(usuario.getTipoUsuario() == TipoUsuario.ALUNO){

                    }
                    else if(usuario.getTipoUsuario() == TipoUsuario.PROFESSOR){
                        //DIRECIONAR PARA O A PAGINA DE PROFESSOR

                    }
                    else if (usuario.getTipoUsuario() == TipoUsuario.COORDENADOR) {
                        //DIRECIONAR PARA O A PAGINA DE COORDENADOR

                    }
                    else if(usuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR){
                        //DIRECIONAR PARA O A PAGINA DE ADMINISTRADOR

                    }

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
