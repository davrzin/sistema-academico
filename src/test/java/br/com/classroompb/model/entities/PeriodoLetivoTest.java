package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testes da entidade PeriodoLetivo.
 */
public class PeriodoLetivoTest {

  @Test
  public void deveCriarUmObjetoPeriodoLetivo() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    Assertions.assertEquals(PeriodoLetivo.class, periodoLetivo.getClass());
  }

  @Test
  public void deveCriarPeriodoLetivoDoPrimeiroSemestre() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "10/02/2026", "30/06/2026");

    Assertions.assertEquals("2026.1", periodoLetivo.getPeriodo());
    Assertions.assertEquals("10/02/2026", periodoLetivo.getDataInicio());
    Assertions.assertEquals("30/06/2026", periodoLetivo.getDataFim());
  }

  @Test
  public void deveCriarPeriodoLetivoDoSegundoSemestre() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026");

    Assertions.assertEquals("2026.2", periodoLetivo.getPeriodo());
    Assertions.assertEquals("01/08/2026", periodoLetivo.getDataInicio());
    Assertions.assertEquals("20/12/2026", periodoLetivo.getDataFim());
  }

  @Test
  public void deveLancarExceptionEmConstrutorComTresDadosVazios() {
    Assertions.assertThrows(EntradaInvalidaException.class, () -> new PeriodoLetivo("", "", ""));
  }

  @Test
  public void deveLancarExceptionEmConstrutorComDoisDadosVazios() {
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> new PeriodoLetivo("2026.1", "", ""));
  }

  @Test
  public void deveLancarExceptionEmConstrutorComUmDadoVazio() {
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> new PeriodoLetivo("2026.1", "02/02/2026", ""));
  }

  @Test
  public void deveRetornarPeriodoLetivoAtivo() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    Assertions.assertFalse(periodoLetivo.getPeriodoAtivo());
  }

  @Test
  public void deveAlterarPeriodo() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    periodoLetivo.setPeriodo("2026.2");

    Assertions.assertEquals("2026.2", periodoLetivo.getPeriodo());
  }

  @Test
  public void deveLancarExceptionEmSetPeriodoDadoVazio() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setPeriodo(""));
  }

  @Test
  public void deveLancarExceptionEmSetPeriodoDadoErrado() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setPeriodo("lasd"));
  }

  @Test
  public void deveLancarExceptionEmSetPeriodoComSemestreInvalido() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> periodoLetivo.setPeriodo("2026.3"));
  }

  @Test
  public void deveAlterarDataInicio() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    periodoLetivo.setDataInicio("03/02/2026");

    Assertions.assertEquals("03/02/2026", periodoLetivo.getDataInicio());
  }

  @Test
  public void deveLancarExceptionEmSetDataInicioComDadoVazio() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setDataInicio(""));
  }

  @Test
  public void deveLancarExceptionEmSetDataInicioComDadoErrado() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> periodoLetivo.setDataInicio("asdasda"));
  }

  @Test
  public void deveAlterarDataFim() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    periodoLetivo.setDataFim("20/06/2026");

    Assertions.assertEquals("20/06/2026", periodoLetivo.getDataFim());
  }

  @Test
  public void deveLancarExceptionEmSetDataFimComDadoVazio() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setDataFim(""));
  }

  @Test
  public void deveLancarExceptionEmSetDataFimComDadoErrado() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> periodoLetivo.setDataFim("asdasd"));
  }

  @Test
  public void deveAlterarPeriodoLetivoAtivo() {
    PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "30/06/2026");

    periodoLetivo.setPeriodoAtivo(true);

    Assertions.assertTrue(periodoLetivo.getPeriodoAtivo());
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
}
