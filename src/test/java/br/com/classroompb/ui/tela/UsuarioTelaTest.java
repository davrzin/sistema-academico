package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.UserRepository;
import br.com.classroompb.model.services.CursoService;
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
  private CursoService cursoService;
  private UsuarioService usuarioService;

  /**
   * Prepara repositories e services isolados.
   */
  @BeforeEach
  public void preparar() {
    cursoRepository =
        new CursoRepository(new ObjectMapper(), tempDir.resolve("cursos").toString());
    userRepository =
        new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
    cursoService = new CursoService(cursoRepository, userRepository);
    usuarioService = new UsuarioService(userRepository, cursoRepository);
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

  private String cadastrarCoordenador(String nome, String email, String opcaoCurso) {
    String entrada = String.join(System.lineSeparator(), nome, email, "senha123", "2", opcaoCurso);
    UsuarioTela tela =
        new UsuarioTela(new Scanner(entrada), usuarioService, cursoService);
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

  private void salvarCoordenadorVinculado(String codigoCurso) {
    Coordenador coordenador = new Coordenador("Ocupada", "ocupada@email.com", "senha123");
    coordenador.setCodigoCurso(codigoCurso);
    usuarioService.cadastrarUsuario(coordenador);
  }

  private Coordenador primeiroCoordenador() {
    return (Coordenador) userRepository.listar(TipoUsuario.COORDENADOR).getFirst();
  }
}
