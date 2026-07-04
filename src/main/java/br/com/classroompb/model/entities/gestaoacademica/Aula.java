package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa uma aula registrada para uma turma.
 */
public class Aula {

  private static final DateTimeFormatter FORMATADOR_DATA =
      DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private String id;
  private String codigoTurma;
  private String data;
  private String horario;
  private Map<String, Boolean> presencas;

  /**
   * Cria uma aula vazia.
   */
  public Aula() {}

  /**
   * Cria uma aula com presencas informadas.
   *
   * @param id identificador da aula.
   * @param codigoTurma codigo da turma.
   * @param data data da aula.
   * @param horario horario da aula.
   * @param presencas presencas registradas.
   */
  public Aula(
      String id, String codigoTurma, String data, String horario, Map<String, Boolean> presencas) {
    setId(id);
    setCodigoTurma(codigoTurma);
    setData(data);
    setHorario(horario);
    setPresencas(presencas);
  }

  /**
   * Cria uma aula sem presencas registradas.
   *
   * @param id identificador da aula.
   * @param codigoTurma codigo da turma.
   * @param data data da aula.
   * @param horario horario da aula.
   */
  public Aula(String id, String codigoTurma, String data, String horario) {
    setId(id);
    setCodigoTurma(codigoTurma);
    setData(data);
    setHorario(horario);
    setPresencas(new HashMap<>());
  }

  /**
   * Retorna o identificador da aula.
   *
   * @return identificador da aula.
   */
  public String getId() {
    return this.id;
  }

  /**
   * Define o identificador da aula.
   *
   * @param id identificador da aula.
   */
  public void setId(String id) {
    validarCodigo(id);
    this.id = id;
  }

  /**
   * Retorna o codigo da turma.
   *
   * @return codigo da turma.
   */
  public String getCodigoTurma() {
    return this.codigoTurma;
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
   * Retorna a data da aula.
   *
   * @return data da aula.
   */
  public String getData() {
    return this.data;
  }

  /**
   * Define a data da aula.
   *
   * @param data data da aula.
   */
  public void setData(String data) {
    validarData(data);
    this.data = data;
  }

  /**
   * Retorna o horario da aula.
   *
   * @return horario da aula.
   */
  public String getHorario() {
    return this.horario;
  }

  /**
   * Define o horario da aula.
   *
   * @param horario horario da aula.
   */
  public void setHorario(String horario) {
    validarHorario(horario);
    this.horario = horario;
  }

  /**
   * Retorna as presencas da aula.
   *
   * @return mapa de presencas.
   */
  public Map<String, Boolean> getPresencas() {
    return this.presencas;
  }

  /**
   * Define as presencas da aula.
   *
   * @param presencas mapa de presencas.
   */
  public void setPresencas(Map<String, Boolean> presencas) {
    validarPresencas(presencas);
    this.presencas = presencas;
  }

  private void validarCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new EntradaInvalidaException("Código da aula não pode ser vazio.");
    }
  }

  private void validarCodigoTurma(String codigoTurma) {
    if (codigoTurma == null || codigoTurma.isBlank()) {
      throw new EntradaInvalidaException("Código da turma não pode ser vazio.");
    }
  }

  private void validarData(String data) {
    if (data == null || data.isBlank()) {
      throw new EntradaInvalidaException("Data não pode ser vazia");
    }

    try {
      LocalDate.parse(data, FORMATADOR_DATA);
    } catch (DateTimeParseException e) {
      throw new EntradaInvalidaException("Formato de data inválido. Use o formato dd/MM/yyyy.");
    }
  }

  private void validarHorario(String horario) {
    if (horario == null || horario.isBlank()) {
      throw new EntradaInvalidaException("Horario não pode ser vazio");
    }
  }

  private void validarPresencas(Map<String, Boolean> presencas) {

    if (presencas == null) {
      throw new EntradaInvalidaException("Atributo não pode ser nulo");
    }
  }
}
