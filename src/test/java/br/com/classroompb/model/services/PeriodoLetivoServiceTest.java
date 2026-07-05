package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.ExistePeriodoAtivoException;
import br.com.classroompb.model.exception.PeriodoLetivoExistenteException;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do servico de periodos letivos.
 */
public class PeriodoLetivoServiceTest {

  @TempDir Path tempDir;

  /**
   * Limpa os arquivos gerados pelos testes.
   */
  @AfterEach
  public void tearDown() {
    File diretorio = tempDir.resolve("periodos").toFile();
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

  private PeriodoLetivoRepository criarRepository() {
    return new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
  }

  private PeriodoLetivoService criarService(PeriodoLetivoRepository repository) {
    return new PeriodoLetivoService(repository);
  }

  @Test
  public void deveCadastrarPeriodoLetivoDoPrimeiroSemestre() {
    PeriodoLetivoRepository repository = criarRepository();
    PeriodoLetivoService service = criarService(repository);

    PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    service.cadastrarPeriodoLetivo(periodo);

    Assertions.assertEquals(1, repository.listarPeriodos().size());
    Assertions.assertEquals("2026.1", repository.listarPeriodos().get(0).getPeriodo());
  }

  @Test
  public void deveCadastrarPeriodoLetivoDoSegundoSemestre() {
    PeriodoLetivoRepository repository = criarRepository();
    PeriodoLetivoService service = criarService(repository);

    PeriodoLetivo periodo = new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026");

    service.cadastrarPeriodoLetivo(periodo);

    Assertions.assertEquals(1, repository.listarPeriodos().size());
    Assertions.assertEquals("2026.2", repository.listarPeriodos().get(0).getPeriodo());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCadastrarPeriodoNull() {
    PeriodoLetivoRepository repository = criarRepository();
    PeriodoLetivoService service = criarService(repository);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.cadastrarPeriodoLetivo(null));
  }

  @Test
  public void deveLancarPeriodoLetivoExistenteExceptionQuandoPeriodoJaExistir() {
    PeriodoLetivoRepository repository = criarRepository();
    PeriodoLetivoService service = criarService(repository);

    service.cadastrarPeriodoLetivo(new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026"));

    PeriodoLetivo periodoRepetido = new PeriodoLetivo("2026.1", "03/02/2026", "20/06/2026");

    Assertions.assertThrows(
        PeriodoLetivoExistenteException.class,
        () -> service.cadastrarPeriodoLetivo(periodoRepetido));
  }

  @Test
  public void deveListarPeriodosLetivos() {
    PeriodoLetivoRepository repository = criarRepository();
    PeriodoLetivoService service = criarService(repository);

    service.cadastrarPeriodoLetivo(new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026"));
    service.cadastrarPeriodoLetivo(new PeriodoLetivo("2026.2", "03/10/2026", "15/12/2026"));

    Assertions.assertEquals(2, service.listarPeriodosLetivos().size());
  }

  @Test
  public void deveRejeitarSegundoSemestreComDatasDeOutroAno() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new PeriodoLetivo("2026.2", "01/08/2021", "20/12/2021"));
  }

  @Test
  public void deveRejeitarPrimeiroSemestreComDatasNoSegundoIntervalo() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new PeriodoLetivo("2026.1", "01/08/2026", "20/12/2026"));
  }

  @Test
  public void deveRejeitarSegundoSemestreComDatasNoPrimeiroIntervalo() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new PeriodoLetivo("2026.2", "01/03/2026", "30/06/2026"));
  }

  @Test
  public void deveRejeitarFimAntesDoInicio() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new PeriodoLetivo("2026.2", "20/12/2026", "01/08/2026"));
  }

  @Test
  public void deveRejeitarFormatoInvalidoDePeriodo() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new PeriodoLetivo("2026.3", "01/08/2026", "20/12/2026"));
  }

  @Test
  public void deveAtivarPeriodoLetivoQuandoNaoExistePeriodoAtivo() {
    PeriodoLetivoRepository repository = criarRepository();
    PeriodoLetivoService service = criarService(repository);

    PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");
    service.cadastrarPeriodoLetivo(periodo);

    boolean atualizou = service.ativarPeriodoLetivo(periodo, 0);

    Assertions.assertTrue(atualizou);
    Assertions.assertTrue(repository.listarPeriodos().get(0).getPeriodoAtivo());
  }

  @Test
  public void deveLancarExistePeriodoAtivoExceptionQuandoJaExistePeriodoAtivo() {
    PeriodoLetivoRepository repository = criarRepository();
    PeriodoLetivoService service = criarService(repository);

    PeriodoLetivo periodoAtivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");
    PeriodoLetivo outroPeriodo = new PeriodoLetivo("2026.2", "03/10/2026", "15/12/2026");

    service.cadastrarPeriodoLetivo(periodoAtivo);
    service.cadastrarPeriodoLetivo(outroPeriodo);
    service.ativarPeriodoLetivo(periodoAtivo, 0);

    Assertions.assertThrows(
        ExistePeriodoAtivoException.class, () -> service.ativarPeriodoLetivo(outroPeriodo, 1));
  }

  @Test
  public void deveDesativarPeriodoLetivo() {
    PeriodoLetivoRepository repository = criarRepository();
    PeriodoLetivoService service = criarService(repository);

    PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");
    service.cadastrarPeriodoLetivo(periodo);
    service.ativarPeriodoLetivo(periodo, 0);

    PeriodoLetivo periodoSalvo = repository.listarPeriodos().get(0);
    boolean atualizou = service.desativarPeriodoLetivo(periodoSalvo, 0);

    Assertions.assertTrue(atualizou);
    Assertions.assertFalse(repository.listarPeriodos().get(0).getPeriodoAtivo());
  }
}
