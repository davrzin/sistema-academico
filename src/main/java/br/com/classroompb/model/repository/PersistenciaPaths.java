package br.com.classroompb.model.repository;

import java.nio.file.Path;

/**
 * Centraliza os caminhos usados para persistencia de dados.
 */
public final class PersistenciaPaths {

  public static final Path DATA = Path.of(System.getProperty("user.dir"), "data");
  public static final Path USUARIOS = DATA.resolve("usuarios");
  public static final Path CURSOS = DATA.resolve("cursos");
  public static final Path DISCIPLINAS = DATA.resolve("disciplinas");
  public static final Path TURMAS = DATA.resolve("turmas");
  public static final Path BOLETINS = DATA.resolve("boletins");
  public static final Path PERIODOS = DATA.resolve("periodos");
  public static final Path AULAS = DATA.resolve("aulas");

  public static final Path USUARIO_ADMINISTRADOR = USUARIOS.resolve("administrador.json");
  public static final Path USUARIO_ALUNO = USUARIOS.resolve("aluno.json");
  public static final Path USUARIO_COORDENADOR = USUARIOS.resolve("coordenador.json");
  public static final Path USUARIO_PROFESSOR = USUARIOS.resolve("professor.json");
  public static final Path CURSOS_ARQUIVO = CURSOS.resolve("cursos.json");
  public static final Path DISCIPLINAS_ARQUIVO = DISCIPLINAS.resolve("disciplinas.json");
  public static final Path TURMAS_ARQUIVO = TURMAS.resolve("turmas.json");
  public static final Path BOLETINS_ARQUIVO = BOLETINS.resolve("boletins.json");
  public static final Path PERIODOS_ARQUIVO = PERIODOS.resolve("periodos_letivos.json");
  public static final Path AULAS_ARQUIVO = AULAS.resolve("aulas.json");

  private PersistenciaPaths() {}
}
