package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.EntradaInvalidaException;

/**
 * Representa um diario de turma cadastrado por um coordenador.
 *
 * <p>Um diario esta associado a uma unica turma e a um professor responsavel, e registra
 * informacoes proprias de acompanhamento da turma, como descricao, horario, sala, carga
 * horaria e situacao.
 */
public final class Diario {

  private String codigo;
  private String codigoTurma;
  private String descricao;
  private String matriculaProfessor;
  private String horario;
  private String sala;
  private int cargaHoraria;
  private SituacaoDiario situacao;

  /**
   * Cria um diario vazio.
   */
  public Diario() {}

  /**
   * Cria um diario sem codigo, com situacao inicial ATIVO.
   *
   * @param codigoTurma codigo da turma associada.
   * @param descricao descricao do diario.
   * @param matriculaProfessor matricula do professor responsavel.
   * @param horario horario do diario.
   * @param sala sala do diario.
   * @param cargaHoraria carga horaria do diario.
   */
  public Diario(
      String codigoTurma,
      String descricao,
      String matriculaProfessor,
      String horario,
      String sala,
      int cargaHoraria) {
    setCodigoTurma(codigoTurma);
    setDescricao(descricao);
    setMatriculaProfessor(matriculaProfessor);
    setHorario(horario);
    setSala(sala);
    setCargaHoraria(cargaHoraria);
    setSituacao(SituacaoDiario.ATIVO);
  }

  /**
   * Cria um diario completo.
   *
   * @param codigo codigo do diario.
   * @param codigoTurma codigo da turma associada.
   * @param descricao descricao do diario.
   * @param matriculaProfessor matricula do professor responsavel.
   * @param horario horario do diario.
   * @param sala sala do diario.
   * @param cargaHoraria carga horaria do diario.
   * @param situacao situacao do diario.
   */
  public Diario(
      String codigo,
      String codigoTurma,
      String descricao,
      String matriculaProfessor,
      String horario,
      String sala,
      int cargaHoraria,
      SituacaoDiario situacao) {
    setCodigo(codigo);
    setCodigoTurma(codigoTurma);
    setDescricao(descricao);
    setMatriculaProfessor(matriculaProfessor);
    setHorario(horario);
    setSala(sala);
    setCargaHoraria(cargaHoraria);
    setSituacao(situacao);
  }

  /**
   * Retorna o codigo do diario.
   *
   * @return codigo do diario.
   */
  public String getCodigo() {
    return codigo;
  }

  /**
   * Define o codigo do diario.
   *
   * @param codigo codigo do diario.
   */
  public void setCodigo(String codigo) {
    validarCampoObrigatorio(codigo, "Código do diário não pode ser vazio.");
    this.codigo = codigo;
  }

  /**
   * Retorna o codigo da turma associada.
   *
   * @return codigo da turma.
   */
  public String getCodigoTurma() {
    return codigoTurma;
  }

  /**
   * Define o codigo da turma associada.
   *
   * @param codigoTurma codigo da turma.
   */
  public void setCodigoTurma(String codigoTurma) {
    validarCampoObrigatorio(codigoTurma, "Diário deve estar associado a uma turma.");
    this.codigoTurma = codigoTurma;
  }

  /**
   * Retorna a descricao do diario.
   *
   * @return descricao do diario.
   */
  public String getDescricao() {
    return descricao;
  }

  /**
   * Define a descricao do diario.
   *
   * @param descricao descricao do diario.
   */
  public void setDescricao(String descricao) {
    validarCampoObrigatorio(descricao, "Descrição do diário não pode ser vazia.");
    this.descricao = descricao;
  }

  /**
   * Retorna a matricula do professor responsavel.
   *
   * @return matricula do professor.
   */
  public String getMatriculaProfessor() {
    return matriculaProfessor;
  }

  /**
   * Define a matricula do professor responsavel.
   *
   * @param matriculaProfessor matricula do professor.
   */
  public void setMatriculaProfessor(String matriculaProfessor) {
    validarCampoObrigatorio(matriculaProfessor, "Diário deve possuir professor responsável.");
    this.matriculaProfessor = matriculaProfessor;
  }

  /**
   * Retorna o horario do diario.
   *
   * @return horario do diario.
   */
  public String getHorario() {
    return horario;
  }

  /**
   * Define o horario do diario.
   *
   * @param horario horario do diario.
   */
  public void setHorario(String horario) {
    validarCampoObrigatorio(horario, "Horário do diário não pode ser vazio.");
    this.horario = horario;
  }

  /**
   * Retorna a sala do diario.
   *
   * @return sala do diario.
   */
  public String getSala() {
    return sala;
  }

  /**
   * Define a sala do diario.
   *
   * @param sala sala do diario.
   */
  public void setSala(String sala) {
    validarCampoObrigatorio(sala, "Sala do diário não pode ser vazia.");
    this.sala = sala;
  }

  /**
   * Retorna a carga horaria do diario.
   *
   * @return carga horaria do diario.
   */
  public int getCargaHoraria() {
    return cargaHoraria;
  }

  /**
   * Define a carga horaria do diario.
   *
   * @param cargaHoraria carga horaria do diario.
   */
  public void setCargaHoraria(int cargaHoraria) {
    validarCargaHoraria(cargaHoraria);
    this.cargaHoraria = cargaHoraria;
  }

  /**
   * Retorna a situacao do diario.
   *
   * @return situacao do diario.
   */
  public SituacaoDiario getSituacao() {
    return situacao;
  }

  /**
   * Define a situacao do diario.
   *
   * @param situacao situacao do diario.
   */
  public void setSituacao(SituacaoDiario situacao) {
    if (situacao == null) {
      throw new EntradaInvalidaException("Situação do diário não pode ser vazia.");
    }
    this.situacao = situacao;
  }

  /**
   * Valida os dados basicos do diario.
   */
  public void validarDadosBasicos() {
    validarCampoObrigatorio(codigoTurma, "Diário deve estar associado a uma turma.");
    validarCampoObrigatorio(descricao, "Descrição do diário não pode ser vazia.");
    validarCampoObrigatorio(matriculaProfessor, "Diário deve possuir professor responsável.");
    validarCampoObrigatorio(horario, "Horário do diário não pode ser vazio.");
    validarCampoObrigatorio(sala, "Sala do diário não pode ser vazia.");
    validarCargaHoraria(cargaHoraria);

    if (situacao == null) {
      throw new EntradaInvalidaException("Situação do diário não pode ser vazia.");
    }
  }

  private void validarCampoObrigatorio(String valor, String mensagemErro) {
    if (valor == null || valor.isBlank()) {
      throw new EntradaInvalidaException(mensagemErro);
    }
  }

  private void validarCargaHoraria(int cargaHoraria) {
    if (cargaHoraria <= 0) {
      throw new EntradaInvalidaException("Carga horária do diário deve ser maior que zero.");
    }
  }

  /**
   * Retorna a representacao textual do diario.
   *
   * @return texto do diario.
   */
  @Override
  public String toString() {
    return """
    ┌────────────────────────────────────────┐
    │                 DIÁRIO                 │
    ├────────────────────────────────────────┤
    │ Código              : %s
    │ Turma               : %s
    │ Descrição           : %s
    │ Professor           : %s
    │ Horário             : %s
    │ Sala                : %s
    │ Carga Horária       : %dh
    │ Situação            : %s
    └────────────────────────────────────────┘
    """
        .formatted(
            codigo,
            codigoTurma,
            descricao,
            matriculaProfessor,
            horario,
            sala,
            cargaHoraria,
            situacao == null ? "-" : situacao.getDescricao());
  }
}
