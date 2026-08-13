package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Avaliacao;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.services.AvaliacaoService;
import br.com.classroompb.model.services.DiarioService;
import br.com.classroompb.model.services.TurmaService;
import br.com.classroompb.model.services.UsuarioService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Tela de interacao via console para operacoes de avaliacoes e lançamento de notas.
 */
public class AvaliacaoTela {

  private final Scanner scanner;
  private final AvaliacaoService avaliacaoService = new AvaliacaoService();
  private final DiarioService diarioService = new DiarioService();
  private final TurmaService turmaService = new TurmaService();
  private final UsuarioService usuarioService = new UsuarioService();

  /**
   * Cria a tela de avaliações.
   *
   * @param scanner leitor de entrada do console
   */
  public AvaliacaoTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Menu para cadastro de avaliacao vinculada a um diario.
   */
  public void cadastrarAvaliacao(Professor professorLogado) {
    try {
      System.out.println("=== CADASTRO DE AVALIAÇÃO EM DIÁRIO ===");
      List<Diario> diarios = listarDiariosAtivosDoProfessor(professorLogado);

      if (diarios == null || diarios.isEmpty()) {
        System.out.println("Você não possui diários vinculados.");
        return;
      }

      Diario diarioSelecionado = selecionarDiario(diarios);
      if (diarioSelecionado == null) {
        return;
      }

      String descricao =
          EntradaTela.lerTextoObrigatorioOuCancelar(
              scanner, "Descrição da avaliação (ex: P1, Trabalho): ", "Descrição");

      int etapa = selecionarEtapa();
      if (etapa == 0) {
        return;
      }

      Avaliacao novaAvaliacao =
          new Avaliacao(
              diarioSelecionado.getCodigo(),
              descricao,
              Avaliacao.PESO_PADRAO,
              etapa,
              Avaliacao.NOTA_MAXIMA_PADRAO);
      avaliacaoService.cadastrarAvaliacao(
          novaAvaliacao, professorLogado.getMatricula());

      System.out.println("Avaliação cadastrada com sucesso! Código: " + novaAvaliacao.getCodigo());

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Operação cancelada.");
    } catch (NumberFormatException e) {
      System.out.println("Valor numérico inválido informado.");
    } catch (EntradaInvalidaException e) {
      System.out.println("Erro ao cadastrar avaliação: " + e.getMessage());
    }
  }

  private int selecionarEtapa() {
    System.out.println("Selecione a unidade da avaliação:");
    System.out.println("1 - Primeira unidade");
    System.out.println("2 - Segunda unidade");
    System.out.println("0 - Voltar");
    return EntradaTela.lerOpcaoOuCancelar(scanner, "Informe a opção: ", 2);
  }

  /**
   * Lista as avaliacoes de um diario.
   */
  public void listarAvaliacoes(Professor professorLogado) {
    try {
      List<Diario> diarios = listarDiariosAtivosDoProfessor(professorLogado);
      if (diarios == null || diarios.isEmpty()) {
        System.out.println("Você não possui diários vinculados.");
        return;
      }

      Diario diarioSelecionado = selecionarDiario(diarios);
      if (diarioSelecionado == null) {
        return;
      }

      List<Avaliacao> avaliacoes =
          avaliacaoService.listarAvaliacoesPorDiario(diarioSelecionado.getCodigo());
      exibirListaAvaliacoes(avaliacoes);

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Consulta cancelada.");
    }
  }

  /**
   * Exibe as notas do aluno logado separadas por diario e avaliacao.
   *
   * @param alunoLogado aluno autenticado.
   */
  public void consultarNotasAluno(Aluno alunoLogado) {
    try {
      List<Diario> diarios = listarDiariosConsultaveisDoAluno(alunoLogado);
      if (diarios.isEmpty()) {
        System.out.println("Nenhum diário disponível para consulta de notas.");
        return;
      }

      Diario diarioSelecionado = selecionarDiario(diarios);
      if (diarioSelecionado == null) {
        return;
      }

      List<Avaliacao> avaliacoes =
          avaliacaoService.listarAvaliacoesPorDiarioDoAluno(
              diarioSelecionado.getCodigo(), alunoLogado.getMatricula());
      if (avaliacoes.isEmpty()) {
        System.out.println("Nenhuma avaliação cadastrada para este diário.");
        return;
      }

      System.out.println("=== NOTAS POR AVALIAÇÃO ===");
      for (Avaliacao avaliacao : avaliacoes) {
        Double nota =
            avaliacaoService.buscarNotaDoAluno(
                avaliacao.getCodigo(),
                diarioSelecionado.getCodigo(),
                alunoLogado.getMatricula());
        System.out.println("Descrição: " + avaliacao.getDescricao());
        System.out.println("Etapa: " + avaliacao.getEtapa());
        System.out.println("Peso: " + avaliacao.getPeso());
        System.out.println("Nota máxima: " + avaliacao.getNotaMaxima());
        System.out.println(
            "Nota: " + (nota == null ? "ainda não lançada" : nota));
        System.out.println();
      }
    } catch (EntradaInvalidaException e) {
      System.out.println("Erro ao consultar notas: " + e.getMessage());
    }
  }

