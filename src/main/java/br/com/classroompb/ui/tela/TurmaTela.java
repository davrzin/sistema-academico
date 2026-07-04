package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
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
      Turma novaTurma = lerDadosTurma(coordenadorLogado.getCodigoCurso());

      turmaService.ofertarTurma(novaTurma, coordenadorLogado.getCodigoCurso());

      System.out.println("Turma cadastrada com sucesso.");
      System.out.println("Codigo da turma: " + novaTurma.getCodigo());

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
      exibirListaTurmasDetalhada(turmas);

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

      System.out.println("Informe o código da turma que deseja atualizar:");
      String codigo = scanner.nextLine();

      Turma turmaAtualizada = lerDadosTurma();

      turmaService.alterarTurma(codigo, turmaAtualizada);

      System.out.println("Turma atualizada com sucesso.");

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

      Turma turmaAtualizada = lerDadosTurma(coordenadorLogado.getCodigoCurso());

      turmaService.alterarTurma(
          turmaSelecionada.getCodigo(), turmaAtualizada, coordenadorLogado.getCodigoCurso());

      System.out.println("Turma atualizada com sucesso.");

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
      exibirOpcaoTurmaAtualizacao(i + 1, turmas.get(i));
    }

    System.out.println("Informe o numero da turma que deseja atualizar:");
    int opcao = lerOpcaoNumerada(turmas.size());

    return turmas.get(opcao - 1);
  }

  private void exibirOpcaoTurmaAtualizacao(int numeroOpcao, Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();

    System.out.println("\nOpcao " + numeroOpcao + ":");
    System.out.println(
        "Disciplina: " + turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
    System.out.println(
        "Professor: " + turmaService.buscarNomeProfessor(turma.getMatriculaProfessor()));
    System.out.println("Periodo letivo: " + turma.getPeriodoLetivo());
    System.out.println("Horario: " + turma.getHorario());
    System.out.println("Sala: " + turma.getSala());
    System.out.println("Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
  }

  /**
   * Solicita o cancelamento de uma turma.
   */
  public void cancelarTurma() {
    try {
      listarTurmas();

      System.out.println("Informe o código da turma que deseja cancelar:");
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
      exibirOpcaoTurmaAtualizacao(i + 1, turmas.get(i));
    }

    System.out.println("Informe o numero da turma que deseja cancelar:");
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
      System.out.println("Informe o código da turma:");
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
      exibirOpcaoTurmaAtualizacao(i + 1, turmas.get(i));
    }

    System.out.println("Informe o numero da turma para visualizar a lista de espera:");
    int opcao = lerOpcaoNumerada(turmas.size());

    return turmas.get(opcao - 1);
  }

  private void exibirListaEspera(Turma turma) {
    List<String> listaEspera = turma.getListaEspera();

    System.out.println("\nLista de espera da turma " + turma.getCodigo() + ":");

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

    System.out.println("======================================");
    System.out.println("Turmas disponiveis para matricula:");

    for (int i = 0; i < turmas.size(); i++) {
      exibirOpcaoTurmaMatricula(i + 1, turmas.get(i));
    }

    System.out.println("Informe o numero da turma que deseja se matricular:");
    int opcao = Integer.parseInt(scanner.nextLine());

    if (opcao < 1 || opcao > turmas.size()) {
      throw new EntradaInvalidaException("Opcao invalida. Escolha uma turma da lista.");
    }

    return turmas.get(opcao - 1);
  }

  private void exibirOpcaoTurmaMatricula(int numeroOpcao, Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();
    String situacao =
        vagasOcupadas >= turma.getLimiteVagas() ? "cheia - lista de espera" : "vagas disponiveis";

    System.out.println("\nOpcao " + numeroOpcao + ":");
    System.out.println(
        "Disciplina: " + turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
    System.out.println(
        "Professor: " + turmaService.buscarNomeProfessor(turma.getMatriculaProfessor()));
    System.out.println("Periodo letivo: " + turma.getPeriodoLetivo());
    System.out.println("Horario: " + turma.getHorario());
    System.out.println("Sala: " + turma.getSala());
    System.out.println("Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
    System.out.println("Situacao: " + situacao);
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
      exibirOpcaoTurmaCancelamento(i + 1, turmasMatriculadas.get(i));
    }

    System.out.println("Informe o numero da turma que deseja cancelar:");
    int opcao = Integer.parseInt(scanner.nextLine());

    if (opcao < 1 || opcao > turmasMatriculadas.size()) {
      throw new EntradaInvalidaException("Opcao invalida. Escolha uma turma da lista.");
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
    System.out.println("\nOpcao " + numeroOpcao + ":");
    System.out.println(
        "Disciplina: " + turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
    System.out.println(
        "Professor: " + turmaService.buscarNomeProfessor(turma.getMatriculaProfessor()));
    System.out.println("Periodo letivo: " + turma.getPeriodoLetivo());
    System.out.println("Horario: " + turma.getHorario());
    System.out.println("Sala: " + turma.getSala());
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

      for (Turma turma : turmasCurso) {
        if (turma.getMatriculados() != null
            && turma.getMatriculados().contains(alunoLogado.getMatricula())) {
          exibirTurmaAluno(turma, "matriculado");
          encontrou = true;
        } else if (turma.getListaEspera() != null
            && turma.getListaEspera().contains(alunoLogado.getMatricula())) {
          exibirTurmaAluno(turma, "lista de espera");
          encontrou = true;
        }
      }

      if (!encontrou) {
        System.out.println("Aluno nao possui matriculas ou entradas em lista de espera.");
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
      exibirOpcaoTurmaProfessor(i + 1, turmas.get(i));
    }

    System.out.println("Informe o numero da turma para lancar frequencia:");
    int opcao = lerOpcaoNumerada(turmas.size());

    return turmas.get(opcao - 1);
  }

  private void exibirOpcaoTurmaProfessor(int numeroOpcao, Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();

    System.out.println("\nOpcao " + numeroOpcao + ":");
    System.out.println(
        "Disciplina: " + turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
    System.out.println("Periodo letivo: " + turma.getPeriodoLetivo());
    System.out.println("Horario: " + turma.getHorario());
    System.out.println("Sala: " + turma.getSala());
    System.out.println("Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
  }

  /**
   * Solicita o lancamento de notas por professor.
   *
   * @param professorLogado professor logado.
   */
  public void adicionarNotas(Professor professorLogado) {
    try {
      Turma turmaSelecionada = selecionarTurmaParaNotas(professorLogado);
      String codigoTurma = turmaSelecionada.getCodigo();

      Turma turma = turmaService.buscarTurmaPorCodigo(codigoTurma, professorLogado.getMatricula());

      if (turmaService.existeAlunosMatriculados(turma)) {
        System.out.println("A turma nao possui alunos matriculados.");
        return;
      }

      String matriculaAluno = selecionarAlunoParaNotas(turma);

      System.out.println("Informe a nota da primeira unidade:");
      float primeiraNota = Float.parseFloat(scanner.nextLine());

      System.out.println("Informe a nota da segunda unidade:");
      float segundaNota = Float.parseFloat(scanner.nextLine());

      boletimService.lancarNotas(
          codigoTurma, matriculaAluno, primeiraNota, segundaNota, professorLogado.getMatricula());

      System.out.println("Notas registradas com sucesso.");
    } catch (TurmaNaoEncontradaException | EntradaInvalidaException | PersistenciaException e) {
      System.out.println(e.getMessage());
    } catch (NumberFormatException e) {
      System.out.println("Nota invalida.");
    }
  }

  private Turma selecionarTurmaParaNotas(Professor professorLogado) {
    List<Turma> turmas = turmaService.listarTurmasPorProfessor(professorLogado.getMatricula());

    if (turmas == null || turmas.isEmpty()) {
      throw new EntradaInvalidaException("Professor nao possui turmas cadastradas.");
    }

    System.out.println("Turmas do professor:");

    for (int i = 0; i < turmas.size(); i++) {
      exibirOpcaoTurmaProfessor(i + 1, turmas.get(i));
    }

    System.out.println("Informe o numero da turma para lancar notas:");
    int opcao = lerOpcaoNumerada(turmas.size());

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
      System.out.println("\nOpcao " + (i + 1) + ":");
      System.out.println("Aluno: " + aluno.getNome());
      System.out.println("Matricula: " + aluno.getMatricula());
    }

    System.out.println("Informe o numero do aluno:");
    int opcao = lerOpcaoNumerada(alunosMatriculados.size());

    return alunosMatriculados.get(opcao - 1);
  }

  private Turma lerDadosTurma() {
    System.out.println("Informe o código da disciplina:");
    final String codigoDisciplina = scanner.nextLine();

    System.out.println("Informe o período letivo da turma. Exemplo: 2026.2");
    final String periodoLetivo = scanner.nextLine();

    System.out.println("Informe a matrícula do professor responsável:");
    final String matriculaProfessor = scanner.nextLine();

    System.out.println("Informe o limite de vagas:");
    final int limiteVagas = Integer.parseInt(scanner.nextLine());

    System.out.println("Informe o horário da turma. Exemplo: SEG 08:00-10:00");
    final String horario = scanner.nextLine();

    System.out.println("Informe a sala da turma:");
    final String sala = scanner.nextLine();

    return new Turma(
        codigoDisciplina, periodoLetivo, matriculaProfessor, limiteVagas, horario, sala);
  }

  private Turma lerDadosTurma(String codigoCurso) {
    Disciplina disciplinaSelecionada = selecionarDisciplinaDoCurso(codigoCurso);
    final String codigoDisciplina = disciplinaSelecionada.getCodigo();

    final String periodoAtivo = turmaService.buscarPeriodoLetivoAtivo();
    String periodoLetivo;

    if (periodoAtivo != null && !periodoAtivo.isBlank()) {
      System.out.println("Periodo letivo ativo: " + periodoAtivo);
      System.out.println(
          "Pressione ENTER para usar o periodo ativo ou informe outro periodo letivo:");
      String entradaPeriodo = scanner.nextLine();
      periodoLetivo = entradaPeriodo.isBlank() ? periodoAtivo : entradaPeriodo;
    } else {
      System.out.println("Informe o periodo letivo da turma. Exemplo: 2026.2");
      periodoLetivo = scanner.nextLine();
    }

    Professor professorSelecionado = selecionarProfessorDoCurso(codigoCurso);
    final String matriculaProfessor = professorSelecionado.getMatricula();

    System.out.println("Informe o limite de vagas:");
    final int limiteVagas = Integer.parseInt(scanner.nextLine());

    System.out.println("Informe o horario da turma. Exemplo: SEG 08:00-10:00");
    final String horario = scanner.nextLine();

    System.out.println("Informe a sala da turma:");
    final String sala = scanner.nextLine();

    return new Turma(
        codigoDisciplina, periodoLetivo, matriculaProfessor, limiteVagas, horario, sala);
  }

  private Disciplina selecionarDisciplinaDoCurso(String codigoCurso) {
    List<Disciplina> disciplinas = disciplinaService.listarDisciplinasPorCurso(codigoCurso);

    if (disciplinas == null || disciplinas.isEmpty()) {
      throw new EntradaInvalidaException("Nenhuma disciplina cadastrada para o curso.");
    }

    System.out.println("Disciplinas do curso:");

    for (int i = 0; i < disciplinas.size(); i++) {
      exibirOpcaoDisciplina(i + 1, disciplinas.get(i));
    }

    System.out.println("Informe o numero da disciplina:");
    int opcao = lerOpcaoNumerada(disciplinas.size());

    return disciplinas.get(opcao - 1);
  }

  private Professor selecionarProfessorDoCurso(String codigoCurso) {
    List<Professor> professores = turmaService.listarProfessoresPorCurso(codigoCurso);

    if (professores == null || professores.isEmpty()) {
      throw new EntradaInvalidaException("Nenhum professor cadastrado para o curso.");
    }

    System.out.println("Professores do curso:");

    for (int i = 0; i < professores.size(); i++) {
      exibirOpcaoProfessor(i + 1, professores.get(i));
    }

    System.out.println("Informe o numero do professor responsavel:");
    int opcao = lerOpcaoNumerada(professores.size());

    return professores.get(opcao - 1);
  }

  private int lerOpcaoNumerada(int quantidadeOpcoes) {
    int opcao;

    try {
      opcao = Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      throw new EntradaInvalidaException("Opcao invalida. Digite o numero de uma opcao listada.");
    }

    if (opcao < 1 || opcao > quantidadeOpcoes) {
      throw new EntradaInvalidaException("Opcao invalida. Escolha uma opcao da lista.");
    }

    return opcao;
  }

  private void exibirOpcaoDisciplina(int numeroOpcao, Disciplina disciplina) {
    System.out.println("\nOpcao " + numeroOpcao + ":");
    System.out.println("Disciplina: " + disciplina.getNome());
    System.out.println("Codigo: " + disciplina.getCodigo());
    System.out.println("Carga horaria: " + disciplina.getCargaHoraria());
    System.out.println("Creditos: " + disciplina.getCreditos());
  }

  private void exibirOpcaoProfessor(int numeroOpcao, Professor professor) {
    System.out.println("\nOpcao " + numeroOpcao + ":");
    System.out.println("Professor: " + professor.getNome());
    System.out.println("Matricula: " + professor.getMatricula());
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

    for (Turma turma : turmas) {
      exibirTurma(turma);
    }
  }

  private void exibirListaTurmasDetalhada(List<Turma> turmas) {
    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Nenhuma turma cadastrada.");
      return;
    }

    for (Turma turma : turmas) {
      exibirTurmaDetalhada(turma);
    }
  }

  private void exibirListaTurmasDisponiveisAluno(List<Turma> turmas) {
    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Nenhuma turma disponivel para o curso do aluno.");
      return;
    }

    for (Turma turma : turmas) {
      exibirTurmaDisponivelAluno(turma);
    }
  }

  private void exibirTurmaDisponivelAluno(Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();
    String situacao =
        vagasOcupadas >= turma.getLimiteVagas() ? "cheia - lista de espera" : "vagas disponiveis";

    System.out.println("\nTurma:");
    System.out.println("Codigo: " + turma.getCodigo());
    System.out.println(
        "Disciplina: " + turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
    System.out.println(
        "Professor: " + turmaService.buscarNomeProfessor(turma.getMatriculaProfessor()));
    System.out.println("Periodo letivo: " + turma.getPeriodoLetivo());
    System.out.println("Horario: " + turma.getHorario());
    System.out.println("Sala: " + turma.getSala());
    System.out.println("Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
    System.out.println("Situacao: " + situacao);
    System.out.println();
  }

  private void exibirTurmaAluno(Turma turma, String situacao) {
    System.out.println("\nMatricula:");
    System.out.println("Turma: " + turma.getCodigo());
    System.out.println(
        "Disciplina: " + turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
    System.out.println(
        "Professor: " + turmaService.buscarNomeProfessor(turma.getMatriculaProfessor()));
    System.out.println("Periodo letivo: " + turma.getPeriodoLetivo());
    System.out.println("Horario: " + turma.getHorario());
    System.out.println("Sala: " + turma.getSala());
    System.out.println("Situacao: " + situacao);
    System.out.println();
  }

  private void exibirTurmaDetalhada(Turma turma) {
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();

    System.out.println("\nTurma:");
    System.out.println("Codigo: " + turma.getCodigo());
    System.out.println(
        "Disciplina: " + turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
    System.out.println(
        "Professor: " + turmaService.buscarNomeProfessor(turma.getMatriculaProfessor()));
    System.out.println("Periodo letivo: " + turma.getPeriodoLetivo());
    System.out.println("Horario: " + turma.getHorario());
    System.out.println("Sala: " + turma.getSala());
    System.out.println("Vagas: " + vagasOcupadas + "/" + turma.getLimiteVagas());
    System.out.println();
  }

  private void exibirTurma(Turma turma) {
    System.out.println("\nTurma:");
    System.out.println("Código: " + turma.getCodigo());
    System.out.println("Código da disciplina: " + turma.getCodigoDisciplina());
    System.out.println("Período letivo: " + turma.getPeriodoLetivo());
    System.out.println("Matrícula do professor: " + turma.getMatriculaProfessor());
    System.out.println("Limite de vagas: " + turma.getLimiteVagas());
    System.out.println("Horário: " + turma.getHorario());
    System.out.println("Sala: " + turma.getSala());
    System.out.println();
  }
}
