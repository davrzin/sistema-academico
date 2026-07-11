package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.TurmaRepository;
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
  private TurmaRepository turmaRepository;
  private BoletimService boletimService;
  private Boletim boletim;

  /**
   * Prepara as variaveis para os testes.
   */
  @BeforeEach
  public void criarVariaveis() {

    boletimRepository =
        new BoletimRepository(new ObjectMapper(), tempDir.resolve("boletins").toString());
    turmaRepository = new TurmaRepository(new ObjectMapper(), tempDir.resolve("turmas").toString());
    boletimService = new BoletimService(boletimRepository, turmaRepository);
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

  @Test
  public void deveAtualizarApenasPrimeiraNotaPreservandoSegunda() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    boletimService.lancarPrimeiraNota("tur00", "al00", 10.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(10.0f, boletimAtualizado.getPrimeiraNota());
    Assertions.assertEquals(9.0f, boletimAtualizado.getSegundaNota());
    Assertions.assertEquals(9.5f, boletimAtualizado.calcularMediaFinal());
  }

  @Test
  public void deveLancarPrimeiraNotaPreservandoSegundaPendente() {
    prepararTurmaComBoletim(null, null);

    boletimService.lancarPrimeiraNota("tur00", "al00", 0.0f, "pr00");

    Boletim atualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(0.0f, atualizado.getPrimeiraNota());
    Assertions.assertNull(atualizado.getSegundaNota());
    Assertions.assertNull(atualizado.calcularMediaFinal());
  }

  @Test
  public void deveAtualizarApenasSegundaNotaPreservandoPrimeira() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    boletimService.lancarSegundaNota("tur00", "al00", 7.5f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(8.0f, boletimAtualizado.getPrimeiraNota());
    Assertions.assertEquals(7.5f, boletimAtualizado.getSegundaNota());
    Assertions.assertEquals(7.75f, boletimAtualizado.calcularMediaFinal());
  }

  @Test
  public void deveLancarSegundaNotaPreservandoPrimeiraPendente() {
    prepararTurmaComBoletim(null, null);

    boletimService.lancarSegundaNota("tur00", "al00", 6.0f, "pr00");

    Boletim atualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertNull(atualizado.getPrimeiraNota());
    Assertions.assertEquals(6.0f, atualizado.getSegundaNota());
    Assertions.assertNull(atualizado.calcularMediaFinal());
  }

  @Test
  public void deveAtualizarAsDuasNotas() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    boletimService.lancarNotas("tur00", "al00", 6.0f, 7.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(6.0f, boletimAtualizado.getPrimeiraNota());
    Assertions.assertEquals(7.0f, boletimAtualizado.getSegundaNota());
    Assertions.assertEquals(6.5f, boletimAtualizado.calcularMediaFinal());
  }

  @Test
  public void deveRejeitarNotaMenorQueZero() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarPrimeiraNota("tur00", "al00", -1.0f, "pr00"));
  }

  @Test
  public void deveRejeitarNotaMaiorQueDez() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarSegundaNota("tur00", "al00", 11.0f, "pr00"));
  }

  @Test
  public void deveUsarCalculoCentralDaMedia() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    boletimService.lancarNotas("tur00", "al00", 10.0f, 8.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(9.0f, boletimAtualizado.calcularMediaFinal());
  }

  @Test
  public void professorNaoDeveLancarNotasEmTurmaDeOutroProfessor() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarNotas("tur00", "al00", 7.0f, 8.0f, "pr01"));
  }

  @Test
  public void naoDeveLancarNotasParaAlunoNaoMatriculado() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarNotas("tur00", "al01", 7.0f, 8.0f, "pr00"));
  }

  @Test
  public void naoDeveCriarBoletimDuplicadoAoAlterarNotas() {
    prepararTurmaComBoletim(null, null);

    boletimService.lancarPrimeiraNota("tur00", "al00", 8.0f, "pr00");
    boletimService.lancarSegundaNota("tur00", "al00", 6.0f, "pr00");

    Assertions.assertEquals(1, boletimService.buscarBoletinsPorAluno("al00").size());
  }

  private void prepararTurmaComBoletim(Float primeiraNota, Float segundaNota) {
    Turma turma =
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turma.getMatriculados().add("al00");
    turmaRepository.salvarTurma(turma);

    boletim.setPrimeiraNota(primeiraNota);
    boletim.setSegundaNota(segundaNota);
    boletimService.criarBoletim(boletim);
  }
}
