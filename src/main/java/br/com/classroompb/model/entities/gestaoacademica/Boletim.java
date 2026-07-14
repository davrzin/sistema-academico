package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

/**
 * Representa o boletim de um aluno em uma turma.
 */
public class Boletim {

  private String idBoletim;
  private String matriculaAluno;
  private String codigoTurma;
  private Float primeiraNota;
  private Float segundaNota;
  private float mediaFinal; // REQUISITO DA TASK 2461: Campo para persistência atômica da média
  private Double frequencia;
  private String situacao; // REQUISITO DA TASK 2465: Campo para persistência da situação final

  /**
   * Cria um boletim vazio.
   */
  public Boletim() {}

  /**
   * Cria um boletim para aluno e turma.
   *
   * @param matriculaAluno matricula do aluno.
   * @param codigoTurma codigo da turma.
   */
  public Boletim(String matriculaAluno, String codigoTurma) {
    setMatriculaAluno(matriculaAluno);
    setCodigoTurma(codigoTurma);
    this.situacao = "EM_ANDAMENTO";
  }

  /**
   * Retorna o identificador do boletim.
   *
   * @return identificador do boletim.
   */
  public String getIdBoletim() {
    return idBoletim;
  }

  /**
   * Define o identificador do boletim.
   *
   * @param idBoletim identificador do boletim.
   */
  public void setIdBoletim(String idBoletim) {
    this.idBoletim = idBoletim;
  }

  /**
   * Retorna a matricula do aluno.
   *
   * @return matricula do aluno.
   */
  public String getMatriculaAluno() {
    return matriculaAluno;
  }

  /**
   * Define a matricula do aluno.
   *
   * @param matriculaAluno matricula do aluno.
   */
  public void setMatriculaAluno(String matriculaAluno) {
    validarMatriculaAluno(matriculaAluno);
    this.matriculaAluno = matriculaAluno;
  }

  /**
   * Retorna o codigo da turma.
   *
   * @return codigo da turma.
   */
  public String getCodigoTurma() {
    return codigoTurma;
  }

  /**
   * Define o codigo da turma.
   *
   * @param codigoTurma codigo da turma.
   */
  public void setCodigoTurma(String codigoTurma) {
    validarCodigoTurma(codigoTurma);
    this.codigoTurma = codigoTurma;
  }

  /**
   * Retorna a primeira nota.
   *
   * @return primeira nota.
   */
  public Float getPrimeiraNota() {
    return primeiraNota;
  }

  /**
   * Define a primeira nota.
   *
   * @param primeiraNota primeira nota.
   */
  public void setPrimeiraNota(Float primeiraNota) {
    validarNota(primeiraNota);
    this.primeiraNota = primeiraNota;
    atualizarSituacaoFinal();
  }

  /**
   * Retorna a segunda nota.
   *
   * @return segunda nota.
   */
  public Float getSegundaNota() {
    return segundaNota;
  }

  /**
   * Define a segunda nota.
   *
   * @param segundaNota segunda nota.
   */
  public void setSegundaNota(Float segundaNota) {
    validarNota(segundaNota);
    this.segundaNota = segundaNota;
    atualizarSituacaoFinal();
  }

  /**
   * Calcula a media final a partir das notas atuais.
   *
   * @return media aritmetica das duas notas.
   */
  public Float calcularMediaFinal() {
    if (!possuiTodasAsNotas()) {
      return null;
    }
    return (primeiraNota + segundaNota) / 2.0f;
  }

  /**
   * Retorna a média final.
   *
   * @return média final.
   */
  public float getMediaFinal() {
    return mediaFinal;
  }

  /**
   * Define a média final.
   *
   * @param mediaFinal média final.
   */
  public void setMediaFinal(float mediaFinal) {
    if (mediaFinal < 0 || mediaFinal > 10.0) {
      throw new EntradaInvalidaException("A média final deve estar entre 0 e 10.");
    }
    this.mediaFinal = mediaFinal;
    atualizarSituacaoFinal();
  }

  /** Retorna se a primeira nota foi lancada. */
  public boolean possuiPrimeiraNota() {
    return primeiraNota != null;
  }

  /** Retorna se a segunda nota foi lancada. */
  public boolean possuiSegundaNota() {
    return segundaNota != null;
  }

