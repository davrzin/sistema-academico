package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import br.com.classroompb.model.services.BoletimService;
import br.com.classroompb.model.services.CursoService;
import br.com.classroompb.model.services.TurmaService;
import br.com.classroompb.model.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes da tela de usuarios.
 */
public class UsuarioTelaTest {

  @TempDir Path tempDir;

  private CursoRepository cursoRepository;
  private UserRepository userRepository;
  private BoletimRepository boletimRepository;
  private TurmaRepository turmaRepository;
  private CursoService cursoService;
  private UsuarioService usuarioService;
  private BoletimService boletimService;
  private TurmaService turmaService;

  /**
   * Prepara repositories e services isolados.
   */
  @BeforeEach
  public void preparar() {
    ObjectMapper mapper = new ObjectMapper();
    cursoRepository =
        new CursoRepository(mapper, tempDir.resolve("cursos").toString());
    userRepository =
        new UserRepository(mapper, tempDir.resolve("usuarios").toString());
    boletimRepository =
        new BoletimRepository(mapper, tempDir.resolve("boletins").toString());
    turmaRepository = new TurmaRepository(mapper, tempDir.resolve("turmas").toString());
    cursoService = new CursoService(cursoRepository, userRepository);
    usuarioService = new UsuarioService(userRepository, cursoRepository);
    boletimService = new BoletimService(boletimRepository, turmaRepository);
    turmaService =
        new TurmaService(
            turmaRepository,
            new DisciplinaRepository(mapper, tempDir.resolve("disciplinas").toString()),
            new PeriodoLetivoRepository(mapper, tempDir.resolve("periodos").toString()),
            userRepository,
            boletimRepository,
            new AulaRepository(mapper, tempDir.resolve("aulas").toString()));
  }

  @Test
  public void deveCadastrarCoordenadorSemCursoQuandoNaoExistiremCursos() {
    cadastrarCoordenador("Coordenadora", "coord@email.com", "1");

    Coordenador coordenador = primeiroCoordenador();
    Assertions.assertNull(coordenador.getCodigoCurso());
  }

  @Test
  public void deveCadastrarCoordenadorSemCursoQuandoExistirCursoLivre() {
    cursoService.cadastrarCurso(new Curso("Computacao", 8, 3200));

    cadastrarCoordenador("Coordenadora", "coord@email.com", "2");

    Assertions.assertNull(primeiroCoordenador().getCodigoCurso());
  }

  @Test
  public void deveCadastrarCoordenadorSelecionandoCursoLivre() {
    Curso curso = new Curso("Computacao", 8, 3200);
    cursoService.cadastrarCurso(curso);

    cadastrarCoordenador("Coordenadora", "coord@email.com", "1");

    Assertions.assertEquals(curso.getCodigo(), primeiroCoordenador().getCodigoCurso());
  }

  @Test
  public void naoDeveListarCursoQueJaPossuiCoordenador() {
    Curso ocupado = new Curso("Computacao", 8, 3200);
    Curso livre = new Curso("Sistemas", 8, 3000);
    cursoService.cadastrarCurso(ocupado);
    cursoService.cadastrarCurso(livre);
    salvarCoordenadorVinculado(ocupado.getCodigo());

    String saida = cadastrarCoordenador("Livre", "livre@email.com", "2");

    Assertions.assertFalse(saida.contains("1 - Computacao"));
    Assertions.assertTrue(saida.contains("1 - Sistemas"));
    Assertions.assertNull(
        ((Coordenador) userRepository.buscarPorMatricula("co01")).getCodigoCurso());
  }

  @Test
  public void devePermitirCoordenadorSemCursoQuandoTodosEstiveremOcupados() {
    Curso curso = new Curso("Computacao", 8, 3200);
    cursoService.cadastrarCurso(curso);
    salvarCoordenadorVinculado(curso.getCodigo());

    String saida = cadastrarCoordenador("Livre", "livre@email.com", "1");

    Assertions.assertTrue(saida.contains("Nenhum curso disponivel para vinculacao."));
    Assertions.assertNull(
        ((Coordenador) userRepository.buscarPorMatricula("co01")).getCodigoCurso());
  }

  @Test
  public void opcaoZeroDeveCancelarSemCadastrarCoordenador() {
    cursoService.cadastrarCurso(new Curso("Computacao", 8, 3200));

    cadastrarCoordenador("Coordenadora", "coord@email.com", "0");

    Assertions.assertTrue(userRepository.listar(TipoUsuario.COORDENADOR).isEmpty());
  }

