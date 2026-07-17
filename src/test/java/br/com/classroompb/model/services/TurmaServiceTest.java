package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.exception.AlunoNaoCumprePreRequisitosException;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.TurmaNaoEncontradaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do servico de turmas.
 */
public class TurmaServiceTest {

  private static final String CODIGO_CURSO = "cur00";

  @TempDir Path tempDir;

  /**
   * Limpa os arquivos gerados pelos testes.
   */
  @AfterEach
  public void tearDown() {
    apagarDiretorio("turmas");
    apagarDiretorio("disciplinas");
    apagarDiretorio("periodos");
    apagarDiretorio("usuarios");
    apagarDiretorio("boletins");
    apagarDiretorio("aulas");
  }

  private void apagarDiretorio(String nomeDiretorio) {
    File diretorio = tempDir.resolve(nomeDiretorio).toFile();
    File[] arquivos = diretorio.listFiles();

    if (arquivos != null) {
      for (File arquivo : arquivos) {
        arquivo.delete();
      }
    }

    if (diretorio.exists() && diretorio.isDirectory()) {
      diretorio.delete();
    }
  }

  private TurmaRepository criarTurmaRepository() {
    return new TurmaRepository(new ObjectMapper(), tempDir.resolve("turmas").toString());
  }

  private DisciplinaRepository criarDisciplinaRepository() {
    return new DisciplinaRepository(new ObjectMapper(), tempDir.resolve("disciplinas").toString());
  }

  private PeriodoLetivoRepository criarPeriodoLetivoRepository() {
    return new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
  }

  private UserRepository criarUserRepository() {
    return new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
  }

  private BoletimRepository criarBoletimRepository() {
    return new BoletimRepository(new ObjectMapper(), tempDir.resolve("boletins").toString());
  }

  private AulaRepository criarAulaRepository() {
    return new AulaRepository(new ObjectMapper(), tempDir.resolve("aulas").toString());
  }

  private TurmaService criarService(
      TurmaRepository turmaRepository,
      DisciplinaRepository disciplinaRepository,
      PeriodoLetivoRepository periodoLetivoRepository,
      UserRepository userRepository) {
    return new TurmaService(
        turmaRepository,
        disciplinaRepository,
        periodoLetivoRepository,
        userRepository,
        criarBoletimRepository(),
        criarAulaRepository());
  }

  private Professor criarProfessor(String nome, String email, String senha, String matricula) {
    Professor professor = new Professor(nome, email, senha);
    professor.setMatricula(matricula);
    professor.setCodigoCurso(CODIGO_CURSO);
    return professor;
  }

  private Aluno criarAluno(String nome, String email, String matricula, String senha) {
    return new Aluno(nome, email, matricula, senha, CODIGO_CURSO);
  }

  private void prepararDadosBasicos(
      DisciplinaRepository disciplinaRepository,
      PeriodoLetivoRepository periodoLetivoRepository,
      UserRepository userRepository) {
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur00", List.of()));
    PeriodoLetivo periodoAtivo = new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026");
    periodoAtivo.setPeriodoAtivo(true);
    periodoLetivoRepository.salvarPeriodoLetivo(periodoAtivo);

    Professor professor = criarProfessor("João", "joao@email.com", "senha123", "pr00");
    userRepository.salvarUsuario(professor);
  }

  @Test
  public void deveOfertarTurmaComProfessorResponsavel() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    final UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

    service.ofertarTurma(turma);

