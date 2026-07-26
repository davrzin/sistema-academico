package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes da entidade Diario.
 */
public class DiarioTest {

  private Diario diario;

  /**
   * Prepara um diario para os testes.
   */
  @BeforeEach
  public void inicializarDiario() {
    diario =
        new Diario(
            "tur00", "Diário de aulas teóricas", "pr00", "SEG 08:00-10:00", "B-109", 60);
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCriarDiarioComDadosInvalidos() {

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> new Diario(null, null, null, null, null, -9));
  }

  @Test
  public void deveIniciarComSituacaoAtiva() {

    Assertions.assertEquals(SituacaoDiario.ATIVO, diario.getSituacao());
  }

  @Test
  public void deveRetornarCodigoTurma() {

    Assertions.assertEquals("tur00", diario.getCodigoTurma());
  }

  @Test
  public void deveRetornarDescricao() {

    Assertions.assertNotNull(diario.getDescricao());
  }

  @Test
  public void deveRetornarMatriculaProfessor() {

    Assertions.assertEquals("pr00", diario.getMatriculaProfessor());
  }

  @Test
  public void deveRetornarHorario() {

    Assertions.assertNotNull(diario.getHorario());
  }

  @Test
  public void deveRetornarSala() {

    Assertions.assertNotNull(diario.getSala());
  }

  @Test
  public void deveRetornarCargaHoraria() {

    Assertions.assertEquals(60, diario.getCargaHoraria());
  }

  @Test
  public void deveAtualizarCodigoTurma() {
    diario.setCodigoTurma("tur01");

    Assertions.assertEquals("tur01", diario.getCodigoTurma());
  }

  @Test
  public void deveAtualizarDescricao() {
    diario.setDescricao("Diário de aulas práticas");

    Assertions.assertEquals("Diário de aulas práticas", diario.getDescricao());
  }

  @Test
  public void deveAtualizarMatriculaProfessor() {
    diario.setMatriculaProfessor("pr01");

    Assertions.assertEquals("pr01", diario.getMatriculaProfessor());
  }

  @Test
  public void deveAtualizarHorario() {
    diario.setHorario("TER 10:00-12:00");

    Assertions.assertEquals("TER 10:00-12:00", diario.getHorario());
  }

  @Test
  public void deveAtualizarSala() {
    diario.setSala("C-206");

    Assertions.assertEquals("C-206", diario.getSala());
  }

  @Test
  public void deveAtualizarCargaHoraria() {
    diario.setCargaHoraria(80);

    Assertions.assertEquals(80, diario.getCargaHoraria());
  }

  @Test
  public void deveAtualizarSituacao() {
    diario.setSituacao(SituacaoDiario.ENCERRADO);

    Assertions.assertEquals(SituacaoDiario.ENCERRADO, diario.getSituacao());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirSituacaoNula() {

    Assertions.assertThrows(EntradaInvalidaException.class, () -> diario.setSituacao(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirCargaHorariaInvalida() {

    Assertions.assertThrows(EntradaInvalidaException.class, () -> diario.setCargaHoraria(0));
  }

  @Test
  public void naoDeveLancarExcecaoAoValidarDadosBasicosValidos() {

    Assertions.assertDoesNotThrow(() -> diario.validarDadosBasicos());
  }
}
