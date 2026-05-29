package br.com.classroompb.ui.tela;

import java.util.List;
import java.util.Scanner;

import br.com.classroompb.model.entities.GestaoAcademica.Curso;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.CursoService;

public class CursoTela {

    private final Scanner scanner;
    private final CursoService cursoService = new CursoService();

    public CursoTela(Scanner scanner) {
        this.scanner = scanner;
    }

    public void cadastrarCurso() {
        try {
            System.out.println("Informe o nome do curso:");
            String nome = scanner.nextLine();

            System.out.println("Informe a quantidade de períodos:");
            int quantidadePeriodos = Integer.parseInt(scanner.nextLine());

            System.out.println("Informe a carga horária total:");
            int cargaHorariaTotal = Integer.parseInt(scanner.nextLine());

            Curso novoCurso = new Curso(nome, quantidadePeriodos, cargaHorariaTotal);
            cursoService.cadastrarCurso(novoCurso);

            System.out.println("Curso cadastrado com sucesso.");
            System.out.println("Código gerado: " + novoCurso.getCodigo());

        } catch (PersistenciaException | EntradaInvalidaException e) {
            System.out.println("Ocorreu um erro ao cadastrar curso: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Valor numérico inválido.");
        }
    }

    public void listarCursos() {
        List<Curso> cursos = cursoService.listarCursos();
        exibirListaCursos(cursos);
    }

    private void exibirListaCursos(List<Curso> cursos) {
        if (cursos == null || cursos.isEmpty()) {
            System.out.println("Nenhum curso cadastrado.");
            return;
        }

        for (Curso curso : cursos) {
            exibirCurso(curso);
        }
    }

    private void exibirCurso(Curso curso) {
        System.out.println("\nCurso:");
        System.out.println("Código: " + curso.getCodigo());
        System.out.println("Nome: " + curso.getNome());
        System.out.println("Quantidade de períodos: " + curso.getQuantidadePeriodos());
        System.out.println("Carga horária total: " + curso.getCargaHorariaTotal() + "h");
        System.out.println();
    }
}
