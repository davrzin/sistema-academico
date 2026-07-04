package br.com.classroompb.model.entities.gestaoacademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

/**
 * Representa o boletim de um aluno em uma turma.
 */
public class Boletim {

  private String idBoletim;
  private String matriculaAluno;
  private String codigoTurma;
  private float primeiraNota;
  private float segundaNota;
  private double frequencia;

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
  public float getPrimeiraNota() {
    return primeiraNota;
  }

  /**
   * Define a primeira nota.
   *
   * @param primeiraNota primeira nota.
   */
  public void setPrimeiraNota(float primeiraNota) {
    validarNota(primeiraNota);
    this.primeiraNota = primeiraNota;
  }

  /**
   * Retorna a segunda nota.
   *
   * @return segunda nota.
   */
  public float getSegundaNota() {
    return segundaNota;
  }

  /**
   * Define a segunda nota.
   *
   * @param segundaNota segunda nota.
   */
  public void setSegundaNota(float segundaNota) {
    validarNota(segundaNota);
    this.segundaNota = segundaNota;
  }

  /**
   * Retorna a frequencia.
   *
   * @return frequencia.
   */
  public double getFrequencia() {
    return frequencia;
  }

  /**
   * Define a frequencia.
   *
   * @param frequencia frequencia.
   */
  public void setFrequencia(double frequencia) {
    validarFrequencia(frequencia);
    this.frequencia = frequencia;
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
      setFrequencia(100.0);
      return 100.0;
    }

    double frequencia = (double) ((quantidadeDeFaltas) * 100) / quantidadeDeAulas;

    setFrequencia(100.00 - frequencia);
    return 100.0 - frequencia;
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

  private void validarFrequencia(double frequencia) {
    if (frequencia < 0 || frequencia > 100.0) {
      throw new EntradaInvalidaException("A frequência deve estar entre 0 e 100");
    }
  }

  private void validarNota(float nota) {
    if (nota < 0 || nota > 10.0) {
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
        + "primeiraNota="
        + primeiraNota
        + ", segundaNota="
        + segundaNota
        + ", frequencia="
        + frequencia
        + '}';
  }
}
