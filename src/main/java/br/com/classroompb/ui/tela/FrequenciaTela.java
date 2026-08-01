package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.exception.TurmaNaoEncontradaException;
import br.com.classroompb.model.services.BoletimService;
import br.com.classroompb.model.services.TurmaService;
import br.com.classroompb.model.services.UsuarioService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Tela de interacao para consulta de frequencia de turmas e alunos.
 */
public class FrequenciaTela {

  private final Scanner scanner;
  private final TurmaService turmaService = new TurmaService();
  private final BoletimService boletimService = new BoletimService();
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
   * Consulta a frequencia dos alunos de uma turma do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void consultarFrequenciaTurma(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);

      Turma turmaSelecionada = selecionarTurmaDoCurso(coordenadorLogado.getCodigoCurso());
      exibirFrequenciaDaTurma(turmaSelecionada);

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Consulta cancelada.");

    } catch (PersistenciaException | EntradaInvalidaException | TurmaNaoEncontradaException e) {
      System.out.println("Ocorreu um erro ao consultar frequência: " + e.getMessage());
    }
  }

  /**
   * Consulta a frequencia dos alunos de uma turma do professor logado.
   *
   * @param professorLogado professor logado.
   */
  public void consultarFrequenciaTurma(Professor professorLogado) {
    try {
      Turma turmaSelecionada = selecionarTurmaDoProfessor(professorLogado);

      if (turmaSelecionada == null) {
        return;
      }

      exibirFrequenciaDaTurma(turmaSelecionada);

    } catch (PersistenciaException | EntradaInvalidaException | TurmaNaoEncontradaException e) {
      System.out.println("Ocorreu um erro ao consultar frequência: " + e.getMessage());
    }
  }

  /**
   * Consulta a frequencia do aluno logado em cada turma matriculada.
   *
   * @param alunoLogado aluno logado.
   */
  public void consultarFrequenciaAluno(Aluno alunoLogado) {
    try {
      validarAlunoComCurso(alunoLogado);

      List<Turma> turmasMatriculadas = turmasMatriculadasDoAluno(alunoLogado);

      if (turmasMatriculadas.isEmpty()) {
        System.out.println("Você não está matriculado em nenhuma turma.");
        return;
      }

      System.out.println("Minha frequência:");

      for (int i = 0; i < turmasMatriculadas.size(); i++) {
        if (i > 0) {
          System.out.println();
        }

        exibirFrequenciaDoAlunoNaTurma(
            i + 1, turmasMatriculadas.get(i), alunoLogado.getMatricula());
      }

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao consultar frequência: " + e.getMessage());
    }
  }

  private void exibirFrequenciaDaTurma(Turma turma) {
    List<String> alunosMatriculados = turma.getMatriculados();

    System.out.println("Frequência da turma " + nomeAmigavelTurma(turma) + ":");

    if (alunosMatriculados == null || alunosMatriculados.isEmpty()) {
      System.out.println("A turma não possui alunos matriculados.");
      return;
    }

    List<Aula> aulasTurma = turmaService.listarAulasPorTurma(turma.getCodigo());
    int totalAulas = aulasTurma.size();

    for (int i = 0; i < alunosMatriculados.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirFrequenciaDoAluno(i + 1, alunosMatriculados.get(i), turma, aulasTurma, totalAulas);
    }
  }

  private void exibirFrequenciaDoAluno(
      int numero, String matriculaAluno, Turma turma, List<Aula> aulasTurma, int totalAulas) {
    int faltas = contarFaltas(matriculaAluno, aulasTurma);
    Boletim boletim = boletimService.buscarBoletimPorAlunoTurma(matriculaAluno, turma.getCodigo());
    Double frequencia = boletim == null ? null : boletim.getFrequencia();

    System.out.println(numero + " - " + buscarNomeAluno(matriculaAluno)
        + " (matrícula: " + matriculaAluno + ")");
    System.out.println("    Aulas ministradas : " + totalAulas);
    System.out.println("    Faltas            : " + faltas);
    System.out.println("    Frequência        : " + formatarFrequencia(frequencia));
  }

  private void exibirFrequenciaDoAlunoNaTurma(int numero, Turma turma, String matriculaAluno) {
    List<Aula> aulasTurma = turmaService.listarAulasPorTurma(turma.getCodigo());
    int totalAulas = aulasTurma.size();
    int faltas = contarFaltas(matriculaAluno, aulasTurma);
    Boletim boletim = boletimService.buscarBoletimPorAlunoTurma(matriculaAluno, turma.getCodigo());
    Double frequencia = boletim == null ? null : boletim.getFrequencia();

    System.out.println(numero + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Professor         : " + buscarNomeProfessor(turma));
    System.out.println("    Aulas ministradas : " + totalAulas);
    System.out.println("    Faltas            : " + faltas);
    System.out.println("    Frequência        : " + formatarFrequencia(frequencia));
  }

  private int contarFaltas(String matriculaAluno, List<Aula> aulasTurma) {
    int faltas = 0;

    for (Aula aula : aulasTurma) {
      Map<String, Boolean> presencas = aula.getPresencas();
      Boolean estaPresente = presencas == null ? null : presencas.get(matriculaAluno);

      if (!Boolean.TRUE.equals(estaPresente)) {
        faltas++;
      }
    }

    return faltas;
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
          + " (código: " + turmas.get(i).getCodigo() + ")");
    }

    int opcao =
        EntradaTela.lerOpcaoOuCancelar(scanner, "Informe o número da turma: ", turmas.size());

    if (opcao == 0) {
      throw new EntradaTela.EntradaCanceladaException();
    }

    return turmas.get(opcao - 1);
  }

  private Turma selecionarTurmaDoProfessor(Professor professorLogado) {
    List<Turma> turmas = turmaService.listarTurmasPorProfessor(professorLogado.getMatricula());

    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Professor não possui turmas cadastradas.");
      return null;
    }

    System.out.println("Turmas do professor:");
    System.out.println("0 - Voltar");

    for (int i = 0; i < turmas.size(); i++) {
      System.out.println((i + 1) + " - " + nomeAmigavelTurma(turmas.get(i))
          + " (código: " + turmas.get(i).getCodigo() + ")");
    }

    int opcao =
        EntradaTela.lerOpcaoOuCancelar(scanner, "Informe o número da turma: ", turmas.size());

    if (opcao == 0) {
      System.out.println("Voltando...");
      return null;
    }

    return turmas.get(opcao - 1);
  }

  private List<Turma> turmasMatriculadasDoAluno(Aluno alunoLogado) {
    List<Turma> turmasMatriculadas = new ArrayList<>();

    for (Turma turma : turmaService.listarTurmasPorCurso(alunoLogado.getCodigoCurso())) {
      if (turma.getMatriculados() != null
          && turma.getMatriculados().contains(alunoLogado.getMatricula())) {
        turmasMatriculadas.add(turma);
      }
    }

    return turmasMatriculadas;
  }

  private void validarCoordenadorComCurso(Coordenador coordenadorLogado) {
    if (coordenadorLogado == null
        || coordenadorLogado.getCodigoCurso() == null
        || coordenadorLogado.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Coordenador não está vinculado a nenhum curso.");
    }
  }

  private void validarAlunoComCurso(Aluno alunoLogado) {
    if (alunoLogado == null
        || alunoLogado.getCodigoCurso() == null
        || alunoLogado.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Aluno não está vinculado a nenhum curso.");
    }
  }

  private String buscarNomeAluno(String matriculaAluno) {
    try {
      return usuarioService.buscarAlunoPorMatricula(matriculaAluno).getNome();
    } catch (RuntimeException e) {
      return matriculaAluno;
    }
  }

  private String buscarNomeProfessor(Turma turma) {
    return turmaService.buscarNomeProfessor(turma.getMatriculaProfessor());
  }

  private String nomeAmigavelTurma(Turma turma) {
    if (turma == null) {
      return "-";
    }

    return turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina())
        + " - "
        + turma.getPeriodoLetivo();
  }

  private String formatarFrequencia(Double frequencia) {
    if (frequencia == null) {
      return "ainda não calculada";
    }

    return String.format("%.1f%%", frequencia);
  }
}
