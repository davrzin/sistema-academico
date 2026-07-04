package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
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
}
