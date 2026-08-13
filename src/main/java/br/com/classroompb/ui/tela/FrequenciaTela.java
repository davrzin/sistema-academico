package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.exception.TurmaNaoEncontradaException;
import br.com.classroompb.model.services.AulaService;
import br.com.classroompb.model.services.DiarioService;
import br.com.classroompb.model.services.TurmaService;
import br.com.classroompb.model.services.UsuarioService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Tela de interacao para consulta de frequencia por diario. */
public class FrequenciaTela {

  private final Scanner scanner;
  private final AulaService aulaService = new AulaService();
  private final DiarioService diarioService = new DiarioService();
  private final TurmaService turmaService = new TurmaService();
  private final UsuarioService usuarioService = new UsuarioService();

  /**
   * Cria a tela de frequencia.
   *
   * @param scanner leitor de entrada.
   */
  public FrequenciaTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Consulta a frequencia de um diario de uma turma do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void consultarFrequenciaTurma(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);
      Turma turma = selecionarTurmaDoCurso(coordenadorLogado.getCodigoCurso());
      Diario diario = selecionarDiario(diarioService.listarDiariosPorTurma(turma.getCodigo()));
      if (diario != null) {
        exibirFrequenciaDoDiario(diario, turma);
      }
    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Consulta cancelada.");
    } catch (PersistenciaException | EntradaInvalidaException | TurmaNaoEncontradaException e) {
      exibirErro(e);
    }
  }

  /**
   * Consulta a frequencia de um diario do professor logado.
   *
   * @param professorLogado professor logado.
   */
  public void consultarFrequenciaTurma(Professor professorLogado) {
    try {
      Diario diario = selecionarDiarioDoProfessor(professorLogado);
      if (diario != null) {
        exibirFrequenciaDoDiario(
            diario, turmaService.buscarTurmaPorCodigo(diario.getCodigoTurma()));
      }
    } catch (PersistenciaException | EntradaInvalidaException | TurmaNaoEncontradaException e) {
      exibirErro(e);
    }
  }

  /**
   * Consulta uma aula de um diario do professor logado.
   *
   * @param professorLogado professor logado.
   */
  public void consultarFrequenciaPorAula(Professor professorLogado) {
    try {
      Diario diario = selecionarDiarioDoProfessor(professorLogado);
      if (diario != null) {
        exibirFrequenciaPorAula(
            diario, turmaService.buscarTurmaPorCodigo(diario.getCodigoTurma()));
      }
    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Consulta cancelada.");
    } catch (PersistenciaException | EntradaInvalidaException | TurmaNaoEncontradaException e) {
      exibirErro(e);
    }
  }

  /**
   * Consulta uma aula de um diario de uma turma do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void consultarFrequenciaPorAula(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);
      Turma turma = selecionarTurmaDoCurso(coordenadorLogado.getCodigoCurso());
      Diario diario = selecionarDiario(diarioService.listarDiariosPorTurma(turma.getCodigo()));
      if (diario != null) {
        exibirFrequenciaPorAula(diario, turma);
      }
    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Consulta cancelada.");
    } catch (PersistenciaException | EntradaInvalidaException | TurmaNaoEncontradaException e) {
      exibirErro(e);
    }
  }

  /**
   * Consulta a frequencia do aluno separada por diario.
   *
   * @param alunoLogado aluno logado.
   */
  public void consultarFrequenciaAluno(Aluno alunoLogado) {
    try {
      validarAlunoComCurso(alunoLogado);
      List<Diario> diarios = diariosDasTurmasDoAluno(alunoLogado);

      if (diarios.isEmpty()) {
        System.out.println("Voce nao possui diarios para consultar.");
        return;
      }

      System.out.println("Minha frequencia por diario:");
      for (int i = 0; i < diarios.size(); i++) {
        Diario diario = diarios.get(i);
        Turma turma = turmaService.buscarTurmaPorCodigo(diario.getCodigoTurma());

        if (i > 0) {
          System.out.println();
        }
        System.out.println((i + 1) + " - " + diario.getDescricao()
            + " (codigo: " + diario.getCodigo() + ")");
        System.out.println("    Turma             : " + nomeAmigavelTurma(turma)
            + " (codigo: " + turma.getCodigo() + ")");
        System.out.println("    Professor         : "
            + diarioService.buscarNomeProfessor(diario.getMatriculaProfessor()));
        List<Aula> aulas = aulaService.listarAulasPorDiario(diario.getCodigo());
        int faltasHora = aulaService.calcularFaltasHora(alunoLogado.getMatricula(), aulas);
        exibirTotais(aulas, alunoLogado.getMatricula(), faltasHora);
      }
    } catch (PersistenciaException | EntradaInvalidaException | TurmaNaoEncontradaException e) {
      exibirErro(e);
    }
  }

  private void exibirFrequenciaDoDiario(Diario diario, Turma turma) {
    List<String> alunos = turma.getMatriculados();
    List<Aula> aulas = aulaService.listarAulasPorDiario(diario.getCodigo());

    System.out.println("Frequencia do diario " + diario.getDescricao()
        + " (codigo: " + diario.getCodigo() + ") - " + nomeAmigavelTurma(turma) + ":");
    if (alunos == null || alunos.isEmpty()) {
      System.out.println("A turma nao possui alunos matriculados.");
      return;
    }

    for (int i = 0; i < alunos.size(); i++) {
      String matricula = alunos.get(i);
      if (i > 0) {
        System.out.println();
      }
      System.out.println((i + 1) + " - " + buscarNomeAluno(matricula)
          + " (matricula: " + matricula + ")");
      exibirTotais(aulas, matricula, aulaService.calcularFaltasHora(matricula, aulas));
    }
  }

  private void exibirFrequenciaPorAula(Diario diario, Turma turma) {
    List<Aula> aulas = aulaService.listarAulasPorDiario(diario.getCodigo());
    if (aulas.isEmpty()) {
      System.out.println("O diario " + diario.getDescricao() + " nao possui aulas registradas.");
      return;
    }

    Aula aula = selecionarAula(aulas);
    if (aula != null) {
      exibirPresencasDaAula(aula, turma);
    }
  }

  private Diario selecionarDiarioDoProfessor(Professor professorLogado) {
    if (professorLogado == null
        || professorLogado.getMatricula() == null
        || professorLogado.getMatricula().isBlank()) {
      throw new EntradaInvalidaException("Professor logado nao possui matricula valida.");
    }
    return selecionarDiario(
        diarioService.listarDiariosPorProfessor(professorLogado.getMatricula()));
  }

  private Diario selecionarDiario(List<Diario> diarios) {
    List<Diario> diariosValidos = filtrarDiariosValidos(diarios);
    if (diariosValidos.isEmpty()) {
      throw new EntradaInvalidaException("Nenhum diario disponivel para consulta.");
    }

    System.out.println("Diarios disponiveis:");
    System.out.println("0 - Voltar");
    for (int i = 0; i < diariosValidos.size(); i++) {
      Diario diario = diariosValidos.get(i);
      System.out.println((i + 1) + " - " + diario.getDescricao()
          + " (codigo: " + diario.getCodigo() + ", situacao: "
          + diario.getSituacao().getDescricao() + ")");
    }

    int opcao = EntradaTela.lerOpcaoOuCancelar(
        scanner, "Informe o numero do diario: ", diariosValidos.size());
    if (opcao == 0) {
      System.out.println("Voltando...");
      return null;
    }
    return diariosValidos.get(opcao - 1);
  }

  private Aula selecionarAula(List<Aula> aulas) {
    System.out.println("Aulas registradas:");
    System.out.println("0 - Cancelar");
    for (int i = 0; i < aulas.size(); i++) {
      Aula aula = aulas.get(i);
      System.out.println((i + 1) + " - " + aula.getData() + " as " + aula.getHorario()
          + " (codigo: " + aula.getId() + ")");
    }

    int opcao = EntradaTela.lerOpcaoOuCancelar(
        scanner, "Informe o numero da aula: ", aulas.size());
    return opcao == 0 ? null : aulas.get(opcao - 1);
  }

  private void exibirPresencasDaAula(Aula aula, Turma turma) {
    List<String> alunos = turma.getMatriculados();
    System.out.println("Frequencia da aula de " + aula.getData() + " as " + aula.getHorario()
        + " - " + nomeAmigavelTurma(turma) + ":");
    if (alunos == null || alunos.isEmpty()) {
      System.out.println("A turma nao possui alunos matriculados.");
      return;
    }

    Map<String, Boolean> presencas = aula.getPresencas();
    int presentes = 0;
    for (int i = 0; i < alunos.size(); i++) {
      String matricula = alunos.get(i);
      boolean presente = presencas != null && Boolean.TRUE.equals(presencas.get(matricula));
      if (presente) {
        presentes++;
      }
      System.out.println((i + 1) + " - " + buscarNomeAluno(matricula)
          + " (matricula: " + matricula + "): " + (presente ? "Presente" : "Falta"));
    }
    System.out.println();
    System.out.println("Presentes: " + presentes + "/" + alunos.size());
  }

  private List<Diario> diariosDasTurmasDoAluno(Aluno alunoLogado) {
    List<Diario> diarios = new ArrayList<>();
    for (Turma turma : turmaService.listarTurmasPorCurso(alunoLogado.getCodigoCurso())) {
      if (turma.getMatriculados() != null
          && turma.getMatriculados().contains(alunoLogado.getMatricula())) {
        diarios.addAll(
            filtrarDiariosValidos(diarioService.listarDiariosPorTurma(turma.getCodigo())));
      }
    }
    return diarios;
  }

  private List<Diario> filtrarDiariosValidos(List<Diario> diarios) {
    List<Diario> diariosValidos = new ArrayList<>();
    if (diarios == null) {
      return diariosValidos;
    }
    for (Diario diario : diarios) {
      if (diario != null
          && (diario.getSituacao() == SituacaoDiario.ATIVO
              || diario.getSituacao() == SituacaoDiario.ENCERRADO)) {
        diariosValidos.add(diario);
      }
    }
    return diariosValidos;
  }

  private Turma selecionarTurmaDoCurso(String codigoCurso) {
    List<Turma> turmas = turmaService.listarTurmasPorCurso(codigoCurso);
    if (turmas == null || turmas.isEmpty()) {
      throw new EntradaInvalidaException("Nenhuma turma cadastrada para o curso.");
    }

    System.out.println("Turmas do curso:");
    System.out.println("0 - Cancelar");
    for (int i = 0; i < turmas.size(); i++) {
      System.out.println((i + 1) + " - " + nomeAmigavelTurma(turmas.get(i))
          + " (codigo: " + turmas.get(i).getCodigo() + ")");
    }
    int opcao = EntradaTela.lerOpcaoOuCancelar(
        scanner, "Informe o numero da turma: ", turmas.size());
    if (opcao == 0) {
      throw new EntradaTela.EntradaCanceladaException();
    }
    return turmas.get(opcao - 1);
  }

  private void exibirTotais(List<Aula> aulas, String matriculaAluno, int faltasHora) {
    System.out.println("    Horas ministradas : " + aulaService.calcularHorasMinistradas(aulas));
    System.out.println("    Faltas-hora       : " + faltasHora);
    System.out.println("    Frequencia        : " + formatarFrequencia(matriculaAluno, aulas));
  }

  private String formatarFrequencia(String matriculaAluno, List<Aula> aulas) {
    Double frequencia = aulaService.calcularFrequencia(matriculaAluno, aulas);
    if (frequencia == null) {
      return "ainda nao calculada";
    }
    return String.format("%.2f%%", frequencia);
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

  private String buscarNomeAluno(String matriculaAluno) {
    try {
      return usuarioService.buscarAlunoPorMatricula(matriculaAluno).getNome();
    } catch (RuntimeException e) {
      return matriculaAluno;
    }
  }

  private String nomeAmigavelTurma(Turma turma) {
    return turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina())
        + " - " + turma.getPeriodoLetivo();
  }

  private void exibirErro(RuntimeException e) {
    System.out.println("Ocorreu um erro ao consultar frequencia: " + e.getMessage());
  }
}
