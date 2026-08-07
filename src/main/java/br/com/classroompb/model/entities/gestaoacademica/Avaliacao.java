package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

/**
 * Representa uma avaliacao cadastrada em um diario de turma.
 */
public class Avaliacao {

  private String codigo;
  private String codigoDiario;
  private String descricao;
  private double peso;
  private int etapa; // Ex: 1 para N1, 2 para N2
  private double notaMaxima;

  public Avaliacao() {}

  public Avaliacao(
      String codigoDiario, String descricao, double peso, int etapa, double notaMaxima) {
    setCodigoDiario(codigoDiario);
    setDescricao(descricao);
    setPeso(peso);
    setEtapa(etapa);
    setNotaMaxima(notaMaxima);
  }

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

  public void setCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new EntradaInvalidaException("Código da avaliação não pode ser vazio.");
    }
    this.codigo = codigo;
  }

  public String getCodigoDiario() {
    return codigoDiario;
  }

  public void setCodigoDiario(String codigoDiario) {
    if (codigoDiario == null || codigoDiario.isBlank()) {
      throw new EntradaInvalidaException("Código do diário não pode ser vazio.");
    }
    this.codigoDiario = codigoDiario;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    if (descricao == null || descricao.isBlank()) {
      throw new EntradaInvalidaException("Descrição da avaliação não pode ser vazia.");
    }
    this.descricao = descricao;
  }

  public double getPeso() {
    return peso;
  }

  public void setPeso(double peso) {
    if (peso <= 0) {
      throw new EntradaInvalidaException("Peso da avaliação deve ser maior que zero.");
    }
    this.peso = peso;
  }

  public int getEtapa() {
    return etapa;
  }

  public void setEtapa(int etapa) {
    if (etapa != 1 && etapa != 2) {
      throw new EntradaInvalidaException("Etapa da avaliação deve ser 1 ou 2.");
    }
    this.etapa = etapa;
  }

  public double getNotaMaxima() {
    return notaMaxima;
  }

  public void setNotaMaxima(double notaMaxima) {
    if (notaMaxima <= 0 || notaMaxima > 10.0) {
      throw new EntradaInvalidaException("Nota máxima deve estar entre 0.1 e 10.0.");
    }
    this.notaMaxima = notaMaxima;
  }
}