    Assertions.assertEquals("tur00", turma.getCodigo());
    Assertions.assertEquals(1, turmaRepository.listarTurmas().size());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoOfertarTurmaNull() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    final DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.ofertarTurma(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoProfessorNaoForInformado() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new Turma("dis00", "2026.2", "", 30, "SEG 08:00-10:00", "LAB 01"));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoProfessorNaoExistir() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    final PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();

    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur00", List.of()));
    periodoLetivoRepository.salvarPeriodoLetivo(
        new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr99", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.ofertarTurma(turma));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoMatriculaForDeAluno() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    final UserRepository userRepository = criarUserRepository();

    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur00", List.of()));
    periodoLetivoRepository.salvarPeriodoLetivo(
        new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    aluno.setMatricula("al00");
    userRepository.salvarUsuario(aluno);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "al00", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.ofertarTurma(turma));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoDisciplinaNaoExistir() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    final DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();

    periodoLetivoRepository.salvarPeriodoLetivo(
        new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));
    Professor professor = criarProfessor("João", "joao@email.com", "senha123", "pr00");
    userRepository.salvarUsuario(professor);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis99", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.ofertarTurma(turma));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoPeriodoLetivoNaoExistir() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    final PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();

    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur00", List.of()));
    Professor professor = criarProfessor("João", "joao@email.com", "senha123", "pr00");
    userRepository.salvarUsuario(professor);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.ofertarTurma(turma));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoProfessorTiverChoqueDeHorario() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

    Turma turmaComConflito = new Turma("dis01", "2026.2", "pr00", 25, "SEG 08:00-10:00", "LAB 02");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.ofertarTurma(turmaComConflito));
  }

  @Test
  public void deveAlterarTurma() {
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    Professor professor = criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    userRepository.salvarUsuario(professor);

    final TurmaRepository turmaRepository = criarTurmaRepository();
    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

    Turma turmaAtualizada = new Turma("dis01", "2026.2", "pr01", 40, "TER 10:00-12:00", "LAB 02");
    service.alterarTurma("tur00", turmaAtualizada);

    Turma turmaEncontrada = turmaRepository.buscarTurmaPorCodigo("tur00");

    Assertions.assertEquals("dis01", turmaEncontrada.getCodigoDisciplina());
    Assertions.assertEquals("pr01", turmaEncontrada.getMatriculaProfessor());
    Assertions.assertEquals(40, turmaEncontrada.getLimiteVagas());
    Assertions.assertEquals("LAB 02", turmaEncontrada.getSala());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoAlterarTurmaInexistente() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turmaAtualizada = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertThrows(
        TurmaNaoEncontradaException.class, () -> service.alterarTurma("tur99", turmaAtualizada));
  }

  @Test
  public void deveIgnorarPropriaTurmaAoValidarConflitoNaAlteracao() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

    Turma turmaAtualizada = new Turma("dis00", "2026.2", "pr00", 35, "SEG 08:00-10:00", "LAB 02");

    Assertions.assertDoesNotThrow(() -> service.alterarTurma("tur00", turmaAtualizada));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoAlterarTurmaGerandoConflitoDeHorario() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    final UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
    service.ofertarTurma(new Turma("dis01", "2026.2", "pr00", 30, "TER 10:00-12:00", "LAB 02"));

    Turma turmaAtualizadaComConflito =
        new Turma("dis01", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 03");

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.alterarTurma("tur01", turmaAtualizadaComConflito));
  }

  @Test
  public void deveCancelarTurma() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

    service.cancelarTurma("tur00");

    Assertions.assertEquals(0, turmaRepository.listarTurmas().size());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCancelarTurmaInexistente() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cancelarTurma("tur99"));
  }

  @Test
  public void deveListarTurmasCorretamente() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    Turma turma1 = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    Turma turma2 = new Turma("dis01", "2026.2", "pr00", 30, "TER 10:00-12:00", "LAB 02");

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    service.ofertarTurma(turma1);
    service.ofertarTurma(turma2);

    List<Turma> turmas = service.listarTurmas();

    Assertions.assertEquals(2, turmas.size());
  }

  @Test
  public void deveListarTurmasPorCodigo() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    Turma turma1 = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    Turma turma2 = new Turma("dis01", "2026.2", "pr00", 30, "TER 10:00-12:00", "LAB 02");

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    service.ofertarTurma(turma1);
    service.ofertarTurma(turma2);

    Turma turmaBuscada = service.buscarTurmaPorCodigo(turma1.getCodigo());

    Assertions.assertNotNull(turmaBuscada);
    Assertions.assertEquals(turmaBuscada.getCodigo(), turma1.getCodigo());
  }

  @Test
  public void deveLancarTurmaNaoEncontradaException() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Assertions.assertThrows(
        TurmaNaoEncontradaException.class, () -> service.buscarTurmaPorCodigo("f23ff"));
  }

  @Test
  public void deveListarTurmasPorProfessor() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
    service.ofertarTurma(new Turma("dis01", "2026.2", "pr00", 30, "TER 10:00-12:00", "LAB 02"));

    List<Turma> turmas = service.listarTurmasPorProfessor("pr00");

    Assertions.assertEquals(2, turmas.size());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoListarTurmasComProfessorNull() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.listarTurmasPorProfessor(null));
  }

  @Test
  public void deveListarTurmasPorPeriodoLetivo() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

    List<Turma> turmas = service.listarTurmasPorPeriodoLetivo("2026.2");

    Assertions.assertEquals(1, turmas.size());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoListarTurmasComPeriodoNulo() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.listarTurmasPorPeriodoLetivo(null));
  }

  @Test
  public void deveCadastrarAlunoCorretamenteEmTurma() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

    Turma turmaAtualizada = service.buscarTurmaPorCodigo(turma.getCodigo());

    Assertions.assertEquals(1, turmaAtualizada.getMatriculados().size());
    Assertions.assertEquals("al00", turmaAtualizada.getMatriculados().getFirst());
    Assertions.assertEquals(1, aluno.getTurmasMatriculadas().size());
    Assertions.assertEquals("tur00", aluno.getTurmasMatriculadas().getFirst());
  }

  @Test
  public void deveImpedirMatriculaEmTurmaDePeriodoLetivoEncerrado() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur00", List.of()));

    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    PeriodoLetivo periodoEncerrado =
        new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026");
    periodoLetivoRepository.salvarPeriodoLetivo(periodoEncerrado);
    PeriodoLetivo periodoAtivo = new PeriodoLetivo("2027.1", "01/02/2027", "20/06/2027");
    periodoAtivo.setPeriodoAtivo(true);
    periodoLetivoRepository.salvarPeriodoLetivo(periodoAtivo);

    UserRepository userRepository = criarUserRepository();
    Professor professor = criarProfessor("Joao", "joao@email.com", "senha123", "pr00");
    userRepository.salvarUsuario(professor);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turmaEncerrada =
        new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turmaEncerrada);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    EntradaInvalidaException excecao =
        Assertions.assertThrows(
            EntradaInvalidaException.class,
            () -> service.cadastrarAlunoEmTurma(turmaEncerrada.getCodigo(), aluno));

    Assertions.assertEquals(
        "Periodo letivo da turma nao esta ativo para matricula.", excecao.getMessage());
    Assertions.assertEquals(1, turmaRepository.listarTurmas().size());
    Assertions.assertTrue(turmaRepository.listarTurmas().getFirst().getMatriculados().isEmpty());
  }

  @Test
  public void deveLancarcEntradaInvalidaExceptionEmCadastrarAlunoComTurmaNull() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.cadastrarAlunoEmTurma(null, aluno));
  }

  @Test
  public void deveLancarTurmaNaoEncontradaExceptionEmCadastrarAlunoComTurmaInexistente() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    Assertions.assertThrows(
        TurmaNaoEncontradaException.class, () -> service.cadastrarAlunoEmTurma("123ds", aluno));
  }

  @Test
  public void deveLancarNullPointerExceptionExceptionEmCadastrarAlunoComAlunoNull() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Assertions.assertThrows(
        NullPointerException.class, () -> service.cadastrarAlunoEmTurma(turma.getCodigo(), null));
  }

  @Test
  public void deveCadastrarAlunoCorretamenteNaListaDeEspera() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 1, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    Aluno aluno2 = criarAluno("Joao", "joao@email.com", "al01", "senha123");
    userRepository.salvarUsuario(aluno);

    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);
    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno2);

    Turma turmaAtualizada = service.buscarTurmaPorCodigo(turma.getCodigo());

    Assertions.assertEquals(1, turmaAtualizada.getListaEspera().size());
  }

  @Test
  public void deveImpedirAlunoEmDuasTurmasDaMesmaDisciplinaNoMesmoPeriodo() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    Professor professor2 = criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    userRepository.salvarUsuario(professor2);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma1 = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    Turma turma2 = new Turma("dis00", "2026.2", "pr01", 30, "TER 10:00-12:00", "LAB 02");
    service.ofertarTurma(turma1);
    service.ofertarTurma(turma2);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);
    service.cadastrarAlunoEmTurma(turma1.getCodigo(), aluno);

    AlunoNaoCumprePreRequisitosException excecao =
        Assertions.assertThrows(
            AlunoNaoCumprePreRequisitosException.class,
            () -> service.cadastrarAlunoEmTurma(turma2.getCodigo(), aluno));

    Assertions.assertEquals(
        "Aluno ja esta matriculado ou em lista de espera em outra turma "
            + "desta disciplina neste periodo.",
        excecao.getMessage());
  }

  @Test
  public void devePermitirAlunoEmDisciplinaDiferenteNoMesmoPeriodo() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));
    Professor professor2 = criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    userRepository.salvarUsuario(professor2);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma1 = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    Turma turma2 = new Turma("dis01", "2026.2", "pr01", 30, "TER 10:00-12:00", "LAB 02");
    service.ofertarTurma(turma1);
    service.ofertarTurma(turma2);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    service.cadastrarAlunoEmTurma(turma1.getCodigo(), aluno);
    service.cadastrarAlunoEmTurma(turma2.getCodigo(), aluno);

    Assertions.assertEquals(2, aluno.getTurmasMatriculadas().size());
  }

  @Test
  public void deveManterValidacaoDeMesmaTurma() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);
    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

    AlunoNaoCumprePreRequisitosException excecao =
        Assertions.assertThrows(
            AlunoNaoCumprePreRequisitosException.class,
            () -> service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno));

    Assertions.assertTrue(excecao.getMessage().contains("matriculado nessa turma"));
  }

  @Test
  public void deveManterValidacaoDeChoqueDeHorarioDoAluno() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));
    Professor professor2 = criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    userRepository.salvarUsuario(professor2);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma1 = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    Turma turma2 = new Turma("dis01", "2026.2", "pr01", 30, "SEG 08:00-10:00", "LAB 02");
    service.ofertarTurma(turma1);
    service.ofertarTurma(turma2);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);
    service.cadastrarAlunoEmTurma(turma1.getCodigo(), aluno);

    AlunoNaoCumprePreRequisitosException excecao =
        Assertions.assertThrows(
            AlunoNaoCumprePreRequisitosException.class,
            () -> service.cadastrarAlunoEmTurma(turma2.getCodigo(), aluno));

    Assertions.assertTrue(excecao.getMessage().contains("choque"));
  }

  @Test
  public void deveManterValidacaoDePreRequisito() {
    final TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of("dis00")));
    Professor professor2 = criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    userRepository.salvarUsuario(professor2);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis01", "2026.2", "pr01", 30, "TER 10:00-12:00", "LAB 02");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    AlunoNaoCumprePreRequisitosException excecao =
        Assertions.assertThrows(
            AlunoNaoCumprePreRequisitosException.class,
            () -> service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno));

    Assertions.assertTrue(excecao.getMessage().contains("requisitos"));
  }

  @Test
  public void deveImpedirAlunoEmListaDeEsperaDeEntrarEmOutraTurmaDaMesmaDisciplinaPeriodo() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    Professor professor2 = criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    userRepository.salvarUsuario(professor2);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma1 = new Turma("dis00", "2026.2", "pr00", 1, "SEG 08:00-10:00", "LAB 01");
    Turma turma2 = new Turma("dis00", "2026.2", "pr01", 30, "TER 10:00-12:00", "LAB 02");
    service.ofertarTurma(turma1);
    service.ofertarTurma(turma2);

    Aluno alunoMatriculado = criarAluno("Ana", "ana@email.com", "al00", "senha123");
    Aluno alunoListaEspera = criarAluno("Maria", "maria@email.com", "al01", "senha123");
    userRepository.salvarUsuario(alunoMatriculado);
    userRepository.salvarUsuario(alunoListaEspera);

    service.cadastrarAlunoEmTurma(turma1.getCodigo(), alunoMatriculado);
    service.cadastrarAlunoEmTurma(turma1.getCodigo(), alunoListaEspera);

    AlunoNaoCumprePreRequisitosException excecao =
        Assertions.assertThrows(
            AlunoNaoCumprePreRequisitosException.class,
            () -> service.cadastrarAlunoEmTurma(turma2.getCodigo(), alunoListaEspera));

    Assertions.assertEquals(
        "Aluno ja esta matriculado ou em lista de espera em outra turma "
            + "desta disciplina neste periodo.",
        excecao.getMessage());
  }

  @Test
  public void deveCancelarAlunoTurmaCorretamente() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

    service.cancelarAlunoTurma(turma.getCodigo(), aluno);

    Assertions.assertEquals(0, aluno.getTurmasMatriculadas().size());
    Assertions.assertEquals(0, turma.getMatriculados().size());
  }

  @Test
  public void deveLancarcEntradaInvalidaExceptionEmCancelarAlunoComTurmaNull() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.cancelarAlunoTurma(null, aluno));
  }

  @Test
  public void deveLancarcEntradaInvalidaExceptionEmCancelarAlunoComTurmaInexistente() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

    Assertions.assertThrows(
        TurmaNaoEncontradaException.class, () -> service.cancelarAlunoTurma("asda", aluno));
  }

  @Test
  public void deveRetornarFalseSeExistirAlunosMatriculados() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);

    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

    Assertions.assertTrue(service.existeAlunosMatriculados(turma));
  }

  @Test
  public void deveCriarTurmaServiceComConstrutorPadrao() {
    TurmaService service = new TurmaService();

    Assertions.assertNotNull(service);
  }

  @Test
  public void deveCriarTurmaServiceComConstrutorDeQuatroParametros() {
    TurmaService service =
        new TurmaService(
            criarTurmaRepository(),
            criarDisciplinaRepository(),
            criarPeriodoLetivoRepository(),
            criarUserRepository());

    Assertions.assertNotNull(service);
  }

  @Test
  public void deveOfertarTurmaParaCoordenadorComSucesso() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

    service.ofertarTurma(turma, CODIGO_CURSO);

    Assertions.assertEquals("tur00", turma.getCodigo());
    Assertions.assertEquals(1, turmaRepository.listarTurmas().size());
  }

  @Test
  public void deveLancarExcecaoAoOfertarTurmaComCodigoCursoCoordenadorVazio() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.ofertarTurma(turma, ""));
  }

  @Test
  public void deveLancarExcecaoAoOfertarTurmaCoordenadorComDisciplinaDeOutroCurso() {
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur01", List.of()));
    periodoLetivoRepository.salvarPeriodoLetivo(
        new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));
    userRepository.salvarUsuario(criarProfessor("João", "joao@email.com", "senha123", "pr00"));

    TurmaRepository turmaRepository = criarTurmaRepository();
    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.ofertarTurma(turma, CODIGO_CURSO));
  }

  @Test
  public void deveLancarExcecaoAoOfertarTurmaCoordenadorComProfessorDeOutroCurso() {
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, CODIGO_CURSO, List.of()));
    periodoLetivoRepository.salvarPeriodoLetivo(
        new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));
    Professor professorOutroCurso = new Professor("Ana", "ana@email.com", "senha123");
    professorOutroCurso.setMatricula("pr01");
    professorOutroCurso.setCodigoCurso("cur01");
    UserRepository userRepository = criarUserRepository();
    userRepository.salvarUsuario(professorOutroCurso);

    TurmaRepository turmaRepository = criarTurmaRepository();
    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr01", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.ofertarTurma(turma, CODIGO_CURSO));
  }

  @Test
  public void deveAlterarTurmaComoCoordenador() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Turma turmaAtualizada = new Turma("dis00", "2026.2", "pr00", 40, "TER 10:00-12:00", "LAB 02");
    service.alterarTurma(turma.getCodigo(), turmaAtualizada, CODIGO_CURSO);

    Turma turmaSalva = service.buscarTurmaPorCodigo(turma.getCodigo());
    Assertions.assertEquals(40, turmaSalva.getLimiteVagas());
    Assertions.assertEquals("TER 10:00-12:00", turmaSalva.getHorario());
  }

  @Test
  public void deveCancelarTurmaComoCoordenador() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    service.cancelarTurma(turma.getCodigo(), CODIGO_CURSO);

    Assertions.assertThrows(
        TurmaNaoEncontradaException.class, () -> service.buscarTurmaPorCodigo(turma.getCodigo()));
  }

  @Test
  public void deveBuscarTurmaPorCodigoProfessorComSucesso() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Turma turmaEncontrada = service.buscarTurmaPorCodigo(turma.getCodigo(), "pr00");

    Assertions.assertEquals(turma.getCodigo(), turmaEncontrada.getCodigo());
  }

  @Test
  public void deveLancarExcecaoAoBuscarTurmaDeOutroProfessor() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.buscarTurmaPorCodigo(turma.getCodigo(), "pr99"));
  }

  @Test
  public void deveListarTurmasPorCurso() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    List<Turma> turmas = service.listarTurmasPorCurso(CODIGO_CURSO);

    Assertions.assertEquals(1, turmas.size());
  }

  @Test
  public void deveLancarExcecaoAoListarTurmasPorCursoComCodigoVazio() {
    TurmaService service =
        criarService(
            criarTurmaRepository(),
            criarDisciplinaRepository(),
            criarPeriodoLetivoRepository(),
            criarUserRepository());

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.listarTurmasPorCurso(""));
  }

  @Test
  public void deveListarProfessoresPorCurso() {
    UserRepository userRepository = criarUserRepository();
    userRepository.salvarUsuario(criarProfessor("João", "joao@email.com", "senha123", "pr00"));

    TurmaService service =
        criarService(
            criarTurmaRepository(),
            criarDisciplinaRepository(),
            criarPeriodoLetivoRepository(),
            userRepository);

    List<Professor> professores = service.listarProfessoresPorCurso(CODIGO_CURSO);

    Assertions.assertEquals(1, professores.size());
    Assertions.assertEquals("pr00", professores.get(0).getMatricula());
  }

  @Test
  public void deveRetornarPeriodoLetivoAtivo() {
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    PeriodoLetivo periodoAtivo = new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026");
    periodoAtivo.setPeriodoAtivo(true);
    periodoLetivoRepository.salvarPeriodoLetivo(periodoAtivo);

    TurmaService service =
        criarService(
            criarTurmaRepository(),
            criarDisciplinaRepository(),
            periodoLetivoRepository,
            criarUserRepository());

    Assertions.assertEquals("2026.2", service.buscarPeriodoLetivoAtivo());
  }

  @Test
  public void deveRetornarNuloQuandoNaoHaPeriodoLetivoAtivo() {
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    periodoLetivoRepository.salvarPeriodoLetivo(
        new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));

    TurmaService service =
        criarService(
            criarTurmaRepository(),
            criarDisciplinaRepository(),
            periodoLetivoRepository,
            criarUserRepository());

    Assertions.assertNull(service.buscarPeriodoLetivoAtivo());
  }

  @Test
  public void deveBuscarNomeDaDisciplinaExistente() {
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, CODIGO_CURSO, List.of()));

    TurmaService service =
        criarService(
            criarTurmaRepository(),
            disciplinaRepository,
            criarPeriodoLetivoRepository(),
            criarUserRepository());

    Assertions.assertEquals("Algoritmos", service.buscarNomeDisciplina("dis00"));
  }

  @Test
  public void deveRetornarCodigoQuandoDisciplinaNaoExistir() {
    TurmaService service =
        criarService(
            criarTurmaRepository(),
            criarDisciplinaRepository(),
            criarPeriodoLetivoRepository(),
            criarUserRepository());

    Assertions.assertEquals("dis99", service.buscarNomeDisciplina("dis99"));
  }

  @Test
  public void deveBuscarNomeDoProfessorExistente() {
    UserRepository userRepository = criarUserRepository();
    userRepository.salvarUsuario(criarProfessor("João", "joao@email.com", "senha123", "pr00"));

    TurmaService service =
        criarService(
            criarTurmaRepository(),
            criarDisciplinaRepository(),
            criarPeriodoLetivoRepository(),
            userRepository);

    Assertions.assertEquals("João", service.buscarNomeProfessor("pr00"));
  }

  @Test
  public void deveRetornarMatriculaQuandoProfessorNaoExistir() {
    TurmaService service =
        criarService(
            criarTurmaRepository(),
            criarDisciplinaRepository(),
            criarPeriodoLetivoRepository(),
            criarUserRepository());

    Assertions.assertEquals("pr99", service.buscarNomeProfessor("pr99"));
  }

  @Test
  public void deveValidarTurmaPertenceAoCursoComSucesso() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Assertions.assertDoesNotThrow(
        () -> service.validarTurmaPertenceAoCurso(turma.getCodigo(), CODIGO_CURSO));
  }

  @Test
  public void deveLancarExcecaoQuandoTurmaNaoPertenceAoCurso() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.validarTurmaPertenceAoCurso(turma.getCodigo(), "cur01"));
  }

  @Test
  public void deveCadastrarNovaAulaEmTurma() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aula aula = new Aula("aul00", turma.getCodigo(), "17/07/2026", "SEG 08:00-10:00");
    service.cadastrarNovaAula(aula, turma.getCodigo());

    Turma turmaAtualizada = service.buscarTurmaPorCodigo(turma.getCodigo());
    Assertions.assertEquals(1, turmaAtualizada.getAulas().size());
    Assertions.assertEquals("aul00", turmaAtualizada.getAulas().get(0));
  }

  @Test
  public void deveCadastrarNovaAulaValidandoProfessorResponsavel() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aula aula = new Aula("aul00", turma.getCodigo(), "17/07/2026", "SEG 08:00-10:00");
    service.cadastrarNovaAula(aula, turma.getCodigo(), "pr00");

    Turma turmaAtualizada = service.buscarTurmaPorCodigo(turma.getCodigo());
    Assertions.assertEquals(1, turmaAtualizada.getAulas().size());
  }

  @Test
  public void deveLancarExcecaoAoCadastrarAulaComProfessorDeOutraTurma() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aula aula = new Aula("aul00", turma.getCodigo(), "17/07/2026", "SEG 08:00-10:00");

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.cadastrarNovaAula(aula, turma.getCodigo(), "pr99"));
  }

  @Test
  public void deveAtualizarFrequenciaDaTurma() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    BoletimRepository boletimRepository = criarBoletimRepository();
    AulaRepository aulaRepository = criarAulaRepository();

    TurmaService service =
        new TurmaService(
            turmaRepository,
            disciplinaRepository,
            periodoLetivoRepository,
            userRepository,
            boletimRepository,
            aulaRepository);

    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);
    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("al00", true);
    Aula aula = new Aula("aul00", turma.getCodigo(), "17/07/2026", "SEG 08:00-10:00", presencas);
    aulaRepository.salvarAula(aula);
    service.cadastrarNovaAula(aula, turma.getCodigo());

    service.atualizarFrequenciaTurma(turma.getCodigo());

    Boletim boletim = boletimRepository.buscarBoletinsPorTurma(turma.getCodigo()).get(0);
    Assertions.assertEquals(100.0, boletim.getFrequencia());
  }

  @Test
  public void deveAtualizarFrequenciaDaTurmaValidandoProfessor() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
    BoletimRepository boletimRepository = criarBoletimRepository();
    AulaRepository aulaRepository = criarAulaRepository();

    TurmaService service =
        new TurmaService(
            turmaRepository,
            disciplinaRepository,
            periodoLetivoRepository,
            userRepository,
            boletimRepository,
            aulaRepository);

    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);
    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("al00", true);
    Aula aula = new Aula("aul00", turma.getCodigo(), "17/07/2026", "SEG 08:00-10:00", presencas);
    aulaRepository.salvarAula(aula);
    service.cadastrarNovaAula(aula, turma.getCodigo());

    service.atualizarFrequenciaTurma(turma.getCodigo(), "pr00");

    Boletim boletim = boletimRepository.buscarBoletinsPorTurma(turma.getCodigo()).get(0);
    Assertions.assertEquals(100.0, boletim.getFrequencia());
  }

  @Test
  public void deveLancarExcecaoAoAtualizarFrequenciaComProfessorErrado() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.atualizarFrequenciaTurma(turma.getCodigo(), "pr99"));
  }

  @Test
  public void devePromoverAlunoDaListaDeEsperaAoCancelarMatricula() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    BoletimRepository boletimRepository = criarBoletimRepository();
    AulaRepository aulaRepository = criarAulaRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        new TurmaService(
            turmaRepository,
            disciplinaRepository,
            periodoLetivoRepository,
            userRepository,
            boletimRepository,
            aulaRepository);

    Turma turma = new Turma("dis00", "2026.2", "pr00", 1, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno alunoMatriculado = criarAluno("Ana", "ana@email.com", "al00", "senha123");
    Aluno alunoListaEspera = criarAluno("Maria", "maria@email.com", "al01", "senha123");
    userRepository.salvarUsuario(alunoMatriculado);
    userRepository.salvarUsuario(alunoListaEspera);

    service.cadastrarAlunoEmTurma(turma.getCodigo(), alunoMatriculado);
    service.cadastrarAlunoEmTurma(turma.getCodigo(), alunoListaEspera);

    String mensagem = service.cancelarAlunoTurma(turma.getCodigo(), alunoMatriculado);

    Turma turmaAtualizada = service.buscarTurmaPorCodigo(turma.getCodigo());
    Assertions.assertTrue(turmaAtualizada.getMatriculados().contains("al01"));
    Assertions.assertTrue(turmaAtualizada.getListaEspera().isEmpty());
    Assertions.assertTrue(mensagem.contains("promovido"));

    List<Boletim> boletinsDoPromovido = boletimRepository.buscarBoletinsPorAluno("al01");
    Assertions.assertEquals(1, boletinsDoPromovido.size());
    Assertions.assertEquals(turma.getCodigo(), boletinsDoPromovido.getFirst().getCodigoTurma());
  }

  @Test
  public void deveRetornarTrueEmExisteAlunosMatriculadosQuandoTurmaEstiverVazia() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Turma turmaSemAlunos = service.buscarTurmaPorCodigo(turma.getCodigo());

    Assertions.assertTrue(service.existeAlunosMatriculados(turmaSemAlunos));
  }

  @Test
  public void deveRetornarFalseEmExisteAlunosMatriculadosQuandoTurmaTiverAlunos() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    userRepository.salvarUsuario(aluno);
    service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

    Turma turmaComAluno = service.buscarTurmaPorCodigo(turma.getCodigo());

    Assertions.assertFalse(service.existeAlunosMatriculados(turmaComAluno));
  }

  @Test
  public void deveValidarDiretamenteQueTurmaPertenceAoProfessor() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Assertions.assertDoesNotThrow(
        () -> service.validarTurmaPertenceAoProfessor(turma.getCodigo(), "pr00"));
  }

  @Test
  public void deveLancarExcecaoAoValidarDiretamenteTurmaDeOutroProfessor() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.validarTurmaPertenceAoProfessor(turma.getCodigo(), "pr99"));
  }

  @Test
  public void deveImpedirMatriculaDeAlunoSemCursoVinculado() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno alunoSemCurso = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    alunoSemCurso.setCodigoCurso(null);
    userRepository.salvarUsuario(alunoSemCurso);

    EntradaInvalidaException excecao =
        Assertions.assertThrows(
            EntradaInvalidaException.class,
            () -> service.cadastrarAlunoEmTurma(turma.getCodigo(), alunoSemCurso));

    Assertions.assertTrue(excecao.getMessage().contains("nao esta vinculado a nenhum curso"));
  }

  @Test
  public void deveImpedirMatriculaDeAlunoJaAprovadoNaDisciplina() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

    TurmaService service =
        criarService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
    Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    service.ofertarTurma(turma);

    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    aluno.getDisciplinasConcluidas().add("dis00");
    userRepository.salvarUsuario(aluno);

    EntradaInvalidaException excecao =
        Assertions.assertThrows(
            EntradaInvalidaException.class,
            () -> service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno));

    Assertions.assertTrue(excecao.getMessage().contains("já foi aprovado"));
  }
}
