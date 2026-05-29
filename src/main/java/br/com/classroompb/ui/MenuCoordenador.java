package br.com.classroompb.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.entities.Usuario.Coordenador;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.ExistePeriodoAtivoException;
import br.com.classroompb.model.exception.PeriodoLetivoExistenteException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.DisciplinaService;
import br.com.classroompb.model.services.PeriodoLetivoService;
import br.com.classroompb.model.services.TurmaService;
import br.com.classroompb.model.services.UsuarioService;

public class MenuCoordenador {

    private Scanner scanner = new Scanner(System.in);
    private Coordenador usuarioLogado;
    private UsuarioService usuarioService = new UsuarioService();
    private DisciplinaService disciplinaService = new DisciplinaService();
    private TurmaService turmaService = new TurmaService();

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
        PeriodoLetivoService periodoLetivoService = new PeriodoLetivoService();

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
            ║ 10 - Consultar turmas             ║
            ║ 0 - Voltar                        ║
            ╚═══════════════════════════════════╝
        \s""");

            System.out.print("Digite uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.println("Informe o nome da disciplina:");
                    String nome = scanner.nextLine();

                    System.out.println("Informe a carga horária:");
                    int cargaHoraria = Integer.parseInt(scanner.nextLine());

                    System.out.println("Informe o período:");
                    int periodoDis = Integer.parseInt(scanner.nextLine());

                    System.out.println("Informe a quantidade de créditos:");
                    int creditos = Integer.parseInt(scanner.nextLine());

                    System.out.println("Informe o código do curso:");
                    String codigoCurso = scanner.nextLine();

                    System.out.println("""
                        Informe os pré-requisitos da disciplina.
                        Digite os códigos separados por vírgula.
                        Caso não exista pré-requisitos, pressione ENTER.
                        """);

                    String entradaPreRequisitos = scanner.nextLine();

                    List<String> preRequisitos = new ArrayList<>();

                    if (!entradaPreRequisitos.isBlank()) {
                        preRequisitos = Arrays.stream(entradaPreRequisitos.split(",")).map(String::trim).toList();
                    }
                    try {
                        Disciplina novaDisciplina = new Disciplina(nome, cargaHoraria, periodoDis, creditos, codigoCurso, preRequisitos);
                        disciplinaService.cadastrarDisciplina(novaDisciplina);
                        System.out.println("Disciplina cadastrada com sucesso.");
                    } catch (
                            PersistenciaException
                            | EntradaInvalidaException e
                    ) {
                        System.out.println("Ocorreu um erro ao cadastrar disciplina: " + e.getMessage());
                    }

                    break;
                case 2:
                    System.out.println(disciplinaService.listarDisciplinas());
                    break;

                case 3:
                    System.out.println("Informe o período letivo (202X.Y)");
                    String periodo = scanner.nextLine();

                    System.out.println("Informe a data de início do período (DD/MM/AAAA):");
                    String dataInicio = scanner.nextLine();

                    System.out.println("Informe a data de fim do período (DD/MM/AAAA): ");
                    String dataFim = scanner.nextLine();

                    try {
                        PeriodoLetivo novoPeriodo = new PeriodoLetivo(periodo, dataInicio, dataFim);
                        periodoLetivoService.cadastrarPeriodoLetivo(novoPeriodo);

                        System.out.println("Período letivo cadastrado com sucesso");

                    } catch (PersistenciaException | EntradaInvalidaException | PeriodoLetivoExistenteException e) {
                        System.out.println("Ocorreu um erro ao cadastrar novo período letivo: " + e.getMessage());
                    }

                    break;

                case 4:

                    System.out.println("Selecione o período letivo que vai ser ativado: ");

                    try {
                        List<PeriodoLetivo> periodosLetivos = periodoLetivoService.listarPeriodosLetivos();

                        int i = 1;
                        for (PeriodoLetivo periodos : periodosLetivos) {

                            if (!periodos.getPeriodoAtivo()) {
                                System.out.println(i + "-) " + periodos.getPeriodo() + " (" + periodos.getDataInicio() + " - " + periodos.getDataFim() + ")");
                                i++;
                            }

                        }

                        int periodoEscolhido = scanner.nextInt();
                        scanner.nextLine();

                        boolean deuCerto = periodoLetivoService.ativarPeriodoLetivo(periodosLetivos.get(periodoEscolhido - 1), periodoEscolhido - 1);

                        if (deuCerto) {
                            System.out.println("Período ativado com sucesso!");
                        } else {
                            System.out.println("Período não ativado.");
                        }

                    } catch (PersistenciaException | ExistePeriodoAtivoException | IndexOutOfBoundsException e) {

                        if (e.getClass() == IndexOutOfBoundsException.class) {
                            System.out.println("Índice de período letivo não existe");
                        } else {
                            System.out.println("Ocorreu um erro. " + e.getMessage());
                        }
                    }
                    break;

                case 5:

                    System.out.println("Selecione o período letivo que vai ser desativado: ");

                    try {
                        List<PeriodoLetivo> listaPeriodos = periodoLetivoService.listarPeriodosLetivos();

                        int i = 1;
                        for (PeriodoLetivo periodos : listaPeriodos) {

                            if (periodos.getPeriodoAtivo()) {
                                System.out.println(i + "-) " + periodos.getPeriodo() + " (" + periodos.getDataInicio() + " - " + periodos.getDataFim() + ")");
                                i++;
                            }
                        }

                        int periodoEscolhido = scanner.nextInt();
                        scanner.nextLine();

                        boolean deuCerto = periodoLetivoService.desativarPeriodoLetivo(listaPeriodos.get(periodoEscolhido - 1), periodoEscolhido - 1);

                        if (deuCerto) {
                            System.out.println("Período desativado com sucesso");
                        } else {
                            System.out.println("Período não desativado.");
                        }

                    } catch (PersistenciaException | IndexOutOfBoundsException e) {

                        if (e.getClass() == IndexOutOfBoundsException.class) {
                            System.out.println("Índice de período letivo não existe.");
                        } else {
                            System.out.println("Ocorreu um erro: " + e.getMessage());

                        }
                    }

                    break;

                case 6:
                    ofertarTurma();
                    break;

                case 7:
                    alterarTurma();
                    break;

                case 8:
                    cancelarTurma();
                    break;

                case 9:
                    buscarUsuarioPorMatricula();
                    break;

                case 10:
                    consultarTurmas();
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

    private void ofertarTurma() {
        try {
            Turma novaTurma = lerDadosTurma();

            turmaService.ofertarTurma(novaTurma);

            System.out.println("Turma ofertada com sucesso.");
            System.out.println("Código da turma: " + novaTurma.getCodigo());
            System.err.println();
        } catch (PersistenciaException | EntradaInvalidaException e) {
            System.out.println("Ocorreu um erro ao ofertar turma: " + e.getMessage());
            System.err.println();
        } catch (NumberFormatException e) {
            System.out.println("Limite de vagas inválido.");
            System.err.println();
        }
    }

    private void alterarTurma() {
        try {
            consultarTurmas();

            System.out.println("Informe o código da turma que deseja alterar:");
            String codigo = scanner.nextLine();

            Turma turmaAtualizada = lerDadosTurma();

            turmaService.alterarTurma(codigo, turmaAtualizada);

            System.out.println("Turma alterada com sucesso.");
            System.err.println();
        } catch (PersistenciaException | EntradaInvalidaException e) {
            System.out.println("Ocorreu um erro ao alterar turma: " + e.getMessage());
            System.err.println();
        } catch (NumberFormatException e) {
            System.out.println("Limite de vagas inválido.");
            System.err.println();
        }
    }

    private void cancelarTurma() {
        try {
            consultarTurmas();

            System.out.println("Informe o código da turma que deseja cancelar:");
            String codigo = scanner.nextLine();

            turmaService.cancelarTurma(codigo);

            System.out.println("Turma cancelada com sucesso.");
            System.err.println();
        } catch (PersistenciaException | EntradaInvalidaException e) {
            System.out.println("Ocorreu um erro ao cancelar turma: " + e.getMessage());
            System.err.println();
        }
    }

    private Turma lerDadosTurma() {
        System.out.println("Informe o código da disciplina:");
        String codigoDisciplina = scanner.nextLine();

        System.out.println("Informe o período letivo da turma. Exemplo: 2026.2");
        String periodoLetivo = scanner.nextLine();

        System.out.println("Informe a matrícula do professor responsável:");
        String matriculaProfessor = scanner.nextLine();

        System.out.println("Informe o limite de vagas:");
        int limiteVagas = Integer.parseInt(scanner.nextLine());

        System.out.println("Informe o horário da turma. Exemplo: SEG 08:00-10:00");
        String horario = scanner.nextLine();

        System.out.println("Informe a sala da turma:");
        String sala = scanner.nextLine();

        return new Turma(
                codigoDisciplina,
                periodoLetivo,
                matriculaProfessor,
                limiteVagas,
                horario,
                sala
        );
    }

    private void consultarTurmas() {
        try {
            List<Turma> turmas = turmaService.listarTurmas();
            exibirTurmas(turmas);
        } catch (PersistenciaException e) {
            System.out.println("Ocorreu um erro ao consultar turmas: " + e.getMessage());
            System.err.println();
        }
    }

    private void exibirTurmas(List<Turma> turmas) {
        if (turmas == null || turmas.isEmpty()) {
            System.out.println("Nenhuma turma cadastrada.");
            System.err.println();
            return;
        }

        for (Turma turma : turmas) {
            System.out.println(turma);
        }
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
