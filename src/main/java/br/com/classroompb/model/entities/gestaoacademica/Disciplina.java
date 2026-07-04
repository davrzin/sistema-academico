package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma disciplina cadastrada no sistema academico.
 */
public class Disciplina {
  private String codigo;
  private String nome;
  private int cargaHoraria;
  private int periodo;
  private int creditos;
  private String codigoCurso;
  private List<String> preRequisitos;

  /**
   * Cria uma disciplina vazia.
   */
  public Disciplina() {
    this.preRequisitos = new ArrayList<>();
  }

  /**
   * Cria uma disciplina sem codigo.
   *
   * @param nome nome da disciplina.
   * @param cargaHoraria carga horaria.
   * @param periodo periodo de oferta.
   * @param creditos quantidade de creditos.
   * @param codigoCurso codigo do curso.
   * @param preRequisitos codigos dos pre-requisitos.
   */
  public Disciplina(
      String nome,
      int cargaHoraria,
      int periodo,
      int creditos,
      String codigoCurso,
      List<String> preRequisitos) {
    setNome(nome);
    setCargaHoraria(cargaHoraria);
    setPeriodo(periodo);
    setCreditos(creditos);
    setCodigoCurso(codigoCurso);
    setPreRequisitos(preRequisitos);
  }

  /**
   * Cria uma disciplina completa.
   *
   * @param codigo codigo da disciplina.
   * @param nome nome da disciplina.
   * @param cargaHoraria carga horaria.
   * @param periodo periodo de oferta.
   * @param creditos quantidade de creditos.
   * @param codigoCurso codigo do curso.
   * @param preRequisitos codigos dos pre-requisitos.
   */
  public Disciplina(
      String codigo,
      String nome,
      int cargaHoraria,
      int periodo,
      int creditos,
      String codigoCurso,
      List<String> preRequisitos) {
    setCodigo(codigo);
    setNome(nome);
    setCargaHoraria(cargaHoraria);
    setPeriodo(periodo);
    setCreditos(creditos);
    setCodigoCurso(codigoCurso);
    setPreRequisitos(preRequisitos);
  }

  /**
   * Retorna o codigo da disciplina.
   *
   * @return codigo da disciplina.
   */
  public String getCodigo() {
    return codigo;
  }

  /**
   * Define o codigo da disciplina.
   *
   * @param codigo codigo da disciplina.
   */
  public void setCodigo(String codigo) {
    validarCodigo(codigo);
    this.codigo = codigo;
  }

  /**
   * Retorna o nome da disciplina.
   *
   * @return nome da disciplina.
   */
  public String getNome() {
    return nome;
  }

  /**
   * Define o nome da disciplina.
   *
   * @param nome nome da disciplina.
   */
  public void setNome(String nome) {
    validarNome(nome);
    this.nome = nome;
  }

  /**
   * Retorna a carga horaria.
   *
   * @return carga horaria.
   */
  public int getCargaHoraria() {
    return cargaHoraria;
  }

  /**
   * Define a carga horaria.
   *
   * @param cargaHoraria carga horaria.
   */
  public void setCargaHoraria(int cargaHoraria) {
    validarCargaHoraria(cargaHoraria);
    this.cargaHoraria = cargaHoraria;
  }

  /**
   * Retorna o periodo de oferta.
   *
   * @return periodo de oferta.
   */
  public int getPeriodo() {
    return periodo;
  }

  /**
   * Define o periodo de oferta.
   *
   * @param periodo periodo de oferta.
   */
  public void setPeriodo(int periodo) {
    validarPeriodo(periodo);
    this.periodo = periodo;
  }

  /**
   * Retorna o codigo do curso.
   *
   * @return codigo do curso.
   */
  public String getCodigoCurso() {
    return codigoCurso;
  }

  /**
   * Define o codigo do curso.
   *
   * @param codigoCurso codigo do curso.
   */
  public void setCodigoCurso(String codigoCurso) {
    validarCodigoCurso(codigoCurso);
    this.codigoCurso = codigoCurso;
  }

  /**
   * Retorna a quantidade de creditos.
   *
   * @return quantidade de creditos.
   */
  public int getCreditos() {
    return creditos;
  }

