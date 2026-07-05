package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.CursoService;
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
  private final CursoService cursoService = new CursoService();

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
      final String nome =
          EntradaTela.lerTextoObrigatorioOuCancelar(
              scanner, "Informe o nome da disciplina: ", "Nome");

      final int cargaHoraria =
          EntradaTela.lerInteiroPositivoOuCancelar(scanner, "Informe a carga horaria: ");

      final int periodo = EntradaTela.lerInteiroPositivoOuCancelar(scanner, "Informe o periodo: ");

      final int creditos =
          EntradaTela.lerInteiroPositivoOuCancelar(
              scanner, "Informe a quantidade de creditos: ");

      final String codigoCurso =
          EntradaTela.lerTextoObrigatorioOuCancelar(
              scanner, "Informe o codigo do curso: ", "Curso");

      System.out.println(
          """
          Informe os pre-requisitos da disciplina.
          Digite os codigos separados por virgula.
          Caso nao exista pre-requisitos, pressione ENTER.
          """);

      String entradaPreRequisitos = scanner.nextLine();

      if ("0".equals(entradaPreRequisitos.trim())) {
        throw new EntradaTela.EntradaCanceladaException();
      }

      List<String> preRequisitos = new ArrayList<>();

      if (!entradaPreRequisitos.isBlank()) {
        preRequisitos = Arrays.stream(entradaPreRequisitos.split(",")).map(String::trim).toList();
      }

      Disciplina novaDisciplina =
          new Disciplina(nome, cargaHoraria, periodo, creditos, codigoCurso, preRequisitos);

      disciplinaService.cadastrarDisciplina(novaDisciplina);

      System.out.println("Disciplina cadastrada com sucesso.");
      System.out.println("Codigo gerado: " + novaDisciplina.getCodigo());

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Cadastro de disciplina cancelado.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cadastrar disciplina: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Valor numerico invalido.");
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

      String nome =
          EntradaTela.lerTextoObrigatorioOuCancelar(
              scanner, "Informe o nome da disciplina: ", "Nome");

      int cargaHoraria =
          EntradaTela.lerInteiroPositivoOuCancelar(scanner, "Informe a carga horaria: ");

      int periodo = EntradaTela.lerInteiroPositivoOuCancelar(scanner, "Informe o periodo: ");

      int creditos =
          EntradaTela.lerInteiroPositivoOuCancelar(
              scanner, "Informe a quantidade de creditos: ");

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

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Cadastro de disciplina cancelado.");

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
    exibirListaDisciplinasDetalhada(disciplinas);
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
      exibirListaDisciplinasDetalhada(disciplinas);
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
      exibirListaDisciplinasAmigavel(disciplinas);
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
    String resposta = lerRespostaSimNao("A disciplina possui pre-requisitos? (S/N): ");

    if (!resposta.equalsIgnoreCase("S")) {
      return new ArrayList<>();
    }

    List<Disciplina> disciplinasDoCurso = disciplinaService.listarDisciplinasPorCurso(codigoCurso);

    if (disciplinasDoCurso == null || disciplinasDoCurso.isEmpty()) {
      System.out.println("Nao ha disciplinas disponiveis para pre-requisito neste curso.");
      return new ArrayList<>();
    }

    listarDisciplinasParaPreRequisito(disciplinasDoCurso);
    System.out.println();
    System.out.println("0 - Finalizar selecao de pre-requisitos");

    while (true) {
      System.out.print("Digite os numeros dos pre-requisitos separados por virgula: ");
      String entrada = scanner.nextLine();

      if ("0".equals(entrada.trim())) {
        return new ArrayList<>();
      }

      if (entrada == null || entrada.isBlank()) {
        System.out.println("Opcao invalida. Informe pelo menos um pre-requisito.");
        System.out.println();
        continue;
      }

      try {
        List<String> preRequisitos = new ArrayList<>();

        for (String item : entrada.split(",")) {
          int opcao = lerOpcaoPreRequisito(item, disciplinasDoCurso.size());
          String codigoDisciplina = disciplinasDoCurso.get(opcao - 1).getCodigo();

          if (!preRequisitos.contains(codigoDisciplina)) {
            preRequisitos.add(codigoDisciplina);
          }
        }

        return preRequisitos;
      } catch (EntradaInvalidaException e) {
        System.out.println(e.getMessage());
        System.out.println();
      }
    }
  }

  private String lerRespostaSimNao(String prompt) {
    while (true) {
      System.out.print(prompt);
      String resposta = scanner.nextLine();

      if ("S".equalsIgnoreCase(resposta) || "N".equalsIgnoreCase(resposta)) {
        return resposta;
      }

      System.out.println("Opcao invalida. Escolha S ou N.");
      System.out.println();
    }
  }

  private void listarDisciplinasParaPreRequisito(List<Disciplina> disciplinas) {
    System.out.println("Disciplinas disponiveis para pre-requisito:");

    for (int i = 0; i < disciplinas.size(); i++) {
      Disciplina disciplina = disciplinas.get(i);
      System.out.println();
      System.out.println((i + 1) + " - " + formatarValor(disciplina.getNome()));
      System.out.println("    Codigo interno: " + formatarValor(disciplina.getCodigo()));
      System.out.println("    Carga horaria: " + disciplina.getCargaHoraria() + "h");
      System.out.println("    Creditos: " + disciplina.getCreditos());
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

  private void exibirListaDisciplinasAmigavel(List<Disciplina> disciplinas) {
    if (disciplinas == null || disciplinas.isEmpty()) {
      System.out.println("Nenhuma disciplina cadastrada.");
      return;
    }

    for (int i = 0; i < disciplinas.size(); i++) {
      exibirDisciplinaAmigavel(i + 1, disciplinas.get(i));
    }
  }

  private void exibirListaDisciplinasDetalhada(List<Disciplina> disciplinas) {
    if (disciplinas == null || disciplinas.isEmpty()) {
      System.out.println("Nenhuma disciplina cadastrada.");
      return;
    }

    for (int i = 0; i < disciplinas.size(); i++) {
      exibirDisciplinaDetalhada(i + 1, disciplinas.get(i));
    }
  }

  private void exibirDisciplinaAmigavel(int numero, Disciplina disciplina) {
    System.out.println();
    System.out.println(numero + " - " + formatarValor(disciplina.getNome()));
    System.out.println("    Carga horaria: " + disciplina.getCargaHoraria() + "h");
    System.out.println("    Periodo recomendado: " + disciplina.getPeriodo());
    System.out.println("    Creditos: " + disciplina.getCreditos());
    System.out.println("    Pre-requisitos: " + formatarPreRequisitos(disciplina));
  }

  private void exibirDisciplinaDetalhada(int numero, Disciplina disciplina) {
    System.out.println();
    System.out.println(numero + " - " + formatarValor(disciplina.getNome()));
    System.out.println("    Codigo interno: " + formatarValor(disciplina.getCodigo()));
    System.out.println("    Curso: " + buscarNomeCurso(disciplina.getCodigoCurso()));
    System.out.println("    Carga horaria: " + disciplina.getCargaHoraria() + "h");
    System.out.println("    Periodo recomendado: " + disciplina.getPeriodo());
    System.out.println("    Creditos: " + disciplina.getCreditos());
    System.out.println("    Pre-requisitos: " + formatarPreRequisitos(disciplina));
    System.out.println();
  }

  private String formatarPreRequisitos(Disciplina disciplina) {
    List<String> preRequisitos = disciplina.getPreRequisitos();

    if (preRequisitos == null || preRequisitos.isEmpty()) {
      return "Nenhum";
    }

    List<String> nomesPreRequisitos = new ArrayList<>();

    for (String codigoDisciplina : preRequisitos) {
      nomesPreRequisitos.add(buscarNomeDisciplina(codigoDisciplina));
    }

    return String.join(", ", nomesPreRequisitos);
  }

  private String buscarNomeDisciplina(String codigoDisciplina) {
    if (codigoDisciplina == null || codigoDisciplina.isBlank()) {
      return "-";
    }

    for (Disciplina disciplina : disciplinaService.listarDisciplinas()) {
      if (disciplina.getCodigo() != null
          && disciplina.getCodigo().equalsIgnoreCase(codigoDisciplina.trim())) {
        return formatarValor(disciplina.getNome());
      }
    }

    return codigoDisciplina;
  }

  private String buscarNomeCurso(String codigoCurso) {
    if (codigoCurso == null || codigoCurso.isBlank()) {
      return "-";
    }

    for (Curso curso : cursoService.listarCursos()) {
      if (curso.getCodigo() != null && curso.getCodigo().equalsIgnoreCase(codigoCurso.trim())) {
        return formatarValor(curso.getNome());
      }
    }

    return codigoCurso;
  }

  private String formatarValor(String valor) {
    if (valor == null || valor.isBlank()) {
      return "-";
    }

    return valor;
  }
}
