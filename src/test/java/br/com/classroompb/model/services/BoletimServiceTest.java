package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
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
  private PeriodoLetivoRepository periodoLetivoRepository;
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
    periodoLetivoRepository =
        new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
    periodoLetivoRepository.salvarPeriodoLetivo(
        new PeriodoLetivo("2026.2", "01/07/2026", "30/11/2026"));
    boletimService =
        new BoletimService(boletimRepository, turmaRepository, periodoLetivoRepository);
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

  @Test
  public void deveCalcularMediaAutomaticamenteAoLancarDuasNotas() {
    prepararTurmaComBoletim(0.0f, 0.0f);

    boletimService.lancarNotas("tur00", "al00", 7.0f, 8.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(7.5f, boletimAtualizado.calcularMediaFinal());
  }

  @Test
  public void deveRecalcularMediaAoRetificarPrimeiraNota() {
    prepararTurmaComBoletim(5.0f, 7.0f);

    boletimService.lancarPrimeiraNota("tur00", "al00", 9.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(8.0f, boletimAtualizado.calcularMediaFinal());
  }

  @Test
  public void deveRecalcularMediaAoRetificarSegundaNota() {
    prepararTurmaComBoletim(6.0f, 4.0f);

    boletimService.lancarSegundaNota("tur00", "al00", 8.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(7.0f, boletimAtualizado.calcularMediaFinal());
  }

  @Test
  public void deveDeterminarAprovacaoPeloServicoDeSituacao() {
    prepararTurmaComBoletim(0.0f, 0.0f);
    boletimService.lancarNotas("tur00", "al00", 7.0f, 7.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    boletimAtualizado.setFrequencia(100.0);
    SituacaoAcademicaService situacaoService = new SituacaoAcademicaService();
    Assertions.assertEquals(
        "Aprovado", situacaoService.determinar(boletimAtualizado).getDescricao());
  }

  @Test
  public void deveDeterminarRecuperacaoPeloServicoDeSituacao() {
    prepararTurmaComBoletim(0.0f, 0.0f);
    boletimService.lancarNotas("tur00", "al00", 5.0f, 5.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    boletimAtualizado.setFrequencia(100.0);
    SituacaoAcademicaService situacaoService = new SituacaoAcademicaService();
    Assertions.assertEquals(
        "Em recuperação", situacaoService.determinar(boletimAtualizado).getDescricao());
  }

  @Test
  public void devePriorizarFaltaPeloServicoDeSituacao() {
    prepararTurmaComBoletim(0.0f, 0.0f);
    boletimService.lancarNotas("tur00", "al00", 9.0f, 9.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    boletimAtualizado.setFrequencia(50.0);
    SituacaoAcademicaService situacaoService = new SituacaoAcademicaService();
    Assertions.assertEquals(
        "Reprovado por falta", situacaoService.determinar(boletimAtualizado).getDescricao());
  }

  @Test
  public void devePermitirAlterarNotaSemUsarPeriodoComoFechamento() {
    prepararTurmaComBoletim(5.0f, 5.0f);

    boletimService.lancarPrimeiraNota("tur00", "al00", 10.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(10.0f, boletimAtualizado.getPrimeiraNota());
    Assertions.assertEquals(5.0f, boletimAtualizado.getSegundaNota());
    Assertions.assertEquals(7.5f, boletimAtualizado.calcularMediaFinal());
  }

  @Test
  public void deveCriarBoletimServiceComConstrutorDeDoisRepositorios() {
    BoletimService service = new BoletimService(boletimRepository, turmaRepository);

    Assertions.assertNotNull(service);
    Assertions.assertEquals(BoletimRepository.class, service.getRepository().getClass());
  }

  @Test
  public void deveCriarBoletimQuandoNaoExistirBoletimParaAlunoETurma() {
    Boletim criado = boletimService.criarBoletimSeNaoExistir("al00", "tur00");

    Assertions.assertNotNull(criado);
    Assertions.assertEquals("al00", criado.getMatriculaAluno());
    Assertions.assertEquals("tur00", criado.getCodigoTurma());
    Assertions.assertEquals(1, boletimService.buscarBoletinsPorAluno("al00").size());
  }

  @Test
  public void deveRetornarBoletimExistenteAoInvesDeDuplicarAoCriarSeNaoExistir() {
    Boletim primeiraChamada = boletimService.criarBoletimSeNaoExistir("al00", "tur00");
    Boletim segundaChamada = boletimService.criarBoletimSeNaoExistir("al00", "tur00");

    Assertions.assertEquals(primeiraChamada.getIdBoletim(), segundaChamada.getIdBoletim());
    Assertions.assertEquals(1, boletimService.buscarBoletinsPorAluno("al00").size());
  }

  @Test
  public void deveBuscarBoletinsPorTurmaCorretamente() {
    boletimService.criarBoletim(boletim);

    List<Boletim> boletinsTurma = boletimService.buscarBoletinsPorTurma("tur00");

    Assertions.assertEquals(1, boletinsTurma.size());
    Assertions.assertEquals("tur00", boletinsTurma.getFirst().getCodigoTurma());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletinsPorTurmaComCodigoVazio() {
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimService.buscarBoletinsPorTurma(""));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorAlunoETurmaComMatriculaVazia() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.buscarBoletimPorAlunoETurma("", "tur00"));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorAlunoETurmaComCodigoTurmaVazio() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.buscarBoletimPorAlunoETurma("al00", ""));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoLancarNotasEmTurmaInexistente() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarNotas("turXX", "al00", 7.0f, 8.0f, "pr00"));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoLancarNotasComCodigoTurmaVazio() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarNotas("", "al00", 7.0f, 8.0f, "pr00"));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoLancarNotasComMatriculaProfessorVazia() {
    prepararTurmaComBoletim(null, null);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarNotas("tur00", "al00", 7.0f, 8.0f, ""));
  }

  @Test
  public void naoDeveLancarNotasQuandoPeriodoLetivoDaTurmaEstiverEncerrado() {
    PeriodoLetivoRepository periodoEncerradoRepository =
        new PeriodoLetivoRepository(
            new ObjectMapper(), tempDir.resolve("periodosEncerrados").toString());
    PeriodoLetivo periodoEncerrado = new PeriodoLetivo("2026.2", "01/07/2026", "30/11/2026");
    periodoEncerrado.setPeriodoEncerrado(true);
    periodoEncerradoRepository.salvarPeriodoLetivo(periodoEncerrado);

    BoletimService service =
        new BoletimService(boletimRepository, turmaRepository, periodoEncerradoRepository);

    Turma turma =
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turma.getMatriculados().add("al00");
    turmaRepository.salvarTurma(turma);
    service.criarBoletim(boletim);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.lancarNotas("tur00", "al00", 8.0f, 8.0f, "pr00"));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoPeriodoLetivoDaTurmaNaoForEncontrado() {
    Turma turma =
        new Turma("tur00", "dis00", "9999.1", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turma.getMatriculados().add("al00");
    turmaRepository.salvarTurma(turma);
    boletimService.criarBoletim(boletim);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarNotas("tur00", "al00", 8.0f, 8.0f, "pr00"));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoLancarNotasParaBoletimInexistente() {
    Turma turma =
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turma.getMatriculados().add("al00");
    turmaRepository.salvarTurma(turma);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarNotas("tur00", "al00", 8.0f, 8.0f, "pr00"));
  }

}
