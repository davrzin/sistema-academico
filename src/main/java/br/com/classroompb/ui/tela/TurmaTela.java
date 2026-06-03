package br.com.classroompb.ui.tela;

import java.util.List;
import java.util.Scanner;

import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.TurmaService;

public class TurmaTela {

    private final Scanner scanner;
    private final TurmaService turmaService = new TurmaService();

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

    public void cadastrarNovoAluno(){
        listarTurmas();

        System.out.println("======================================");
        System.out.println("Informe o código da turma que deseja se matricular: ");
        String codigoTurma = scanner.nextLine();


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
