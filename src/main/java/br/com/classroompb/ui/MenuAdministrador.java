package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.GestaoAcademica.Curso;
import br.com.classroompb.model.entities.Usuario.Administrador;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.CursoService;
import br.com.classroompb.model.services.UsuarioService;


public class MenuAdministrador {

    private Scanner scanner = new Scanner(System.in);
    private Administrador usuarioLogado;
    private UsuarioService usuarioService = new UsuarioService();
    private CursoService cursoService = new CursoService();

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
                    System.out.println("Informe o nome do curso:");
                    String nome = scanner.nextLine();

                    System.out.println("Informe a quantidade de períodos:");
                    int quantidadePeriodos = Integer.parseInt(scanner.nextLine());

                    System.out.println("Informe a carga horária total:");
                    int cargaHorariaTotal = Integer.parseInt(scanner.nextLine());

                    try{
                        Curso novoCurso = cursoService.cadastrarCurso(nome, quantidadePeriodos, cargaHorariaTotal);
                        System.out.println("Curso cadastrado com sucesso.");
                    }catch(
                            PersistenciaException
                            | EntradaInvalidaException e
                    ){
                        System.out.println("Ocorreu um erro ao cadastrar curso: "+ e.getMessage());
                    }
                    break;

                case 4:
                    System.out.println(cursoService.listarCursos());
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
