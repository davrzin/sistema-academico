package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.DiarioRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do servico de aulas.
 */
public class AulaServiceTest {

  @TempDir Path tempDir;

  private Turma turma;
  private AulaRepository aulaRepository;
  private DiarioRepository diarioRepository;
  private TurmaRepository turmaRepository;
  private AulaService aulaService;

  /**
   * Prepara uma turma para os testes.
   */
  @BeforeEach
  public void criarTurma() {
    turma = new Turma("tur00", "dis00", "6", "pro00", 40, "Seg 08:00-10:00", "C-108");
    aulaRepository = criarAulaRepository();
    diarioRepository = criarDiarioRepository();
    turmaRepository = criarTurmaRepository();
    aulaService = criarAulaService(aulaRepository, diarioRepository, turmaRepository);
  }

  private AulaRepository criarAulaRepository() {
    return new AulaRepository(new ObjectMapper(), tempDir.resolve("aulas").toString());
  }

  private DiarioRepository criarDiarioRepository() {
    return new DiarioRepository(new ObjectMapper(), tempDir.resolve("diarios").toString());
  }

  private AulaService criarAulaService(
      AulaRepository aulaRepository, DiarioRepository diarioRepository,
      TurmaRepository turmaRepository) {
    return new AulaService(aulaRepository, turmaRepository, diarioRepository);
  }

  private TurmaRepository criarTurmaRepository() {
    return new TurmaRepository(new ObjectMapper(), tempDir.resolve("turmas").toString());
  }

  @Test
  public void deveCriarAulaServiceCorretamenteComConstrutorVazio() {
    AulaService aulaService1 = new AulaService();

    Assertions.assertNotNull(aulaService1);
  }

  @Test
  public void deveCriarAulaServiceCorretamente() {
    AulaService aulaService1 = new AulaService(criarAulaRepository(), criarTurmaRepository());

    Assertions.assertNotNull(aulaService1);
  }

  @Test
  public void deveGerarAulaCorretamente() {
    Aula aula = aulaService.gerarAula(turma);

    Assertions.assertNotNull(aula);
  }

  @Test
  public void deveLancarEntradaInvalidaException() {

    Assertions.assertThrows(NullPointerException.class, () -> aulaService.gerarAula(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionEmSalvarAula() {

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionEmSalvarAulaEmAulaComAtributosNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> aulaService.salvarAula(new Aula(null, null, null, null, null)));
  }

  @Test
  public void deveLancarExcecaoQuandoCodigoTurmaForNulo() {
    Aula aula = new Aula();

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(aula));
  }

  @Test
  public void deveLancarExcecaoQuandoDataForNula() {
    Aula aula = new Aula();
    aula.setCodigoTurma("tur00");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(aula));
  }

