package br.com.classroompb.model.entities.usuario;

import br.com.classroompb.model.enums.TipoUsuario;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um usuario aluno do sistema.
 */
public final class Aluno extends Usuario {

  private List<String> disciplinasConcluidas;
  private List<String> turmasMatriculadas;
  private String codigoCurso;

  /**
   * Cria um aluno vazio.
   */
  public Aluno() {
    super();
  }

  /**
   * Cria um aluno sem matricula.
   *
   * @param nome nome do aluno.
   * @param email email do aluno.
   * @param senha senha do aluno.
   */
  public Aluno(String nome, String email, String senha) {
    super(nome, email, senha, TipoUsuario.ALUNO);
    setDisciplinasConcluidas();
    setTurmasMatriculadas();
  }

  /**
   * Cria um aluno com matricula.
   *
   * @param nome nome do aluno.
   * @param email email do aluno.
   * @param matricula matricula do aluno.
   * @param senha senha do aluno.
   */
  public Aluno(String nome, String email, String matricula, String senha) {
    super(nome, email, matricula, senha, TipoUsuario.ALUNO);
    setDisciplinasConcluidas();
    setTurmasMatriculadas();
  }

  /**
   * Cria um aluno com curso.
   *
   * @param nome nome do aluno.
   * @param email email do aluno.
   * @param matricula matricula do aluno.
   * @param senha senha do aluno.
   * @param codigoCurso codigo do curso.
   */
  public Aluno(String nome, String email, String matricula, String senha, String codigoCurso) {
    super(nome, email, matricula, senha, TipoUsuario.ALUNO);
    setCodigoCurso(codigoCurso);
    setDisciplinasConcluidas();
    setTurmasMatriculadas();
  }

  /**
   * Cria um aluno com tipo informado.
   *
   * @param nome nome do aluno.
   * @param email email do aluno.
   * @param matricula matricula do aluno.
   * @param senha senha do aluno.
   * @param tipoUsuario tipo do usuario.
   */
  public Aluno(String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
    super(nome, email, matricula, senha, TipoUsuario.ALUNO);
    setDisciplinasConcluidas();
    setTurmasMatriculadas();
  }

  /**
   * Cria um aluno completo.
   *
   * @param nome nome do aluno.
   * @param email email do aluno.
   * @param matricula matricula do aluno.
   * @param senha senha do aluno.
   * @param tipoUsuario tipo do usuario.
   * @param codigoCurso codigo do curso.
   */
  public Aluno(
      String nome,
      String email,
      String matricula,
      String senha,
      TipoUsuario tipoUsuario,
      String codigoCurso) {
    super(nome, email, matricula, senha, TipoUsuario.ALUNO);
    setCodigoCurso(codigoCurso);
    setDisciplinasConcluidas();
    setTurmasMatriculadas();
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
    this.codigoCurso = codigoCurso;
  }

  /**
   * Inicializa as disciplinas concluidas.
   */
  public void setDisciplinasConcluidas() {
    this.disciplinasConcluidas = new ArrayList<>();
  }

  /**
   * Retorna as disciplinas concluidas.
   *
   * @return codigos das disciplinas concluidas.
   */
  public List<String> getDisciplinasConcluidas() {
    return disciplinasConcluidas;
  }

  /**
   * Inicializa as turmas matriculadas.
   */
  public void setTurmasMatriculadas() {
    this.turmasMatriculadas = new ArrayList<>();
  }

  /**
   * Retorna as turmas matriculadas.
   *
   * @return codigos das turmas matriculadas.
   */
  public List<String> getTurmasMatriculadas() {
    return turmasMatriculadas;
  }
}
