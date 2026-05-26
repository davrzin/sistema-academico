package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.entities.Usuario.Coordenador;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PeriodoLetivoExistenteException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.GestaoAcademicaService;
import br.com.classroompb.model.services.UsuarioService;

public class MenuCoordenador {

    private Scanner scanner = new Scanner(System.in);
    private Coordenador usuarioLogado;
    private UsuarioService usuarioService = new UsuarioService();

    public MenuCoordenador(Coordenador usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public Coordenador getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Coordenador usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar() {
        int opcao;

        Scanner scanner = new Scanner(System.in);
        GestaoAcademicaService gestaoAcademicaService = new GestaoAcademicaService();

        do {
            System.out.println("""
            ╔═══════════════════════════════════╗
            ║         MENU COORDENADOR          ║
            ╠═══════════════════════════════════╣
            ║ 1 - Cadastrar disciplina          ║
            ║ 2 - Listar disciplinas            ║
            ║ 3 - Cadastrar período letivo      ║
            ║ 4 - Ativar período letivo         ║
            ║ 5 - Encerrar período letivo       ║
            ║ 6 - Ofertar turma                 ║
            ║ 7 - Alterar turma                 ║
            ║ 8 - Cancelar turma                ║
            ║ 9 - Buscar aluno/professor        ║
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
                    System.out.println("Informe o período letivo (202X.Y)");
                    String periodo = scanner.nextLine();

                    System.out.println("Informe a data de início do período (DD/MM/AAAA):");
                    String dataInicio = scanner.nextLine();

                    System.out.println("Informe a data de fim do período (DD/MM/AAAA): ");
                    String dataFim = scanner.nextLine();

                    try{
                        PeriodoLetivo novoPeriodo = gestaoAcademicaService.cadastrarPeriodoLetivo(periodo, dataInicio, dataFim);

                        System.out.println("Período letivo cadastrado com sucesso");

                    }catch(PersistenciaException | EntradaInvalidaException | PeriodoLetivoExistenteException e){
                        System.out.println("Ocorreu um erro ao cadastrar novo período letivo: " + e.getMessage());
                    }

                    break;

                case 4:

                    break;

                case 5:

                    break;

                case 6:

                    break;

                case 7:

                    break;

                case 8:

                    break;

                case 9:
                    buscarUsuarioPorMatricula();
                    break;

                case 0:
                    System.out.println("Voltando...");
                    System.err.println();
                    break;

                default:
                    System.out.println("Opção inválida");
                    System.err.println();
            }

        } while (opcao != 0);
    }

    private void buscarUsuarioPorMatricula() {
        System.out.print("Digite a matrícula do aluno ou professor: ");
        String matricula = scanner.nextLine();
        System.err.println();

        try {
            Usuario usuarioEncontrado = usuarioService.buscarUsuarioPorMatricula(usuarioLogado, matricula);
            exibirUsuario(usuarioEncontrado);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.err.println();
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
