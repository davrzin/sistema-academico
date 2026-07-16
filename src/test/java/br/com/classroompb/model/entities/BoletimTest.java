package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes da entidade Boletim.
 */
public class BoletimTest {

  private Boletim boletimTeste;

  /**
   * Prepara um boletim para os testes.
   */
  @BeforeEach
  public void criarBoletim() {
    boletimTeste = new Boletim("al00", "tur00");
  }

  @Test
  public void deveCriarBoletimComConstrutorVazio() {
    Boletim boletim = new Boletim();

    Assertions.assertNotNull((boletim));
  }

  @Test
  public void deveCriarBoletimComConstrutorPadrao() {
    Boletim boletim = new Boletim("al00", "tur00");

    Assertions.assertNotNull(boletim);
    Assertions.assertNull(boletim.getPrimeiraNota());
    Assertions.assertNull(boletim.getSegundaNota());
    Assertions.assertNull(boletim.getFrequencia());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCriarBoletimComEntradaInvalida() {

    Assertions.assertThrows(EntradaInvalidaException.class, () -> new Boletim(null, null));
  }

  @Test
  public void deveRetonarMatriculaDeAlunoCorretamente() {
    String matriculaEsperada = "al00";

    Assertions.assertEquals(matriculaEsperada, boletimTeste.getMatriculaAluno());
  }

  @Test
  public void deveRetornarCodigoTurmaCorretamente() {
    String codigoEsperado = "tur00";

    Assertions.assertEquals(codigoEsperado, boletimTeste.getCodigoTurma());
  }

  @Test
  public void deveAlterarMatriculaDoAlunoCorretamente() {
    boletimTeste.setMatriculaAluno("al01");

    String matriculaEsperada = "al01";

    Assertions.assertEquals(matriculaEsperada, boletimTeste.getMatriculaAluno());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoAlterarMatriculaAlunoComNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimTeste.setMatriculaAluno(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoAlterarMatriculaAlunoComEntradaVazia() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimTeste.setMatriculaAluno(""));
  }

  @Test
  public void deveAlterarCodigoTurmaCorretamente() {
    boletimTeste.setCodigoTurma("tur01");

    String codigoEsperado = "tur01";

    Assertions.assertEquals(codigoEsperado, boletimTeste.getCodigoTurma());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoAlterarCodigoTurmaComNull() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimTeste.setCodigoTurma(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoAlterarCodigoTurmaComEntradaVazia() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimTeste.setCodigoTurma(null));
  }

  @Test
  public void deveAlterarPrimeiraNotaCorretamente() {
    boletimTeste.setPrimeiraNota(10.0f);

    float notaEsperada = 10;

    Assertions.assertEquals(notaEsperada, boletimTeste.getPrimeiraNota());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptioAoAlterarPrimeiraNotaComNotaInvalida() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimTeste.setPrimeiraNota(20.0f));
  }

  @Test
  public void deveAlterarSegundaNotaCorretamente() {
    boletimTeste.setSegundaNota(10.0f);

    float notaEsperada = 10;

    Assertions.assertEquals(notaEsperada, boletimTeste.getSegundaNota());
  }

  @Test
  public void deveCalcularMediaFinalDiretamenteDasNotas() {
    boletimTeste.setPrimeiraNota(8.0f);
    boletimTeste.setSegundaNota(6.0f);

    Assertions.assertEquals(7.0f, boletimTeste.calcularMediaFinal());
  }

  @Test
  public void deveAtualizarMediaQuandoUmaNotaMudar() {
    boletimTeste.setPrimeiraNota(8.0f);
    boletimTeste.setSegundaNota(6.0f);
    boletimTeste.setPrimeiraNota(10.0f);

    Assertions.assertEquals(8.0f, boletimTeste.calcularMediaFinal());
  }

