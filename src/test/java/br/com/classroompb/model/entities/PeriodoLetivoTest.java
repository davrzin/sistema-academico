package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PeriodoLetivoTest {


    @Test
    public void deveCriarUmObjetoPeriodoLetivo(){

        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        Assertions.assertEquals(PeriodoLetivo.class, periodoLetivo.getClass());
    }

    @Test
    public void deveLancarExceptionEmConstrutorComTresDadosVazios(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new PeriodoLetivo("", "", ""));
    }

    @Test
    public void deveLancarExceptionEmConstrutorComDoisDadosVazios(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new PeriodoLetivo("2026.1", "", ""));
    }

    @Test
    public void deveLancarExceptionEmConstrutorComUmDadoVazio(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new PeriodoLetivo("2026.1", "02/02/2026", ""));
    }


    @Test
    public void deveRetornarPeriodo(){
        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        Assertions.assertEquals("2026.1", periodoLetivo.getPeriodo());
    }

    @Test
    public void deveRetornarDataInicio(){
        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        Assertions.assertEquals("02/02/2026", periodoLetivo.getDataInicio());
    }

    @Test
    public void deveRetornarDataFim(){
        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        Assertions.assertEquals("15/09/2026", periodoLetivo.getDataFim());
    }

    @Test
    public void deveRetornarPeriodoLetivoAtivo(){
        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        Assertions.assertFalse(periodoLetivo.getPeriodoAtivo());
    }

    @Test
    public void deveAlterarPeriodo(){
        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        periodoLetivo.setPeriodo("2026.2");

        Assertions.assertEquals("2026.2", periodoLetivo.getPeriodo());
    }

    @Test
    public void deveLancarExceptionEmSetPeriodoDadoVazio(){

        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");


        Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setPeriodo(""));
    }

    @Test
    public void deveLancarExceptionEmSetPeriodoDadoErrado(){

        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");


        Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setPeriodo("lasd"));
    }

    @Test
    public void deveAlterarDataInicio(){
        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        periodoLetivo.setDataInicio("03/02/2026");

        Assertions.assertEquals("03/02/2026", periodoLetivo.getDataInicio());

    }

    @Test
    public void deveLancarExceptionEmSetDataInicioComDadoVazio(){

        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");


        Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setDataInicio(""));
    }

    @Test
    public void deveLancarExceptionEmSetDataInicioComDadoErrado(){

        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");


        Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setDataInicio("asdasda"));
    }

    @Test
    public void deveAlterarDataFim(){
        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        periodoLetivo.setDataInicio("16/09/2026");

        Assertions.assertEquals("16/09/2026", periodoLetivo.getDataInicio());
    }

    @Test
    public void deveLancarExceptionEmSetDataFimComDadoVazio(){

        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");


        Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setDataFim(""));
    }

    @Test
    public void deveLancarExceptionEmSetDataFimComDadoErrado(){

        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");


        Assertions.assertThrows(EntradaInvalidaException.class, () -> periodoLetivo.setDataFim("asdasd"));
    }

    @Test
    public void deveAlterarPeriodoLetivoAtivo(){
        PeriodoLetivo periodoLetivo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        periodoLetivo.setPeriodoAtivo(true);

        Assertions.assertTrue(periodoLetivo.getPeriodoAtivo());
    }


}
