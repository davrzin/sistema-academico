package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do repositorio de cursos.
 */
public class CursoRepositoryTest {

  @TempDir Path tempDir;

  CursoRepository repository;

  /**
   * Prepara as variaveis para os testes.
   */
  @BeforeEach
  public void criarVariaveis() {
    repository = criarCursoRepository();
  }

  /**
   * Limpa os arquivos gerados pelos testes.
   */
  @AfterEach
  public void tearDown() {
    File diretorio = tempDir.resolve("cursos").toFile();
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

  private CursoRepository criarCursoRepository() {
    return new CursoRepository(new ObjectMapper(), tempDir.resolve("cursos").toString());
  }

  @Test
  public void deveRetornarObjectMapper() {

    Assertions.assertEquals(ObjectMapper.class, repository.getObjectMapper().getClass());
  }

  @Test
  public void deveDefinirObjectMapperCorretamente() {

    ObjectMapper novoMapper = new ObjectMapper();
    repository.setObjectMapper(novoMapper);

    Assertions.assertSame(novoMapper, repository.getObjectMapper());
  }

  @Test
  public void deveRetornarDiretorioCursos() {

    Assertions.assertNotNull(repository.getDiretorioCursos());
  }

  @Test
  public void deveSalvarCurso() {
    Curso curso = new Curso("Ciência da Computação", 8, 3000);

    Assertions.assertTrue(repository.salvarCurso(curso));
  }

  @Test
  public void deveLancarIllegalArgumentExceptioAoSalvarCursoNull() {

    Assertions.assertThrows(IllegalArgumentException.class, () -> repository.salvarCurso(null));
  }

  @Test
  public void deveListarCursosSalvos() {
    Curso curso = new Curso("321", "Ciência da Computação", 8, 3000);

    Curso curso2 = new Curso("123", "Ciências Contábeis", 8, 3000);

    repository.salvarCurso(curso);
    repository.salvarCurso(curso2);

    List<Curso> listaCursos = repository.listarCursos();

    Assertions.assertEquals(2, listaCursos.size());
    Assertions.assertEquals(curso.getNome(), listaCursos.getFirst().getNome());
    Assertions.assertEquals(curso2.getNome(), listaCursos.getLast().getNome());
  }

  @Test
  public void deveRetornarListaVaziaDeCursos() {

    List<Curso> listaCursos = repository.listarCursos();

    Assertions.assertTrue(listaCursos.isEmpty());
  }

  @Test
  public void deveEncontarCursoComCodigoCorreto() {

    Curso curso = new Curso("123", "Ciência da Computação", 8, 3000);

    repository.salvarCurso(curso);

    Assertions.assertNotNull(repository.buscarPorCodigo(curso.getCodigo()));
  }

  @Test
  public void deveRetornarNullSeNaoEncontrarCursoPorCodigo() {

    Curso curso = new Curso("123", "Ciência da Computação", 8, 3000);

    repository.salvarCurso(curso);

    Assertions.assertNull(repository.buscarPorCodigo("Farmácia"));
  }

  @Test
  public void deveEncontarCursoComNomeCorreto() {

    Curso curso = new Curso("123", "Ciência da Computação", 8, 3000);

    repository.salvarCurso(curso);

    Assertions.assertNotNull(repository.buscarPorNome("Ciência da Computação"));
  }

  @Test
  public void deveRetornarNullSeNaoEncontrarCursoPorNome() {

    Curso curso = new Curso("123", "Ciência da Computação", 8, 3000);

    repository.salvarCurso(curso);

    Assertions.assertNull(repository.buscarPorNome("Farmácia"));
  }

  @Test
  public void deveAtualizarCursoCorretamente() {

    Curso curso = new Curso("123", "Ciência da Computação", 8, 3000);
    repository.salvarCurso(curso);

    Curso cursoAtualizado = new Curso("123", "Engenharia de Software", 10, 3600);

    Assertions.assertTrue(repository.atualizarCurso(cursoAtualizado));
    Curso cursoPersistido = repository.buscarPorCodigo("123");
    Assertions.assertEquals("Engenharia de Software", cursoPersistido.getNome());
    Assertions.assertEquals(10, cursoPersistido.getQuantidadePeriodos());
    Assertions.assertEquals(3600, cursoPersistido.getCargaHorariaTotal());
  }

  @Test
  public void deveRetornarFalseAoAtualizarCursoInexistente() {

    Curso curso = new Curso("123", "Ciência da Computação", 8, 3000);
    repository.salvarCurso(curso);

    Curso cursoInexistente = new Curso("999", "Farmácia", 10, 3600);

    Assertions.assertFalse(repository.atualizarCurso(cursoInexistente));
  }

  @Test
  public void deveLancarIllegalArgumentExceptionAoAtualizarCursoNull() {

    Assertions.assertThrows(IllegalArgumentException.class, () -> repository.atualizarCurso(null));
  }

  @Test
  public void deveRemoverCursoPorCodigoCorretamente() {

    Curso curso = new Curso("123", "Ciência da Computação", 8, 3000);
    repository.salvarCurso(curso);

    Assertions.assertTrue(repository.removerPorCodigo("123"));
    Assertions.assertTrue(repository.listarCursos().isEmpty());
  }

  @Test
  public void deveRetornarFalseAoRemoverCursoInexistente() {

    Curso curso = new Curso("123", "Ciência da Computação", 8, 3000);
    repository.salvarCurso(curso);

    Assertions.assertFalse(repository.removerPorCodigo("999"));
    Assertions.assertEquals(1, repository.listarCursos().size());
  }
}
