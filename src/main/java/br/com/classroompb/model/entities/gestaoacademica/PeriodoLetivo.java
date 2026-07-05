package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Representa um periodo letivo do sistema academico.
 */
public class PeriodoLetivo {

  private static final DateTimeFormatter FORMATADOR_DATA =
      DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

  private String periodo;
  private String dataInicio;
  private String dataFim;
  private boolean periodoAtivo;

  /**
   * Cria um periodo letivo vazio.
   */
  public PeriodoLetivo() {}

  /**
   * Cria um periodo letivo com intervalo de datas.
   *
   * @param periodo identificador do periodo.
   * @param dataInicio data de inicio.
   * @param dataFim data de fim.
   */
  public PeriodoLetivo(String periodo, String dataInicio, String dataFim) {
    setPeriodo(periodo);
    setDataInicio(dataInicio);
    setDataFim(dataFim);
    validarCoerenciaPeriodoDatas();
    this.periodoAtivo = false;
  }

  /**
   * Retorna o identificador do periodo.
   *
   * @return identificador do periodo.
   */
  public String getPeriodo() {
    return periodo;
  }

  /**
   * Define o identificador do periodo.
   *
   * @param periodo identificador do periodo.
   */
  public void setPeriodo(String periodo) {
    validarPeriodo(periodo);
    this.periodo = periodo;
  }

  /**
   * Retorna a data de inicio.
   *
   * @return data de inicio.
   */
  public String getDataInicio() {
    return dataInicio;
  }

  /**
   * Define a data de inicio.
   *
   * @param dataInicio data de inicio.
   */
  public void setDataInicio(String dataInicio) {
    validarData(dataInicio, "Data de inicio invalida.");
    this.dataInicio = dataInicio;
  }

  /**
   * Retorna a data de fim.
   *
   * @return data de fim.
   */
  public String getDataFim() {
    return dataFim;
  }

  /**
   * Define a data de fim.
   *
   * @param dataFim data de fim.
   */
  public void setDataFim(String dataFim) {
    validarData(dataFim, "Data de fim invalida.");
    this.dataFim = dataFim;
  }

  /**
   * Retorna se o periodo esta ativo.
   *
   * @return verdadeiro se o periodo esta ativo.
   */
  public boolean getPeriodoAtivo() {
    return periodoAtivo;
  }

  /**
   * Define se o periodo esta ativo.
   *
   * @param periodoAtivo estado ativo do periodo.
   */
  public void setPeriodoAtivo(boolean periodoAtivo) {
    this.periodoAtivo = periodoAtivo;
  }

  /**
   * Valida os dados basicos do periodo letivo.
   */
  public void validarDadosBasicos() {
    validarPeriodo(periodo);
    validarData(dataInicio, "Data de inicio invalida.");
    validarData(dataFim, "Data de fim invalida.");
    validarCoerenciaPeriodoDatas();
  }

  private void validarPeriodo(String periodo) {
    if (periodo == null || periodo.isBlank()) {
      throw new EntradaInvalidaException("Periodo letivo nao pode ser vazio.");
    }

    if (!periodo.matches("\\d{4}\\.[12]")) {
      throw new EntradaInvalidaException(
          "Formato de periodo invalido. Use o formato 2024.1, por exemplo.");
    }
  }

  private void validarData(String data, String mensagemErro) {
    if (data == null || data.isBlank()) {
      throw new EntradaInvalidaException(mensagemErro);
    }

    try {
      converterData(data);
    } catch (DateTimeParseException e) {
      throw new EntradaInvalidaException("Formato de data invalido. Use o formato dd/MM/yyyy.");
    }
  }

  private void validarCoerenciaPeriodoDatas() {
    LocalDate inicio = converterData(dataInicio);
    LocalDate fim = converterData(dataFim);

    if (!fim.isAfter(inicio)) {
      throw new EntradaInvalidaException("A data de fim deve ser posterior a data de inicio.");
    }

    int anoPeriodo = Integer.parseInt(periodo.substring(0, 4));
    int semestre = Integer.parseInt(periodo.substring(5));

    if (semestre == 1 && !datasPertencemAoSemestre(inicio, fim, anoPeriodo, 1, 6)) {
      throw new EntradaInvalidaException(
          "Datas do primeiro semestre devem estar entre janeiro e junho de " + anoPeriodo + ".");
    }

    if (semestre == 2 && !datasPertencemAoSemestre(inicio, fim, anoPeriodo, 7, 12)) {
      throw new EntradaInvalidaException(
          "Datas do segundo semestre devem estar entre julho e dezembro de " + anoPeriodo + ".");
    }
  }

  private boolean datasPertencemAoSemestre(
      LocalDate inicio, LocalDate fim, int anoPeriodo, int mesInicial, int mesFinal) {
    return dataPertenceAoSemestre(inicio, anoPeriodo, mesInicial, mesFinal)
        && dataPertenceAoSemestre(fim, anoPeriodo, mesInicial, mesFinal);
  }

  private boolean dataPertenceAoSemestre(
      LocalDate data, int anoPeriodo, int mesInicial, int mesFinal) {
    return data.getYear() == anoPeriodo
        && data.getMonthValue() >= mesInicial
        && data.getMonthValue() <= mesFinal;
  }

  private LocalDate converterData(String data) {
    return LocalDate.parse(data, FORMATADOR_DATA);
  }
}
