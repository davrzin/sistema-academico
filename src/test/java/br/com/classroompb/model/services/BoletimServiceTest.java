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
  }

  @Test
  public void deveAtualizarApenasSegundaNotaPreservandoPrimeira() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    boletimService.lancarSegundaNota("tur00", "al00", 7.5f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(8.0f, boletimAtualizado.getPrimeiraNota());
    Assertions.assertEquals(7.5f, boletimAtualizado.getSegundaNota());
  }

  @Test
  public void deveAtualizarAsDuasNotas() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    boletimService.lancarNotas("tur00", "al00", 6.0f, 7.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(6.0f, boletimAtualizado.getPrimeiraNota());
    Assertions.assertEquals(7.0f, boletimAtualizado.getSegundaNota());
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
  public void devePreservarCalculoDeMediaExistente() {
    prepararTurmaComBoletim(8.0f, 9.0f);

    boletimService.lancarNotas("tur00", "al00", 10.0f, 8.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    float media = (boletimAtualizado.getPrimeiraNota() + boletimAtualizado.getSegundaNota()) / 2;
    Assertions.assertEquals(9.0f, media);
  }

  private void prepararTurmaComBoletim(float primeiraNota, float segundaNota) {
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
    Assertions.assertEquals(7.5f, boletimAtualizado.getMediaFinal());
  }

  @Test
  public void deveRecalcularMediaAoRetificarPrimeiraNota() {
    prepararTurmaComBoletim(5.0f, 7.0f); // Média inicial: 6.0

    boletimService.lancarPrimeiraNota("tur00", "al00", 9.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(8.0f, boletimAtualizado.getMediaFinal());
  }

  @Test
  public void deveRecalcularMediaAoRetificarSegundaNota() {
    prepararTurmaComBoletim(6.0f, 4.0f); // Média inicial: 5.0

    boletimService.lancarSegundaNota("tur00", "al00", 8.0f, "pr00");

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals(7.0f, boletimAtualizado.getMediaFinal());
  }
  
  @Test
  public void deveDefinirSituacaoComoAprovadoQuandoMediaESemestreDentroDoLimite() {
    prepararTurmaComBoletim(0.0f, 0.0f);
    Boletim b = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    
    b.setFrequencia(80.0); // Frequência acima de 75%
    boletimService.lancarNotas("tur00", "al00", 7.0f, 7.0f, "pr00"); // Média 7.0

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals("APROVADO", boletimAtualizado.getSituacao());
  }

  @Test
  public void deveDefinirSituacaoComoReprovadoPorMediaQuandoNotaAbaixoDeSete() {
    prepararTurmaComBoletim(0.0f, 0.0f);
    Boletim b = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    
    b.setFrequencia(90.0); // Frequência ok
    boletimService.lancarNotas("tur00", "al00", 5.0f, 6.0f, "pr00"); // Média 5.5

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals("REPROVADO_POR_MEDIA", boletimAtualizado.getSituacao());
  }

  @Test
  public void deveDefinirSituacaoComoReprovadoPorFaltaQuandoFrequenciaAbaixoDeSetentaECinco() {
    prepararTurmaComBoletim(0.0f, 0.0f);
    Boletim b = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    
    b.setFrequencia(70.0); // Frequência menor que 75% (mais de 25% de faltas)
    boletimService.lancarNotas("tur00", "al00", 9.0f, 9.0f, "pr00"); // Média alta

    Boletim boletimAtualizado = boletimService.buscarBoletimPorAlunoETurma("al00", "tur00");
    Assertions.assertEquals("REPROVADO_POR_FALTA", boletimAtualizado.getSituacao());
  }
  /**
 * Ajustar esse teste
 * @Test
  public void deveBloquearAlteracaoDeNotaEmTurmaDePeriodoEncerrado() {
    // Configura uma turma com período passado "2025.1"
    Turma turmaPassada = new Turma("tur_velha", "dis00", "2025.1", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turmaPassada.getMatriculados().add("al00");
    turmaRepository.salvarTurma(turmaPassada);

    // Salva o boletim inicial
    Boletim boletimVelho = new Boletim("al00", "tur_velha");
    boletimVelho.setPrimeiraNota(5.0f);
    boletimVelho.setSegundaNota(5.0f);
    boletimService.criarBoletim(boletioVelho);

    // Força a existência de um período ativo no sistema para ativar a trava
    br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo periodoAtivo = 
        new br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo("2026.2", "01/07/2026", "30/11/2026");
    periodoAtivo.setPeriodoAtivo(true);
    // Nota: Como o repositório de períodos lê do arquivo json físico simulado por tempDir, 
    // a trava bloqueará a alteração se a turma possuir período divergente do ativo configurado.
    
    // Configura o mock do arquivo para simular o período ativo atual
    try {
      com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
      java.io.File arquivoPeriodos = new java.io.File(tempDir.resolve("periodos").toFile(), "periodos.json");
      arquivoPeriodos.getParentFile().mkdirs();
      mapper.writeValue(arquivoPeriodos, java.util.List.of(periodoAtivo));
    } catch (java.io.IOException e) {
      Assertions.fail("Falha ao preparar mock de períodos ativos para o teste.");
    }

    // Valida se a exceção é disparada ao tentar alterar notas de período antigo
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarPrimeiraNota("tur_velha", "al00", 10.0f, "pr00"));
  }
 */

}
