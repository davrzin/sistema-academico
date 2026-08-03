package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Diario;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do repositorio de diarios.
 */
public class DiarioRepositoryTest {

  @TempDir Path tempDir;

  /**
   * Limpa os arquivos gerados pelos testes.
   */
  @AfterEach
  public void tearDown() {
    File diretorio = tempDir.resolve("diarios").toFile();
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

  private DiarioRepository criarRepository() {
    return new DiarioRepository(new ObjectMapper(), tempDir.resolve("diarios").toString());
  }

  private Diario criarDiario(String codigo, String codigoTurma, String matriculaProfessor) {
    return new Diario(
        codigo,
        codigoTurma,
        "Diário de aulas teóricas",
        matriculaProfessor,
        "SEG 08:00-10:00",
        "LAB 01",
        60,
        br.com.classroompb.model.enums.SituacaoDiario.ATIVO);
  }

  @Test
  public void deveCriarRepositorySomenteComObjectMapper() {
    DiarioRepository repository = new DiarioRepository(new ObjectMapper());

    Assertions.assertEquals(DiarioRepository.class, repository.getClass());
  }

  @Test
  public void deveRetornarObjectMapper() {
    DiarioRepository repository = criarRepository();

    Assertions.assertNotNull(repository.getObjectMapper());
  }

  @Test
  public void deveDefinirObjectMapperCorretamente() {
    DiarioRepository repository = criarRepository();
    ObjectMapper novoMapper = new ObjectMapper();

    repository.setObjectMapper(novoMapper);

    Assertions.assertSame(novoMapper, repository.getObjectMapper());
  }

  @Test
  public void deveRetornarDiretorioDosDiarios() {
    DiarioRepository repository = criarRepository();

    Assertions.assertNotNull(repository.getDiretorioDiarios());
  }

  @Test
  public void deveSalvarDiarioEmArquivo() {
    DiarioRepository repository = criarRepository();
    Diario diario = criarDiario("dia00", "tur00", "pr00");

    repository.salvarDiario(diario);

    File arquivo = tempDir.resolve("diarios").resolve("diarios.json").toFile();

    Assertions.assertTrue(arquivo.exists());
    Assertions.assertEquals(1, repository.listarDiarios().size());
    Assertions.assertEquals("dia00", repository.listarDiarios().get(0).getCodigo());
  }

  @Test
  public void deveLancarIllegalArgumentExceptionAoSalvarDiarioNulo() {
    DiarioRepository repository = criarRepository();

    Assertions.assertThrows(IllegalArgumentException.class, () -> repository.salvarDiario(null));
  }

  @Test
  public void deveListarNenhumDiarioQuandoArquivoNaoExiste() {
    DiarioRepository repository = criarRepository();

    Assertions.assertEquals(0, repository.listarDiarios().size());
  }

  @Test
  public void deveBuscarDiarioPorCodigo() {
    DiarioRepository repository = criarRepository();
    Diario diario = criarDiario("dia00", "tur00", "pr00");

    repository.salvarDiario(diario);

    Diario diarioEncontrado = repository.buscarDiarioPorCodigo("dia00");

    Assertions.assertNotNull(diarioEncontrado);
    Assertions.assertEquals("tur00", diarioEncontrado.getCodigoTurma());
  }

  @Test
  public void naoDeveEncontrarDiarioComCodigoInexistente() {
    DiarioRepository repository = criarRepository();
    repository.salvarDiario(criarDiario("dia00", "tur00", "pr00"));

    Assertions.assertNull(repository.buscarDiarioPorCodigo("dia99"));
  }

  @Test
  public void deveBuscarDiariosPorTurma() {
    DiarioRepository repository = criarRepository();
    repository.salvarDiario(criarDiario("dia00", "tur00", "pr00"));
    repository.salvarDiario(criarDiario("dia01", "tur00", "pr01"));
    repository.salvarDiario(criarDiario("dia02", "tur01", "pr00"));

    List<Diario> diariosDaTurma = repository.buscarDiariosPorTurma("tur00");

    Assertions.assertEquals(2, diariosDaTurma.size());
  }

  @Test
  public void deveBuscarDiariosPorProfessor() {
    DiarioRepository repository = criarRepository();
    repository.salvarDiario(criarDiario("dia00", "tur00", "pr00"));
    repository.salvarDiario(criarDiario("dia01", "tur01", "pr00"));
    repository.salvarDiario(criarDiario("dia02", "tur02", "pr01"));

    List<Diario> diariosDoProfessor = repository.buscarDiariosPorMatriculaDeProfessor("pr00");

    Assertions.assertEquals(2, diariosDoProfessor.size());
  }

  @Test
  public void deveAtualizarDiarioEmArquivo() {
    DiarioRepository repository = criarRepository();
    Diario diario = criarDiario("dia00", "tur00", "pr00");
    Diario diarioAtualizado = criarDiario("dia00", "tur01", "pr01");

    repository.salvarDiario(diario);
    boolean atualizou = repository.atualizarDiario(diarioAtualizado);

    Diario diarioEncontrado = repository.buscarDiarioPorCodigo("dia00");

    Assertions.assertTrue(atualizou);
    Assertions.assertEquals(1, repository.listarDiarios().size());
    Assertions.assertEquals("tur01", diarioEncontrado.getCodigoTurma());
    Assertions.assertEquals("pr01", diarioEncontrado.getMatriculaProfessor());
  }

  @Test
  public void naoDeveAtualizarDiarioInexistente() {
    DiarioRepository repository = criarRepository();
    Diario diarioAtualizado = criarDiario("dia99", "tur01", "pr01");

    boolean atualizou = repository.atualizarDiario(diarioAtualizado);

    Assertions.assertFalse(atualizou);
  }

  @Test
  public void deveRemoverDiarioPorCodigo() {
    DiarioRepository repository = criarRepository();
    repository.salvarDiario(criarDiario("dia00", "tur00", "pr00"));
    repository.salvarDiario(criarDiario("dia01", "tur01", "pr01"));

    boolean removeu = repository.removerDiarioPorCodigo("dia00");

    Assertions.assertTrue(removeu);
    Assertions.assertEquals(1, repository.listarDiarios().size());
    Assertions.assertNull(repository.buscarDiarioPorCodigo("dia00"));
    Assertions.assertNotNull(repository.buscarDiarioPorCodigo("dia01"));
  }

  @Test
  public void naoDeveRemoverDiarioInexistente() {
    DiarioRepository repository = criarRepository();
    repository.salvarDiario(criarDiario("dia00", "tur00", "pr00"));

    boolean removeu = repository.removerDiarioPorCodigo("dia99");

    Assertions.assertFalse(removeu);
    Assertions.assertEquals(1, repository.listarDiarios().size());
  }
}