  @Test
  public void deveManterMediaPendenteEnquantoFaltarNota() {
    boletimTeste.setPrimeiraNota(8.0f);

    Assertions.assertTrue(boletimTeste.possuiPrimeiraNota());
    Assertions.assertFalse(boletimTeste.possuiSegundaNota());
    Assertions.assertFalse(boletimTeste.possuiTodasAsNotas());
    Assertions.assertNull(boletimTeste.calcularMediaFinal());
  }

  @Test
  public void deveDiferenciarZeroDeNotaAusente() {
    boletimTeste.setPrimeiraNota(0.0f);

    Assertions.assertEquals(0.0f, boletimTeste.getPrimeiraNota());
    Assertions.assertTrue(boletimTeste.possuiPrimeiraNota());
    Assertions.assertNull(boletimTeste.getSegundaNota());
  }

  @Test
  public void settersDevemAceitarValoresNulos() {
    boletimTeste.setPrimeiraNota(null);
    boletimTeste.setSegundaNota(null);
    boletimTeste.setFrequencia(null);

    Assertions.assertNull(boletimTeste.getPrimeiraNota());
    Assertions.assertNull(boletimTeste.getSegundaNota());
    Assertions.assertNull(boletimTeste.getFrequencia());
  }

  @Test
  public void settersDevemRejeitarValoresNaoFinitos() {
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimTeste.setPrimeiraNota(Float.NaN));
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimTeste.setSegundaNota(Float.POSITIVE_INFINITY));
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimTeste.setFrequencia(Double.NaN));
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimTeste.setFrequencia(Double.NEGATIVE_INFINITY));
  }

  @Test
  public void naoDevePersistirDadosCalculadosSeparadamente() throws JsonProcessingException {
    boletimTeste.setPrimeiraNota(8.0f);
    boletimTeste.setSegundaNota(6.0f);

    String json = new ObjectMapper().writeValueAsString(boletimTeste);

    Assertions.assertFalse(json.contains("mediaFinal"));
    Assertions.assertFalse(json.contains("situacao"));
  }

  @Test
  public void jsonDeveDiferenciarValoresNulosDeZero() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    boletimTeste.setPrimeiraNota(0.0f);
    String json = mapper.writeValueAsString(boletimTeste);

    Assertions.assertTrue(json.contains("\"primeiraNota\":0.0"));
    Assertions.assertTrue(json.contains("\"segundaNota\":null"));
    Assertions.assertTrue(json.contains("\"frequencia\":null"));

    Boletim restaurado = mapper.readValue(json, Boletim.class);
    Assertions.assertEquals(0.0f, restaurado.getPrimeiraNota());
    Assertions.assertNull(restaurado.getSegundaNota());
    Assertions.assertNull(restaurado.getFrequencia());
  }

  @Test
  public void develancarEntradaInvalidaExceptionAoAlterarSegundaNotaComNotaInvalida() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimTeste.setSegundaNota(42.0f));
  }

  @Test
  public void deveAlterarFrequenciaCorretamente() {
    boletimTeste.setFrequencia(100.0);

    double frequenciaEsperada = 100.0;

    Assertions.assertEquals(frequenciaEsperada, boletimTeste.getFrequencia());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoAlterarFrequenciaComDadoInvalido() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> boletimTeste.setFrequencia(412.0));
  }

  @Test
  public void deveManterFrequenciaPendenteSemNenhumaAula() {
    Assertions.assertNull(boletimTeste.calcularFrequencia(0, 0, 30));
    Assertions.assertFalse(boletimTeste.possuiFrequenciaCalculada());
  }

  @Test
  public void deveDiferenciarFrequenciaZeroDeFrequenciaPendente() {
    boletimTeste.calcularFrequencia(30, 30, 30);

    Assertions.assertEquals(0.0, boletimTeste.getFrequencia());
    Assertions.assertTrue(boletimTeste.possuiFrequenciaCalculada());
  }

  @Test
  public void deveCalcularFrequenciaCorretamente() {
    double frequenciaEsperada = 90.0;

    Assertions.assertEquals(frequenciaEsperada, boletimTeste.calcularFrequencia(3, 30, 30));
  }
}
