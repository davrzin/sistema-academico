package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.Usuario.Administrador;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.entities.Usuario.Coordenador;
import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
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
            ║ 2 - Cadastro de Usuário      ║
            ║ 0 - Sair                     ║
            ╚══════════════════════════════╝
            """);

            System.out.print("Digite uma opção: ");

            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Email: ");
                    String emailUsuario = scanner.nextLine();

                    System.out.print("Senha: ");
                    String senhaUsuario = scanner.nextLine();
                    System.out.println();

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
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();

                    System.out.print("Email: ");
                    String email = scanner.nextLine();

                    System.out.print("Senha: ");
                    String senha = scanner.nextLine();

                    try {
                        TipoUsuario tipoUsuario = escolherTipoUsuario();

                        Usuario novoUsuario = criarUsuarioPorTipo(tipoUsuario, nome, email, senha);

                        usuarioService.cadastrarUsuario(novoUsuario);

                        System.out.println("Usuário cadastrado com sucesso!");
                        System.out.println("Matrícula gerada: " + novoUsuario.getMatricula());

                    } catch (RuntimeException e) {
                        System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
                    }

                    break;
                case 0:
                    System.out.println("Fechando o Sistema");
                    break;
                default:
                    System.out.println("Opção inválida");
            }

        } while(opcao != 0);
    }

    private TipoUsuario escolherTipoUsuario() {
        while (true) {
            System.out.println("""
                Escolha o tipo de usuário:
                1 - Administrador
                2 - Coordenador
                3 - Professor
                4 - Aluno
                """);

            System.out.print("Digite uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    return TipoUsuario.ADMINISTRADOR;

                case "2":
                    return TipoUsuario.COORDENADOR;

                case "3":
                    return TipoUsuario.PROFESSOR;

                case "4":
                    return TipoUsuario.ALUNO;

                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    private Usuario criarUsuarioPorTipo(
        TipoUsuario tipoUsuario,
        String nome,
        String email,
        String senha
    ) {
        switch (tipoUsuario) {
            case ALUNO:
                return new Aluno(nome, email, senha);

            case PROFESSOR:
                return new Professor(nome, email, senha);

            case COORDENADOR:
                return new Coordenador(nome, email, senha);

            case ADMINISTRADOR:
                return new Administrador(nome, email, senha);

            default:
                throw new IllegalArgumentException("Tipo de usuário inválido.");
        }
    }

}
