package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do vinculo entre curso e coordenador.
 */
public class VinculoCursoCoordenadorServiceTest {

  @TempDir Path tempDir;

  private CursoRepository cursoRepository;
  private UserRepository userRepository;
  private VinculoCursoCoordenadorService service;

  /**
   * Prepara repositorios isolados para cada teste.
   */
  @BeforeEach
  public void preparar() {
    cursoRepository =
        new CursoRepository(new ObjectMapper(), tempDir.resolve("cursos").toString());
    userRepository =
        new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
    service = new VinculoCursoCoordenadorService(cursoRepository, userRepository);
  }

  @Test
  public void deveVincularAlterandoSomenteCoordenador() {
    salvarCurso("cur00", "Computacao");
    salvarCoordenador("co00", "coord@email.com");

    service.vincular("co00", "cur00");

    Assertions.assertEquals("cur00", buscarCoordenador("co00").getCodigoCurso());
    Assertions.assertNotNull(cursoRepository.buscarPorCodigo("cur00"));
  }

  @Test
  public void deveBuscarCoordenadorResponsavelPeloCurso() {
    salvarCurso("cur00", "Computacao");
    salvarCoordenador("co00", "coord@email.com");
    service.vincular("co00", "cur00");

    Coordenador encontrado = service.buscarCoordenadorPorCurso("cur00");

    Assertions.assertEquals("co00", encontrado.getMatricula());
  }

  @Test
  public void deveRetornarNullQuandoCursoNaoPossuirCoordenador() {
    salvarCurso("cur00", "Computacao");

    Assertions.assertNull(service.buscarCoordenadorPorCurso("cur00"));
  }

  @Test
  public void deveListarSomenteCoordenadoresSemCurso() {
    salvarCurso("cur00", "Computacao");
    salvarCoordenador("co00", "coord1@email.com");
    salvarCoordenador("co01", "coord2@email.com");
    service.vincular("co00", "cur00");

    Assertions.assertEquals(1, service.listarCoordenadoresSemCurso().size());
    Assertions.assertEquals(
        "co01", service.listarCoordenadoresSemCurso().getFirst().getMatricula());
  }

  @Test
  public void deveImpedirSegundoCoordenadorNoMesmoCurso() {
    salvarCurso("cur00", "Computacao");
    salvarCoordenador("co00", "coord1@email.com");
    salvarCoordenador("co01", "coord2@email.com");
    service.vincular("co00", "cur00");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.vincular("co01", "cur00"));
  }

  @Test
  public void deveImpedirCoordenadorEmDoisCursos() {
    salvarCurso("cur00", "Computacao");
    salvarCurso("cur01", "Medicina");
    salvarCoordenador("co00", "coord@email.com");
    service.vincular("co00", "cur00");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.vincular("co00", "cur01"));
  }

  @Test
  public void deveDesvincularLimpandoCodigoCursoDoCoordenador() {
    salvarCurso("cur00", "Computacao");
    salvarCoordenador("co00", "coord@email.com");
    service.vincular("co00", "cur00");

    service.desvincular("co00");

    Assertions.assertNull(buscarCoordenador("co00").getCodigoCurso());
  }

  @Test
  public void deveDetectarMaisDeUmCoordenadorNoMesmoCurso() {
    salvarCurso("cur00", "Computacao");
    Coordenador primeiro = salvarCoordenador("co00", "coord1@email.com");
    Coordenador segundo = salvarCoordenador("co01", "coord2@email.com");
    primeiro.setCodigoCurso("cur00");
    segundo.setCodigoCurso("cur00");
    userRepository.atualizarUsuario(primeiro);
    userRepository.atualizarUsuario(segundo);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.buscarCoordenadorPorCurso("cur00"));
  }

  private Curso salvarCurso(String codigo, String nome) {
    Curso curso = new Curso(codigo, nome, 8, 3200);
    cursoRepository.salvarCurso(curso);
    return curso;
  }

  private Coordenador salvarCoordenador(String matricula, String email) {
    Coordenador coordenador = new Coordenador("Coordenadora", email, "senha123");
    coordenador.setMatricula(matricula);
    userRepository.salvarUsuario(coordenador);
    return coordenador;
  }

  private Coordenador buscarCoordenador(String matricula) {
    return (Coordenador) userRepository.buscarPorMatricula(matricula);
  }
}
