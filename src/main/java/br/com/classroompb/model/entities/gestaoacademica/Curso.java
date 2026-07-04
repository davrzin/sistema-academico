package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

/**
 * Representa um curso cadastrado no sistema academico.
 */
public class Curso {
  private String codigo;
  private String nome;
  private int quantidadePeriodos;
  private int cargaHorariaTotal;
  private String matriculaCoordenador;

  /**
   * Cria um curso vazio.
   */
  public Curso() {}

  /**
   * Cria um curso sem coordenador.
   *
   * @param nome nome do curso.
   * @param quantidadePeriodos quantidade de periodos.
   * @param cargaHorariaTotal carga horaria total.
   */
  public Curso(String nome, int quantidadePeriodos, int cargaHorariaTotal) {
    setNome(nome);
    setQuantidadePeriodos(quantidadePeriodos);
    setCargaHorariaTotal(cargaHorariaTotal);
  }

  /**
   * Cria um curso com coordenador.
   *
   * @param nome nome do curso.
   * @param quantidadePeriodos quantidade de periodos.
   * @param cargaHorariaTotal carga horaria total.
   * @param matriculaCoordenador matricula do coordenador.
   */
  public Curso(
      String nome, int quantidadePeriodos, int cargaHorariaTotal, String matriculaCoordenador) {
    setNome(nome);
    setQuantidadePeriodos(quantidadePeriodos);
    setCargaHorariaTotal(cargaHorariaTotal);
    setMatriculaCoordenador(matriculaCoordenador);
  }

  /**
   * Cria um curso com codigo.
   *
   * @param codigo codigo do curso.
   * @param nome nome do curso.
   * @param quantidadePeriodos quantidade de periodos.
   * @param cargaHorariaTotal carga horaria total.
   */
  public Curso(String codigo, String nome, int quantidadePeriodos, int cargaHorariaTotal) {
    setCodigo(codigo);
    setNome(nome);
    setQuantidadePeriodos(quantidadePeriodos);
    setCargaHorariaTotal(cargaHorariaTotal);
  }

  /**
   * Cria um curso completo.
   *
   * @param codigo codigo do curso.
   * @param nome nome do curso.
   * @param quantidadePeriodos quantidade de periodos.
   * @param cargaHorariaTotal carga horaria total.
   * @param matriculaCoordenador matricula do coordenador.
   */
  public Curso(
      String codigo,
      String nome,
      int quantidadePeriodos,
      int cargaHorariaTotal,
      String matriculaCoordenador) {
    setCodigo(codigo);
    setNome(nome);
    setQuantidadePeriodos(quantidadePeriodos);
    setCargaHorariaTotal(cargaHorariaTotal);
    setMatriculaCoordenador(matriculaCoordenador);
  }

  /**
   * Retorna o codigo do curso.
   *
   * @return codigo do curso.
   */
  public String getCodigo() {
    return codigo;
  }

  /**
   * Define o codigo do curso.
   *
   * @param codigo codigo do curso.
   */
  public void setCodigo(String codigo) {
    validarCodigo(codigo);
    this.codigo = codigo;
  }

  /**
   * Retorna o nome do curso.
   *
   * @return nome do curso.
   */
  public String getNome() {
    return nome;
  }

  /**
   * Define o nome do curso.
   *
   * @param nome nome do curso.
   */
  public void setNome(String nome) {
    validarNome(nome);
    this.nome = nome;
  }

  /**
   * Retorna a quantidade de periodos.
   *
   * @return quantidade de periodos.
   */
  public int getQuantidadePeriodos() {
    return quantidadePeriodos;
  }

  /**
   * Define a quantidade de periodos.
   *
   * @param quantidadePeriodos quantidade de periodos.
   */
  public void setQuantidadePeriodos(int quantidadePeriodos) {
    validarQuantidadePeriodos(quantidadePeriodos);
    this.quantidadePeriodos = quantidadePeriodos;
  }

  /**
   * Retorna a carga horaria total.
   *
   * @return carga horaria total.
   */
  public int getCargaHorariaTotal() {
    return cargaHorariaTotal;
  }

  /**
   * Define a carga horaria total.
   *
   * @param cargaHorariaTotal carga horaria total.
   */
  public void setCargaHorariaTotal(int cargaHorariaTotal) {
    validarCargaHorariaTotal(cargaHorariaTotal);
    this.cargaHorariaTotal = cargaHorariaTotal;
  }

  /**
   * Retorna a matricula do coordenador.
   *
   * @return matricula do coordenador.
   */
  public String getMatriculaCoordenador() {
    return matriculaCoordenador;
  }

  /**
   * Define a matricula do coordenador.
   *
   * @param matriculaCoordenador matricula do coordenador.
   */
  public void setMatriculaCoordenador(String matriculaCoordenador) {
    this.matriculaCoordenador = matriculaCoordenador;
  }

  /**
   * Valida os dados basicos do curso.
   */
  public void validarDadosBasicos() {
    validarNome(nome);
    validarQuantidadePeriodos(quantidadePeriodos);
    validarCargaHorariaTotal(cargaHorariaTotal);
  }

  private void validarCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new EntradaInvalidaException("Código do curso não pode ser vazio.");
    }
  }

  private void validarNome(String nome) {
    if (nome == null || nome.isBlank()) {
      throw new EntradaInvalidaException("Nome do curso não pode ser vazio.");
    }

    if (nome.length() < 3) {
      throw new EntradaInvalidaException("Nome do curso deve possuir pelo menos 3 caracteres.");
    }

    if (nome.length() > 200) {
      throw new EntradaInvalidaException("Nome do curso deve possuir no máximo 100 caracteres.");
    }
  }

  private void validarQuantidadePeriodos(int quantidadePeriodos) {
    if (quantidadePeriodos <= 0) {
      throw new EntradaInvalidaException("Quantidade de períodos inválida.");
    }

    if (quantidadePeriodos > 13) {
      throw new EntradaInvalidaException("A quantidade de períodos não pode ultrapassar 13.");
    }
  }

  private void validarCargaHorariaTotal(int cargaHorariaTotal) {
    if (cargaHorariaTotal <= 0) {
      throw new EntradaInvalidaException("Carga horária inválida.");
    }

    if (cargaHorariaTotal > 10000) {
      throw new EntradaInvalidaException("Carga horária inválida.");
    }
  }

  /**
   * Retorna a representacao textual do curso.
   *
   * @return texto do curso.
   */
  @Override
  public String toString() {
    return """
    ┌───────────────────────────────────────┐
    │                 CURSO                 │
    ├───────────────────────────────────────┤
    │ Código        : %s
    │ Nome          : %s
    │ Períodos      : %d
    │ Carga Horária : %dh
    └───────────────────────────────────────┘
    """
            .formatted(codigo, nome, quantidadePeriodos, cargaHorariaTotal)
        + "Coordenador: "
        + matriculaCoordenador
        + System.lineSeparator();
  }
}