  /**
   * Define a quantidade de creditos.
   *
   * @param creditos quantidade de creditos.
   */
  public void setCreditos(int creditos) {
    validarCreditos(creditos);
    this.creditos = creditos;
  }

  /**
   * Retorna os pre-requisitos.
   *
   * @return codigos dos pre-requisitos.
   */
  public List<String> getPreRequisitos() {
    return preRequisitos;
  }

  /**
   * Define os pre-requisitos.
   *
   * @param preRequisitos codigos dos pre-requisitos.
   */
  public void setPreRequisitos(List<String> preRequisitos) {
    validarPreRequisitosBasicos(preRequisitos);

    if (preRequisitos == null) {
      this.preRequisitos = new ArrayList<>();
    } else {
      this.preRequisitos = new ArrayList<>(preRequisitos);
    }
  }

  /**
   * Valida os dados basicos da disciplina.
   */
  public void validarDadosBasicos() {
    validarNome(nome);
    validarCargaHoraria(cargaHoraria);
    validarPeriodo(periodo);
    validarCreditos(creditos);
    validarCodigoCurso(codigoCurso);
    validarPreRequisitosBasicos(preRequisitos);
  }

  private void validarCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new EntradaInvalidaException("Código da disciplina não pode ser vazio.");
    }
  }

  private void validarNome(String nome) {
    if (nome == null || nome.isBlank()) {
      throw new EntradaInvalidaException("Nome da disciplina não pode ser vazio.");
    }

    if (nome.length() < 3) {
      throw new EntradaInvalidaException(
          "Nome da disciplina deve possuir pelo menos 3 caracteres.");
    }

    if (nome.length() > 200) {
      throw new EntradaInvalidaException(
          "Nome da disciplina deve possuir no máximo 100 caracteres.");
    }
  }

  private void validarCargaHoraria(int cargaHoraria) {
    if (cargaHoraria <= 0) {
      throw new EntradaInvalidaException("Carga horária da disciplina inválida.");
    }

    if (cargaHoraria > 300) {
      throw new EntradaInvalidaException("Carga horária da disciplina inválida.");
    }
  }

  private void validarPeriodo(int periodo) {
    if (periodo <= 0) {
      throw new EntradaInvalidaException("Período da disciplina inválido.");
    }

    if (periodo > 12) {
      throw new EntradaInvalidaException("O período de oferta deve estar entre 1 e 13.");
    }
  }

  private void validarCreditos(int creditos) {
    if (creditos <= 0) {
      throw new EntradaInvalidaException("Quantidade de créditos inválida.");
    }

    if (creditos > 30) {
      throw new EntradaInvalidaException("Quantidade de créditos inválida.");
    }
  }

  private void validarCodigoCurso(String codigoCurso) {
    if (codigoCurso == null || codigoCurso.isBlank()) {
      throw new EntradaInvalidaException("Código do curso da disciplina não pode ser vazio.");
    }

    codigoCurso = codigoCurso.trim();

    if (!codigoCurso.matches("cur\\d{2,}")) {
      throw new EntradaInvalidaException("Formato do código curso inválido.");
    }
  }

  private void validarPreRequisitosBasicos(List<String> preRequisitos) {
    if (preRequisitos == null) {
      return;
    }

    for (String codigoDisciplina : preRequisitos) {
      if (codigoDisciplina == null || codigoDisciplina.isBlank()) {
        throw new EntradaInvalidaException("Código de pré-requisito inválido.");
      }

      if (!codigoDisciplina.matches("dis\\d{2,}")) {
        throw new EntradaInvalidaException("Formato do código de pré-requisito inválido.");
      }
    }
  }

  /**
   * Retorna a representacao textual da disciplina.
   *
   * @return texto da disciplina.
   */
  @Override
  public String toString() {
    return """
    ┌────────────────────────────────────────┐
    │              DISCIPLINA                │
    ├────────────────────────────────────────┤
    │ Código        : %s
    │ Nome          : %s
    │ Carga Horária : %dh
    │ Período       : %d
    │ Curso         : %s
    │ Créditos      : %d
    │ Pré-requisitos: %s
    └────────────────────────────────────────┘
    """
        .formatted(codigo, nome, cargaHoraria, periodo, codigoCurso, creditos, preRequisitos);
  }
}
