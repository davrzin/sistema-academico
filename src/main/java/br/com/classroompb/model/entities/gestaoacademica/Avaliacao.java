package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

/**
 * Representa uma avaliacao cadastrada em um diario de turma.
 */
public class Avaliacao {

  public static final double PESO_PADRAO = 1.0;
  public static final double NOTA_MAXIMA_PADRAO = 10.0;

  private String codigo;
  private String codigoDiario;
  private String descricao;
  private double peso;
  private int etapa; // Ex: 1 para N1, 2 para N2
  private double notaMaxima;

  /**
   * Construtor padrão.
   */
  public Avaliacao() {}

  /**
   * Cria uma avaliação sem código previamente definido.
   *
   * @param codigoDiario código do diário ao qual a avaliação pertence
   * @param descricao descrição da avaliação
   * @param peso peso da avaliação
   * @param etapa etapa da avaliação (1 ou 2)
   * @param notaMaxima nota máxima permitida para a avaliação
   */
  public Avaliacao(
      String codigoDiario, String descricao, double peso, int etapa, double notaMaxima) {
    setCodigoDiario(codigoDiario);
    setDescricao(descricao);
    setPeso(peso);
    setEtapa(etapa);
    setNotaMaxima(notaMaxima);
  }

  /**
   * Cria uma avaliação com código previamente definido.
   *
   * @param codigo código da avaliação
   * @param codigoDiario código do diário ao qual a avaliação pertence
   * @param descricao descrição da avaliação
   * @param peso peso da avaliação
   * @param etapa etapa da avaliação (1 ou 2)
   * @param notaMaxima nota máxima permitida para a avaliação
   */
  public Avaliacao(
      String codigo,
      String codigoDiario,
      String descricao,
      double peso,
      int etapa,
      double notaMaxima) {
    this(codigoDiario, descricao, peso, etapa, notaMaxima);
    setCodigo(codigo);
  }

  public String getCodigo() {
    return codigo;
  }

  /**
   * Define o código da avaliação.
   *
   * @param codigo código da avaliação
   */
  public void setCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new EntradaInvalidaException("Código da avaliação não pode ser vazio.");
    }
    this.codigo = codigo;
  }

  public String getCodigoDiario() {
    return codigoDiario;
  }

  /**
   * Define o código do diário associado à avaliação.
   *
   * @param codigoDiario código do diário
   */
  public void setCodigoDiario(String codigoDiario) {
    if (codigoDiario == null || codigoDiario.isBlank()) {
      throw new EntradaInvalidaException("Código do diário não pode ser vazio.");
    }
    this.codigoDiario = codigoDiario;
  }

  public String getDescricao() {
    return descricao;
  }

  /**
   * Define a descrição da avaliação.
   *
   * @param descricao descrição da avaliação
   */
  public void setDescricao(String descricao) {
    if (descricao == null || descricao.isBlank()) {
      throw new EntradaInvalidaException("Descrição da avaliação não pode ser vazia.");
    }
    this.descricao = descricao;
  }

  public double getPeso() {
    return peso;
  }

  /**
   * Define o peso da avaliação.
   *
   * @param peso peso da avaliação, deve ser maior que zero
   */
  public void setPeso(double peso) {
    if (peso <= 0) {
      throw new EntradaInvalidaException("Peso da avaliação deve ser maior que zero.");
    }
    this.peso = peso;
  }

  public int getEtapa() {
    return etapa;
  }

  /**
   * Define a etapa da avaliação.
   *
   * @param etapa etapa da avaliação, deve ser 1 ou 2
   */
  public void setEtapa(int etapa) {
    if (etapa != 1 && etapa != 2) {
      throw new EntradaInvalidaException("Etapa da avaliação deve ser 1 ou 2.");
    }
    this.etapa = etapa;
  }

  public double getNotaMaxima() {
    return notaMaxima;
  }

  /**
   * Define a nota máxima permitida para a avaliação.
   *
   * @param notaMaxima nota máxima, deve estar entre 0.1 e 10.0
   */
  public void setNotaMaxima(double notaMaxima) {
    if (notaMaxima <= 0 || notaMaxima > 10.0) {
      throw new EntradaInvalidaException("Nota máxima deve estar entre 0.1 e 10.0.");
    }
    this.notaMaxima = notaMaxima;
  }
}
