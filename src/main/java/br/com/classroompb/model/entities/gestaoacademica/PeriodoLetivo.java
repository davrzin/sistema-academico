package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Representa um periodo letivo do sistema academico.
 */
public class PeriodoLetivo {

  private static final DateTimeFormatter FORMATADOR_DATA =
      DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
    validarIntervaloDatas(dataInicio, dataFim);
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
    validarData(dataInicio, "Data de início inválida.");
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
    validarData(dataFim, "Data de fim inválida.");
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
    validarData(dataInicio, "Data de início inválida.");
    validarData(dataFim, "Data de fim inválida.");
    validarIntervaloDatas(dataInicio, dataFim);
  }

  private void validarPeriodo(String periodo) {
    if (periodo == null || periodo.isBlank()) {
      throw new EntradaInvalidaException("Período letivo não pode ser vazio.");
    }

    if (!periodo.matches("\\d{4}\\.\\d+")) {
      throw new EntradaInvalidaException(
          "Formato de período inválido. Use o formato 2024.1, por exemplo.");
    }
  }

  private void validarData(String data, String mensagemErro) {
    if (data == null || data.isBlank()) {
      throw new EntradaInvalidaException(mensagemErro);
    }

    try {
      LocalDate.parse(data, FORMATADOR_DATA);
    } catch (DateTimeParseException e) {
      throw new EntradaInvalidaException("Formato de data inválido. Use o formato dd/MM/yyyy.");
    }
  }

  private void validarIntervaloDatas(String dataInicio, String dataFim) {
    LocalDate inicio = LocalDate.parse(dataInicio, FORMATADOR_DATA);
    LocalDate fim = LocalDate.parse(dataFim, FORMATADOR_DATA);

    if (!fim.isAfter(inicio)) {
      throw new EntradaInvalidaException("A data final deve ser posterior à data inicial.");
    }
  }
}
