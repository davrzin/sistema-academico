package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.exception.AlunoNaoCumprePreRequisitosException;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.exception.TurmaCheiaException;
import br.com.classroompb.model.exception.TurmaNaoEncontradaException;
import br.com.classroompb.model.services.AulaService;
import br.com.classroompb.model.services.BoletimService;
import br.com.classroompb.model.services.DisciplinaService;
import br.com.classroompb.model.services.TurmaService;
import br.com.classroompb.model.services.UsuarioService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Tela de interacao para operacoes de turma.
 */
public class TurmaTela {

  private final Scanner scanner;
  private final TurmaService turmaService = new TurmaService();
  private final AulaService aulaService = new AulaService();
  private final BoletimService boletimService = new BoletimService();
  private final UsuarioService usuarioService = new UsuarioService();
  private final DisciplinaService disciplinaService = new DisciplinaService();

  private static class OpcaoMatricula {
    private final Turma turma;
    private final String motivo;

    OpcaoMatricula(Turma turma, String motivo) {
      this.turma = turma;
      this.motivo = motivo;
    }
  }

  private static class ClassificacaoMatricula {
    private final List<OpcaoMatricula> matriculaDireta = new ArrayList<>();
    private final List<OpcaoMatricula> listaEspera = new ArrayList<>();
    private final List<OpcaoMatricula> indisponiveis = new ArrayList<>();
  }

  private static class ProfessorIndisponivel {
    private final Professor professor;
    private final Turma turmaConflitante;

    ProfessorIndisponivel(Professor professor, Turma turmaConflitante) {
      this.professor = professor;
      this.turmaConflitante = turmaConflitante;
    }
  }

  /**
   * Cria a tela de turmas.
   *
   * @param scanner leitor de entrada.
   */
  public TurmaTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Solicita os dados para cadastro de turma.
   */
  public void cadastrarTurma() {
    try {
      Turma novaTurma = lerDadosTurma();

      turmaService.ofertarTurma(novaTurma);

      System.out.println("Turma cadastrada com sucesso.");
      System.out.println("Código da turma: " + novaTurma.getCodigo());

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Cadastro de turma cancelado.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cadastrar turma: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Limite de vagas inválido.");
    }
  }

  /**
   * Solicita os dados para cadastro de turma por coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void cadastrarTurma(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);
      Turma novaTurma = lerDadosNovaTurma(coordenadorLogado.getCodigoCurso());

      turmaService.ofertarTurma(novaTurma, coordenadorLogado.getCodigoCurso());

      System.out.println("Turma cadastrada com sucesso.");
      System.out.println("Codigo da turma: " + novaTurma.getCodigo());

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Cadastro de turma cancelado.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cadastrar turma: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Limite de vagas invalido.");
    }
  }

  /**
   * Lista as turmas cadastradas no terminal.
   */
  public void listarTurmas() {
    try {
      List<Turma> turmas = turmaService.listarTurmas();
      exibirListaTurmas(turmas);

    } catch (PersistenciaException e) {
      System.out.println("Ocorreu um erro ao listar turmas: " + e.getMessage());
    }
  }

  /**
   * Lista turmas do curso do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void listarTurmas(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);
      List<Turma> turmas = turmaService.listarTurmasPorCurso(coordenadorLogado.getCodigoCurso());
      exibirListaTurmasDetalhada(turmas);

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao listar turmas: " + e.getMessage());
    }
  }

  /**
   * Lista turmas do curso do aluno.
   *
   * @param alunoLogado aluno logado.
   */
  public void listarTurmas(Aluno alunoLogado) {
    try {
      validarAlunoComCurso(alunoLogado);
      List<Turma> turmas = turmaService.listarTurmasPorCurso(alunoLogado.getCodigoCurso());
      exibirListaTurmasDisponiveisAluno(turmas);

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao listar turmas: " + e.getMessage());
    }
  }

  /**
   * Lista as turmas vinculadas ao usuario logado.
   *
   * @param usuarioLogado usuario logado.
   */
  public void listarMinhasTurmas(Usuario usuarioLogado) {
    try {
      List<Turma> turmas = turmaService.listarTurmasPorProfessor(usuarioLogado.getMatricula());
      exibirListaMinhasTurmasProfessor(turmas);

    } catch (RuntimeException e) {
      System.out.println("Ocorreu um erro ao listar turmas: " + e.getMessage());
    }
  }

  /**
   * Solicita a atualizacao de uma turma.
   */
  public void atualizarTurma() {
    try {
      listarTurmas();

      System.out.print("Informe o codigo da turma que deseja atualizar: ");
      String codigo = scanner.nextLine();

      Turma turmaAtualizada = lerDadosTurma();

      turmaService.alterarTurma(codigo, turmaAtualizada);

      System.out.println("Turma atualizada com sucesso.");

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Atualizacao de turma cancelada.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao atualizar turma: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Limite de vagas inválido.");
    }
  }

  /**
   * Solicita a atualizacao de uma turma por coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void atualizarTurma(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);
      Turma turmaSelecionada = selecionarTurmaParaAtualizacao(coordenadorLogado.getCodigoCurso());

      Turma turmaAtualizada =
          lerDadosTurma(coordenadorLogado.getCodigoCurso(), turmaSelecionada);

      turmaService.alterarTurma(
          turmaSelecionada.getCodigo(), turmaAtualizada, coordenadorLogado.getCodigoCurso());

      System.out.println("Turma atualizada com sucesso.");

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Atualizacao de turma cancelada.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao atualizar turma: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Limite de vagas invalido.");
    }
  }

  private Turma selecionarTurmaParaAtualizacao(String codigoCurso) {
    List<Turma> turmas = turmaService.listarTurmasPorCurso(codigoCurso);

    if (turmas == null || turmas.isEmpty()) {
      throw new EntradaInvalidaException("Nenhuma turma cadastrada para o curso.");
    }

    System.out.println("Turmas do curso:");

    for (int i = 0; i < turmas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirOpcaoTurmaAtualizacao(i + 1, turmas.get(i));
    }

    System.out.print("Informe o numero da turma que deseja atualizar: ");
    int opcao = lerOpcaoNumerada(turmas.size());

    return turmas.get(opcao - 1);
  }

  private void exibirOpcaoTurmaAtualizacao(int numeroOpcao, Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();

    System.out.println(numeroOpcao + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Codigo interno: " + formatarValor(turma.getCodigo()));
    System.out.println("    Professor: " + buscarNomeProfessorTurma(turma));
    System.out.println("    Periodo letivo: " + formatarValor(turma.getPeriodoLetivo()));
    System.out.println("    Horario: " + formatarValor(turma.getHorario()));
    System.out.println("    Sala: " + formatarValor(turma.getSala()));
    System.out.println("    Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
  }

  /**
   * Solicita o cancelamento de uma turma.
   */
  public void cancelarTurma() {
    try {
      listarTurmas();

      System.out.print("Informe o codigo da turma que deseja cancelar: ");
      String codigo = scanner.nextLine();

      turmaService.cancelarTurma(codigo);

      System.out.println("Turma cancelada com sucesso.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cancelar turma: " + e.getMessage());
    }
  }

