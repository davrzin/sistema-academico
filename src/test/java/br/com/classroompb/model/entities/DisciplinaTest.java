package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testes da entidade Disciplina.
 */
public class DisciplinaTest {

  private static final String NOME_VALIDO = "Estrutura de Dados";
  private static final int CARGA_VALIDA = 60;
  private static final int PERIODO_VALIDO = 3;
  private static final int CREDITOS_VALIDOS = 4;
  private static final String COD_CURSO_VALIDO = "cur01";
  private static final List<String> PRE_REQ_VAZIO = new ArrayList<>();

  @Test
  public void deveCriarDisciplinaComConstrutorVazio() {
    Disciplina disciplina = new Disciplina();
    Assertions.assertEquals(Disciplina.class, disciplina.getClass());
  }

  @Test
  public void deveCriarDisciplinaComSeisParametros() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertEquals(Disciplina.class, disciplina.getClass());
  }

  @Test
  public void deveCriarDisciplinaComSeteParametros() {
    Disciplina disciplina =
        new Disciplina(
            "DIS001",
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertEquals(Disciplina.class, disciplina.getClass());
  }

  @Test
  public void deveCriarDisciplinaComPreRequisitosPreenchidos() {
    List<String> preReqs = List.of("dis01", "dis02");
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO, CARGA_VALIDA, PERIODO_VALIDO, CREDITOS_VALIDOS, COD_CURSO_VALIDO, preReqs);
    Assertions.assertEquals(preReqs, disciplina.getPreRequisitos());
  }

  @Test
  public void deveLancarExceptionNoConstrutorQuandoNomeVazio() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            new Disciplina(
                "",
                CARGA_VALIDA,
                PERIODO_VALIDO,
                CREDITOS_VALIDOS,
                COD_CURSO_VALIDO,
                PRE_REQ_VAZIO));
  }

  @Test
  public void deveLancarExceptionNoConstrutorQuandoCargaHorariaZero() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            new Disciplina(
                NOME_VALIDO, 0, PERIODO_VALIDO, CREDITOS_VALIDOS, COD_CURSO_VALIDO, PRE_REQ_VAZIO));
  }

  @Test
  public void deveLancarExceptionNoConstrutorQuandoPeriodoZero() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            new Disciplina(
                NOME_VALIDO, CARGA_VALIDA, 0, CREDITOS_VALIDOS, COD_CURSO_VALIDO, PRE_REQ_VAZIO));
  }

  @Test
  public void deveLancarExceptionNoConstrutorQuandoCreditosZero() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            new Disciplina(
                NOME_VALIDO, CARGA_VALIDA, PERIODO_VALIDO, 0, COD_CURSO_VALIDO, PRE_REQ_VAZIO));
  }

  @Test
  public void deveLancarExceptionNoConstrutorQuandoCodigoCursoVazio() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            new Disciplina(
                NOME_VALIDO, CARGA_VALIDA, PERIODO_VALIDO, CREDITOS_VALIDOS, "", PRE_REQ_VAZIO));
  }

  @Test
  public void deveRetornarCodigo() {
    Disciplina disciplina =
        new Disciplina(
            "DIS001",
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertEquals("DIS001", disciplina.getCodigo());
  }

  @Test
  public void deveAlterarCodigo() {
    Disciplina disciplina =
        new Disciplina(
            "DIS001",
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setCodigo("DIS002");
    Assertions.assertEquals("DIS002", disciplina.getCodigo());
  }

  @Test
  public void deveLancarExceptionEmSetCodigoVazio() {
    Disciplina disciplina =
        new Disciplina(
            "DIS001",
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCodigo(""));
  }

  @Test
  public void deveLancarExceptionEmSetCodigoNulo() {
    Disciplina disciplina =
        new Disciplina(
            "DIS001",
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCodigo(null));
  }

  @Test
  public void deveRetornarNome() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertEquals(NOME_VALIDO, disciplina.getNome());
  }

  @Test
  public void deveAlterarNome() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setNome("Algoritmos e Programação");
    Assertions.assertEquals("Algoritmos e Programação", disciplina.getNome());
  }

  @Test
  public void deveLancarExceptionEmSetNomeVazio() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setNome(""));
  }

  @Test
  public void deveLancarExceptionEmSetNomeNulo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setNome(null));
  }

  @Test
  public void deveLancarExceptionEmSetNomeMuitoCurto() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setNome("AB"));
  }

  @Test
  public void deveLancarExceptionEmSetNomeMuitoLongo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> disciplina.setNome("A".repeat(201)));
  }

  @Test
  public void deveRetornarCargaHoraria() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertEquals(CARGA_VALIDA, disciplina.getCargaHoraria());
  }

  @Test
  public void deveAlterarCargaHoraria() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setCargaHoraria(80);
    Assertions.assertEquals(80, disciplina.getCargaHoraria());
  }

  @Test
  public void deveLancarExceptionEmSetCargaHorariaZero() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCargaHoraria(0));
  }

  @Test
  public void deveLancarExceptionEmSetCargaHorariaNegativa() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCargaHoraria(-10));
  }

  @Test
  public void deveLancarExceptionEmSetCargaHorariaAcimaDoLimite() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCargaHoraria(301));
  }

  @Test
  public void deveAceitarCargaHorariaNoLimiteMaximo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setCargaHoraria(300);
    Assertions.assertEquals(300, disciplina.getCargaHoraria());
  }

  @Test
  public void deveRetornarPeriodo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertEquals(PERIODO_VALIDO, disciplina.getPeriodo());
  }

  @Test
  public void deveAlterarPeriodo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setPeriodo(5);
    Assertions.assertEquals(5, disciplina.getPeriodo());
  }

  @Test
  public void deveLancarExceptionEmSetPeriodoZero() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setPeriodo(0));
  }

  @Test
  public void deveLancarExceptionEmSetPeriodoNegativo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setPeriodo(-1));
  }

  @Test
  public void deveLancarExceptionEmSetPeriodoAcimaDoLimite() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setPeriodo(13));
  }

  @Test
  public void deveAceitarPeriodoNoLimiteMaximo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setPeriodo(12);
    Assertions.assertEquals(12, disciplina.getPeriodo());
  }

  @Test
  public void deveRetornarCreditos() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertEquals(CREDITOS_VALIDOS, disciplina.getCreditos());
  }

  @Test
  public void deveAlterarCreditos() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setCreditos(6);
    Assertions.assertEquals(6, disciplina.getCreditos());
  }

  @Test
  public void deveLancarExceptionEmSetCreditosZero() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCreditos(0));
  }

  @Test
  public void deveLancarExceptionEmSetCreditosNegativo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCreditos(-1));
  }

  @Test
  public void deveLancarExceptionEmSetCreditosAcimaDoLimite() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCreditos(31));
  }

  @Test
  public void deveAceitarCreditosNoLimiteMaximo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setCreditos(30);
    Assertions.assertEquals(30, disciplina.getCreditos());
  }

  @Test
  public void deveRetornarCodigoCurso() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertEquals(COD_CURSO_VALIDO, disciplina.getCodigoCurso());
  }

  @Test
  public void deveAlterarCodigoCurso() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setCodigoCurso("cur99");
    Assertions.assertEquals("cur99", disciplina.getCodigoCurso());
  }

  @Test
  public void deveLancarExceptionEmSetCodigoCursoVazio() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCodigoCurso(""));
  }

  @Test
  public void deveLancarExceptionEmSetCodigoCursoNulo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(EntradaInvalidaException.class, () -> disciplina.setCodigoCurso(null));
  }

  @Test
  public void deveLancarExceptionEmSetCodigoCursoFormatoInvalido() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> disciplina.setCodigoCurso("ESW001"));
  }

  @Test
  public void deveLancarExceptionEmSetCodigoCursoComDigitoInsuficiente() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    // Precisa de pelo menos 2 dígitos após "cur"
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> disciplina.setCodigoCurso("cur1"));
  }

  @Test
  public void deveRetornarPreRequisitosVazios() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertTrue(disciplina.getPreRequisitos().isEmpty());
  }

  @Test
  public void deveAlterarPreRequisitos() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    List<String> novosPreReqs = List.of("dis01", "dis02");
    disciplina.setPreRequisitos(novosPreReqs);
    Assertions.assertEquals(novosPreReqs, disciplina.getPreRequisitos());
  }

  @Test
  public void deveAceitarPreRequisitosNulos() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    disciplina.setPreRequisitos(null);
    Assertions.assertNotNull(disciplina.getPreRequisitos());
    Assertions.assertTrue(disciplina.getPreRequisitos().isEmpty());
  }

  @Test
  public void deveLancarExceptionEmSetPreRequisitosComItemVazio() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    List<String> preReqsInvalidos = new ArrayList<>();
    preReqsInvalidos.add("");
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> disciplina.setPreRequisitos(preReqsInvalidos));
  }

  @Test
  public void deveLancarExceptionEmSetPreRequisitosComItemNulo() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    List<String> preReqsInvalidos = new ArrayList<>();
    preReqsInvalidos.add(null);
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> disciplina.setPreRequisitos(preReqsInvalidos));
  }

  @Test
  public void deveValidarDadosBasicosComDadosValidos() {
    Disciplina disciplina =
        new Disciplina(
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertDoesNotThrow(disciplina::validarDadosBasicos);
  }

  @Test
  public void deveRetornarToStringNaoNulo() {
    Disciplina disciplina =
        new Disciplina(
            "DIS001",
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertNotNull(disciplina.toString());
  }

  @Test
  public void deveConterNomeNoToString() {
    Disciplina disciplina =
        new Disciplina(
            "DIS001",
            NOME_VALIDO,
            CARGA_VALIDA,
            PERIODO_VALIDO,
            CREDITOS_VALIDOS,
            COD_CURSO_VALIDO,
            PRE_REQ_VAZIO);
    Assertions.assertTrue(disciplina.toString().contains(NOME_VALIDO));
  }
}
