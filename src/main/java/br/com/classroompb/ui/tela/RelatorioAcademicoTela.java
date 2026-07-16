package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioAlunoTurma;
import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioReprovacaoDisciplina;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioAlunosTurma;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioOcupacaoVagas;
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

  /**
   * Exibe o relatorio de ocupacao de vagas por turma.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void gerarRelatorioOcupacaoVagas(Coordenador coordenadorLogado) {
    try {
      List<Turma> turmas = relatorioService.listarTurmasDoCoordenador(coordenadorLogado);

      if (turmas.isEmpty()) {
        System.out.println("Nenhuma turma encontrada para este curso.");
        return;
      }

      System.out.println("Relatório de ocupação de vagas");

      for (Turma turma : turmas) {
        RelatorioOcupacaoVagas relatorio =
            relatorioService.gerarRelatorioOcupacaoVagas(coordenadorLogado, turma);

        exibirRelatorioOcupacaoVagas(relatorio);
      }

    } catch (RuntimeException e) {
      System.out.println("Erro ao gerar relatório: " + e.getMessage());
    }
  }

  /**
   * Exibe o relatorio de reprovacao por disciplina.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void gerarRelatorioReprovacaoPorDisciplina(Coordenador coordenadorLogado) {
    try {
      List<ItemRelatorioReprovacaoDisciplina> relatorios =
          relatorioService.gerarRelatorioReprovacaoPorDisciplina(coordenadorLogado);

      if (relatorios.isEmpty()) {
        System.out.println("Nenhuma disciplina encontrada para este curso.");
        return;
      }

      System.out.println("Relatório de reprovação por disciplina");

      for (ItemRelatorioReprovacaoDisciplina relatorio : relatorios) {
        exibirRelatorioReprovacao(relatorio);
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

  private void exibirRelatorioOcupacaoVagas(RelatorioOcupacaoVagas relatorio) {
    System.out.println();
    System.out.println(
        "Turma: "
            + formatarValor(relatorio.getNomeDisciplina())
            + " - "
            + formatarValor(relatorio.getCodigoTurma()));
    System.out.println("Período letivo: " + formatarValor(relatorio.getPeriodoLetivo()));
    System.out.println("Total de vagas: " + relatorio.getLimiteVagas());
    System.out.println("Vagas ocupadas: " + relatorio.getVagasOcupadas());
    System.out.println("Vagas disponíveis: " + relatorio.getVagasDisponiveis());
    System.out.printf("Ocupação: %.1f%%%n", relatorio.getPercentualOcupacao());
  }

  private void exibirRelatorioReprovacao(ItemRelatorioReprovacaoDisciplina relatorio) {
    System.out.println();
    System.out.println(
        "Disciplina: "
            + formatarValor(relatorio.getNomeDisciplina())
            + " - "
            + formatarValor(relatorio.getCodigoDisciplina()));
    System.out.println(
        "Total de resultados finais: " + relatorio.getTotalResultadosFinais());
    System.out.println(
        "Reprovados por nota: " + relatorio.getTotalReprovadosPorNota());
    System.out.println(
        "Reprovados por falta: " + relatorio.getTotalReprovadosPorFalta());
    System.out.println("Total de reprovados: " + relatorio.getTotalReprovados());
    System.out.printf(
        "Percentual de reprovação: %.1f%%%n", relatorio.getPercentualReprovacao());
  }

  private String formatarValor(String valor) {
    if (valor == null || valor.isBlank()) {
      return "-";
    }

    return valor;
  }
}
