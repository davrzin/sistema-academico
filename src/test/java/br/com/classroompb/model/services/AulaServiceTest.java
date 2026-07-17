package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
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
  private AulaService aulaService;

  /**
   * Prepara uma turma para os testes.
   */
  @BeforeEach
  public void criarTurma() {
    turma = new Turma("tur00", "dis00", "6", "pro00", 40, "Seg 08:00-10:00", "C-108");
    aulaRepository = criarAulaRepository();
    aulaService = criarAulaService(aulaRepository);
  }

  private AulaRepository criarAulaRepository() {
    return new AulaRepository(new ObjectMapper(), tempDir.resolve("aulas").toString());
  }

  private AulaService criarAulaService(AulaRepository aulaRepository) {
    return new AulaService(aulaRepository, criarTurmaRepository());
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
