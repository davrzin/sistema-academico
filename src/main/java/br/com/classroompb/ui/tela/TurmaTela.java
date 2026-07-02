package br.com.classroompb.ui.tela;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import br.com.classroompb.model.entities.GestaoAcademica.Aula;
import br.com.classroompb.model.entities.GestaoAcademica.Boletim;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.exception.*;
import br.com.classroompb.model.services.AulaService;
import br.com.classroompb.model.services.BoletimService;
import br.com.classroompb.model.services.TurmaService;
import br.com.classroompb.model.services.UsuarioService;

public class TurmaTela {

    private final Scanner scanner;
    private final TurmaService turmaService = new TurmaService();
    private final AulaService aulaService = new AulaService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final BoletimService boletimService = new BoletimService();

    public TurmaTela(Scanner scanner) {
        this.scanner = scanner;
    }

    public void cadastrarTurma() {
        try {
            Turma novaTurma = lerDadosTurma();

            turmaService.ofertarTurma(novaTurma);

            System.out.println("Turma cadastrada com sucesso.");
            System.out.println("Código da turma: " + novaTurma.getCodigo());

        } catch (PersistenciaException | EntradaInvalidaException e) {
            System.out.println("Ocorreu um erro ao cadastrar turma: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Limite de vagas inválido.");
        }
    }

    public void listarTurmas() {
        try {
            List<Turma> turmas = turmaService.listarTurmas();
            exibirListaTurmas(turmas);

        } catch (PersistenciaException e) {
            System.out.println("Ocorreu um erro ao listar turmas: " + e.getMessage());
        }
    }

    public void listarMinhasTurmas(Usuario usuarioLogado) {
        try {
            List<Turma> turmas = turmaService.listarTurmasPorProfessor(usuarioLogado.getMatricula());
            exibirListaTurmas(turmas);

        } catch (RuntimeException e) {
            System.out.println("Ocorreu um erro ao listar turmas: " + e.getMessage());
        }
    }

    public void atualizarTurma() {
        try {
            listarTurmas();

            System.out.println("Informe o código da turma que deseja atualizar:");
            String codigo = scanner.nextLine();

            Turma turmaAtualizada = lerDadosTurma();

            turmaService.alterarTurma(codigo, turmaAtualizada);

            System.out.println("Turma atualizada com sucesso.");

        } catch (PersistenciaException | EntradaInvalidaException e) {
            System.out.println("Ocorreu um erro ao atualizar turma: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Limite de vagas inválido.");
        }
    }

    public void cancelarTurma() {
        try {
            listarTurmas();

            System.out.println("Informe o código da turma que deseja cancelar:");
            String codigo = scanner.nextLine();

            turmaService.cancelarTurma(codigo);

            System.out.println("Turma cancelada com sucesso.");

        } catch (PersistenciaException | EntradaInvalidaException e) {
            System.out.println("Ocorreu um erro ao cancelar turma: " + e.getMessage());
        }
    }

    public void cadastrarNovoAluno(Aluno alunoLogado){
        try{
            listarTurmas();

            System.out.println("======================================");
            System.out.println("Informe o código da turma que deseja se matricular: ");
            String codigoTurma = scanner.nextLine();


            turmaService.cadastrarAlunoEmTurma(codigoTurma, alunoLogado);

            Boletim boletim = new Boletim(alunoLogado.getMatricula(), codigoTurma);

            boletimService.criarBoletim(boletim);

            System.out.println("Aluno matriculado com sucesso!");
        }catch(AlunoNaoCumprePreRequisitosException | TurmaCheiaException | PersistenciaException | EntradaInvalidaException e){
            //INSERIR TELA PARA ENTRADA EM UMA LISTA DE ESPERA
            System.out.println("Ocorreu um erro ao realizar matrícula: "+ e.getMessage());
        }


    }

    public void cancelarTurmaAluno(Aluno alunoLogado){
        try{
            listarTurmas();
            System.out.println("======================================");
            System.out.println("Informe o código da turma que deseja cancelar:");
            String codigoTurma = scanner.nextLine();

            String msgComplementar = turmaService.cancelarAlunoTurma(codigoTurma, alunoLogado);

            System.out.println("Matrícula cancelada com sucesso.");

            if(!msgComplementar.isBlank()){
                System.out.println(msgComplementar);
            }
        }catch(PersistenciaException | EntradaInvalidaException e){
            System.out.println("Ocorreu um erro ao cancelar matrícula: " + e.getMessage());
        }
    }

    public void adicionarFrequencia() {

        System.out.println("Informe o código da turma:");
        String codigoTurma = scanner.nextLine();

        try{
            Turma turma = turmaService.buscarTurmaPorCodigo(codigoTurma);

            if(turmaService.existeAlunosMatriculados(turma)){
                System.out.println("A turma não possui alunos matriculados.");
                return;
            }

            Aula aula = aulaService.gerarAula(turma);

            Map<String, Boolean> presencas = new HashMap<>();

            List<String> alunosMatriculados = turma.getMatriculados();

            System.out.println("\nRegistro de frequência");
            System.out.println("Digite P para Presente ou F para Falta\n");

            for (String matriculaAluno : alunosMatriculados) {
                while (true){
                    Aluno aluno = usuarioService.buscarAlunoPorMatricula(matriculaAluno);

                    System.out.print(aluno.getNome() + " (P/F): ");
                    String resposta = scanner.nextLine().trim().toUpperCase();

                    if (resposta.equals("P")){
                        presencas.put(matriculaAluno, true);
                        break;
                    }

                    if (resposta.equals("F")){
                        presencas.put(matriculaAluno, false);
                        break;
                    }

                    System.out.println("Opção inválida. Digite apenas P ou F.");
                }
            }
            aula.setPresencas(presencas);

            aulaService.salvarAula(aula);

            turmaService.cadastrarNovaAula(aula, codigoTurma);

            turmaService.atualizarFrequenciaTurma(codigoTurma);

        }
        catch(TurmaNaoEncontradaException e){
            System.out.println(e.getMessage());
        }

        System.out.println("Frequência registrada com sucesso.");
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

    private void exibirListaTurmas(List<Turma> turmas) {
        if (turmas == null || turmas.isEmpty()) {
            System.out.println("Nenhuma turma cadastrada.");
            return;
        }

        for (Turma turma : turmas) {
            exibirTurma(turma);
        }
    }

    private void exibirTurma(Turma turma) {
        System.out.println("\nTurma:");
        System.out.println("Código: " + turma.getCodigo());
        System.out.println("Código da disciplina: " + turma.getCodigoDisciplina());
        System.out.println("Período letivo: " + turma.getPeriodoLetivo());
        System.out.println("Matrícula do professor: " + turma.getMatriculaProfessor());
        System.out.println("Limite de vagas: " + turma.getLimiteVagas());
        System.out.println("Horário: " + turma.getHorario());
        System.out.println("Sala: " + turma.getSala());
        System.out.println();
    }
}
