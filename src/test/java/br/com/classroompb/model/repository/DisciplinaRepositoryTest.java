package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do repositorio de disciplinas.
 */
public class DisciplinaRepositoryTest {

  @TempDir Path tempDir;

  /**
   * Limpa os arquivos gerados pelos testes.
   */
  @AfterEach
  public void tearDown() {
    File diretorio = tempDir.resolve("disciplinas").toFile();
    File[] arquivos = diretorio.listFiles();

    if (arquivos != null) {
      for (File arquivo : arquivos) {
        arquivo.delete();
      }
    }

    if (diretorio.exists() && diretorio.isDirectory()) {
      diretorio.delete();
    }
  }

  private DisciplinaRepository criarRepository() {
    return new DisciplinaRepository(new ObjectMapper(), tempDir.resolve("disciplinas").toString());
  }

  private Disciplina criarDisciplina(String codigo, String nome, String codigoCurso) {
    return new Disciplina(codigo, nome, 60, 3, 4, codigoCurso, new ArrayList<>());
  }

  @Test
  public void deveRetornarObjectMapper() {
    DisciplinaRepository repository = criarRepository();
    Assertions.assertNotNull(repository.getObjectMapper());
  }

  @Test
  public void deveAlterarObjectMapper() {
    DisciplinaRepository repository = criarRepository();
    ObjectMapper novoMapper = new ObjectMapper();
    repository.setObjectMapper(novoMapper);
    Assertions.assertEquals(novoMapper, repository.getObjectMapper());
  }

  @Test
  public void deveRetornarDiretorioDisciplinas() {
    DisciplinaRepository repository = criarRepository();
    Assertions.assertNotNull(repository.getDiretorioDisciplinas());
  }

  @Test
  public void deveSalvarDisciplinaEmArquivo() {
    DisciplinaRepository repository = criarRepository();
    Disciplina disciplina = criarDisciplina("DIS001", "Estrutura de Dados", "cur01");

    repository.salvarDisciplina(disciplina);

    File arquivo = tempDir.resolve("disciplinas").resolve("disciplinas.json").toFile();

    Assertions.assertTrue(arquivo.exists());
    Assertions.assertEquals(1, repository.listarDisciplinas().size());
  }