  /** Retorna se todas as notas foram lancadas. */
  public boolean possuiTodasAsNotas() {
    return possuiPrimeiraNota() && possuiSegundaNota();
  }

  /** Retorna se a frequencia foi calculada. */
  public boolean possuiFrequenciaCalculada() {
    return frequencia != null;
  }

  /**
   * Retorna a frequencia.
   *
   * @return frequencia.
   */
  public Double getFrequencia() {
    return frequencia;
  }

  /**
   * Define a frequencia.
   *
   * @param frequencia frequencia.
   */
  public void setFrequencia(Double frequencia) {
    validarFrequencia(frequencia);
    this.frequencia = frequencia;
    atualizarSituacaoFinal();
  }

  /**
   * Retorna a situação final do aluno.
   *
   * @return situação final.
   */
  public String getSituacao() {
    return situacao;
  }

  /**
   * Define a situação final do aluno.
   *
   * @param situacao situação final.
   */
  public void setSituacao(String situacao) {
    this.situacao = situacao;
  }

  /**
   * REQUISITO TASK 2465: Centraliza as regras de aprovação e reprovação regulatórias.
   */
  private void atualizarSituacaoFinal() {
    if (this.frequencia != null && this.frequencia < 75.0) {
      this.situacao = "REPROVADO_POR_FALTA";
    } else if (this.mediaFinal >= 7.0f) {
      this.situacao = "APROVADO";
    } else {
      this.situacao = "REPROVADO_POR_MEDIA";
    }
  }

  /**
   * Calcula a frequencia do aluno.
   *
   * @param quantidadeDeFaltas quantidade de faltas.
   * @param quantidadeDeAulas quantidade de aulas.
   * @param cargaHorariaTotal carga horaria total.
   * @return frequencia calculada.
   */
  public Double calcularFrequencia(
      int quantidadeDeFaltas, int quantidadeDeAulas, int cargaHorariaTotal) {
    validarQuantidadeDeAulasMinistradas(quantidadeDeAulas, cargaHorariaTotal);

    if (quantidadeDeAulas == 0) {
      setFrequencia(null);
      return null;
    }

    double freqCalculada = (double) ((quantidadeDeFaltas) * 100) / quantidadeDeAulas;

    setFrequencia(100.00 - freqCalculada);
    return 100.0 - freqCalculada;
  }

  private void validarMatriculaAluno(String matriculaAluno) {
    if (matriculaAluno == null || matriculaAluno.isBlank()) {
      throw new EntradaInvalidaException("Aluno não pode ser null.");
    }
  }

  private void validarCodigoTurma(String codigoTurma) {
    if (codigoTurma == null || codigoTurma.isBlank()) {
      throw new EntradaInvalidaException("Disciplina não pode ser null.");
    }
  }

  private void validarFrequencia(Double frequencia) {
    if (frequencia != null
        && (!Double.isFinite(frequencia) || frequencia < 0 || frequencia > 100.0)) {
      throw new EntradaInvalidaException("A frequência deve estar entre 0 e 100");
    }
  }

  private void validarNota(Float nota) {
    if (nota != null && (!Float.isFinite(nota) || nota < 0 || nota > 10.0)) {
      throw new EntradaInvalidaException("A nota deve entrar entre 0 e 10.");
    }
  }

  private void validarQuantidadeDeAulasMinistradas(int quantidadeDeAulas, int cargaHorariaTotal) {
    if (cargaHorariaTotal < 30 || cargaHorariaTotal > 90) {
      throw new EntradaInvalidaException("Carga horária total deve estar entre 30 e 90");
    }
    if (quantidadeDeAulas < 0 || quantidadeDeAulas > cargaHorariaTotal) {
      throw new EntradaInvalidaException("Quantidade de aulas inválida.");
    }
  }

  /**
   * Retorna a representacao textual do boletim.
   *
   * @return texto do boletim.
   */
  @Override
  public String toString() {
    return "Boletim{"
        + "idBoletim='" + idBoletim + '\''
        + ", matriculaAluno='" + matriculaAluno + '\''
        + ", codigoTurma='" + codigoTurma + '\''
        + ", primeiraNota=" + primeiraNota
        + ", segundaNota=" + segundaNota
        + ", mediaFinal=" + mediaFinal
        + ", frequencia=" + frequencia
        + ", situacao='" + situacao + '\''
        + '}';
  }
}