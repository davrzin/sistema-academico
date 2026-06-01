package br.com.classroompb.ui.tela;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.DisciplinaService;

public class DisciplinaTela {

    private final Scanner scanner;
    private final DisciplinaService disciplinaService = new DisciplinaService();

    public DisciplinaTela(Scanner scanner) {
        this.scanner = scanner;
    }

    public void cadastrarDisciplina() {
        try {
            System.out.println("Informe o nome da disciplina:");
            String nome = scanner.nextLine();

            System.out.println("Informe a carga horária:");
            int cargaHoraria = Integer.parseInt(scanner.nextLine());

            System.out.println("Informe o período:");
            int periodo = Integer.parseInt(scanner.nextLine());

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

            Disciplina novaDisciplina = new Disciplina(nome, cargaHoraria, periodo, creditos, codigoCurso, preRequisitos);

            disciplinaService.cadastrarDisciplina(novaDisciplina);

            System.out.println("Disciplina cadastrada com sucesso.");
            System.out.println("Código gerado: " + novaDisciplina.getCodigo());

        } catch (PersistenciaException | EntradaInvalidaException e) {
            System.out.println("Ocorreu um erro ao cadastrar disciplina: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Valor numérico inválido.");
        }
    }

    public void listarDisciplinas() {
        List<Disciplina> disciplinas = disciplinaService.listarDisciplinas();
        exibirListaDisciplinas(disciplinas);
    }

    private void exibirListaDisciplinas(List<Disciplina> disciplinas) {
        if (disciplinas == null || disciplinas.isEmpty()) {
            System.out.println("Nenhuma disciplina cadastrada.");
            return;
        }

        for (Disciplina disciplina : disciplinas) {
            exibirDisciplina(disciplina);
        }
    }

    private void exibirDisciplina(Disciplina disciplina) {
        System.out.println("\nDisciplina:");
        System.out.println("Código: " + disciplina.getCodigo());
        System.out.println("Nome: " + disciplina.getNome());
        System.out.println("Carga horária: " + disciplina.getCargaHoraria() + "h");
        System.out.println("Período: " + disciplina.getPeriodo());
        System.out.println("Créditos: " + disciplina.getCreditos());
        System.out.println("Código do curso: " + disciplina.getCodigoCurso());
        System.out.println("Pré-requisitos: " + disciplina.getPreRequisitos());
        System.out.println();
    }
}
