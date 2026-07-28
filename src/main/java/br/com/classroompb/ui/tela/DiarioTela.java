package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
/*import br.com.classroompb.model.enums.SituacaoDiario;*/
import br.com.classroompb.model.exception.DiarioNaoEncontradoException;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.DiarioService;
import br.com.classroompb.model.services.TurmaService;
import java.util.List;
import java.util.Scanner;

/**
 * Tela de interacao para operacoes de cadastro de diario de turma.
 */
public class DiarioTela {

  private final Scanner scanner;
  private final DiarioService diarioService = new DiarioService();
  private final TurmaService turmaService = new TurmaService();

  /**
   * Cria a tela de diarios.
   *
   * @param scanner leitor de entrada.
   */
  public DiarioTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Solicita os dados para cadastro de um diario para uma turma do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void cadastrarDiario(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);

      Turma turmaSelecionada = selecionarTurmaDoCurso(coordenadorLogado.getCodigoCurso());
      Diario novoDiario = lerDadosDiario(coordenadorLogado.getCodigoCurso(), turmaSelecionada);

      diarioService.cadastrarDiario(novoDiario, coordenadorLogado.getCodigoCurso());

      System.out.println("Diário cadastrado com sucesso.");
      System.out.println("Código do diário: " + novoDiario.getCodigo());

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Cadastro de diário cancelado.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cadastrar diário: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Carga horária inválida.");
    }
  }

  /**
   * Lista os diarios das turmas do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void listarDiarios(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);

      List<Diario> diarios =
          diarioService.listarDiariosPorCurso(coordenadorLogado.getCodigoCurso());
      exibirListaDiarios(diarios);

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao listar diários: " + e.getMessage());
    }
  }

  /**
   * Lista os diarios de uma turma especifica do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void listarDiariosPorTurma(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);

      Turma turmaSelecionada = selecionarTurmaDoCurso(coordenadorLogado.getCodigoCurso());
      List<Diario> diarios = diarioService.listarDiariosPorTurma(turmaSelecionada.getCodigo());

      System.out.println("Diários da turma " + nomeAmigavelTurma(turmaSelecionada) + ":");
      exibirListaDiarios(diarios);

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Consulta cancelada.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao listar diários: " + e.getMessage());
    }
  }

  /**
   * Solicita a atualizacao de um diario do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void atualizarDiario(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);

      Diario diarioSelecionado = selecionarDiarioDoCurso(coordenadorLogado.getCodigoCurso(),
          "Informe o número do diário que deseja atualizar: ");

      Turma turmaAtual = turmaService.buscarTurmaPorCodigo(diarioSelecionado.getCodigoTurma());
      Diario diarioAtualizado = lerDadosDiario(coordenadorLogado.getCodigoCurso(), turmaAtual);

      diarioService.alterarDiario(
          diarioSelecionado.getCodigo(), diarioAtualizado, coordenadorLogado.getCodigoCurso());

      System.out.println("Diário atualizado com sucesso.");

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Atualização de diário cancelada.");

    } catch (PersistenciaException | EntradaInvalidaException | DiarioNaoEncontradoException e) {
      System.out.println("Ocorreu um erro ao atualizar diário: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Carga horária inválida.");
    }
  }

  /**
   * Solicita o encerramento de um diario do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void encerrarDiario(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);

      Diario diarioSelecionado = selecionarDiarioDoCurso(coordenadorLogado.getCodigoCurso(),
          "Informe o número do diário que deseja encerrar: ");

      diarioService.encerrarDiario(
          diarioSelecionado.getCodigo(), coordenadorLogado.getCodigoCurso());

      System.out.println("Diário encerrado com sucesso.");

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Operação cancelada.");

    } catch (PersistenciaException | EntradaInvalidaException | DiarioNaoEncontradoException e) {
      System.out.println("Ocorreu um erro ao encerrar diário: " + e.getMessage());
    }
  }

  /**
   * Solicita o cancelamento de um diario do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void cancelarDiario(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);

      Diario diarioSelecionado = selecionarDiarioDoCurso(coordenadorLogado.getCodigoCurso(),
          "Informe o número do diário que deseja cancelar: ");

      diarioService.cancelarDiario(
          diarioSelecionado.getCodigo(), coordenadorLogado.getCodigoCurso());

      System.out.println("Diário cancelado com sucesso.");

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Operação cancelada.");

    } catch (PersistenciaException | EntradaInvalidaException | DiarioNaoEncontradoException e) {
      System.out.println("Ocorreu um erro ao cancelar diário: " + e.getMessage());
    }
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

  private Professor selecionarProfessorDoCurso(String codigoCurso) {
    List<Professor> professores = turmaService.listarProfessoresPorCurso(codigoCurso);

    if (professores == null || professores.isEmpty()) {
      throw new EntradaInvalidaException("Nenhum professor cadastrado para o curso.");
    }

    System.out.println("Professores do curso:");
    System.out.println("0 - Cancelar");

    for (int i = 0; i < professores.size(); i++) {
      System.out.println((i + 1) + " - " + professores.get(i).getNome()
          + " (matrícula: " + professores.get(i).getMatricula() + ")");
    }

    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner, "Informe o número do professor responsável: ", professores.size());

    if (opcao == 0) {
      throw new EntradaTela.EntradaCanceladaException();
    }

    return professores.get(opcao - 1);
  }

  private Diario selecionarDiarioDoCurso(String codigoCurso, String prompt) {
    List<Diario> diarios = diarioService.listarDiariosPorCurso(codigoCurso);

    if (diarios == null || diarios.isEmpty()) {
      throw new EntradaInvalidaException("Nenhum diário cadastrado para o curso.");
    }

    System.out.println("Diários do curso:");
    System.out.println("0 - Cancelar");

    for (int i = 0; i < diarios.size(); i++) {
      exibirDiarioResumido(i + 1, diarios.get(i));
    }

    int opcao = EntradaTela.lerOpcaoOuCancelar(scanner, prompt, diarios.size());

    if (opcao == 0) {
      throw new EntradaTela.EntradaCanceladaException();
    }

    return diarios.get(opcao - 1);
  }

  private Diario lerDadosDiario(String codigoCurso, Turma turmaSelecionada) {
    final String descricao =
        EntradaTela.lerTextoObrigatorioOuCancelar(
            scanner, "Informe a descrição do diário: ", "Descrição");

    Professor professorSelecionado = selecionarProfessorDoCurso(codigoCurso);
    final String matriculaProfessor = professorSelecionado.getMatricula();

    final String horario =
        EntradaTela.lerTextoObrigatorioOuCancelar(
            scanner, "Informe o horário do diário. Exemplo: SEG 08:00-10:00: ", "Horário");

    final String sala =
        EntradaTela.lerTextoObrigatorioOuCancelar(scanner, "Informe a sala do diário: ", "Sala");

    final int cargaHoraria =
        EntradaTela.lerInteiroPositivoOuCancelar(scanner, "Informe a carga horária (em horas): ");

    return new Diario(
        turmaSelecionada.getCodigo(), descricao, matriculaProfessor, horario, sala, cargaHoraria);
  }

  private void validarCoordenadorComCurso(Coordenador coordenadorLogado) {
    if (coordenadorLogado == null
        || coordenadorLogado.getCodigoCurso() == null
        || coordenadorLogado.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Coordenador não está vinculado a nenhum curso.");
    }
  }

  private void exibirListaDiarios(List<Diario> diarios) {
    if (diarios == null || diarios.isEmpty()) {
      System.out.println("Nenhum diário cadastrado.");
      return;
    }

    for (int i = 0; i < diarios.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirDiarioDetalhado(i + 1, diarios.get(i));
    }
  }

  private void exibirDiarioResumido(int numero, Diario diario) {
    System.out.println(
        numero
            + " - "
            + diario.getDescricao()
            + " (código: "
            + diario.getCodigo()
            + ", turma: "
            + nomeAmigavelTurmaPorCodigo(diario.getCodigoTurma())
            + ", situação: "
            + diario.getSituacao().getDescricao()
            + ")");
  }

  private void exibirDiarioDetalhado(int numero, Diario diario) {
    System.out.println(numero + " - " + diario.getDescricao());
    System.out.println("    Código interno   : " + diario.getCodigo());
    System.out.println(
        "    Turma            : " + nomeAmigavelTurmaPorCodigo(diario.getCodigoTurma()));
    System.out.println(
        "    Professor        : "
            + diarioService.buscarNomeProfessor(diario.getMatriculaProfessor()));
    System.out.println("    Horário          : " + diario.getHorario());
    System.out.println("    Sala             : " + diario.getSala());
    System.out.println("    Carga horária    : " + diario.getCargaHoraria() + "h");
    System.out.println("    Situação         : " + diario.getSituacao().getDescricao());
  }

  private String nomeAmigavelTurma(Turma turma) {
    if (turma == null) {
      return "-";
    }

    return turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina())
        + " - "
        + turma.getPeriodoLetivo();
  }

  private String nomeAmigavelTurmaPorCodigo(String codigoTurma) {
    try {
      Turma turma = turmaService.buscarTurmaPorCodigo(codigoTurma);
      return nomeAmigavelTurma(turma);
    } catch (RuntimeException e) {
      return codigoTurma;
    }
  }
}
