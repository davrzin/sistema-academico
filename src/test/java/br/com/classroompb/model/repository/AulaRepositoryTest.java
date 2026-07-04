package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do repositorio de aulas.
 */
public class AulaRepositoryTest {

  @TempDir Path tempDir;

  private AulaRepository aulaRepository;
  private Aula aula;

  /**
   * Prepara as variaveis para os testes.
   */
  @BeforeEach
  public void criarVariavel() {
    aulaRepository = new AulaRepository(new ObjectMapper(), tempDir.resolve("aulas").toString());
    aula = new Aula("aula00", "tur00", "23/06/2026", "Seg 07:00-09:00");
  }

  /**
   * Limpa os arquivos gerados pelos testes.
   */
  @AfterEach
  public void tearDown() {
    File diretorio = tempDir.resolve("aulas").toFile();
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

  @Test
  public void deveCriarRepositorySomenteComMapper() {
    AulaRepository repository = new AulaRepository(new ObjectMapper());

    Assertions.assertNotNull(repository);
  }

  @Test
  public void deveRetornarObjectMapper() {

    Assertions.assertEquals(ObjectMapper.class, aulaRepository.getObjectMapper().getClass());
  }

  @Test
  public void deveRetornarDiretorioAulas() {
    Assertions.assertNotNull(aulaRepository.getDiretorioAulas());
  }

  @Test
  public void deveSalvarAulaCorretamente() {

    Assertions.assertTrue(aulaRepository.salvarAula(aula));
  }

  @Test
  public void deveLancarIllegalArgumentExceptionEmSalvarAulaNull() {

    Assertions.assertThrows(IllegalArgumentException.class, () -> aulaRepository.salvarAula(null));
  }

  @Test
  public void deveAtualizarAulaCorretamente() {

    aulaRepository.salvarAula(aula);

    Aula aulaAtualizada = new Aula("aula00", "tur00", "23/06/2026", "Quarta 09:00-11:00");

    Assertions.assertTrue(aulaRepository.atualizarAula(aulaAtualizada));
  }

  @Test
  public void deveLancarIllegalArgumentException() {

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> aulaRepository.atualizarAula(null));
  }

  @Test
  public void deveListarAulasCorretamente() {

    aulaRepository.salvarAula(aula);

    List<Aula> aulasRegistradas = aulaRepository.listarAulas();

    Assertions.assertEquals(1, aulasRegistradas.size());
  }

  @Test
  public void deveRetonarListDeAulasVazia() {

    List<Aula> aulasRegistradas = aulaRepository.listarAulas();

    Assertions.assertEquals(0, aulasRegistradas.size());
  }

  @Test
  public void deveBuscarAulaPorIdCorretamente() {

    aulaRepository.salvarAula(aula);

    Assertions.assertNotNull(aulaRepository.buscarAulaPorId("aula00"));
  }

  @Test
  public void deveRetornarEntradaInvalidaExceptionComCodigoNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> aulaRepository.buscarAulaPorId(null));
  }

  @Test
  public void deveRetornarEntradaInvalidaExceptionComCodigoVazio() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> aulaRepository.buscarAulaPorId(""));
  }

  @Test
  public void deveRetornarNullSeNaoEncontrarAulaProcurada() {

    aulaRepository.salvarAula(aula);

    Assertions.assertNull(aulaRepository.buscarAulaPorId("sdfs"));
  }
}
