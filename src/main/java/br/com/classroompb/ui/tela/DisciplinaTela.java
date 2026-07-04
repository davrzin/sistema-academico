package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.DisciplinaService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Tela de interacao para operacoes de disciplina.
 */
public class DisciplinaTela {

  private final Scanner scanner;
  private final DisciplinaService disciplinaService = new DisciplinaService();

  /**
   * Cria a tela de disciplinas.
   *
   * @param scanner leitor de entrada.
   */
  public DisciplinaTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Solicita os dados para cadastro de disciplina.
   */
  public void cadastrarDisciplina() {
    try {
      System.out.println("Informe o nome da disciplina:");
      final String nome = scanner.nextLine();

      System.out.println("Informe a carga horária:");
      final int cargaHoraria = Integer.parseInt(scanner.nextLine());

      System.out.println("Informe o período:");
      final int periodo = Integer.parseInt(scanner.nextLine());

      System.out.println("Informe a quantidade de créditos:");
      final int creditos = Integer.parseInt(scanner.nextLine());

      System.out.println("Informe o código do curso:");
      String codigoCurso = scanner.nextLine();

      System.out.println(
          """
          Informe os pré-requisitos da disciplina.
          Digite os códigos separados por vírgula.
          Caso não exista pré-requisitos, pressione ENTER.
          """);

      String entradaPreRequisitos = scanner.nextLine();

      List<String> preRequisitos = new ArrayList<>();

      if (!entradaPreRequisitos.isBlank()) {
        preRequisitos = Arrays.stream(entradaPreRequisitos.split(",")).map(String::trim).toList();
      }

      Disciplina novaDisciplina =
          new Disciplina(nome, cargaHoraria, periodo, creditos, codigoCurso, preRequisitos);

      disciplinaService.cadastrarDisciplina(novaDisciplina);

      System.out.println("Disciplina cadastrada com sucesso.");
      System.out.println("Código gerado: " + novaDisciplina.getCodigo());

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cadastrar disciplina: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Valor numérico inválido.");
    }
  }

  /**
   * Solicita os dados para cadastro de disciplina por coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void cadastrarDisciplina(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);

      System.out.println("Informe o nome da disciplina:");
      String nome = scanner.nextLine();

      System.out.println("Informe a carga horaria:");
      int cargaHoraria = Integer.parseInt(scanner.nextLine());

      System.out.println("Informe o periodo:");
      int periodo = Integer.parseInt(scanner.nextLine());

      System.out.println("Informe a quantidade de creditos:");
      int creditos = Integer.parseInt(scanner.nextLine());

      List<String> preRequisitos = selecionarPreRequisitos(coordenadorLogado.getCodigoCurso());

      Disciplina novaDisciplina =
          new Disciplina(
              nome,
              cargaHoraria,
              periodo,
              creditos,
              coordenadorLogado.getCodigoCurso(),
              preRequisitos);

      disciplinaService.cadastrarDisciplina(novaDisciplina);

      System.out.println("Disciplina cadastrada com sucesso.");
      System.out.println("Codigo gerado: " + novaDisciplina.getCodigo());

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cadastrar disciplina: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Valor numerico invalido.");
    }
  }

  /**
   * Lista as disciplinas cadastradas no terminal.
   */
  public void listarDisciplinas() {
    List<Disciplina> disciplinas = disciplinaService.listarDisciplinas();
    exibirListaDisciplinas(disciplinas);
  }

  /**
   * Lista disciplinas do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void listarDisciplinas(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);
      List<Disciplina> disciplinas =
          disciplinaService.listarDisciplinasPorCurso(coordenadorLogado.getCodigoCurso());
      exibirListaDisciplinas(disciplinas);
    } catch (EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao listar disciplinas: " + e.getMessage());
    }
  }

  /**
   * Lista disciplinas do curso do aluno.
   *
   * @param alunoLogado aluno logado.
   */
  public void listarDisciplinas(Aluno alunoLogado) {
    try {
      validarAlunoComCurso(alunoLogado);
      List<Disciplina> disciplinas =
          disciplinaService.listarDisciplinasPorCurso(alunoLogado.getCodigoCurso());
      exibirListaDisciplinas(disciplinas);
    } catch (EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao listar disciplinas: " + e.getMessage());
    }
  }

  private void validarCoordenadorComCurso(Coordenador coordenadorLogado) {
    if (coordenadorLogado == null
        || coordenadorLogado.getCodigoCurso() == null
        || coordenadorLogado.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Coordenador nao esta vinculado a nenhum curso.");
    }
  }

  private void validarAlunoComCurso(Aluno alunoLogado) {
    if (alunoLogado == null
        || alunoLogado.getCodigoCurso() == null
        || alunoLogado.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Aluno nao esta vinculado a nenhum curso.");
    }
  }

  private List<String> selecionarPreRequisitos(String codigoCurso) {
    System.out.println("A disciplina possui pre-requisitos? (S/N):");
    String resposta = scanner.nextLine();

    if (!resposta.equalsIgnoreCase("S")) {
      return new ArrayList<>();
    }

    List<Disciplina> disciplinasDoCurso = disciplinaService.listarDisciplinasPorCurso(codigoCurso);

    if (disciplinasDoCurso == null || disciplinasDoCurso.isEmpty()) {
      System.out.println("Nao ha disciplinas disponiveis para pre-requisito neste curso.");
      return new ArrayList<>();
    }

    listarDisciplinasParaPreRequisito(disciplinasDoCurso);

    System.out.println("Digite os numeros dos pre-requisitos separados por virgula:");
    String entrada = scanner.nextLine();

    if (entrada == null || entrada.isBlank()) {
      throw new EntradaInvalidaException("Opcao invalida. Informe pelo menos um pre-requisito.");
    }

    List<String> preRequisitos = new ArrayList<>();

    for (String item : entrada.split(",")) {
      int opcao = lerOpcaoPreRequisito(item, disciplinasDoCurso.size());
      String codigoDisciplina = disciplinasDoCurso.get(opcao - 1).getCodigo();

      if (!preRequisitos.contains(codigoDisciplina)) {
        preRequisitos.add(codigoDisciplina);
      }
    }

    return preRequisitos;
  }

  private void listarDisciplinasParaPreRequisito(List<Disciplina> disciplinas) {
    System.out.println("Disciplinas disponiveis para pre-requisito:");

    for (int i = 0; i < disciplinas.size(); i++) {
      Disciplina disciplina = disciplinas.get(i);
      System.out.println(
          (i + 1)
              + " - "
              + disciplina.getNome()
              + " ("
              + disciplina.getCodigo()
              + ")"
              + " | Carga horaria: "
              + disciplina.getCargaHoraria()
              + "h"
              + " | Creditos: "
              + disciplina.getCreditos());
    }
  }

  private int lerOpcaoPreRequisito(String entrada, int quantidadeDisciplinas) {
    int opcao;

    try {
      opcao = Integer.parseInt(entrada.trim());
    } catch (NumberFormatException e) {
      throw new EntradaInvalidaException(
          "Opcao invalida. Digite apenas numeros de opcoes listadas.");
    }

    if (opcao < 1 || opcao > quantidadeDisciplinas) {
      throw new EntradaInvalidaException("Opcao invalida. Escolha uma opcao da lista.");
    }

    return opcao;
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