  /**
   * Exibe as notas de uma avaliacao de diario do professor logado.
   *
   * @param professorLogado professor autenticado.
   */
  public void consultarNotasProfessor(Professor professorLogado) {
    try {
      List<Diario> diarios = listarDiariosConsultaveisDoProfessor(professorLogado);
      if (diarios.isEmpty()) {
        System.out.println("Nenhum diário disponível para consulta de notas.");
        return;
      }

      Diario diarioSelecionado = selecionarDiario(diarios);
      if (diarioSelecionado == null) {
        return;
      }

      List<Avaliacao> avaliacoes =
          avaliacaoService.listarAvaliacoesPorDiarioDoProfessor(
              diarioSelecionado.getCodigo(), professorLogado.getMatricula());
      if (avaliacoes.isEmpty()) {
        System.out.println("Nenhuma avaliação cadastrada para este diário.");
        return;
      }

      Avaliacao avaliacaoSelecionada = selecionarAvaliacao(avaliacoes);
      if (avaliacaoSelecionada == null) {
        return;
      }

      Turma turma = turmaService.buscarTurmaPorCodigo(diarioSelecionado.getCodigoTurma());
      List<String> matriculas = turma.getMatriculados();
      if (matriculas == null || matriculas.isEmpty()) {
        System.out.println("Nenhum aluno matriculado na turma deste diário.");
        return;
      }

      System.out.println("=== NOTAS DA AVALIAÇÃO ===");
      System.out.println("Avaliação: " + avaliacaoSelecionada.getDescricao());
      for (String matricula : matriculas) {
        Aluno aluno = usuarioService.buscarAlunoPorMatricula(matricula);
        Double nota =
            avaliacaoService.buscarNotaDoAlunoParaProfessor(
                avaliacaoSelecionada.getCodigo(),
                diarioSelecionado.getCodigo(),
                matricula,
                professorLogado.getMatricula());
        System.out.println(
            aluno.getNome()
                + " (matrícula: "
                + matricula
                + ") - Nota: "
                + (nota == null ? "pendente" : nota));
      }
    } catch (EntradaInvalidaException e) {
      System.out.println("Erro ao consultar notas: " + e.getMessage());
    }
  }

  /**
   * Lança nota para um aluno em uma avaliacao especifica.
   */
  public void lancarNotaPorAluno(Professor professorLogado) {
    try {
      List<Diario> diarios = listarDiariosAtivosDoProfessor(professorLogado);
      if (diarios == null || diarios.isEmpty()) {
        System.out.println("Você não possui diários vinculados.");
        return;
      }

      Diario diarioSelecionado = selecionarDiario(diarios);
      if (diarioSelecionado == null) {
        return;
      }

      List<Avaliacao> avaliacoes =
          avaliacaoService.listarAvaliacoesPorDiario(diarioSelecionado.getCodigo());
      if (avaliacoes.isEmpty()) {
        System.out.println("Nenhuma avaliação cadastrada para este diário.");
        return;
      }

      System.out.println("Selecione a avaliação:");
      for (int i = 0; i < avaliacoes.size(); i++) {
        Avaliacao av = avaliacoes.get(i);
        System.out.println(
            (i + 1)
                + " - "
                + av.getDescricao()
                + " (Nota Máx: "
                + av.getNotaMaxima()
                + ", Peso: "
                + av.getPeso()
                + ")");
      }

      System.out.println("0 - Voltar");
      int opAv = EntradaTela.lerOpcaoOuCancelar(scanner, "Opção: ", avaliacoes.size());
      if (opAv == 0) {
        return;
      }
      Avaliacao avSelecionada = avaliacoes.get(opAv - 1);

      String matriculaAluno = selecionarAlunoMatriculado(diarioSelecionado);
      if (matriculaAluno == null) {
        return;
      }

      Double notaAtual =
          avaliacaoService.buscarNotaDoAluno(avSelecionada.getCodigo(), matriculaAluno);
      if (notaAtual == null) {
        System.out.println("Nota atual: não lançada.");
      } else {
        System.out.println("Nota atual: " + notaAtual);
      }

      String strNota =
          EntradaTela.lerTextoObrigatorioOuCancelar(
              scanner,
              "Informe a nota obtida (0.0 a " + avSelecionada.getNotaMaxima() + "): ",
              "Nota");
      float nota = Float.parseFloat(strNota);

      avaliacaoService.lancarNota(
          avSelecionada.getCodigo(), matriculaAluno, nota, professorLogado.getMatricula());
      System.out.println("Nota lançada com sucesso!");

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Lançamento de nota cancelado.");
    } catch (NumberFormatException e) {
      System.out.println("Valor de nota inválido.");
    } catch (EntradaInvalidaException e) {
      System.out.println("Erro ao lançar nota: " + e.getMessage());
    }
  }