  @Test
  public void deveSalvarMultiplasDisciplinasEmArquivo() {
    DisciplinaRepository repository = criarRepository();

    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));
    repository.salvarDisciplina(criarDisciplina("DIS002", "Algoritmos", "cur01"));
    repository.salvarDisciplina(criarDisciplina("DIS003", "Banco de Dados", "cur02"));

    Assertions.assertEquals(3, repository.listarDisciplinas().size());
  }

  @Test
  public void deveLancarIllegalArgumentExceptionAoSalvarDisciplinaNula() {
    DisciplinaRepository repository = criarRepository();

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> repository.salvarDisciplina(null));
  }

  @Test
  public void deveCriarDiretorioAoSalvarQuandoNaoExiste() {
    DisciplinaRepository repository = criarRepository();
    Disciplina disciplina = criarDisciplina("DIS001", "Estrutura de Dados", "cur01");

    repository.salvarDisciplina(disciplina);

    File diretorio = tempDir.resolve("disciplinas").toFile();
    Assertions.assertTrue(diretorio.exists());
  }

  @Test
  public void deveRetornarListaVaziaQuandoArquivoNaoExiste() {
    DisciplinaRepository repository = criarRepository();

    List<Disciplina> disciplinas = repository.listarDisciplinas();

    Assertions.assertNotNull(disciplinas);
    Assertions.assertEquals(0, disciplinas.size());
  }

  @Test
  public void deveListarTodasAsDisciplinasSalvas() {
    DisciplinaRepository repository = criarRepository();

    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));
    repository.salvarDisciplina(criarDisciplina("DIS002", "Algoritmos", "cur01"));

    Assertions.assertEquals(2, repository.listarDisciplinas().size());
  }

  @Test
  public void deveEncontrarDisciplinaPorCodigo() {
    DisciplinaRepository repository = criarRepository();
    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));

    Disciplina encontrada = repository.buscarPorCodigo("DIS001");

    Assertions.assertNotNull(encontrada);
    Assertions.assertEquals("DIS001", encontrada.getCodigo());
  }

  @Test
  public void deveEncontrarDisciplinaPorCodigoIgnorandoCase() {
    DisciplinaRepository repository = criarRepository();
    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));

    Disciplina encontrada = repository.buscarPorCodigo("dis001");

    Assertions.assertNotNull(encontrada);
    Assertions.assertEquals("DIS001", encontrada.getCodigo());
  }

  @Test
  public void deveRetornarNuloAoBuscarPorCodigoInexistente() {
    DisciplinaRepository repository = criarRepository();
    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));

    Disciplina encontrada = repository.buscarPorCodigo("DIS999");

    Assertions.assertNull(encontrada);
  }

  @Test
  public void deveRetornarNuloAoBuscarPorCodigoComListaVazia() {
    DisciplinaRepository repository = criarRepository();

    Disciplina encontrada = repository.buscarPorCodigo("DIS001");

    Assertions.assertNull(encontrada);
  }

  @Test
  public void deveEncontrarDisciplinaPorNome() {
    DisciplinaRepository repository = criarRepository();
    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));

    Disciplina encontrada = repository.buscarPorNome("Estrutura de Dados");

    Assertions.assertNotNull(encontrada);
    Assertions.assertEquals("Estrutura de Dados", encontrada.getNome());
  }

  @Test
  public void deveEncontrarDisciplinaPorNomeIgnorandoCase() {
    DisciplinaRepository repository = criarRepository();
    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));

    Disciplina encontrada = repository.buscarPorNome("estrutura de dados");

    Assertions.assertNotNull(encontrada);
    Assertions.assertEquals("Estrutura de Dados", encontrada.getNome());
  }

  @Test
  public void deveRetornarNuloAoBuscarPorNomeInexistente() {
    DisciplinaRepository repository = criarRepository();
    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));

    Disciplina encontrada = repository.buscarPorNome("Cálculo Diferencial");

    Assertions.assertNull(encontrada);
  }

  @Test
  public void deveRetornarNuloAoBuscarPorNomeComListaVazia() {
    DisciplinaRepository repository = criarRepository();

    Disciplina encontrada = repository.buscarPorNome("Estrutura de Dados");

    Assertions.assertNull(encontrada);
  }

  @Test
  public void deveEncontrarDisciplinasDoCurso() {
    DisciplinaRepository repository = criarRepository();

    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));
    repository.salvarDisciplina(criarDisciplina("DIS002", "Algoritmos", "cur01"));
    repository.salvarDisciplina(criarDisciplina("DIS003", "Banco de Dados", "cur02"));

    List<Disciplina> disciplinasCur01 = repository.buscarPorCurso("cur01");

    Assertions.assertEquals(2, disciplinasCur01.size());
  }

  @Test
  public void deveEncontrarDisciplinasDoCursoIgnorandoCase() {
    DisciplinaRepository repository = criarRepository();

    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));
    repository.salvarDisciplina(criarDisciplina("DIS002", "Algoritmos", "cur01"));

    List<Disciplina> encontradas = repository.buscarPorCurso("CUR01");

    Assertions.assertEquals(2, encontradas.size());
  }

  @Test
  public void deveRetornarListaVaziaAoBuscarPorCursoInexistente() {
    DisciplinaRepository repository = criarRepository();

    repository.salvarDisciplina(criarDisciplina("DIS001", "Estrutura de Dados", "cur01"));

    List<Disciplina> encontradas = repository.buscarPorCurso("cur99");

    Assertions.assertNotNull(encontradas);
    Assertions.assertTrue(encontradas.isEmpty());
  }

  @Test
  public void deveRetornarListaVaziaAoBuscarPorCursoComRepositorioVazio() {
    DisciplinaRepository repository = criarRepository();

    List<Disciplina> encontradas = repository.buscarPorCurso("cur01");

    Assertions.assertNotNull(encontradas);
    Assertions.assertTrue(encontradas.isEmpty());
  }
}