  @Test
  public void deveLancarExcecaoQuandoHorarioForNulo() {
    Aula aula = new Aula();
    aula.setCodigoTurma("tur00");
    aula.setData("17/07/2026");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(aula));
  }

  @Test
  public void deveLancarExcecaoQuandoPresencasForemNulas() {
    Aula aula = new Aula();
    aula.setCodigoTurma("tur00");
    aula.setData("17/07/2026");
    aula.setHorario("Seg 08:00-10:00");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(aula));
  }

  @Test
  public void deveLancarExcecaoQuandoPresencasEstiveremVazias() {
    Aula aula =
        new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", new HashMap<>());

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(aula));
  }

  @Test
  public void deveSalvarAulaComDadosValidos() {
    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    presencas.put("alu01", false);
    Aula aula = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);

    aulaService.salvarAula(aula);

    List<Aula> aulasSalvas = aulaRepository.listarAulas();
    Assertions.assertEquals(1, aulasSalvas.size());
    Assertions.assertEquals("aul00", aulasSalvas.get(0).getId());
  }

  @Test
  public void deveSalvarAulaQuandoTurmaPossuiDiarioAtivo() {
    diarioRepository.salvarDiario(criarDiario("dia00", "tur00", SituacaoDiario.ATIVO));

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    Aula aula = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);

    aulaService.salvarAula(aula);

    Assertions.assertEquals(1, aulaRepository.listarAulas().size());
  }

  @Test
  public void deveLancarExcecaoQuandoDiarioDaTurmaEstiverEncerrado() {
    diarioRepository.salvarDiario(criarDiario("dia00", "tur00", SituacaoDiario.ENCERRADO));

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    Aula aula = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(aula));
    Assertions.assertTrue(aulaRepository.listarAulas().isEmpty());
  }

  @Test
  public void deveLancarExcecaoQuandoDiarioDaTurmaEstiverCancelado() {
    diarioRepository.salvarDiario(criarDiario("dia00", "tur00", SituacaoDiario.CANCELADO));

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    Aula aula = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(aula));
    Assertions.assertTrue(aulaRepository.listarAulas().isEmpty());
  }

  @Test
  public void devePermitirRegistroQuandoExisteUmDiarioAtivoEntreVarios() {
    diarioRepository.salvarDiario(criarDiario("dia00", "tur00", SituacaoDiario.CANCELADO));
    diarioRepository.salvarDiario(criarDiario("dia01", "tur00", SituacaoDiario.ATIVO));

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    Aula aula = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);

    aulaService.salvarAula(aula);

    Assertions.assertEquals(1, aulaRepository.listarAulas().size());
  }

  private Diario criarDiario(String codigo, String codigoTurma, SituacaoDiario situacao) {
    return new Diario(
        codigo, codigoTurma, "Diário de teste", "pro00", "Seg 08:00-10:00", "C-108", 60,
        situacao);
  }

  @Test
  public void deveSalvarAulaQuandoProfessorForDonoDaTurma() {
    turmaRepository.salvarTurma(turma);

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    Aula aula = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);

    aulaService.salvarAula(aula, "pro00");

    Assertions.assertEquals(1, aulaRepository.listarAulas().size());
  }

  @Test
  public void deveLancarExcecaoQuandoProfessorNaoForDonoDaTurma() {
    turmaRepository.salvarTurma(turma);

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    Aula aula = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> aulaService.salvarAula(aula, "outroProfessor"));
    Assertions.assertTrue(aulaRepository.listarAulas().isEmpty());
  }

  @Test
  public void deveLancarExcecaoQuandoMatriculaProfessorForVaziaAoSalvarAula() {
    turmaRepository.salvarTurma(turma);

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    Aula aula = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> aulaService.salvarAula(aula, ""));
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> aulaService.salvarAula(aula, null));
  }

  @Test
  public void deveLancarExcecaoQuandoTurmaDaAulaNaoExistirAoValidarProfessor() {
    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    Aula aula = new Aula("aul00", "turXX", "17/07/2026", "Seg 08:00-10:00", presencas);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> aulaService.salvarAula(aula, "pro00"));
  }

  @Test
  public void deveLancarExcecaoQuandoDiarioFechadoMesmoComProfessorDono() {
    turmaRepository.salvarTurma(turma);
    diarioRepository.salvarDiario(criarDiario("dia00", "tur00", SituacaoDiario.ENCERRADO));

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);
    Aula aula = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> aulaService.salvarAula(aula, "pro00"));
  }

  @Test
  public void deveGerarAulaComCodigoBaseadoNaTurma() {
    Aula aula = aulaService.gerarAula(turma);

    Assertions.assertEquals("tur00", aula.getCodigoTurma());
    Assertions.assertEquals(turma.getHorario(), aula.getHorario());
    Assertions.assertTrue(aula.getId().startsWith("aul"));
  }

  @Test
  public void deveGerarCodigoDeAulaSemColidirComAulaJaExistente() {
    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("alu00", true);

    Aula aulaExistente = new Aula("aul00", "tur00", "17/07/2026", "Seg 08:00-10:00", presencas);
    aulaService.salvarAula(aulaExistente);

    Aula novaAula = aulaService.gerarAula(turma);

    Assertions.assertEquals("aul01", novaAula.getId());
  }
}
