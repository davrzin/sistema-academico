package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma turma ofertada no sistema academico.
 */
public final class Turma {

  private String codigo;
  private String codigoDisciplina;
  private String periodoLetivo;
  private String matriculaProfessor;
  private int limiteVagas;
  private String horario;
  private String sala;
  private List<String> matriculados;
  private List<String> listaEspera;
  private List<String> aulas;

  /**
   * Cria uma turma vazia.
   */
  public Turma() {}

  /**
   * Cria uma turma sem codigo.
   *
   * @param codigoDisciplina codigo da disciplina.
   * @param periodoLetivo periodo letivo.
   * @param matriculaProfessor matricula do professor.
   * @param limiteVagas limite de vagas.
   * @param horario horario da turma.
   * @param sala sala da turma.
   */
  public Turma(
      String codigoDisciplina,
      String periodoLetivo,
      String matriculaProfessor,
      int limiteVagas,
      String horario,
      String sala) {
    setCodigoDisciplina(codigoDisciplina);
    setPeriodoLetivo(periodoLetivo);
    setMatriculaProfessor(matriculaProfessor);
    setLimiteVagas(limiteVagas);
    setHorario(horario);
    setSala(sala);
    setMatriculados();
    setListaEspera();
    setAulas();
  }

  /**
   * Cria uma turma completa.
   *
   * @param codigo codigo da turma.
   * @param codigoDisciplina codigo da disciplina.
   * @param periodoLetivo periodo letivo.
   * @param matriculaProfessor matricula do professor.
   * @param limiteVagas limite de vagas.
   * @param horario horario da turma.
   * @param sala sala da turma.
   */
  public Turma(
      String codigo,
      String codigoDisciplina,
      String periodoLetivo,
      String matriculaProfessor,
      int limiteVagas,
      String horario,
      String sala) {
    setCodigo(codigo);
    setCodigoDisciplina(codigoDisciplina);
    setPeriodoLetivo(periodoLetivo);
    setMatriculaProfessor(matriculaProfessor);
    setLimiteVagas(limiteVagas);
    setHorario(horario);
    setSala(sala);
    setMatriculados();
    setListaEspera();
    setAulas();
  }

  /**
   * Retorna o codigo da turma.
   *
   * @return codigo da turma.
   */
  public String getCodigo() {
    return codigo;
  }

  /**
   * Define o codigo da turma.
   *
   * @param codigo codigo da turma.
   */
  public void setCodigo(String codigo) {
    validarCampoObrigatorio(codigo, "Código da turma não pode ser vazio.");
    this.codigo = codigo;
  }

  /**
   * Retorna o codigo da disciplina.
   *
   * @return codigo da disciplina.
   */
  public String getCodigoDisciplina() {
    return codigoDisciplina;
  }

  /**
   * Define o codigo da disciplina.
   *
   * @param codigoDisciplina codigo da disciplina.
   */
  public void setCodigoDisciplina(String codigoDisciplina) {
    validarCampoObrigatorio(codigoDisciplina, "Código da disciplina não pode ser vazio.");
    this.codigoDisciplina = codigoDisciplina;
  }

  /**
   * Retorna o periodo letivo.
   *
   * @return periodo letivo.
   */
  public String getPeriodoLetivo() {
    return periodoLetivo;
  }

  /**
   * Define o periodo letivo.
   *
   * @param periodoLetivo periodo letivo.
   */
  public void setPeriodoLetivo(String periodoLetivo) {
    validarCampoObrigatorio(periodoLetivo, "Período letivo da turma não pode ser vazio.");
    this.periodoLetivo = periodoLetivo;
  }

  /**
   * Retorna a matricula do professor.
   *
   * @return matricula do professor.
   */
  public String getMatriculaProfessor() {
    return matriculaProfessor;
  }

  /**
   * Define a matricula do professor.
   *
   * @param matriculaProfessor matricula do professor.
   */
  public void setMatriculaProfessor(String matriculaProfessor) {
    validarCampoObrigatorio(matriculaProfessor, "Turma deve possuir professor responsável.");
    this.matriculaProfessor = matriculaProfessor;
  }

