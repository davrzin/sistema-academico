package br.com.classroompb.ui.tela;

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
import java.util.Scanner;

/**
 * Tela de interacao para consulta da media parcial de turmas e alunos.
 */
public class MediaTela {

  private final Scanner scanner;
  private final TurmaService turmaService = new TurmaService();
  private final BoletimService boletimService = new BoletimService();
  private final UsuarioService usuarioService = new UsuarioService();

  /**
   * Cria a tela de media parcial.
   *
   * @param scanner leitor de entrada.
   */
  public MediaTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Consulta a media parcial dos alunos de uma turma do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void consultarMediaParcialTurma(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);

      Turma turmaSelecionada = selecionarTurmaDoCurso(coordenadorLogado.getCodigoCurso());
      exibirMediaParcialDaTurma(turmaSelecionada);

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Consulta cancelada.");

    } catch (PersistenciaException | EntradaInvalidaException | TurmaNaoEncontradaException e) {
      System.out.println("Ocorreu um erro ao consultar média parcial: " + e.getMessage());
    }
  }

  /**
   * Consulta a media parcial dos alunos de uma turma do professor logado.
   *
   * @param professorLogado professor logado.
   */
  public void consultarMediaParcialTurma(Professor professorLogado) {
    try {
      Turma turmaSelecionada = selecionarTurmaDoProfessor(professorLogado);

      if (turmaSelecionada == null) {
        return;
      }

      exibirMediaParcialDaTurma(turmaSelecionada);

    } catch (PersistenciaException | EntradaInvalidaException | TurmaNaoEncontradaException e) {
      System.out.println("Ocorreu um erro ao consultar média parcial: " + e.getMessage());
    }
  }

  /**
   * Consulta a media parcial do aluno logado em cada turma matriculada.
   *
   * @param alunoLogado aluno logado.
   */
  public void consultarMediaParcial(Aluno alunoLogado) {
    try {
      validarAlunoComCurso(alunoLogado);

      List<Turma> turmasMatriculadas = turmasMatriculadasDoAluno(alunoLogado);

      if (turmasMatriculadas.isEmpty()) {
        System.out.println("Você não está matriculado em nenhuma turma.");
        return;
      }

      System.out.println("Minha média parcial:");

      for (int i = 0; i < turmasMatriculadas.size(); i++) {
        if (i > 0) {
          System.out.println();
        }

        exibirMediaParcialDoAlunoNaTurma(
            i + 1, turmasMatriculadas.get(i), alunoLogado.getMatricula());
      }

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao consultar média parcial: " + e.getMessage());
    }
  }

  private void exibirMediaParcialDaTurma(Turma turma) {
    List<String> alunosMatriculados = turma.getMatriculados();

    System.out.println("Média parcial da turma " + nomeAmigavelTurma(turma) + ":");

    if (alunosMatriculados == null || alunosMatriculados.isEmpty()) {
      System.out.println("A turma não possui alunos matriculados.");
      return;
    }

    for (int i = 0; i < alunosMatriculados.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirMediaParcialDoAluno(i + 1, alunosMatriculados.get(i), turma);
    }
  }

  private void exibirMediaParcialDoAluno(int numero, String matriculaAluno, Turma turma) {
    Boletim boletim = boletimService.buscarBoletimPorAlunoTurma(matriculaAluno, turma.getCodigo());

    System.out.println(numero + " - " + buscarNomeAluno(matriculaAluno)
        + " (matrícula: " + matriculaAluno + ")");
    exibirNotasComMedia(boletim);
  }

  private void exibirMediaParcialDoAlunoNaTurma(int numero, Turma turma, String matriculaAluno) {
    Boletim boletim = boletimService.buscarBoletimPorAlunoTurma(matriculaAluno, turma.getCodigo());

    System.out.println(numero + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Professor         : " + buscarNomeProfessor(turma));
    exibirNotasComMedia(boletim);
  }

  private void exibirNotasComMedia(Boletim boletim) {
    Float notaUm = boletim == null ? null : boletim.getPrimeiraNota();
    Float notaDois = boletim == null ? null : boletim.getSegundaNota();
    Float mediaParcial = boletim == null ? null : boletim.calcularMediaParcial();

    System.out.println("    Nota 1            : " + formatarNota(notaUm));
    System.out.println("    Nota 2            : " + formatarNota(notaDois));
    System.out.println("    Média parcial     : " + formatarNota(mediaParcial));
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

  private String formatarNota(Float nota) {
    if (nota == null) {
      return "-- (ainda não lançada)";
    }

    return String.format("%.1f", nota);
  }
}
