package br.com.classroompb.model.entities.gestaoacademica;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o relatorio geral de usuarios cadastrados.
 */
public class RelatorioGeralUsuarios {

  private int totalGeral;
  private int totalAdministradores;
  private int totalCoordenadores;
  private int totalProfessores;
  private int totalAlunos;
  private List<ItemRelatorioUsuario> usuarios = new ArrayList<>();

  /**
   * Cria um relatorio vazio.
   */
  public RelatorioGeralUsuarios() {}

  /**
   * Retorna o total geral.
   *
   * @return total geral.
   */
  public int getTotalGeral() {
    return totalGeral;
  }

  /**
   * Define o total geral.
   *
   * @param totalGeral total geral.
   */
  public void setTotalGeral(int totalGeral) {
    this.totalGeral = totalGeral;
  }

  /**
   * Retorna o total de administradores.
   *
   * @return total de administradores.
   */
  public int getTotalAdministradores() {
    return totalAdministradores;
  }

  /**
   * Define o total de administradores.
   *
   * @param totalAdministradores total de administradores.
   */
  public void setTotalAdministradores(int totalAdministradores) {
    this.totalAdministradores = totalAdministradores;
  }

  /**
   * Retorna o total de coordenadores.
   *
   * @return total de coordenadores.
   */
  public int getTotalCoordenadores() {
    return totalCoordenadores;
  }

  /**
   * Define o total de coordenadores.
   *
   * @param totalCoordenadores total de coordenadores.
   */
  public void setTotalCoordenadores(int totalCoordenadores) {
    this.totalCoordenadores = totalCoordenadores;
  }

  /**
   * Retorna o total de professores.
   *
   * @return total de professores.
   */
  public int getTotalProfessores() {
    return totalProfessores;
  }

  /**
   * Define o total de professores.
   *
   * @param totalProfessores total de professores.
   */
  public void setTotalProfessores(int totalProfessores) {
    this.totalProfessores = totalProfessores;
  }

  /**
   * Retorna o total de alunos.
   *
   * @return total de alunos.
   */
  public int getTotalAlunos() {
    return totalAlunos;
  }

  /**
   * Define o total de alunos.
   *
   * @param totalAlunos total de alunos.
   */
  public void setTotalAlunos(int totalAlunos) {
    this.totalAlunos = totalAlunos;
  }

  /**
   * Retorna os usuarios do relatorio.
   *
   * @return usuarios do relatorio.
   */
  public List<ItemRelatorioUsuario> getUsuarios() {
    return usuarios;
  }

  /**
   * Define os usuarios do relatorio.
   *
   * @param usuarios usuarios do relatorio.
   */
  public void setUsuarios(List<ItemRelatorioUsuario> usuarios) {
    if (usuarios == null) {
      this.usuarios = new ArrayList<>();
      return;
    }

    this.usuarios = new ArrayList<>(usuarios);
  }
}
