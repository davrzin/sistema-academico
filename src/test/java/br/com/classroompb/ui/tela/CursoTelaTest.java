package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.UserRepository;
import br.com.classroompb.model.services.CursoService;
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

/** Testes da tela de cursos. */
public class CursoTelaTest {

  @TempDir Path tempDir;

  private CursoRepository cursoRepository;
  private UserRepository userRepository;
  private CursoService cursoService;

  /** Prepara repositorios isolados. */
  @BeforeEach
  public void preparar() {
    cursoRepository =
        new CursoRepository(new ObjectMapper(), tempDir.resolve("cursos").toString());
    userRepository =
        new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
    cursoService = new CursoService(cursoRepository, userRepository);
  }

  @Test
  public void deveExibirCursoSemCoordenadorComoNaoDefinido() {
    cursoService.cadastrarCurso(new Curso("Computacao", 8, 3200));
    CursoTela tela = new CursoTela(new Scanner(""), cursoService);
    ByteArrayOutputStream saida = new ByteArrayOutputStream();
    PrintStream saidaOriginal = System.out;

    try {
      System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
      tela.listarCursos();
    } finally {
      System.setOut(saidaOriginal);
    }

    String texto = saida.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(texto.contains("Coordenador:"));
    Assertions.assertTrue(texto.contains("definido"));
  }

  @Test
  public void deveListarDisponiveisSelecionandoCoordenadorPorNumero() {
    salvarCoordenador("co00", "Joao", "joao@sistema.com", null);
    salvarCoordenador("co01", "Maria", "maria@sistema.com", null);

    String saida = cadastrarCurso("Computacao\n8\n3200\n2\n");

    Assertions.assertTrue(saida.contains("1 - Joao"));
    Assertions.assertTrue(saida.contains("2 - Maria"));
    Assertions.assertTrue(saida.contains("Matricula: co01"));
    Assertions.assertFalse(saida.contains("Digite a matricula"));
    Coordenador maria = (Coordenador) userRepository.buscarPorMatricula("co01");
    Assertions.assertEquals("cur00", maria.getCodigoCurso());
  }

  @Test
  public void naoDeveListarCoordenadorJaVinculado() {
    Curso existente = new Curso("Computacao", 8, 3200);
    cursoService.cadastrarCurso(existente);
    salvarCoordenador("co00", "Ocupado", "ocupado@sistema.com", existente.getCodigo());
    salvarCoordenador("co01", "Livre", "livre@sistema.com", null);

    String saida = cadastrarCurso("Sistemas\n8\n3000\n1\n");

    Assertions.assertFalse(saida.contains("1 - Ocupado"));
    Assertions.assertTrue(saida.contains("1 - Livre"));
  }

  @Test
  public void deveCadastrarSemCoordenadorPelaOpcaoDinamica() {
    salvarCoordenador("co00", "Joao", "joao@sistema.com", null);

    String saida = cadastrarCurso("Computacao\n8\n3200\n2\n");

    Assertions.assertTrue(saida.contains("2 - Cadastrar curso sem coordenador"));
    Assertions.assertEquals(1, cursoRepository.listarCursos().size());
    Assertions.assertNull(
        ((Coordenador) userRepository.buscarPorMatricula("co00")).getCodigoCurso());
  }

  @Test
  public void opcaoZeroDeveCancelarSemPersistirCurso() {
    salvarCoordenador("co00", "Joao", "joao@sistema.com", null);

    String saida = cadastrarCurso("Computacao\n8\n3200\n0\n");

    Assertions.assertTrue(saida.contains("Cadastro de curso cancelado."));
    Assertions.assertTrue(cursoRepository.listarCursos().isEmpty());
  }

  @Test
  public void devePermitirCursoSemCoordenadorQuandoNaoHouverDisponivel() {
    String saida = cadastrarCurso("Computacao\n8\n3200\n1\n");

    Assertions.assertTrue(saida.contains("Nenhum coordenador disponivel para vinculacao."));
    Assertions.assertTrue(saida.contains("1 - Cadastrar curso sem coordenador"));
    Assertions.assertEquals(1, cursoRepository.listarCursos().size());
  }

  private void salvarCoordenador(
      String matricula, String nome, String email, String codigoCurso) {
    Coordenador coordenador = new Coordenador(nome, email, "senha123");
    coordenador.setMatricula(matricula);
    coordenador.setCodigoCurso(codigoCurso);
    userRepository.salvarUsuario(coordenador);
  }

  private String cadastrarCurso(String entrada) {
    CursoTela tela = new CursoTela(new Scanner(entrada), cursoService);
    ByteArrayOutputStream saida = new ByteArrayOutputStream();
    PrintStream saidaOriginal = System.out;
    try {
      System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
      tela.cadastrarCurso();
    } finally {
      System.setOut(saidaOriginal);
    }
    return saida.toString(StandardCharsets.UTF_8);
  }
}