  @Test
  public void deveExibirPendenciasComZeroSemTruncarSituacao() {
    Boletim boletim = new Boletim("al00", "tur00");
    boletim.setIdBoletim("bol00");
    boletim.setPrimeiraNota(0.0f);
    boletimRepository.salvarBoletim(boletim);
    turmaRepository.salvarTurma(
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
    String texto = exibirBoletim("al00");
    Assertions.assertTrue(texto.contains("0,0"));
    Assertions.assertTrue(texto.contains("Em andamento"));
    Assertions.assertTrue(texto.contains("-- = nota ainda nao lancada"));
    Assertions.assertTrue(texto.contains("Frequencia -- = ainda nao calculada"));
    Assertions.assertFalse(texto.contains("Em andamen..."));
  }

  @Test
  public void deveExibirMediaComFrequenciaPendenteEmAndamento() {
    salvarTurmaComBoletim("tur00", "al00", 8.0f, 6.0f, null);

    String texto = exibirBoletim("al00");

    Assertions.assertTrue(texto.contains("7,00"));
    Assertions.assertTrue(texto.contains("Em andamento"));
    Assertions.assertTrue(texto.contains("Frequencia -- = ainda nao calculada"));
  }

  @Test
  public void deveExibirValoresMaximosSemTruncarSituacao() {
    salvarTurmaComBoletim("tur00", "al00", 10.0f, 10.0f, 100.0);
    salvarTurmaComBoletim("tur01", "al00", 10.0f, 10.0f, 0.0);

    String texto = exibirBoletim("al00");

    Assertions.assertTrue(texto.contains("10,0"));
    Assertions.assertTrue(texto.contains("10,00"));
    Assertions.assertTrue(texto.contains("100,0%"));
    Assertions.assertTrue(texto.contains("Reprovado por falta"));
    Assertions.assertFalse(texto.contains("Reprovado por fal..."));
  }

  private String cadastrarCoordenador(String nome, String email, String opcaoCurso) {
    String entrada = String.join(System.lineSeparator(), nome, email, "senha123", "2", opcaoCurso);
    UsuarioTela tela =
        criarTela(new Scanner(entrada));
    ByteArrayOutputStream saida = new ByteArrayOutputStream();
    PrintStream original = System.out;
    try {
      System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
      tela.cadastrarUsuario();
    } finally {
      System.setOut(original);
    }
    return saida.toString(StandardCharsets.UTF_8);
  }

  private UsuarioTela criarTela(Scanner scanner) {
    return new UsuarioTela(
        scanner, usuarioService, cursoService, boletimService, turmaService);
  }

  private void salvarTurmaComBoletim(
      String codigoTurma,
      String matriculaAluno,
      Float primeiraNota,
      Float segundaNota,
      Double frequencia) {
    turmaRepository.salvarTurma(
        new Turma(codigoTurma, "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
    Boletim boletim = new Boletim(matriculaAluno, codigoTurma);
    boletim.setIdBoletim("bol" + codigoTurma);
    boletim.setPrimeiraNota(primeiraNota);
    boletim.setSegundaNota(segundaNota);
    boletim.setFrequencia(frequencia);
    boletimRepository.salvarBoletim(boletim);
  }

  private String exibirBoletim(String matriculaAluno) {
    Aluno aluno = new Aluno("Aluno", "aluno@email.com", "senha", "cur00");
    aluno.setMatricula(matriculaAluno);
    ByteArrayOutputStream saida = new ByteArrayOutputStream();
    PrintStream original = System.out;
    try {
      System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
      criarTela(new Scanner("")).exibirBoletinAluno(aluno);
    } finally {
      System.setOut(original);
    }
    return saida.toString(StandardCharsets.UTF_8);
  }

  private void salvarCoordenadorVinculado(String codigoCurso) {
    Coordenador coordenador = new Coordenador("Ocupada", "ocupada@email.com", "senha123");
    coordenador.setCodigoCurso(codigoCurso);
    usuarioService.cadastrarUsuario(coordenador);
  }

  private Coordenador primeiroCoordenador() {
    return (Coordenador) userRepository.listar(TipoUsuario.COORDENADOR).getFirst();
  }
}
