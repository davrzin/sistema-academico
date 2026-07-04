package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.CursoRepository;
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
 * Testes do servico de cursos.
 */
public class CursoServiceTest {

  @TempDir Path tempDir;

  CursoRepository repository;
  CursoService service;

  /**
   * Prepara as variaveis para os testes.
   */
  @BeforeEach
  public void criarVariaveis() {
    repository = criarCursoRepository();
    service = criarCursoService(repository);
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
    return new CursoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
  }

  private CursoService criarCursoService(CursoRepository cursoRepository) {
    return new CursoService(cursoRepository);
  }

  @Test
  public void deveCadastrarCurso() {

    Curso curso = new Curso("Ciência da Computação", 8, 3000);

    service.cadastrarCurso(curso);

    Assertions.assertEquals(1, repository.listarCursos().size());
    Assertions.assertEquals(curso.getNome(), repository.listarCursos().getFirst().getNome());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCadastrarCursoNull() {

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarCurso(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCadastrarCursoJaExistente() {
    Curso curso = new Curso("Ciência da Computação", 8, 3000);

    Curso curso2 = new Curso("Ciência da Computação", 8, 3000);

    service.cadastrarCurso(curso);

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarCurso(curso2));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionQuandoNomeJaExistir() {
    Curso curso = new Curso("Ciência da Computação", 8, 3000);

    Curso curso2 = new Curso("Ciência da Computação", 10, 4000);

    service.cadastrarCurso(curso);

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarCurso(curso2));
  }

  @Test
  public void deveListarTodosOsCursosCadastrados() {
    Curso curso = new Curso("Ciência da Computação", 8, 3000);

    Curso curso2 = new Curso("Medicina", 10, 4000);

    Curso curso3 = new Curso("Farmácia", 10, 4000);

    service.cadastrarCurso(curso);
    service.cadastrarCurso(curso2);
    service.cadastrarCurso(curso3);

    List<Curso> listaCursos = service.listarCursos();

    Assertions.assertEquals(3, listaCursos.size());
    Assertions.assertEquals(curso.getNome(), listaCursos.getFirst().getNome());
    Assertions.assertEquals(curso2.getNome(), listaCursos.get(1).getNome());
    Assertions.assertEquals(curso3.getNome(), listaCursos.getLast().getNome());
  }

  @Test
  public void deveRetornarListaVaziaSeNaoHouverCursosCadastrados() {

    List<Curso> listaVaziaCursos = service.listarCursos();

    Assertions.assertTrue(listaVaziaCursos.isEmpty());
  }

  @Test
  public void deveGerarCodigoSequencialParaCadaCurso() {
    Curso curso1 = new Curso("Ciência da Computação", 8, 3000);
    Curso curso2 = new Curso("Medicina", 10, 4000);

    service.cadastrarCurso(curso1);
    service.cadastrarCurso(curso2);

    List<Curso> cursos = repository.listarCursos();

    Assertions.assertEquals("cur00", cursos.get(0).getCodigo());
    Assertions.assertEquals("cur01", cursos.get(1).getCodigo());
  }

  @Test
  public void deveGerarCodigoCorretoParaPrimeiroCurso() {
    Curso curso = new Curso("Engenharia de Software", 8, 3200);

    service.cadastrarCurso(curso);

    Assertions.assertEquals("cur00", repository.listarCursos().getFirst().getCodigo());
  }

  @Test
  public void deveCadastrarCursoComNomeDiferenteCodigoDiferente() {
    Curso curso1 = new Curso("Engenharia de Software", 8, 3200);
    Curso curso2 = new Curso("Sistemas de Informação", 8, 3000);
    Curso curso3 = new Curso("Redes de Computadores", 6, 2800);

    service.cadastrarCurso(curso1);
    service.cadastrarCurso(curso2);
    service.cadastrarCurso(curso3);

    List<Curso> cursos = repository.listarCursos();
    long codigosDistintos = cursos.stream().map(Curso::getCodigo).distinct().count();

    Assertions.assertEquals(3, codigosDistintos);
  }

  @Test
  public void deveAtribuirCodigoAutomaticamenteAoCadastrar() {
    Curso curso = new Curso("Farmácia", 10, 4000);

    service.cadastrarCurso(curso);

    Assertions.assertNotNull(repository.listarCursos().getFirst().getCodigo());
    Assertions.assertFalse(repository.listarCursos().getFirst().getCodigo().isBlank());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCadastrarCursoComNomeVazio() {
    Assertions.assertThrows(EntradaInvalidaException.class, () -> new Curso("", 8, 3000));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCadastrarCursoComPeriodosInvalidos() {
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> new Curso("Engenharia de Software", 0, 3200));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCadastrarCursoComCargaHorariaInvalida() {
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> new Curso("Engenharia de Software", 8, 0));
  }

  @Test
  public void deveManterIntegridadeDaListaAposErroDeNomeDuplicado() {
    Curso curso1 = new Curso("Direito", 10, 4000);
    Curso curso2 = new Curso("Direito", 8, 3500);

    service.cadastrarCurso(curso1);

    Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarCurso(curso2));

    Assertions.assertEquals(1, service.listarCursos().size());
  }

  @Test
  public void deveListarCursosRetornarMesmaQuantidadeDoRepository() {
    service.cadastrarCurso(new Curso("Odontologia", 10, 4000));
    service.cadastrarCurso(new Curso("Nutrição", 8, 3200));

    Assertions.assertEquals(repository.listarCursos().size(), service.listarCursos().size());
  }
}
