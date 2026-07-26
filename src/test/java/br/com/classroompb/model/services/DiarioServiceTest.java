package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.DiarioNaoEncontradoException;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.TurmaNaoEncontradaException;
import br.com.classroompb.model.repository.DiarioRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do servico de diarios.
 */
public class DiarioServiceTest {

  private static final String CODIGO_CURSO = "cur00";
  private static final String OUTRO_CODIGO_CURSO = "cur01";

  @TempDir Path tempDir;

  /**
   * Limpa os arquivos gerados pelos testes.
   */
  @AfterEach
  public void tearDown() {
    apagarDiretorio("diarios");
    apagarDiretorio("turmas");
    apagarDiretorio("disciplinas");
    apagarDiretorio("usuarios");
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

  private DiarioRepository criarDiarioRepository() {
    return new DiarioRepository(new ObjectMapper(), tempDir.resolve("diarios").toString());
  }

  private TurmaRepository criarTurmaRepository() {
    return new TurmaRepository(new ObjectMapper(), tempDir.resolve("turmas").toString());
  }

  private DisciplinaRepository criarDisciplinaRepository() {
    return new DisciplinaRepository(new ObjectMapper(), tempDir.resolve("disciplinas").toString());
  }

  private UserRepository criarUserRepository() {
    return new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
  }

  private DiarioService criarService(
      DiarioRepository diarioRepository,
      TurmaRepository turmaRepository,
      DisciplinaRepository disciplinaRepository,
      UserRepository userRepository) {
    return new DiarioService(
        diarioRepository, turmaRepository, disciplinaRepository, userRepository);
  }

  private Professor criarProfessor(String matricula, String codigoCurso) {
    Professor professor = new Professor("João", matricula + "@email.com", "senha123");
    professor.setMatricula(matricula);
    professor.setCodigoCurso(codigoCurso);
    return professor;
  }

  private Diario criarDiario(String codigoTurma, String matriculaProfessor) {
    return new Diario(
        codigoTurma,
        "Diário de aulas teóricas",
        matriculaProfessor,
        "SEG 08:00-10:00",
        "LAB 01",
        60);
  }

  private void prepararDadosBasicos(
      DisciplinaRepository disciplinaRepository,
      TurmaRepository turmaRepository,
      UserRepository userRepository) {
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, CODIGO_CURSO, List.of()));

    Professor professor = criarProfessor("pr00", CODIGO_CURSO);
    userRepository.salvarUsuario(professor);

    turmaRepository.salvarTurma(
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
  }

