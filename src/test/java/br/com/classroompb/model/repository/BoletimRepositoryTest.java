package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
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
 * Testes do repositorio de boletins.
 */
public class BoletimRepositoryTest {

  @TempDir Path tempDir;

  private BoletimRepository boletimRepository;
  private Boletim boletim;

  /**
   * Prepara as variaveis para os testes.
   */
  @BeforeEach
  public void criarVariaveis() {
    boletimRepository =
        new BoletimRepository(new ObjectMapper(), tempDir.resolve("boletins").toString());
    boletim = new Boletim("al00", "tur00");
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

    BoletimRepository boletimRepository1 = new BoletimRepository(new ObjectMapper());

    Assertions.assertNotNull(boletimRepository1);
  }

  @Test
  public void deveCriarRepositoryComConstrutorCompleto() {
    BoletimRepository boletimRepository1 =
        new BoletimRepository(new ObjectMapper(), tempDir.resolve("boletins").toString());

    Assertions.assertNotNull(boletimRepository1);
  }

  @Test
  public void deveRetornarObjectMapperCorretamente() {

    Assertions.assertEquals(ObjectMapper.class, boletimRepository.getObjectMapper().getClass());
  }

  @Test
  public void deveRetornarCaminhoDoArquivoCorretamente() {

    String caminhoEsperado = tempDir.resolve("boletins").toString();

    Assertions.assertEquals(caminhoEsperado, boletimRepository.getDiretorioBoletins());
  }

  @Test
  public void deveListarBoletinsCorretamente() {

    List<Boletim> boletins = boletimRepository.listarBoletins();

    Assertions.assertEquals(0, boletins.size());
  }

  @Test
  public void deveSalvarBoletimCorretamente() {

    boletimRepository.salvarBoletim(boletim);

    List<Boletim> boletins = boletimRepository.listarBoletins();

    Assertions.assertEquals(1, boletins.size());
    Assertions.assertEquals(boletim.getMatriculaAluno(), boletins.getFirst().getMatriculaAluno());
    Assertions.assertEquals(boletim.getCodigoTurma(), boletins.getFirst().getCodigoTurma());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoSalvarBoletimComBoletimNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimRepository.salvarBoletim(null));
  }

  @Test
  public void deveDefinirObjectMapperCorretamente() {

    ObjectMapper novoMapper = new ObjectMapper();
    boletimRepository.setObjectMapper(novoMapper);

    Assertions.assertSame(novoMapper, boletimRepository.getObjectMapper());
  }

  @Test
  public void deveAtualizarBoletimCorretamente() {

    boletim.setIdBoletim("bol00");
    boletimRepository.salvarBoletim(boletim);

    Boletim boletimAtualizado = new Boletim("al00", "tur00");
    boletimAtualizado.setIdBoletim("bol00");
    boletimAtualizado.setPrimeiraNota(8.5f);
    boletimAtualizado.setSegundaNota(7.0f);

    boletimRepository.atualizarBoletins(boletimAtualizado);

    Boletim boletimPersistido = boletimRepository.buscarBoletimPorCodigo("bol00");

    Assertions.assertEquals(8.5f, boletimPersistido.getPrimeiraNota());
    Assertions.assertEquals(7.0f, boletimPersistido.getSegundaNota());
  }

  @Test
  public void naoDeveAlterarBoletinsAoAtualizarCodigoInexistente() {

    boletim.setIdBoletim("bol00");
    boletimRepository.salvarBoletim(boletim);

    Boletim boletimInexistente = new Boletim("al01", "tur01");
    boletimInexistente.setIdBoletim("bol99");
    boletimInexistente.setPrimeiraNota(10f);

    boletimRepository.atualizarBoletins(boletimInexistente);

    Assertions.assertEquals(1, boletimRepository.listarBoletins().size());
    Assertions.assertNull(boletimRepository.buscarBoletimPorCodigo("bol00").getPrimeiraNota());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoAtualizarBoletimNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimRepository.atualizarBoletins(null));
  }

  @Test
  public void deveBuscarBoletimPorCodigoCorretamente() {

    boletim.setIdBoletim("bol00");
    boletimRepository.salvarBoletim(boletim);

    Boletim boletimEncontrado = boletimRepository.buscarBoletimPorCodigo("bol00");

    Assertions.assertNotNull(boletimEncontrado);
    Assertions.assertEquals(boletim.getIdBoletim(), boletimEncontrado.getIdBoletim());
    Assertions.assertEquals(boletim.getMatriculaAluno(), boletimEncontrado.getMatriculaAluno());
    Assertions.assertEquals(boletim.getCodigoTurma(), boletimEncontrado.getCodigoTurma());
  }

  @Test
  public void deveRetornarNullAoBuscarBoletimPorCodigoInexistente() {

    boletim.setIdBoletim("bol00");
    boletimRepository.salvarBoletim(boletim);

    Boletim boletimEncontrado = boletimRepository.buscarBoletimPorCodigo("bolInexistente");

    Assertions.assertNull(boletimEncontrado);
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorCodigoNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimRepository.buscarBoletimPorCodigo(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorCodigoVazio() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimRepository.buscarBoletimPorCodigo(""));
  }

  @Test
  public void deveBuscarBoletimPorAlunoCorretamente() {

    boletimRepository.salvarBoletim(boletim);

    List<Boletim> boletinsluno = boletimRepository.buscarBoletinsPorAluno("al00");

    Assertions.assertEquals(1, boletinsluno.size());
    Assertions.assertEquals(
        boletim.getMatriculaAluno(), boletinsluno.getFirst().getMatriculaAluno());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorMatriculaAlunoNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimRepository.buscarBoletinsPorAluno(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorMatriculaAlunoVazio() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimRepository.buscarBoletinsPorAluno(""));
  }

  @Test
  public void deveBuscarBoletimPorTurmaCorretamente() {

    boletimRepository.salvarBoletim(boletim);

    List<Boletim> boletinsluno = boletimRepository.buscarBoletinsPorTurma("tur00");

    Assertions.assertEquals(1, boletinsluno.size());
    Assertions.assertEquals(boletim.getCodigoTurma(), boletinsluno.getFirst().getCodigoTurma());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorCodigoTurmaNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimRepository.buscarBoletinsPorTurma(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorCodigoTurmaVazio() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimRepository.buscarBoletinsPorTurma(""));
  }
}
