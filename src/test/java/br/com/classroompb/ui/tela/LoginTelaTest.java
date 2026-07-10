package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.UserRepository;
import br.com.classroompb.model.services.UsuarioService;
import br.com.classroompb.ui.menu.MenuPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Testes da navegacao com um unico leitor de entrada. */
public class LoginTelaTest {

  @TempDir java.nio.file.Path tempDir;

  @Test
  public void menuPrincipalDeveManterScannerInjetado() throws ReflectiveOperationException {
    Scanner scanner = new Scanner("0\n");
    MenuPrincipal menu = new MenuPrincipal(scanner);
    Field campoScanner = MenuPrincipal.class.getDeclaredField("scanner");
    campoScanner.setAccessible(true);

    Assertions.assertSame(scanner, campoScanner.get(menu));
    menu.iniciar();
  }

  @Test
  public void deveRedirecionarAdministradorComMesmoFluxo() {
    verificarRedirecionamento(
        new Administrador("Admin", "admin@teste.com", "ad99", "senha123"),
        "MENU ADMINISTRADOR");
  }

  @Test
  public void deveRedirecionarCoordenadorComMesmoFluxo() {
    verificarRedirecionamento(
        new Coordenador("Coord", "coord@teste.com", "co99", "senha123"), "MENU COORDENADOR");
  }

  @Test
  public void deveRedirecionarProfessorComMesmoFluxo() {
    verificarRedirecionamento(
        new Professor("Prof", "prof@teste.com", "pr99", "senha123"), "MENU PROFESSOR");
  }

  @Test
  public void deveRedirecionarAlunoComMesmoFluxo() {
    verificarRedirecionamento(
        new Aluno("Aluno", "aluno@teste.com", "al99", "senha123"), "MENU ALUNO");
  }

  private void verificarRedirecionamento(Usuario usuario, String tituloMenu) {
    ObjectMapper mapper = new ObjectMapper();
    UserRepository userRepository =
        new UserRepository(mapper, tempDir.resolve("usuarios").toString());
    CursoRepository cursoRepository =
        new CursoRepository(mapper, tempDir.resolve("cursos").toString());
    UsuarioService usuarioService = new UsuarioService(userRepository, cursoRepository);
    userRepository.salvarUsuario(usuario);

    String entrada = usuario.getEmail() + "\n" + usuario.getSenha() + "\n0\n";
    Scanner scanner = new Scanner(entrada);
    LoginTela loginTela = new LoginTela(scanner, usuarioService);
    ByteArrayOutputStream saida = new ByteArrayOutputStream();
    PrintStream saidaOriginal = System.out;

    try {
      System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
      Assertions.assertDoesNotThrow(loginTela::login);
    } finally {
      System.setOut(saidaOriginal);
    }

    String texto = saida.toString(StandardCharsets.UTF_8);
    Assertions.assertTrue(texto.contains(tituloMenu));
    Assertions.assertTrue(texto.contains("Voltando"));
  }
}