  /**
   * Solicita o cancelamento de uma turma por coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void cancelarTurma(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);
      Turma turmaSelecionada =
          selecionarTurmaParaCancelamentoCoordenador(coordenadorLogado.getCodigoCurso());

      turmaService.cancelarTurma(turmaSelecionada.getCodigo(), coordenadorLogado.getCodigoCurso());

      System.out.println("Turma cancelada com sucesso.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cancelar turma: " + e.getMessage());
    }
  }

  private Turma selecionarTurmaParaCancelamentoCoordenador(String codigoCurso) {
    List<Turma> turmas = turmaService.listarTurmasPorCurso(codigoCurso);

    if (turmas == null || turmas.isEmpty()) {
      throw new EntradaInvalidaException("Nenhuma turma cadastrada para o curso.");
    }

    System.out.println("Turmas do curso:");

    for (int i = 0; i < turmas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirOpcaoTurmaAtualizacao(i + 1, turmas.get(i));
    }

    System.out.print("Informe o numero da turma que deseja cancelar: ");
    int opcao = lerOpcaoNumerada(turmas.size());

    return turmas.get(opcao - 1);
  }

  /**
   * Mostra a lista de espera das turmas.
   */
  public void mostrarListaEsperaTurmas() {
    try {
      listarTurmas();

      System.out.println("======================================");
      System.out.print("Informe o codigo da turma: ");
      String codigoTurma = scanner.nextLine();

      Turma turma = turmaService.buscarTurmaPorCodigo(codigoTurma);

      exibirListaEspera(turma);

    } catch (TurmaNaoEncontradaException | EntradaInvalidaException | PersistenciaException e) {
      System.out.println("Ocorreu um erro ao consultar a lista de espera: " + e.getMessage());
    }
  }

  /**
   * Mostra a lista de espera das turmas do coordenador.
   *
   * @param coordenadorLogado coordenador logado.
   */
  public void mostrarListaEsperaTurmas(Coordenador coordenadorLogado) {
    try {
      validarCoordenadorComCurso(coordenadorLogado);
      Turma turma = selecionarTurmaParaListaEsperaCoordenador(coordenadorLogado.getCodigoCurso());

      turmaService.validarTurmaPertenceAoCurso(
          turma.getCodigo(), coordenadorLogado.getCodigoCurso());

      exibirListaEspera(turma);

    } catch (TurmaNaoEncontradaException | EntradaInvalidaException | PersistenciaException e) {
      System.out.println("Ocorreu um erro ao consultar a lista de espera: " + e.getMessage());
    }
  }

  private Turma selecionarTurmaParaListaEsperaCoordenador(String codigoCurso) {
    List<Turma> turmas = turmaService.listarTurmasPorCurso(codigoCurso);

    if (turmas == null || turmas.isEmpty()) {
      throw new EntradaInvalidaException("Nenhuma turma cadastrada para o curso.");
    }

    System.out.println("Turmas do curso:");

    for (int i = 0; i < turmas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirOpcaoTurmaAtualizacao(i + 1, turmas.get(i));
    }

    System.out.print("Informe o numero da turma para visualizar a lista de espera: ");
    int opcao = lerOpcaoNumerada(turmas.size());

    return turmas.get(opcao - 1);
  }

  private void exibirListaEspera(Turma turma) {
    List<String> listaEspera = turma.getListaEspera();

    System.out.println("Lista de espera da turma " + nomeAmigavelTurma(turma) + ":");
    System.out.println("    Codigo interno: " + formatarValor(turma.getCodigo()));

    if (listaEspera == null || listaEspera.isEmpty()) {
      System.out.println("Nenhum aluno na lista de espera.");
      return;
    }

    int posicao = 1;

    for (String matriculaAluno : listaEspera) {
      try {
        Aluno aluno = usuarioService.buscarAlunoPorMatricula(matriculaAluno);
        System.out.println(
            posicao + "º - " + aluno.getNome() + " (matrícula: " + matriculaAluno + ")");
      } catch (EntradaInvalidaException e) {
        System.out.println(posicao + "º - Matrícula " + matriculaAluno + " (aluno não encontrado)");
      }

      posicao++;
    }
  }

  /**
   * Solicita o cadastro de aluno em turma.
   *
   * @param alunoLogado aluno logado.
   */
  public void cadastrarNovoAluno(Aluno alunoLogado) {
    try {
      Turma turmaSelecionada = selecionarTurmaParaMatricula(alunoLogado);

      if (turmaSelecionada == null) {
        return;
      }

      int listaEsperaCond =
          turmaService.cadastrarAlunoEmTurma(turmaSelecionada.getCodigo(), alunoLogado);

      if (listaEsperaCond == 0) {
        System.out.println("Aluno matriculado com sucesso!");
      } else {
        System.out.println("Aluno na lista de espera!");
      }

    } catch (AlunoNaoCumprePreRequisitosException
        | TurmaCheiaException
        | PersistenciaException
        | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao realizar matrícula: " + e.getMessage());
    } catch (NumberFormatException e) {
      System.out.println("Opcao invalida. Digite o numero de uma turma listada.");
    }
  }

  private Turma selecionarTurmaParaMatricula(Aluno alunoLogado) {
    validarAlunoComCurso(alunoLogado);

    List<Turma> turmas = turmaService.listarTurmasPorCurso(alunoLogado.getCodigoCurso());

    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Nenhuma turma disponivel para o curso do aluno.");
      return null;
    }

    ClassificacaoMatricula classificacao = classificarTurmasParaMatricula(alunoLogado, turmas);
    List<Turma> turmasSelecionaveis = montarListaTurmasSelecionaveis(classificacao);

    System.out.println("======================================");
    exibirClassificacaoMatricula(classificacao);

    if (turmasSelecionaveis.isEmpty()) {
      System.out.println("Nenhuma turma disponivel para matricula ou lista de espera no momento.");
      return null;
    }

