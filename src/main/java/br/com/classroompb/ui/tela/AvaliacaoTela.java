package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Avaliacao;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.services.AvaliacaoService;
import br.com.classroompb.model.services.DiarioService;
import java.util.List;
import java.util.Scanner;

/**
 * Tela de interacao via console para operacoes de avaliacoes e lançamento de notas.
 */
public class AvaliacaoTela {

  private final Scanner scanner;
  private final AvaliacaoService avaliacaoService = new AvaliacaoService();
  private final DiarioService diarioService = new DiarioService();

  public AvaliacaoTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Menu para cadastro de avaliacao vinculada a um diario.
   */
  public void cadastrarAvaliacao(Professor professorLogado) {
    try {
      System.out.println("=== CADASTRO DE AVALIAÇÃO EM DIÁRIO ===");
      List<Diario> diarios = diarioService.listarDiariosPorProfessor(professorLogado.getMatricula());

      if (diarios == null || diarios.isEmpty()) {
        System.out.println("Você não possui diários vinculados.");
        return;
      }

      Diario diarioSelecionado = selecionarDiario(diarios);
      if (diarioSelecionado == null) return;

      String descricao = EntradaTela.lerTextoObrigatorioOuCancelar(scanner, "Descrição da avaliação (ex: P1, Trabalho): ", "Descrição");
      
      String strPeso = EntradaTela.lerTextoObrigatorioOuCancelar(scanner, "Informe o peso da avaliação (ex: 1.0, 2.0): ", "Peso");
      float peso = Float.parseFloat(strPeso);

      int etapa = EntradaTela.lerInteiroPositivoOuCancelar(scanner, "Informe a etapa (ex: 1 para N1, 2 para N2): ");

      String strNotaMax = EntradaTela.lerTextoObrigatorioOuCancelar(scanner, "Informe a nota máxima (ex: 10.0): ", "Nota Máxima");
      float notaMaxima = Float.parseFloat(strNotaMax);

      Avaliacao novaAvaliacao = new Avaliacao(diarioSelecionado.getCodigo(), descricao, peso, etapa, notaMaxima);
      avaliacaoService.cadastrarAvaliacao(novaAvaliacao);

      System.out.println("Avaliação cadastrada com sucesso! Código: " + novaAvaliacao.getCodigo());

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Operação cancelada.");
    } catch (NumberFormatException e) {
      System.out.println("Valor numérico inválido informado.");
    } catch (EntradaInvalidaException e) {
      System.out.println("Erro ao cadastrar avaliação: " + e.getMessage());
    }
  }

  /**
   * Lista as avaliacoes de um diario.
   */
  public void listarAvaliacoes(Professor professorLogado) {
    try {
      List<Diario> diarios = diarioService.listarDiariosPorProfessor(professorLogado.getMatricula());
      if (diarios == null || diarios.isEmpty()) {
        System.out.println("Você não possui diários vinculados.");
        return;
      }

      Diario diarioSelecionado = selecionarDiario(diarios);
      if (diarioSelecionado == null) return;

      List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesPorDiario(diarioSelecionado.getCodigo());
      exibirListaAvaliacoes(avaliacoes);

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Consulta cancelada.");
    }
  }

  /**
   * Lança nota para um aluno em uma avaliacao especifica.
   */
  public void lancarNotaPorAluno(Professor professorLogado) {
    try {
      List<Diario> diarios = diarioService.listarDiariosPorProfessor(professorLogado.getMatricula());
      if (diarios == null || diarios.isEmpty()) {
        System.out.println("Você não possui diários vinculados.");
        return;
      }

      Diario diarioSelecionado = selecionarDiario(diarios);
      if (diarioSelecionado == null) return;

      List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesPorDiario(diarioSelecionado.getCodigo());
      if (avaliacoes.isEmpty()) {
        System.out.println("Nenhuma avaliação cadastrada para este diário.");
        return;
      }

      System.out.println("Selecione a avaliação:");
      for (int i = 0; i < avaliacoes.size(); i++) {
        Avaliacao av = avaliacoes.get(i);
        System.out.println((i + 1) + " - " + av.getDescricao() + " (Nota Máx: " + av.getNotaMaxima() + ", Peso: " + av.getPeso() + ")");
      }

      int opAv = EntradaTela.lerOpcaoOuCancelar(scanner, "Opção: ", avaliacoes.size());
      if (opAv == 0) return;
      Avaliacao avSelecionada = avaliacoes.get(opAv - 1);

      String matriculaAluno = EntradaTela.lerTextoObrigatorioOuCancelar(scanner, "Matrícula do aluno: ", "Matrícula");
      
      String strNota = EntradaTela.lerTextoObrigatorioOuCancelar(scanner, "Informe a nota obtida (0.0 a " + avSelecionada.getNotaMaxima() + "): ", "Nota");
      float nota = Float.parseFloat(strNota);

      avaliacaoService.lancarNota(avSelecionada.getCodigo(), matriculaAluno, nota);
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
    System.out.println("0 - Cancelar");
    for (int i = 0; i < diarios.size(); i++) {
      System.out.println((i + 1) + " - " + diarios.get(i).getDescricao() + " (" + diarios.get(i).getCodigo() + ")");
    }
    int op = EntradaTela.lerOpcaoOuCancelar(scanner, "Informe a opção: ", diarios.size());
    if (op == 0) return null;
    return diarios.get(op - 1);
  }

  private void exibirListaAvaliacoes(List<Avaliacao> avaliacoes) {
    if (avaliacoes == null || avaliacoes.isEmpty()) {
      System.out.println("Nenhuma avaliação cadastrada.");
      return;
    }
    System.out.println("=== AVALIAÇÕES DO DIÁRIO ===");
    for (Avaliacao av : avaliacoes) {
      System.out.println("Código: " + av.getCodigo() + " | Descrição: " + av.getDescricao() +
          " | Etapa: " + av.getEtapa() + " | Peso: " + av.getPeso() + " | Nota Máx: " + av.getNotaMaxima());
    }
  }
}