  /**
   * Retorna o limite de vagas.
   *
   * @return limite de vagas.
   */
  public int getLimiteVagas() {
    return limiteVagas;
  }

  /**
   * Define o limite de vagas.
   *
   * @param limiteVagas limite de vagas.
   */
  public void setLimiteVagas(int limiteVagas) {
    validarLimiteVagas(limiteVagas);
    this.limiteVagas = limiteVagas;
  }

  /**
   * Retorna o horario da turma.
   *
   * @return horario da turma.
   */
  public String getHorario() {
    return horario;
  }

  /**
   * Define o horario da turma.
   *
   * @param horario horario da turma.
   */
  public void setHorario(String horario) {
    validarCampoObrigatorio(horario, "Horário da turma não pode ser vazio.");
    this.horario = horario;
  }

  /**
   * Retorna a sala da turma.
   *
   * @return sala da turma.
   */
  public String getSala() {
    return sala;
  }

  /**
   * Define a sala da turma.
   *
   * @param sala sala da turma.
   */
  public void setSala(String sala) {
    validarCampoObrigatorio(sala, "Sala da turma não pode ser vazia.");
    this.sala = sala;
  }

  /**
   * Retorna os matriculados da turma.
   *
   * @return matriculas dos alunos.
   */
  public List<String> getMatriculados() {
    return matriculados;
  }

  /**
   * Inicializa a lista de matriculados.
   */
  public void setMatriculados() {
    this.matriculados = new ArrayList<>();
  }

  /**
   * Inicializa a lista de espera.
   */
  public void setListaEspera() {
    this.listaEspera = new ArrayList<>();
  }

  /**
   * Retorna a lista de espera.
   *
   * @return matriculas em lista de espera.
   */
  public List<String> getListaEspera() {
    return this.listaEspera;
  }

  /**
   * Inicializa a lista de aulas.
   */
  public void setAulas() {
    this.aulas = new ArrayList<>();
  }

  /**
   * Retorna as aulas da turma.
   *
   * @return identificadores das aulas.
   */
  public List<String> getAulas() {
    return this.aulas;
  }

  /**
   * Valida os dados basicos da turma.
   */
  public void validarDadosBasicos() {
    validarCampoObrigatorio(codigoDisciplina, "Código da disciplina não pode ser vazio.");
    validarCampoObrigatorio(periodoLetivo, "Período letivo da turma não pode ser vazio.");
    validarCampoObrigatorio(matriculaProfessor, "Turma deve possuir professor responsável.");
    validarLimiteVagas(limiteVagas);
    validarCampoObrigatorio(horario, "Horário da turma não pode ser vazio.");
    validarCampoObrigatorio(sala, "Sala da turma não pode ser vazia.");
  }

  private void validarCampoObrigatorio(String valor, String mensagemErro) {
    if (valor == null || valor.isBlank()) {
      throw new EntradaInvalidaException(mensagemErro);
    }
  }

  private void validarLimiteVagas(int limiteVagas) {
    if (limiteVagas <= 0) {
      throw new EntradaInvalidaException("Limite de vagas da turma deve ser maior que zero.");
    }
  }

  /**
   * Retorna a representacao textual da turma.
   *
   * @return texto da turma.
   */
  @Override
  public String toString() {
    return """
    ┌────────────────────────────────────────┐
    │                 TURMA                  │
    ├────────────────────────────────────────┤
    │ Código              : %s
    │ Disciplina          : %s
    │ Período Letivo      : %s
    │ Professor           : %s
    │ Limite de Vagas     : %d
    │ Horário             : %s
    │ Sala                : %s
    └────────────────────────────────────────┘
    """
        .formatted(
            codigo,
            codigoDisciplina,
            periodoLetivo,
            matriculaProfessor,
            limiteVagas,
            horario,
            sala);
  }
}