  @Test
  public void deveCadastrarDiarioComTurmaProfessorValidos() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "pr00");

    service.cadastrarDiario(diario);

    Assertions.assertEquals("dia00", diario.getCodigo());
    Assertions.assertEquals(SituacaoDiario.ATIVO, diario.getSituacao());
    Assertions.assertEquals(1, diarioRepository.listarDiarios().size());
  }

  @Test
  public void deveCadastrarMaisDeUmDiarioParaMesmaTurma() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);

    service.cadastrarDiario(criarDiario("tur00", "pr00"));
    service.cadastrarDiario(criarDiario("tur00", "pr00"));

    List<Diario> diariosDaTurma = service.listarDiariosPorTurma("tur00");

    Assertions.assertEquals(2, diariosDaTurma.size());
  }

  @Test
  public void deveCadastrarDiarioComCoordenadorQuandoTurmaProfessorPertencemAoCurso() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "pr00");

    service.cadastrarDiario(diario, CODIGO_CURSO);

    Assertions.assertEquals("dia00", diario.getCodigo());
    Assertions.assertEquals(1, diarioRepository.listarDiarios().size());
  }

  @Test
  public void deveLancarExcecaoAoCadastrarDiarioComCoordenadorQuandoTurmaNaoPertenceAoCurso() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "pr00");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.cadastrarDiario(diario, OUTRO_CODIGO_CURSO));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCadastrarDiarioNulo() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarDiario(null));
  }

  @Test
  public void deveLancarTurmaNaoEncontradaExceptionQuandoTurmaNaoExistir() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();

    Professor professor = criarProfessor("pr00", CODIGO_CURSO);
    userRepository.salvarUsuario(professor);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur99", "pr00");

    Assertions.assertThrows(
        TurmaNaoEncontradaException.class, () -> service.cadastrarDiario(diario));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoProfessorNaoExistir() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();

    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, CODIGO_CURSO, List.of()));
    turmaRepository.salvarTurma(
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "pr99");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarDiario(diario));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoMatriculaForDeAluno() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();

    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, CODIGO_CURSO, List.of()));
    turmaRepository.salvarTurma(
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

    Aluno aluno = new Aluno("Maria", "maria@email.com", "al00", "senha123", CODIGO_CURSO);
    userRepository.salvarUsuario(aluno);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "al00");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarDiario(diario));
  }

  @Test
  public void deveAlterarDiarioCadastrado() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "pr00");
    service.cadastrarDiario(diario);

    Diario diarioAtualizado =
        new Diario("tur00", "Diário atualizado", "pr00", "TER 10:00-12:00", "LAB 02", 80);
    service.alterarDiario(diario.getCodigo(), diarioAtualizado);

    Diario diarioEncontrado = service.buscarDiarioPorCodigo(diario.getCodigo());

    Assertions.assertEquals("Diário atualizado", diarioEncontrado.getDescricao());
    Assertions.assertEquals("LAB 02", diarioEncontrado.getSala());
    Assertions.assertEquals(80, diarioEncontrado.getCargaHoraria());
  }

  @Test
  public void deveLancarDiarioNaoEncontradoExceptionAoAlterarDiarioInexistente() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diarioAtualizado = criarDiario("tur00", "pr00");

    Assertions.assertThrows(
        DiarioNaoEncontradoException.class,
        () -> service.alterarDiario("dia99", diarioAtualizado));
  }

  @Test
  public void deveEncerrarDiarioCadastrado() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "pr00");
    service.cadastrarDiario(diario, CODIGO_CURSO);

    service.encerrarDiario(diario.getCodigo(), CODIGO_CURSO);

    Diario diarioEncontrado = service.buscarDiarioPorCodigo(diario.getCodigo());

    Assertions.assertEquals(SituacaoDiario.ENCERRADO, diarioEncontrado.getSituacao());
  }

  @Test
  public void deveCancelarDiarioCadastrado() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "pr00");
    service.cadastrarDiario(diario, CODIGO_CURSO);

    service.cancelarDiario(diario.getCodigo(), CODIGO_CURSO);

    Diario diarioEncontrado = service.buscarDiarioPorCodigo(diario.getCodigo());

    Assertions.assertEquals(SituacaoDiario.CANCELADO, diarioEncontrado.getSituacao());
  }

  @Test
  public void deveLancarExcecaoAoAlterarSituacaoDeDiarioDeOutroCurso() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "pr00");
    service.cadastrarDiario(diario, CODIGO_CURSO);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.cancelarDiario(diario.getCodigo(), OUTRO_CODIGO_CURSO));
  }

  @Test
  public void deveBuscarDiarioPorCodigo() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    Diario diario = criarDiario("tur00", "pr00");
    service.cadastrarDiario(diario);

    Diario diarioEncontrado = service.buscarDiarioPorCodigo(diario.getCodigo());

    Assertions.assertNotNull(diarioEncontrado);
    Assertions.assertEquals("tur00", diarioEncontrado.getCodigoTurma());
  }

  @Test
  public void deveLancarDiarioNaoEncontradoExceptionQuandoCodigoNaoExistir() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);

    Assertions.assertThrows(
        DiarioNaoEncontradoException.class, () -> service.buscarDiarioPorCodigo("dia99"));
  }

  @Test
  public void deveListarDiariosPorCurso() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, OUTRO_CODIGO_CURSO, List.of()));
    turmaRepository.salvarTurma(
        new Turma("tur01", "dis01", "2026.2", "pr00", 30, "TER 08:00-10:00", "LAB 02"));

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    service.cadastrarDiario(criarDiario("tur00", "pr00"));
    service.cadastrarDiario(criarDiario("tur01", "pr00"));

    List<Diario> diariosDoCurso = service.listarDiariosPorCurso(CODIGO_CURSO);

    Assertions.assertEquals(1, diariosDoCurso.size());
    Assertions.assertEquals("tur00", diariosDoCurso.get(0).getCodigoTurma());
  }

  @Test
  public void deveListarDiariosPorProfessor() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    UserRepository userRepository = criarUserRepository();
    prepararDadosBasicos(disciplinaRepository, turmaRepository, userRepository);

    DiarioRepository diarioRepository = criarDiarioRepository();
    DiarioService service =
        criarService(diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    service.cadastrarDiario(criarDiario("tur00", "pr00"));

    List<Diario> diariosDoProfessor = service.listarDiariosPorProfessor("pr00");

    Assertions.assertEquals(1, diariosDoProfessor.size());
  }
}
