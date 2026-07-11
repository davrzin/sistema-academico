package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.enums.SituacaoAcademica;

/** Centraliza a classificacao da situacao academica de um boletim. */
public class SituacaoAcademicaService {

  private static final double FREQUENCIA_MINIMA = 75.0;
  private static final float MEDIA_APROVACAO = 7.0f;
  private static final float MEDIA_RECUPERACAO = 4.0f;

  /** Determina a situacao atual do aluno no boletim informado. */
  public SituacaoAcademica determinar(Boletim boletim) {
    if (boletim == null
        || !boletim.possuiTodasAsNotas()
        || !boletim.possuiFrequenciaCalculada()) {
      return SituacaoAcademica.EM_ANDAMENTO;
    }

    if (boletim.getFrequencia() < FREQUENCIA_MINIMA) {
      return SituacaoAcademica.REPROVADO_POR_FALTA;
    }

    float media = boletim.calcularMediaFinal();
    if (media >= MEDIA_APROVACAO) {
      return SituacaoAcademica.APROVADO;
    }
    if (media >= MEDIA_RECUPERACAO) {
      return SituacaoAcademica.EM_RECUPERACAO;
    }
    return SituacaoAcademica.REPROVADO_POR_NOTA;
  }
}