    System.out.println();
    System.out.println("0 - Voltar");
    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner,
            "Informe o numero da turma que deseja se matricular: ",
            turmasSelecionaveis.size());

    if (opcao == 0) {
      System.out.println("Voltando...");
      return null;
    }

    return turmasSelecionaveis.get(opcao - 1);
  }

  private ClassificacaoMatricula classificarTurmasParaMatricula(
      Aluno alunoLogado, List<Turma> turmas) {
    ClassificacaoMatricula classificacao = new ClassificacaoMatricula();

    for (Turma turma : turmas) {
      String motivoIndisponibilidade = identificarMotivoIndisponibilidade(alunoLogado, turma);

      if (!motivoIndisponibilidade.isBlank()) {
        classificacao.indisponiveis.add(new OpcaoMatricula(turma, motivoIndisponibilidade));
      } else if (turmaCheia(turma)) {
        classificacao.listaEspera.add(new OpcaoMatricula(turma, "turma cheia"));
      } else {
        classificacao.matriculaDireta.add(new OpcaoMatricula(turma, ""));
      }
    }

    return classificacao;
  }

  private List<Turma> montarListaTurmasSelecionaveis(ClassificacaoMatricula classificacao) {
    List<Turma> turmasSelecionaveis = new ArrayList<>();

    for (OpcaoMatricula opcao : classificacao.matriculaDireta) {
      turmasSelecionaveis.add(opcao.turma);
    }

    for (OpcaoMatricula opcao : classificacao.listaEspera) {
      turmasSelecionaveis.add(opcao.turma);
    }

    return turmasSelecionaveis;
  }

  private void exibirClassificacaoMatricula(ClassificacaoMatricula classificacao) {
    int numeroOpcao = 1;

    System.out.println("Turmas com matricula direta:");
    if (classificacao.matriculaDireta.isEmpty()) {
      System.out.println("Nenhuma turma com matricula direta disponivel.");
    } else {
      for (int i = 0; i < classificacao.matriculaDireta.size(); i++) {
        if (i > 0) {
          System.out.println();
        }

        OpcaoMatricula opcao = classificacao.matriculaDireta.get(i);
        exibirOpcaoTurmaMatricula(numeroOpcao, opcao.turma, "");
        numeroOpcao++;
      }
    }

    System.out.println();
    System.out.println("Turmas com lista de espera:");
    if (classificacao.listaEspera.isEmpty()) {
      System.out.println("Nenhuma turma com lista de espera disponivel.");
    } else {
      for (int i = 0; i < classificacao.listaEspera.size(); i++) {
        if (i > 0) {
          System.out.println();
        }

        OpcaoMatricula opcao = classificacao.listaEspera.get(i);
        exibirOpcaoTurmaMatricula(numeroOpcao, opcao.turma, opcao.motivo);
        numeroOpcao++;
      }
    }

    if (!classificacao.indisponiveis.isEmpty()) {
      System.out.println();
      System.out.println("Turmas indisponiveis:");
      for (OpcaoMatricula opcao : classificacao.indisponiveis) {
        System.out.println("- " + nomeAmigavelTurma(opcao.turma) + " - " + opcao.motivo);
      }
    }
  }

  private void exibirOpcaoTurmaMatricula(int numeroOpcao, Turma turma, String motivo) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();

    System.out.println(numeroOpcao + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Professor: " + buscarNomeProfessorTurma(turma));
    System.out.println("    Periodo letivo: " + formatarValor(turma.getPeriodoLetivo()));
    System.out.println("    Horario: " + formatarValor(turma.getHorario()));
    System.out.println("    Sala: " + formatarValor(turma.getSala()));
    System.out.println("    Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
    if (motivo != null && !motivo.isBlank()) {
      System.out.println("    Motivo: " + motivo);
    }
  }

  private String identificarMotivoIndisponibilidade(Aluno alunoLogado, Turma turma) {
    Disciplina disciplina = buscarDisciplinaPorCodigo(turma.getCodigoDisciplina());

    if (disciplina == null) {
      return "disciplina nao encontrada";
    }

    if (!turmaEstaEmPeriodoLetivoAtivo(turma)) {
      return "periodo letivo encerrado";
    }

    if (alunoJaConcluiuDisciplina(alunoLogado, disciplina)) {
      return "disciplina ja concluida";
    }

    List<String> preRequisitosPendentes = listarPreRequisitosPendentes(alunoLogado, disciplina);
    if (!preRequisitosPendentes.isEmpty()) {
      return formatarPreRequisitosPendentes(preRequisitosPendentes);
    }

    if (alunoJaParticipaDestaTurma(alunoLogado, turma)) {
      return "aluno ja esta matriculado ou em lista de espera nesta turma";
    }

    if (alunoJaParticipaDeOutraTurmaDaDisciplinaNoPeriodo(alunoLogado, turma)) {
      return "voce ja esta matriculado ou em lista de espera em outra turma desta disciplina "
          + "neste periodo";
    }

    Turma turmaComChoque = buscarTurmaComChoqueHorario(alunoLogado, turma);
    if (turmaComChoque != null) {
      return "choque de horario com " + nomeAmigavelTurma(turmaComChoque);
    }

    return "";
  }

  private Disciplina buscarDisciplinaPorCodigo(String codigoDisciplina) {
    if (codigoDisciplina == null || codigoDisciplina.isBlank()) {
      return null;
    }

    for (Disciplina disciplina : disciplinaService.listarDisciplinas()) {
      if (disciplina.getCodigo() != null
          && disciplina.getCodigo().equalsIgnoreCase(codigoDisciplina.trim())) {
        return disciplina;
      }
    }

    return null;
  }

  private boolean alunoJaConcluiuDisciplina(Aluno alunoLogado, Disciplina disciplina) {
    return alunoLogado.getDisciplinasConcluidas() != null
        && alunoLogado.getDisciplinasConcluidas().contains(disciplina.getCodigo());
  }

  private List<String> listarPreRequisitosPendentes(Aluno alunoLogado, Disciplina disciplina) {
    List<String> preRequisitosPendentes = new ArrayList<>();

    if (disciplina.getPreRequisitos() == null || disciplina.getPreRequisitos().isEmpty()) {
      return preRequisitosPendentes;
    }

    List<String> disciplinasConcluidas = alunoLogado.getDisciplinasConcluidas();

    for (String codigoPreRequisito : disciplina.getPreRequisitos()) {
      if (disciplinasConcluidas == null || !disciplinasConcluidas.contains(codigoPreRequisito)) {
        preRequisitosPendentes.add(buscarNomeDisciplina(codigoPreRequisito));
      }
    }

    return preRequisitosPendentes;
  }

  private String formatarPreRequisitosPendentes(List<String> preRequisitosPendentes) {
    if (preRequisitosPendentes.size() == 1) {
      return "pre-requisito nao cumprido: " + preRequisitosPendentes.getFirst();
    }

    return "pre-requisitos nao cumpridos: " + String.join(", ", preRequisitosPendentes);
  }

  private String buscarNomeDisciplina(String codigoDisciplina) {
    Disciplina disciplina = buscarDisciplinaPorCodigo(codigoDisciplina);
    if (disciplina == null) {
      return formatarValor(codigoDisciplina);
    }
    return formatarValor(disciplina.getNome());
  }

  private boolean alunoJaParticipaDestaTurma(Aluno alunoLogado, Turma turma) {
    return contemMatricula(turma.getMatriculados(), alunoLogado.getMatricula())
        || contemMatricula(turma.getListaEspera(), alunoLogado.getMatricula())
        || contemMatricula(alunoLogado.getTurmasMatriculadas(), turma.getCodigo());
  }

  private boolean alunoJaParticipaDeOutraTurmaDaDisciplinaNoPeriodo(
      Aluno alunoLogado, Turma turmaDestino) {
    for (Turma turma : turmaService.listarTurmas()) {
      if (mesmaTurma(turma, turmaDestino)) {
        continue;
      }

      if (mesmaDisciplinaMesmoPeriodo(turma, turmaDestino)
          && (contemMatricula(turma.getMatriculados(), alunoLogado.getMatricula())
              || contemMatricula(turma.getListaEspera(), alunoLogado.getMatricula())
              || contemMatricula(alunoLogado.getTurmasMatriculadas(), turma.getCodigo()))) {
        return true;
      }
    }

    return false;
  }

  private Turma buscarTurmaComChoqueHorario(Aluno alunoLogado, Turma turmaDestino) {
    if (alunoLogado.getTurmasMatriculadas() == null) {
      return null;
    }

    for (String codigoTurmaAluno : alunoLogado.getTurmasMatriculadas()) {
      try {
        Turma turmaAluno = turmaService.buscarTurmaPorCodigo(codigoTurmaAluno);
        if (turmaAluno != null
            && turmaAluno.getHorario() != null
            && turmaDestino.getHorario() != null
            && turmaAluno.getHorario().equalsIgnoreCase(turmaDestino.getHorario())) {
          return turmaAluno;
        }
      } catch (RuntimeException e) {
        // Ignora referencias antigas para manter a tela resiliente.
      }
    }

    return null;
  }

  private boolean turmaEstaEmPeriodoLetivoAtivo(Turma turma) {
    String periodoAtivo = turmaService.buscarPeriodoLetivoAtivo();

    return periodoAtivo != null
        && turma.getPeriodoLetivo() != null
        && periodoAtivo.equalsIgnoreCase(turma.getPeriodoLetivo());
  }

  private boolean mesmaDisciplinaMesmoPeriodo(Turma primeiraTurma, Turma segundaTurma) {
    return primeiraTurma.getCodigoDisciplina() != null
        && segundaTurma.getCodigoDisciplina() != null
        && primeiraTurma.getPeriodoLetivo() != null
        && segundaTurma.getPeriodoLetivo() != null
        && primeiraTurma.getCodigoDisciplina().equalsIgnoreCase(segundaTurma.getCodigoDisciplina())
        && primeiraTurma.getPeriodoLetivo().equalsIgnoreCase(segundaTurma.getPeriodoLetivo());
  }

  private boolean turmaCheia(Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();
    return vagasOcupadas >= turma.getLimiteVagas();
  }

  private boolean contemMatricula(List<String> matriculas, String matricula) {
    return matriculas != null && matricula != null && matriculas.contains(matricula);
  }

  /**
   * Solicita o cancelamento de matricula do aluno em turma.
   *
   * @param alunoLogado aluno logado.
   */
  public void cancelarTurmaAluno(Aluno alunoLogado) {
    try {
      Turma turmaSelecionada = selecionarTurmaParaCancelamento(alunoLogado);

      if (turmaSelecionada == null) {
        return;
      }

      String msgComplementar =
          turmaService.cancelarAlunoTurma(turmaSelecionada.getCodigo(), alunoLogado);

      System.out.println("Matrícula cancelada com sucesso.");

      if (!msgComplementar.isBlank()) {
        System.out.println(msgComplementar);
      }
    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cancelar matrícula: " + e.getMessage());
    } catch (NumberFormatException e) {
      System.out.println("Opcao invalida. Digite o numero de uma turma listada.");
    }
  }

  private Turma selecionarTurmaParaCancelamento(Aluno alunoLogado) {
    validarAlunoComCurso(alunoLogado);

    List<Turma> turmasMatriculadas = listarTurmasMatriculadasAluno(alunoLogado);

    if (turmasMatriculadas.isEmpty()) {
      System.out.println("Aluno nao possui matriculas para cancelar.");
      return null;
    }

    System.out.println("======================================");
    System.out.println("Turmas matriculadas:");

    for (int i = 0; i < turmasMatriculadas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirOpcaoTurmaCancelamento(i + 1, turmasMatriculadas.get(i));
    }

    System.out.println();
    System.out.println("0 - Voltar");
    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner,
            "Informe o numero da turma que deseja cancelar: ",
            turmasMatriculadas.size());

    if (opcao == 0) {
      System.out.println("Voltando...");
      return null;
    }

    return turmasMatriculadas.get(opcao - 1);
  }

  private List<Turma> listarTurmasMatriculadasAluno(Aluno alunoLogado) {
    List<Turma> turmasMatriculadas = new ArrayList<>();
    List<Turma> turmasCurso = turmaService.listarTurmasPorCurso(alunoLogado.getCodigoCurso());

    for (Turma turma : turmasCurso) {
      if (turma.getMatriculados() != null
          && turma.getMatriculados().contains(alunoLogado.getMatricula())) {
        turmasMatriculadas.add(turma);
      }
    }

    return turmasMatriculadas;
  }

  private void exibirOpcaoTurmaCancelamento(int numeroOpcao, Turma turma) {
    System.out.println(numeroOpcao + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Professor: " + buscarNomeProfessorTurma(turma));
    System.out.println("    Periodo letivo: " + formatarValor(turma.getPeriodoLetivo()));
    System.out.println("    Horario: " + formatarValor(turma.getHorario()));
    System.out.println("    Sala: " + formatarValor(turma.getSala()));
  }

  /**
   * Lista as matriculas do aluno.
   *
   * @param alunoLogado aluno logado.
   */
  public void listarMatriculasAluno(Aluno alunoLogado) {
    try {
      validarAlunoComCurso(alunoLogado);

      List<Turma> turmasCurso = turmaService.listarTurmasPorCurso(alunoLogado.getCodigoCurso());
      boolean encontrou = false;
      int numero = 1;

      for (Turma turma : turmasCurso) {
        if (turma.getMatriculados() != null
            && turma.getMatriculados().contains(alunoLogado.getMatricula())) {
          if (numero > 1) {
            System.out.println();
          }

          exibirTurmaAluno(numero, turma, "matriculado");
          numero++;
          encontrou = true;
        } else if (turma.getListaEspera() != null
            && turma.getListaEspera().contains(alunoLogado.getMatricula())) {
          if (numero > 1) {
            System.out.println();
          }

          exibirTurmaAluno(numero, turma, "lista de espera");
          numero++;
          encontrou = true;
        }
      }

      if (!encontrou) {
        System.out.println("Nenhuma matricula encontrada para este aluno.");
      }

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao listar matriculas: " + e.getMessage());
    }
  }

  /**
   * Solicita o registro de frequencia por professor.
   *
   * @param professorLogado professor logado.
   */
  public void adicionarFrequencia(Professor professorLogado) {
    try {
      Turma turmaSelecionada = selecionarTurmaParaFrequencia(professorLogado);
      if (turmaSelecionada == null) {
        return;
      }
      String codigoTurma = turmaSelecionada.getCodigo();

      Turma turma = turmaService.buscarTurmaPorCodigo(codigoTurma, professorLogado.getMatricula());

      if (turmaService.existeAlunosMatriculados(turma)) {
        System.out.println("A turma não possui alunos matriculados.");
        return;
      }

      Map<String, Boolean> presencas = new HashMap<>();

      List<String> alunosMatriculados = turma.getMatriculados();

      System.out.println("\nRegistro de frequência");
      System.out.println("Digite P para Presente ou F para Falta\n");

      for (String matriculaAluno : alunosMatriculados) {
        while (true) {
          Aluno aluno = usuarioService.buscarAlunoPorMatricula(matriculaAluno);

          System.out.print(aluno.getNome() + " (P/F): ");
          String resposta = scanner.nextLine().trim().toUpperCase();

          if (resposta.equals("P")) {
            presencas.put(matriculaAluno, true);
            break;
          }

          if (resposta.equals("F")) {
            presencas.put(matriculaAluno, false);
            break;
          }

          System.out.println("Opção inválida. Digite apenas P ou F.");
        }
      }

      Aula aula = aulaService.gerarAula(turma);
      aula.setPresencas(presencas);

      aulaService.salvarAula(aula);

      turmaService.cadastrarNovaAula(aula, codigoTurma, professorLogado.getMatricula());

      turmaService.atualizarFrequenciaTurma(codigoTurma, professorLogado.getMatricula());

    } catch (TurmaNaoEncontradaException | EntradaInvalidaException | PersistenciaException e) {
      System.out.println(e.getMessage());
      return;
    }

    System.out.println("Frequência registrada com sucesso.");
  }

  private Turma selecionarTurmaParaFrequencia(Professor professorLogado) {
    List<Turma> turmas = turmaService.listarTurmasPorProfessor(professorLogado.getMatricula());

    if (turmas == null || turmas.isEmpty()) {
      throw new EntradaInvalidaException("Professor nao possui turmas cadastradas.");
    }

    System.out.println("Turmas do professor:");

    for (int i = 0; i < turmas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirOpcaoTurmaProfessor(i + 1, turmas.get(i));
    }

    System.out.println();
    System.out.println("0 - Voltar");
    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner, "Informe o numero da turma para lancar frequencia: ", turmas.size());

    if (opcao == 0) {
      System.out.println("Voltando...");
      return null;
    }

    return turmas.get(opcao - 1);
  }

  private void exibirOpcaoTurmaProfessor(int numeroOpcao, Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();

    System.out.println(numeroOpcao + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Periodo letivo: " + formatarValor(turma.getPeriodoLetivo()));
    System.out.println("    Horario: " + formatarValor(turma.getHorario()));
    System.out.println("    Sala: " + formatarValor(turma.getSala()));
    System.out.println("    Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
  }

  /**
   * Solicita o lancamento ou alteracao de notas por professor.
   *
   * @param professorLogado professor logado.
   */
  public void adicionarNotas(Professor professorLogado) {
    try {
      Turma turmaSelecionada = selecionarTurmaParaNotas(professorLogado);
      if (turmaSelecionada == null) {
        return;
      }
      String codigoTurma = turmaSelecionada.getCodigo();

      Turma turma = turmaService.buscarTurmaPorCodigo(codigoTurma, professorLogado.getMatricula());

      if (turmaService.existeAlunosMatriculados(turma)) {
        System.out.println("A turma nao possui alunos matriculados.");
        return;
      }

      String matriculaAluno = selecionarAlunoParaNotas(turma);
      if (matriculaAluno == null) {
        return;
      }

      int opcaoLancamento = selecionarOpcaoLancamentoNotas();
      if (opcaoLancamento == 0) {
        return;
      }

      lancarNotasSelecionadas(codigoTurma, matriculaAluno, professorLogado, opcaoLancamento);
    } catch (TurmaNaoEncontradaException | EntradaInvalidaException | PersistenciaException e) {
      System.out.println(e.getMessage());
    }
  }

  private int selecionarOpcaoLancamentoNotas() {
    System.out.println();
    System.out.println("O que deseja lancar ou alterar?");
    System.out.println("1 - Primeira nota");
    System.out.println("2 - Segunda nota");
    System.out.println("3 - Primeira e segunda nota");
    System.out.println("0 - Voltar");
    System.out.print("Escolha uma opcao: ");

    try {
      int opcao = Integer.parseInt(scanner.nextLine());
      if (opcao >= 0 && opcao <= 3) {
        return opcao;
      }
    } catch (NumberFormatException e) {
      // Trata abaixo como opcao invalida.
    }

    throw new EntradaInvalidaException("Opcao invalida. Escolha 0, 1, 2 ou 3.");
  }

  private void lancarNotasSelecionadas(
      String codigoTurma, String matriculaAluno, Professor professorLogado, int opcaoLancamento) {
    String matriculaProfessor = professorLogado.getMatricula();

    if (opcaoLancamento == 1) {
      float primeiraNota = lerNota("Nota da primeira unidade: ");
      boletimService.lancarPrimeiraNota(
          codigoTurma, matriculaAluno, primeiraNota, matriculaProfessor);
      exibirComprovanteLancamentoVisual(codigoTurma, matriculaAluno);
      return;
    }

    if (opcaoLancamento == 2) {
      float segundaNota = lerNota("Nota da segunda unidade: ");
      boletimService.lancarSegundaNota(
          codigoTurma, matriculaAluno, segundaNota, matriculaProfessor);
      exibirComprovanteLancamentoVisual(codigoTurma, matriculaAluno);
      return;
    }

    float primeiraNota = lerNota("Nota da primeira unidade: ");
    float segundaNota = lerNota("Nota da segunda unidade: ");
    boletimService.lancarNotas(
        codigoTurma, matriculaAluno, primeiraNota, segundaNota, matriculaProfessor);
    exibirComprovanteLancamentoVisual(codigoTurma, matriculaAluno);
  }

  private void exibirComprovanteLancamentoVisual(String codigoTurma, String matriculaAluno) {
    Boletim boletim = boletimService.buscarBoletimPorAlunoTurma(matriculaAluno, codigoTurma);
    Aluno aluno = usuarioService.buscarAlunoPorMatricula(matriculaAluno);

    if (boletim != null && aluno != null) {
      System.out.println("\n=========================================================");
      System.out.println("          COMPROVANTE DE LANCAMENTO DE NOTAS             ");
      System.out.println("=========================================================");
      System.out.println(" CODIGO DA TURMA       : " + codigoTurma);
      System.out.println(
          " ESTUDANTE AVALIADO    : " + aluno.getNome() + " (" + matriculaAluno + ")");
      System.out.println("---------------------------------------------------------");
      System.out.println(
          " NOTA DA ETAPA 1       : " + formatarNotaComprovante(boletim.getPrimeiraNota()));
      System.out.println(
          " NOTA DA ETAPA 2       : " + formatarNotaComprovante(boletim.getSegundaNota()));
      System.out.println("---------------------------------------------------------");
      System.out.println(
          " MEDIA CONSOLIDADA     : " + formatarMediaComprovante(boletim.calcularMediaFinal()));
      System.out.println("=========================================================\n");
    } else {
      System.out.println("Notas registradas com sucesso.");
    }
  }

  static String formatarNotaComprovante(Float nota) {
    return nota == null ? "--" : String.format("%.1f", nota);
  }

  static String formatarMediaComprovante(Float media) {
    return media == null ? "--" : String.format("%.2f", media);
  }

  private float lerNota(String rotulo) {
    while (true) {
      System.out.print(rotulo);
      String valor = scanner.nextLine();

      try {
        float nota = Float.parseFloat(valor.replace(',', '.'));
        if (nota >= 0 && nota <= 10) {
          return nota;
        }
      } catch (NumberFormatException e) {
        // Trata abaixo como nota invalida.
      }

      System.out.println("Nota invalida. Informe um valor entre 0 e 10.");
      System.out.println();
    }
  }

  private Turma selecionarTurmaParaNotas(Professor professorLogado) {
    List<Turma> turmas = turmaService.listarTurmasPorProfessor(professorLogado.getMatricula());

    if (turmas == null || turmas.isEmpty()) {
      throw new EntradaInvalidaException("Professor nao possui turmas cadastradas.");
    }

    System.out.println("Turmas do professor:");

    for (int i = 0; i < turmas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirOpcaoTurmaProfessor(i + 1, turmas.get(i));
    }

    System.out.println();
    System.out.println("0 - Voltar");
    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner, "Informe o numero da turma para lancar notas: ", turmas.size());

    if (opcao == 0) {
      System.out.println("Voltando...");
      return null;
    }

    return turmas.get(opcao - 1);
  }

  private String selecionarAlunoParaNotas(Turma turma) {
    List<String> alunosMatriculados = turma.getMatriculados();

    if (alunosMatriculados == null || alunosMatriculados.isEmpty()) {
      throw new EntradaInvalidaException("A turma nao possui alunos matriculados.");
    }

    System.out.println("Alunos matriculados:");

    for (int i = 0; i < alunosMatriculados.size(); i++) {
      Aluno aluno = usuarioService.buscarAlunoPorMatricula(alunosMatriculados.get(i));
      if (i > 0) {
        System.out.println();
      }

      System.out.println((i + 1) + " - " + aluno.getNome());
      System.out.println("    Matricula: " + aluno.getMatricula());
    }

    System.out.println();
    System.out.println("0 - Voltar");
    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner, "Informe o numero do aluno: ", alunosMatriculados.size());

    if (opcao == 0) {
      System.out.println("Voltando...");
      return null;
    }

    return alunosMatriculados.get(opcao - 1);
  }

  private Turma lerDadosTurma() {
    final String codigoDisciplina =
        EntradaTela.lerTextoObrigatorioOuCancelar(
            scanner, "Informe o codigo da disciplina: ", "Disciplina");

    final String periodoLetivo =
          EntradaTela.lerTextoObrigatorioOuCancelar(
            scanner,
            "Informe o periodo letivo da turma. Exemplo: 2026.2: ",
            "Periodo letivo");

    final String matriculaProfessor =
        EntradaTela.lerTextoObrigatorioOuCancelar(
            scanner,
            "Informe a matricula do professor responsavel: ",
            "Professor");

    final int limiteVagas =
        EntradaTela.lerInteiroPositivoOuCancelar(scanner, "Informe o limite de vagas: ");

    final String horario =
        EntradaTela.lerTextoObrigatorioOuCancelar(
            scanner, "Informe o horario da turma. Exemplo: SEG 08:00-10:00: ", "Horario");

    final String sala =
        EntradaTela.lerTextoObrigatorioOuCancelar(scanner, "Informe a sala da turma: ", "Sala");

    return new Turma(
        codigoDisciplina, periodoLetivo, matriculaProfessor, limiteVagas, horario, sala);
  }

  private Turma lerDadosTurma(String codigoCurso, Turma turmaAtual) {
    Disciplina disciplinaSelecionada = selecionarDisciplinaDoCurso(codigoCurso);
    final String codigoDisciplina = disciplinaSelecionada.getCodigo();

    final String periodoAtivo = turmaService.buscarPeriodoLetivoAtivo();
    String periodoLetivo;

    if (periodoAtivo != null && !periodoAtivo.isBlank()) {
      System.out.println("Periodo letivo ativo: " + periodoAtivo);
      System.out.print(
          "Pressione ENTER para usar o periodo ativo ou informe outro periodo letivo: ");
      String entradaPeriodo = scanner.nextLine();
      if ("0".equals(entradaPeriodo.trim())) {
        throw new EntradaTela.EntradaCanceladaException();
      }
      periodoLetivo = entradaPeriodo.isBlank() ? periodoAtivo : entradaPeriodo;
    } else {
      periodoLetivo =
          EntradaTela.lerTextoObrigatorioOuCancelar(
              scanner,
              "Informe o periodo letivo da turma. Exemplo: 2026.2: ",
              "Periodo letivo");
    }

    final int limiteVagas =
        EntradaTela.lerInteiroPositivoOuCancelar(scanner, "Informe o limite de vagas: ");

    final String horario =
        EntradaTela.lerTextoObrigatorioOuCancelar(
            scanner, "Informe o horario da turma. Exemplo: SEG 08:00-10:00: ", "Horario");

    final String sala =
        EntradaTela.lerTextoObrigatorioOuCancelar(scanner, "Informe a sala da turma: ", "Sala");

    String codigoTurmaIgnorada = turmaAtual == null ? null : turmaAtual.getCodigo();
    Professor professorSelecionado =
        selecionarProfessorDisponivelDoCurso(
            codigoCurso, periodoLetivo, horario, codigoTurmaIgnorada, "0 - Cancelar atualizacao");
    final String matriculaProfessor = professorSelecionado.getMatricula();

    return new Turma(
        codigoDisciplina, periodoLetivo, matriculaProfessor, limiteVagas, horario, sala);
  }

  private Turma lerDadosNovaTurma(String codigoCurso) {
    Disciplina disciplinaSelecionada = selecionarDisciplinaDoCurso(codigoCurso);
    final String codigoDisciplina = disciplinaSelecionada.getCodigo();
    final String periodoLetivo = lerPeriodoLetivoDaTurma();
    final int limiteVagas =
        EntradaTela.lerInteiroPositivoOuCancelar(scanner, "Informe o limite de vagas: ");
    final String horario =
        EntradaTela.lerTextoObrigatorioOuCancelar(
            scanner, "Informe o horario da turma. Exemplo: SEG 08:00-10:00: ", "Horario");
    final String sala =
        EntradaTela.lerTextoObrigatorioOuCancelar(scanner, "Informe a sala da turma: ", "Sala");

    Professor professorSelecionado =
        selecionarProfessorDisponivelDoCurso(
            codigoCurso, periodoLetivo, horario, null, "0 - Cancelar cadastro");
    final String matriculaProfessor = professorSelecionado.getMatricula();

    return new Turma(
        codigoDisciplina, periodoLetivo, matriculaProfessor, limiteVagas, horario, sala);
  }

  private String lerPeriodoLetivoDaTurma() {
    final String periodoAtivo = turmaService.buscarPeriodoLetivoAtivo();

    if (periodoAtivo != null && !periodoAtivo.isBlank()) {
      System.out.println("Periodo letivo ativo: " + periodoAtivo);
      System.out.print(
          "Pressione ENTER para usar o periodo ativo ou informe outro periodo letivo: ");
      String entradaPeriodo = scanner.nextLine();
      if ("0".equals(entradaPeriodo.trim())) {
        throw new EntradaTela.EntradaCanceladaException();
      }
      return entradaPeriodo.isBlank() ? periodoAtivo : entradaPeriodo;
    }

    return EntradaTela.lerTextoObrigatorioOuCancelar(
        scanner,
        "Informe o periodo letivo da turma. Exemplo: 2026.2: ",
        "Periodo letivo");
  }

  private Disciplina selecionarDisciplinaDoCurso(String codigoCurso) {
    List<Disciplina> disciplinas = disciplinaService.listarDisciplinasPorCurso(codigoCurso);

    if (disciplinas == null || disciplinas.isEmpty()) {
      throw new EntradaInvalidaException("Nenhuma disciplina cadastrada para o curso.");
    }

    System.out.println("Disciplinas do curso:");
    System.out.println("0 - Cancelar cadastro");

    for (int i = 0; i < disciplinas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirOpcaoDisciplina(i + 1, disciplinas.get(i));
    }

    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner, "Informe o numero da disciplina: ", disciplinas.size());
    if (opcao == 0) {
      throw new EntradaTela.EntradaCanceladaException();
    }

    return disciplinas.get(opcao - 1);
  }

  private Professor selecionarProfessorDisponivelDoCurso(
      String codigoCurso,
      String periodoLetivo,
      String horario,
      String codigoTurmaIgnorada,
      String textoCancelar) {
    List<Professor> professores = turmaService.listarProfessoresPorCurso(codigoCurso);

    if (professores == null || professores.isEmpty()) {
      throw new EntradaInvalidaException("Nenhum professor cadastrado para o curso.");
    }

    List<Professor> professoresDisponiveis = new ArrayList<>();
    List<ProfessorIndisponivel> professoresIndisponiveis = new ArrayList<>();

    for (Professor professor : professores) {
      Turma turmaConflitante =
          buscarTurmaComChoqueProfessor(professor, periodoLetivo, horario, codigoTurmaIgnorada);

      if (turmaConflitante == null) {
        professoresDisponiveis.add(professor);
      } else {
        professoresIndisponiveis.add(new ProfessorIndisponivel(professor, turmaConflitante));
      }
    }

    exibirProfessoresDisponiveis(professoresDisponiveis, textoCancelar);
    exibirProfessoresIndisponiveis(professoresIndisponiveis);

    if (professoresDisponiveis.isEmpty()) {
      throw new EntradaInvalidaException("Nenhum professor disponivel para esse horario.");
    }

    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner,
            "Informe o numero do professor ou 0 para cancelar: ",
            professoresDisponiveis.size());
    if (opcao == 0) {
      throw new EntradaTela.EntradaCanceladaException();
    }

    return professoresDisponiveis.get(opcao - 1);
  }

  private Turma buscarTurmaComChoqueProfessor(
      Professor professor, String periodoLetivo, String horario, String codigoTurmaIgnorada) {
    for (Turma turma : turmaService.listarTurmasPorProfessor(professor.getMatricula())) {
      if (mesmaTurmaPorCodigo(turma, codigoTurmaIgnorada)) {
        continue;
      }

      boolean mesmoPeriodo =
          turma.getPeriodoLetivo() != null
              && periodoLetivo != null
              && turma.getPeriodoLetivo().equalsIgnoreCase(periodoLetivo);
      boolean mesmoHorario =
          turma.getHorario() != null
              && horario != null
              && turma.getHorario().equalsIgnoreCase(horario);

      if (mesmoPeriodo && mesmoHorario) {
        return turma;
      }
    }

    return null;
  }

  private boolean mesmaTurmaPorCodigo(Turma turma, String codigoTurmaIgnorada) {
    return turma != null
        && turma.getCodigo() != null
        && codigoTurmaIgnorada != null
        && turma.getCodigo().equalsIgnoreCase(codigoTurmaIgnorada);
  }

  private void exibirProfessoresDisponiveis(
      List<Professor> professoresDisponiveis, String textoCancelar) {
    System.out.println();
    System.out.println("Professores disponiveis:");

    if (professoresDisponiveis.isEmpty()) {
      System.out.println("Nenhum professor disponivel para esse horario.");
      return;
    }

    System.out.println(textoCancelar);

    for (int i = 0; i < professoresDisponiveis.size(); i++) {
      Professor professor = professoresDisponiveis.get(i);
      System.out.println();
      System.out.println((i + 1) + " - " + professor.getNome());
      System.out.println("    Email: " + professor.getEmail());
    }
  }

  private void exibirProfessoresIndisponiveis(
      List<ProfessorIndisponivel> professoresIndisponiveis) {
    if (professoresIndisponiveis.isEmpty()) {
      return;
    }

    System.out.println();
    System.out.println("Professores indisponiveis:");

    for (ProfessorIndisponivel professorIndisponivel : professoresIndisponiveis) {
      System.out.println(
          "- "
              + professorIndisponivel.professor.getNome()
              + " - choque de horario com "
              + nomeAmigavelTurma(professorIndisponivel.turmaConflitante));
    }
  }

  private int lerOpcaoNumerada(int quantidadeOpcoes) {
    while (true) {
      String entrada = scanner.nextLine();

      try {
        int opcao = Integer.parseInt(entrada);
        if (opcao >= 1 && opcao <= quantidadeOpcoes) {
          return opcao;
        }
      } catch (NumberFormatException e) {
        // Trata abaixo como opcao invalida.
      }

      System.out.println("Opcao invalida. Escolha uma opcao da lista.");
      System.out.println();
      System.out.print("Escolha uma opcao: ");
    }
  }

  private void exibirOpcaoDisciplina(int numeroOpcao, Disciplina disciplina) {
    System.out.println(numeroOpcao + " - " + disciplina.getNome());
    System.out.println("    Codigo interno: " + disciplina.getCodigo());
    System.out.println("    Carga horaria: " + disciplina.getCargaHoraria() + "h");
    System.out.println("    Creditos: " + disciplina.getCreditos());
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

  private void exibirListaTurmas(List<Turma> turmas) {
    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Nenhuma turma cadastrada.");
      return;
    }

    for (int i = 0; i < turmas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirTurmaDetalhada(i + 1, turmas.get(i));
    }
  }

  private void exibirListaTurmasDetalhada(List<Turma> turmas) {
    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Nenhuma turma cadastrada.");
      return;
    }

    for (int i = 0; i < turmas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirTurmaDetalhada(i + 1, turmas.get(i));
    }
  }

  private void exibirListaMinhasTurmasProfessor(List<Turma> turmas) {
    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Professor nao possui turmas cadastradas.");
      return;
    }

    for (int i = 0; i < turmas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirMinhaTurmaProfessor(i + 1, turmas.get(i));
    }
  }

  private void exibirListaTurmasDisponiveisAluno(List<Turma> turmas) {
    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Nenhuma turma disponivel para o curso do aluno.");
      return;
    }

    for (int i = 0; i < turmas.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirTurmaDisponivelAluno(i + 1, turmas.get(i));
    }
  }

  private void exibirTurmaDisponivelAluno(int numero, Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();
    String situacao =
        vagasOcupadas >= turma.getLimiteVagas() ? "cheia - lista de espera" : "vagas disponiveis";

    System.out.println(numero + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Professor: " + buscarNomeProfessorTurma(turma));
    System.out.println("    Periodo letivo: " + formatarValor(turma.getPeriodoLetivo()));
    System.out.println("    Horario: " + formatarValor(turma.getHorario()));
    System.out.println("    Sala: " + formatarValor(turma.getSala()));
    System.out.println("    Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
    System.out.println("    Situacao: " + situacao);
  }

  private void exibirMinhaTurmaProfessor(int numero, Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();

    System.out.println(numero + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Periodo letivo: " + formatarValor(turma.getPeriodoLetivo()));
    System.out.println("    Horario: " + formatarValor(turma.getHorario()));
    System.out.println("    Sala: " + formatarValor(turma.getSala()));
    System.out.println("    Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
  }

  private void exibirTurmaAluno(int numero, Turma turma, String situacao) {
    System.out.println(numero + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Professor: " + buscarNomeProfessorTurma(turma));
    System.out.println("    Periodo letivo: " + formatarValor(turma.getPeriodoLetivo()));
    System.out.println("    Horario: " + formatarValor(turma.getHorario()));
    System.out.println("    Sala: " + formatarValor(turma.getSala()));
    System.out.println("    Situacao: " + situacao);
  }

  private void exibirTurmaDetalhada(int numero, Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();

    System.out.println(numero + " - " + nomeAmigavelTurma(turma));
    System.out.println("    Codigo interno: " + formatarValor(turma.getCodigo()));
    System.out.println("    Professor: " + buscarNomeProfessorTurma(turma));
    System.out.println("    Periodo letivo: " + formatarValor(turma.getPeriodoLetivo()));
    System.out.println("    Horario: " + formatarValor(turma.getHorario()));
    System.out.println("    Sala: " + formatarValor(turma.getSala()));
    System.out.println("    Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
  }

  private String nomeAmigavelTurma(Turma turma) {
    if (turma == null) {
      return "-";
    }

    return buscarNomeDisciplinaTurma(turma) + " - Turma " + calcularNumeroTurmaDisciplina(turma);
  }

  private int calcularNumeroTurmaDisciplina(Turma turma) {
    List<Turma> turmasMesmaDisciplina = listarTurmasMesmaDisciplina(turma);

    for (int i = 0; i < turmasMesmaDisciplina.size(); i++) {
      if (mesmaTurma(turmasMesmaDisciplina.get(i), turma)) {
        return i + 1;
      }
    }

    return 1;
  }

  private List<Turma> listarTurmasMesmaDisciplina(Turma turmaReferencia) {
    List<Turma> turmasMesmaDisciplinaPeriodo = new ArrayList<>();

    if (turmaReferencia == null
        || turmaReferencia.getCodigoDisciplina() == null
        || turmaReferencia.getPeriodoLetivo() == null) {
      return turmasMesmaDisciplinaPeriodo;
    }

    for (Turma turma : turmaService.listarTurmas()) {
      if (turma.getCodigoDisciplina() != null
          && turma.getPeriodoLetivo() != null
          && turma.getCodigoDisciplina().equalsIgnoreCase(turmaReferencia.getCodigoDisciplina())
          && turma.getPeriodoLetivo().equalsIgnoreCase(turmaReferencia.getPeriodoLetivo())) {
        turmasMesmaDisciplinaPeriodo.add(turma);
      }
    }

    turmasMesmaDisciplinaPeriodo.sort(
        (primeiraTurma, segundaTurma) ->
            formatarValor(primeiraTurma.getCodigo())
                .compareToIgnoreCase(formatarValor(segundaTurma.getCodigo())));

    return turmasMesmaDisciplinaPeriodo;
  }

  private boolean mesmaTurma(Turma primeiraTurma, Turma segundaTurma) {
    if (primeiraTurma == null || segundaTurma == null) {
      return false;
    }

    if (primeiraTurma.getCodigo() == null || segundaTurma.getCodigo() == null) {
      return primeiraTurma == segundaTurma;
    }

    return primeiraTurma.getCodigo().equalsIgnoreCase(segundaTurma.getCodigo());
  }

  private String buscarNomeDisciplinaTurma(Turma turma) {
    if (turma == null) {
      return "-";
    }

    return formatarValor(turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
  }

  private String buscarNomeProfessorTurma(Turma turma) {
    if (turma == null) {
      return "-";
    }

    return formatarValor(turmaService.buscarNomeProfessor(turma.getMatriculaProfessor()));
  }

  private String formatarValor(String valor) {
    if (valor == null || valor.isBlank()) {
      return "-";
    }

    return valor;
  }
}
