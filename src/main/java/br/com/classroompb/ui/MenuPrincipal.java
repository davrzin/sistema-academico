package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.Administrador;
import br.com.classroompb.model.entities.Aluno;
import br.com.classroompb.model.entities.Coordenador;
import br.com.classroompb.model.entities.Professor;
import br.com.classroompb.model.entities.Usuario;
import br.com.classroompb.model.services.UsuarioService;

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

                    switch (usuario.getTipoUsuario()) {
                        case ALUNO:
                            MenuAluno menuAluno = new MenuAluno((Aluno) usuario);
                            menuAluno.iniciar();
                            break;

                        case PROFESSOR:
                            MenuProfessor menuProfessor = new MenuProfessor((Professor) usuario);
                            menuProfessor.iniciar();
                            break;

                        case COORDENADOR:
                            MenuCoordenador menuCoordenador = new MenuCoordenador((Coordenador) usuario);
                            menuCoordenador.iniciar();
                            break;

                        case ADMINISTRADOR:
                            MenuAdministrador menuAdmin = new MenuAdministrador((Administrador) usuario);
                            menuAdmin.iniciar();
                            break;

                        default:
                            System.out.println("Tipo de usuário inválido!");
                            break;
                    }

                    break;
                case 2:
                    System.out.println();
                    break;
                case 0:
                    System.out.println("Fechando o Sistema");
                    break;
                default:
                    System.out.println("Opção inválida");
            }

        } while(opcao != 0);


    }

}
