package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioAlunoTurma;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioAlunosTurma;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.services.RelatorioAcademicoService;
import java.util.List;

/**
 * Tela de interacao para relatorios academicos.
 */
public class RelatorioAcademicoTela {

  private final RelatorioAcademicoService relatorioService = new RelatorioAcademicoService();

  /**
   * Cria a tela de relatorios academicos.
   */
  public RelatorioAcademicoTela() {}

  /**
   * Exibe o relatorio de alunos matriculados por turma.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void gerarRelatorioAlunosPorTurma(Coordenador coordenadorLogado) {
    try {
      List<Turma> turmas = relatorioService.listarTurmasDoCoordenador(coordenadorLogado);

      if (turmas.isEmpty()) {
        System.out.println("Nenhuma turma encontrada para este curso.");
        return;
      }

      System.out.println("Relatório de alunos matriculados por turma");

      for (Turma turma : turmas) {
        RelatorioAlunosTurma relatorio =
            relatorioService.gerarRelatorioAlunosTurma(coordenadorLogado, turma);

        exibirRelatorio(relatorio);
      }

    } catch (RuntimeException e) {
      System.out.println("Erro ao gerar relatório: " + e.getMessage());
    }
  }

  private void exibirRelatorio(RelatorioAlunosTurma relatorio) {
    System.out.println();
    System.out.println(
        "Turma: "
            + formatarValor(relatorio.getNomeDisciplina())
            + " - "
            + formatarValor(relatorio.getCodigoTurma()));
    System.out.println("Período letivo: " + formatarValor(relatorio.getPeriodoLetivo()));
    System.out.println("Professor: " + formatarValor(relatorio.getNomeProfessor()));
    System.out.println("Total de alunos matriculados: " + relatorio.getTotalMatriculados());

    if (relatorio.getAlunos().isEmpty()) {
      System.out.println();
      System.out.println("Nenhum aluno matriculado nesta turma.");
      return;
    }

    for (int i = 0; i < relatorio.getAlunos().size(); i++) {
      System.out.println();
      exibirAluno(i + 1, relatorio.getAlunos().get(i));
    }
  }

  private void exibirAluno(int numero, ItemRelatorioAlunoTurma aluno) {
    System.out.println(numero + " - " + formatarValor(aluno.getNome()));
    System.out.println("    Matrícula: " + formatarValor(aluno.getMatricula()));
    System.out.println("    Email: " + formatarValor(aluno.getEmail()));
  }

  private String formatarValor(String valor) {
    if (valor == null || valor.isBlank()) {
      return "-";
    }

    return valor;
  }
}
