package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.ItemHistoricoAcademico;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.services.HistoricoAcademicoService;
import br.com.classroompb.model.services.UsuarioService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Tela de interacao para consulta de historico academico.
 */
public class HistoricoAcademicoTela {

  private final Scanner scanner;
  private final HistoricoAcademicoService historicoService = new HistoricoAcademicoService();
  private final UsuarioService usuarioService = new UsuarioService();

  /**
   * Cria a tela de historico academico.
   *
   * @param scanner leitor de entrada.
   */
  public HistoricoAcademicoTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Exibe o historico academico do aluno logado.
   *
   * @param alunoLogado aluno logado.
   */
  public void listarHistoricoAluno(Aluno alunoLogado) {
    try {
      exibirHistorico(alunoLogado, "Histórico acadêmico:");
    } catch (RuntimeException e) {
      System.out.println("Erro ao consultar historico academico: " + e.getMessage());
    }
  }

  /**
   * Permite ao coordenador consultar o historico de alunos do seu curso.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void consultarHistoricoAluno(Coordenador coordenadorLogado) {
    try {
      List<Aluno> alunos = listarAlunosDoCurso(coordenadorLogado);

      if (alunos.isEmpty()) {
        System.out.println("Nenhum aluno encontrado neste curso.");
        return;
      }

      exibirAlunosDoCurso(alunos);
      System.out.print("Informe o numero do aluno: ");
      int opcao = lerOpcaoAluno(alunos.size());

      if (opcao == 0) {
        System.out.println("Voltando...");
        return;
      }

      Aluno alunoSelecionado = alunos.get(opcao - 1);
      exibirHistorico(
          alunoSelecionado, "Histórico acadêmico de " + alunoSelecionado.getNome() + ":");

    } catch (RuntimeException e) {
      System.out.println("Aluno nao encontrado ou nao pertence ao seu curso.");
    }
  }

  private List<Aluno> listarAlunosDoCurso(Coordenador coordenadorLogado) {
    List<Aluno> alunos = new ArrayList<>();

    for (Usuario usuario : usuarioService.listarUsuarios(coordenadorLogado)) {
      if (usuario instanceof Aluno aluno) {
        alunos.add(aluno);
      }
    }

    return alunos;
  }

  private void exibirAlunosDoCurso(List<Aluno> alunos) {
    System.out.println("Alunos do curso:");

    for (int i = 0; i < alunos.size(); i++) {
      Aluno aluno = alunos.get(i);

      if (i > 0) {
        System.out.println();
      }

      System.out.println((i + 1) + " - " + formatarValor(aluno.getNome()));
      System.out.println("    Matricula: " + formatarValor(aluno.getMatricula()));
      System.out.println("    Email: " + formatarValor(aluno.getEmail()));
    }

    System.out.println();
    System.out.println("0 - Voltar");
  }

  private int lerOpcaoAluno(int quantidadeAlunos) {
    while (true) {
      String entrada = scanner.nextLine();

      try {
        int opcao = Integer.parseInt(entrada);

        if (opcao >= 0 && opcao <= quantidadeAlunos) {
          return opcao;
        }
      } catch (NumberFormatException e) {
        // Trata abaixo como opcao invalida.
      }

      System.out.println("Opcao invalida. Escolha uma opcao da lista.");
      System.out.println();
      System.out.print("Informe o numero do aluno: ");
    }
  }

  private void exibirHistorico(Aluno aluno, String titulo) {
    List<ItemHistoricoAcademico> historico = historicoService.listarHistoricoAluno(aluno);

    if (historico.isEmpty()) {
      System.out.println("Nenhum historico academico encontrado para este aluno.");
      return;
    }

    System.out.println(titulo);

    for (int i = 0; i < historico.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirItemHistorico(i + 1, historico.get(i));
    }
  }

  private void exibirItemHistorico(int numero, ItemHistoricoAcademico item) {
    System.out.println(numero + " - " + formatarValor(item.getNomeDisciplina()));
    System.out.println("    Período letivo: " + formatarValor(item.getPeriodoLetivo()));
    System.out.println("    Professor: " + formatarValor(item.getNomeProfessor()));
    System.out.println("    Turma: " + formatarValor(item.getCodigoTurma()));
    System.out.println("    Nota final: " + formatarDecimal(item.getNotaFinal()));
    System.out.println("    Frequencia: " + formatarFrequencia(item.getFrequencia()));
    System.out.println("    Situação: " + formatarValor(item.getSituacao()));
  }

  private String formatarValor(String valor) {
    if (valor == null || valor.isBlank()) {
      return "-";
    }

    return valor;
  }

  private String formatarDecimal(Double valor) {
    if (valor == null) {
      return "--";
    }

    return String.format("%.2f", valor);
  }

  private String formatarFrequencia(Double frequencia) {
    if (frequencia == null) {
      return "--";
    }

    return String.format("%.1f%%", frequencia);
  }
}
