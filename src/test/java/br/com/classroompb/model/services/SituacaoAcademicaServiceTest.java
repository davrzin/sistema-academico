package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.enums.SituacaoAcademica;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Testes da regra central de situacao academica. */
public class SituacaoAcademicaServiceTest {

  private SituacaoAcademicaService service;
  private Boletim boletim;

  /** Prepara um boletim para cada classificacao. */
  @BeforeEach
  public void preparar() {
    service = new SituacaoAcademicaService();
    boletim = new Boletim("al00", "tur00");
  }

  @Test
  public void deveFicarEmAndamentoComNotaPendente() {
    boletim.setPrimeiraNota(8.0f);
    boletim.setFrequencia(80.0);

    Assertions.assertEquals(SituacaoAcademica.EM_ANDAMENTO, service.determinar(boletim));
  }

  @Test
  public void deveFicarEmAndamentoComFrequenciaPendente() {
    definirNotas(8.0f, 6.0f);

    Assertions.assertEquals(SituacaoAcademica.EM_ANDAMENTO, service.determinar(boletim));
  }

  @Test
  public void deveAprovarNosLimitesMinimos() {
    definirNotas(7.0f, 7.0f);
    boletim.setFrequencia(75.0);

    Assertions.assertEquals(SituacaoAcademica.APROVADO, service.determinar(boletim));
  }

  @Test
  public void deveColocarEmRecuperacaoComMediaQuatro() {
    definirNotas(4.0f, 4.0f);
    boletim.setFrequencia(75.0);

    Assertions.assertEquals(SituacaoAcademica.EM_RECUPERACAO, service.determinar(boletim));
  }

  @Test
  public void deveColocarEmRecuperacaoComMediaIntermediaria() {
    definirNotas(5.0f, 6.0f);
    boletim.setFrequencia(80.0);

    Assertions.assertEquals(SituacaoAcademica.EM_RECUPERACAO, service.determinar(boletim));
  }

  @Test
  public void deveReprovarPorNotaComMediaAbaixoDeQuatro() {
    definirNotas(3.0f, 4.0f);
    boletim.setFrequencia(80.0);

    Assertions.assertEquals(SituacaoAcademica.REPROVADO_POR_NOTA, service.determinar(boletim));
  }

  @Test
  public void devePriorizarReprovacaoPorFaltaMesmoComMediaAlta() {
    definirNotas(10.0f, 10.0f);
    boletim.setFrequencia(74.99);

    Assertions.assertEquals(SituacaoAcademica.REPROVADO_POR_FALTA, service.determinar(boletim));
  }

  private void definirNotas(float primeiraNota, float segundaNota) {
    boletim.setPrimeiraNota(primeiraNota);
    boletim.setSegundaNota(segundaNota);
  }
}