  private Diario selecionarDiario(List<Diario> diarios) {
    System.out.println("Selecione o Diário:");
    System.out.println("0 - Voltar");
    for (int i = 0; i < diarios.size(); i++) {
      System.out.println(
          (i + 1)
              + " - "
              + diarios.get(i).getDescricao()
              + " ("
              + diarios.get(i).getCodigo()
              + ")");
    }
    int op = EntradaTela.lerOpcaoOuCancelar(scanner, "Informe a opção: ", diarios.size());
    if (op == 0) {
      return null;
    }
    return diarios.get(op - 1);
  }

  private List<Diario> listarDiariosAtivosDoProfessor(Professor professorLogado) {
    return diarioService.listarDiariosPorProfessor(professorLogado.getMatricula()).stream()
        .filter(diario -> diario.getSituacao() == SituacaoDiario.ATIVO)
        .toList();
  }

  private List<Diario> listarDiariosConsultaveisDoProfessor(Professor professorLogado) {
    return diarioService.listarDiariosPorProfessor(professorLogado.getMatricula()).stream()
        .filter(
            diario ->
                diario.getSituacao() == SituacaoDiario.ATIVO
                    || diario.getSituacao() == SituacaoDiario.ENCERRADO)
        .toList();
  }

  private Avaliacao selecionarAvaliacao(List<Avaliacao> avaliacoes) {
    System.out.println("Selecione a avaliação:");
    System.out.println("0 - Voltar");
    for (int i = 0; i < avaliacoes.size(); i++) {
      Avaliacao avaliacao = avaliacoes.get(i);
      System.out.println(
          (i + 1)
              + " - "
              + avaliacao.getDescricao()
              + " (etapa: "
              + avaliacao.getEtapa()
              + ")");
    }

    int opcao =
        EntradaTela.lerOpcaoOuCancelar(scanner, "Informe a opção: ", avaliacoes.size());
    if (opcao == 0) {
      return null;
    }
    return avaliacoes.get(opcao - 1);
  }

  private List<Diario> listarDiariosConsultaveisDoAluno(Aluno alunoLogado) {
    List<Diario> diarios = new ArrayList<>();

    for (Turma turma : turmaService.listarTurmasPorCurso(alunoLogado.getCodigoCurso())) {
      boolean matriculado =
          turma.getMatriculados() != null
              && turma.getMatriculados().stream()
                  .anyMatch(
                      matricula ->
                          matricula.equalsIgnoreCase(alunoLogado.getMatricula().trim()));
      if (!matriculado) {
        continue;
      }

      for (Diario diario : diarioService.listarDiariosPorTurma(turma.getCodigo())) {
        if (diario.getSituacao() == SituacaoDiario.ATIVO
            || diario.getSituacao() == SituacaoDiario.ENCERRADO) {
          diarios.add(diario);
        }
      }
    }

    return diarios;
  }

  private String selecionarAlunoMatriculado(Diario diario) {
    Turma turma = turmaService.buscarTurmaPorCodigo(diario.getCodigoTurma());
    List<String> matriculas = turma.getMatriculados();

    if (matriculas == null || matriculas.isEmpty()) {
      System.out.println("Nenhum aluno matriculado na turma deste diário.");
      return null;
    }

    System.out.println("Selecione o aluno:");
    System.out.println("0 - Voltar");
    for (int i = 0; i < matriculas.size(); i++) {
      System.out.println((i + 1) + " - " + matriculas.get(i));
    }

    int opcao =
        EntradaTela.lerOpcaoOuCancelar(scanner, "Informe a opção: ", matriculas.size());
    if (opcao == 0) {
      return null;
    }
    return matriculas.get(opcao - 1);
  }

  private void exibirListaAvaliacoes(List<Avaliacao> avaliacoes) {
    if (avaliacoes == null || avaliacoes.isEmpty()) {
      System.out.println("Nenhuma avaliação cadastrada.");
      return;
    }
    System.out.println("=== AVALIAÇÕES DO DIÁRIO ===");
    for (Avaliacao av : avaliacoes) {
      System.out.println(
          "Código: "
              + av.getCodigo()
              + " | Descrição: "
              + av.getDescricao()
              + " | Etapa: "
              + av.getEtapa()
              + " | Peso: "
              + av.getPeso()
              + " | Nota Máx: "
              + av.getNotaMaxima());
    }
  }
}
