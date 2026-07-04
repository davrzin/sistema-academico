package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.BoletimRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do servico de boletins.
 */
public class BoletimServiceTest {

  @TempDir Path tempDir;

  private BoletimRepository boletimRepository;
  private BoletimService boletimService;
  private Boletim boletim;

  /**
   * Prepara as variaveis para os testes.
   */
  @BeforeEach
  public void criarVariaveis() {

    boletimRepository =
        new BoletimRepository(new ObjectMapper(), tempDir.resolve("boletins").toString());
    boletimService = new BoletimService(boletimRepository);
    boletim = new Boletim("al00", "tur00");
  }

  @Test
  public void deveCriarBoletimServiceComConstrutorVazio() {
    BoletimService service = new BoletimService();

    Assertions.assertNotNull(service);
    Assertions.assertEquals(BoletimService.class, service.getClass());
  }

  @Test
  public void deveCriarBoletimComSegundoConstrutor() {

    BoletimService service = new BoletimService(boletimRepository);

    Assertions.assertNotNull(service);
    Assertions.assertEquals(BoletimService.class, service.getClass());
  }

  @Test
  public void deveRetornarBoletimRepositoryCorretamente() {

    BoletimRepository repository = boletimService.getRepository();

    Assertions.assertEquals(BoletimRepository.class, repository.getClass());
  }

  @Test
  public void deveCriarBoletimCorretamente() {
    Boletim boletim1 = boletimService.criarBoletim(boletim);

    Assertions.assertEquals(boletim1.getClass(), boletim.getClass());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionEmCriarBoletimComBoletimNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimService.criarBoletim(null));
  }

  @Test
  public void deveBuscarBoletinsPorAlunoCorretamente() {
    boletimService.criarBoletim(boletim);
    List<Boletim> boletinsAluno = boletimService.buscarBoletinsPorAluno("al00");

    Assertions.assertEquals("al00", boletinsAluno.getFirst().getMatriculaAluno());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletinsDeAlunoComMatriculaNull() {

    boletimService.criarBoletim(boletim);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimService.buscarBoletinsPorAluno(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletinsDeAlunoComMatriculaVazia() {
    boletimService.criarBoletim(boletim);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimService.buscarBoletinsPorAluno(""));
  }
}
