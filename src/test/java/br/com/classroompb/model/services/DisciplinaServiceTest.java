package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
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
 * Testes do servico de disciplinas.
 */
public class DisciplinaServiceTest {

  @TempDir Path tempDir;

  DisciplinaRepository repository;
  CursoRepository cursoRepository;
  DisciplinaService service;
  CursoService cursoService;

  /**
   * Prepara as variaveis para os testes.
   */
  @BeforeEach
  public void criarVariaveis() {
    repository = criarDisciplinaRepository();
    cursoRepository = criarCursoRepository();
    service = new DisciplinaService(repository, cursoRepository);
    cursoService = new CursoService(cursoRepository);
  }

  /**
   * Limpa os arquivos gerados pelos testes.
   */
  @AfterEach
  public void tearDown() {
    limparDiretorio(tempDir.resolve("disciplinas").toFile());
    limparDiretorio(tempDir.resolve("cursos").toFile());
  }

  private void limparDiretorio(File diretorio) {
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

  private DisciplinaRepository criarDisciplinaRepository() {
    return new DisciplinaRepository(new ObjectMapper(), tempDir.resolve("disciplinas").toString());
  }

  private CursoRepository criarCursoRepository() {
    return new CursoRepository(new ObjectMapper(), tempDir.resolve("cursos").toString());
  }

  private String cadastrarCursoApoio(String nome, int periodos, int cargaHoraria) {
    cursoService.cadastrarCurso(new Curso(nome, periodos, cargaHoraria));
    return cursoRepository.buscarPorNome(nome).getCodigo();
  }

  private Disciplina novaDisciplina(
      String nome, int cargaHoraria, int periodo, String codigoCurso) {
    return new Disciplina(nome, cargaHoraria, periodo, 4, codigoCurso, null);
  }

  @Test
  public void deveCadastrarDisciplina() {
    String codigoCurso = cadastrarCursoApoio("Ciência da Computação", 8, 3000);

    Disciplina disciplina = novaDisciplina("Algoritmos", 60, 1, codigoCurso);
    service.cadastrarDisciplina(disciplina);

    Assertions.assertEquals(1, repository.listarDisciplinas().size());
    Assertions.assertEquals("Algoritmos", repository.listarDisciplinas().getFirst().getNome());
  }

  @Test
  public void deveCadastrarDisciplinaSemPreRequisitos() {
    String codigoCurso = cadastrarCursoApoio("Engenharia de Software", 8, 3200);

    service.cadastrarDisciplina(novaDisciplina("Engenharia de Requisitos", 60, 3, codigoCurso));

    Assertions.assertEquals(1, repository.listarDisciplinas().size());
  }

  @Test
  public void deveCadastrarDisciplinaComPreRequisitoValido() {
    String codigoCurso = cadastrarCursoApoio("Sistemas de Informação", 8, 3000);

    service.cadastrarDisciplina(novaDisciplina("Lógica de Programação", 60, 1, codigoCurso));
    String codigoPreReq = repository.listarDisciplinas().getFirst().getCodigo();

    Disciplina disc2 =
        new Disciplina("Estrutura de Dados", 60, 2, 4, codigoCurso, List.of(codigoPreReq));
    service.cadastrarDisciplina(disc2);

    Assertions.assertEquals(2, repository.listarDisciplinas().size());
  }

  @Test
  public void deveCadastrarDisciplinaComListaDePreRequisitosVazia() {
    String codigoCurso = cadastrarCursoApoio("Letras", 8, 3000);

    Disciplina disciplina =
        new Disciplina("Literatura Brasileira", 60, 2, 4, codigoCurso, List.of());
    service.cadastrarDisciplina(disciplina);

    Assertions.assertEquals(1, repository.listarDisciplinas().size());
  }

  @Test
  public void deveAtribuirCodigoAutomaticoAoCadastrar() {
    String codigoCurso = cadastrarCursoApoio("Farmácia", 10, 4000);

    service.cadastrarDisciplina(novaDisciplina("Bioquímica", 60, 1, codigoCurso));

    String codigo = repository.listarDisciplinas().getFirst().getCodigo();
    Assertions.assertNotNull(codigo);
    Assertions.assertFalse(codigo.isBlank());
  }

  @Test
  public void deveGerarCodigoCorretoParaPrimeiraDisciplina() {
    String codigoCurso = cadastrarCursoApoio("Medicina", 10, 4000);

    service.cadastrarDisciplina(novaDisciplina("Anatomia", 60, 1, codigoCurso));

    Assertions.assertEquals("dis00", repository.listarDisciplinas().getFirst().getCodigo());
  }

  @Test
  public void deveGerarCodigoSequencialParaCadaDisciplina() {
    String codigoCurso = cadastrarCursoApoio("Odontologia", 10, 4000);

    service.cadastrarDisciplina(novaDisciplina("Periodontia", 60, 1, codigoCurso));
    service.cadastrarDisciplina(novaDisciplina("Endodontia", 60, 2, codigoCurso));

    List<Disciplina> disciplinas = repository.listarDisciplinas();
    Assertions.assertEquals("dis00", disciplinas.get(0).getCodigo());
    Assertions.assertEquals("dis01", disciplinas.get(1).getCodigo());
  }

  @Test
  public void deveCadastrarDisciplinasComCodigosDistintos() {
    String codigoCurso = cadastrarCursoApoio("Nutrição", 8, 3200);

    service.cadastrarDisciplina(novaDisciplina("Nutrição Clínica", 60, 1, codigoCurso));
    service.cadastrarDisciplina(novaDisciplina("Bioquímica Nutricional", 60, 2, codigoCurso));
    service.cadastrarDisciplina(novaDisciplina("Fisiologia", 60, 3, codigoCurso));

    long codigosDistintos =
        repository.listarDisciplinas().stream().map(Disciplina::getCodigo).distinct().count();

    Assertions.assertEquals(3, codigosDistintos);
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaComNomeVazio() {
    String codigoCurso = cadastrarCursoApoio("Direito", 10, 4000);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> new Disciplina("", 60, 1, 4, codigoCurso, null));
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaComCargaHorariaZero() {
    String codigoCurso = cadastrarCursoApoio("Administração", 8, 3000);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new Disciplina("Gestão de Projetos", 0, 1, 4, codigoCurso, null));
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaComCargaHorariaAcimaDoMaximo() {
    String codigoCurso = cadastrarCursoApoio("Engenharia Civil", 10, 4000);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new Disciplina("Mecânica dos Solos", 301, 1, 4, codigoCurso, null));
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaComPeriodoZero() {
    String codigoCurso = cadastrarCursoApoio("Arquitetura", 8, 3200);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new Disciplina("Projeto Arquitetônico", 60, 0, 4, codigoCurso, null));
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaComPeriodoAcimaDoMaximo() {
    String codigoCurso = cadastrarCursoApoio("Psicologia", 10, 3000);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new Disciplina("Psicoterapia", 60, 13, 4, codigoCurso, null));
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaComCreditosZero() {
    String codigoCurso = cadastrarCursoApoio("Filosofia", 8, 3000);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> new Disciplina("Ética", 60, 1, 0, codigoCurso, null));
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaComCodigoCursoVazio() {
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> new Disciplina("Álgebra", 60, 1, 4, "", null));
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaComFormatoCodigoCursoInvalido() {
    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new Disciplina("Álgebra", 60, 1, 4, "disciplina01", null));
  }

  @Test
  public void deveLancarExceptionQuandoPreRequisitoTemFormatoInvalido() {
    String codigoCurso = cadastrarCursoApoio("Ciências Contábeis", 8, 3000);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> new Disciplina("Auditoria", 60, 4, 4, codigoCurso, List.of("INVALIDO")));
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaNull() {
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.cadastrarDisciplina(null));
  }

  @Test
  public void deveLancarExceptionAoCadastrarDisciplinaComNomeDuplicado() {
    String codigoCurso = cadastrarCursoApoio("Redes de Computadores", 6, 2800);

    service.cadastrarDisciplina(novaDisciplina("Redes I", 60, 1, codigoCurso));

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.cadastrarDisciplina(novaDisciplina("Redes I", 80, 2, codigoCurso)));
  }

  @Test
  public void deveManterIntegridadeDaListaAposErroDeNomeDuplicado() {
    String codigoCurso = cadastrarCursoApoio("Física", 8, 3200);

    service.cadastrarDisciplina(novaDisciplina("Física I", 60, 1, codigoCurso));

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> service.cadastrarDisciplina(novaDisciplina("Física I", 80, 2, codigoCurso)));

    Assertions.assertEquals(1, service.listarDisciplinas().size());
  }

  @Test
  public void deveLancarExceptionQuandoCursoNaoExistir() {
    // cur99 não foi cadastrado
    Disciplina disciplina = novaDisciplina("Disciplina Órfã", 60, 1, "cur99");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.cadastrarDisciplina(disciplina));
  }

  @Test
  public void deveLancarExceptionQuandoPeriodoDisciplinaExcedePeriodosDoCurso() {
    // Curso tem 6 períodos; período 7 supera esse limite no service
    String codigoCurso = cadastrarCursoApoio("Biomedicina", 6, 3000);

    Disciplina disciplina = novaDisciplina("Hematologia", 60, 7, codigoCurso);

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.cadastrarDisciplina(disciplina));
  }

  @Test
  public void deveCadastrarDisciplinaComPeriodoIgualAoMaximoDoCurso() {
    // Período exatamente no limite do curso — deve ser aceito
    String codigoCurso = cadastrarCursoApoio("Psicologia", 10, 4000);

    service.cadastrarDisciplina(novaDisciplina("Psicopatologia", 60, 10, codigoCurso));

    Assertions.assertEquals(1, repository.listarDisciplinas().size());
  }

  @Test
  public void deveLancarExceptionQuandoPreRequisitoNaoExistirNoRepositorio() {
    String codigoCurso = cadastrarCursoApoio("Marketing", 8, 3000);

    // dis99 tem formato válido mas não está cadastrado
    Disciplina disciplina =
        new Disciplina("Marketing Digital", 60, 3, 4, codigoCurso, List.of("dis99"));

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.cadastrarDisciplina(disciplina));
  }

  @Test
  public void deveLancarExceptionQuandoUmDosPreRequisitosNaoExistirNoRepositorio() {
    String codigoCurso = cadastrarCursoApoio("Publicidade", 8, 3000);

    service.cadastrarDisciplina(novaDisciplina("Fundamentos de Publicidade", 60, 1, codigoCurso));
    String codigoValido = repository.listarDisciplinas().getFirst().getCodigo();

    // mistura um código válido e um inexistente
    Disciplina disciplina =
        new Disciplina(
            "Publicidade Digital", 60, 3, 4, codigoCurso, List.of(codigoValido, "dis99"));

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> service.cadastrarDisciplina(disciplina));
  }

  @Test
  public void deveListarTodasAsDisciplinasCadastradas() {
    String codigoCurso = cadastrarCursoApoio("Jornalismo", 8, 3000);

    service.cadastrarDisciplina(novaDisciplina("Jornalismo Impresso", 60, 1, codigoCurso));
    service.cadastrarDisciplina(novaDisciplina("Jornalismo Digital", 60, 2, codigoCurso));
    service.cadastrarDisciplina(novaDisciplina("Telejornalismo", 60, 3, codigoCurso));

    List<Disciplina> lista = service.listarDisciplinas();

    Assertions.assertEquals(3, lista.size());
    Assertions.assertEquals("Jornalismo Impresso", lista.get(0).getNome());
    Assertions.assertEquals("Jornalismo Digital", lista.get(1).getNome());
    Assertions.assertEquals("Telejornalismo", lista.get(2).getNome());
  }

  @Test
  public void deveRetornarListaVaziaSeNaoHouverDisciplinasCadastradas() {
    Assertions.assertTrue(service.listarDisciplinas().isEmpty());
  }

  @Test
  public void deveListarDisciplinasRetornarMesmaQuantidadeDoRepository() {
    String codigoCurso = cadastrarCursoApoio("Design Gráfico", 8, 3000);

    service.cadastrarDisciplina(novaDisciplina("Tipografia", 60, 1, codigoCurso));
    service.cadastrarDisciplina(novaDisciplina("Ilustração Digital", 60, 2, codigoCurso));

    Assertions.assertEquals(
        repository.listarDisciplinas().size(), service.listarDisciplinas().size());
  }

  @Test
  public void deveListarDisciplinasPorCurso() {
    String codigoCurso1 = cadastrarCursoApoio("Engenharia de Software", 8, 3200);
    String codigoCurso2 = cadastrarCursoApoio("Análise e Desenvolvimento de Sistemas", 6, 2800);

    service.cadastrarDisciplina(novaDisciplina("Engenharia de Requisitos", 60, 1, codigoCurso1));
    service.cadastrarDisciplina(novaDisciplina("Arquitetura de Software", 60, 3, codigoCurso1));
    service.cadastrarDisciplina(novaDisciplina("Banco de Dados", 60, 2, codigoCurso2));

    Assertions.assertEquals(2, service.listarDisciplinasPorCurso(codigoCurso1).size());
    Assertions.assertEquals(1, service.listarDisciplinasPorCurso(codigoCurso2).size());
  }

  @Test
  public void deveRetornarListaVaziaAoListarDisciplinasPorCursoSemDisciplinas() {
    String codigoCurso = cadastrarCursoApoio("Relações Internacionais", 8, 4000);

    Assertions.assertTrue(service.listarDisciplinasPorCurso(codigoCurso).isEmpty());
  }

  @Test
  public void deveRetornarApenasAsDisciplinasDoCursoCorreto() {
    String codigoCurso1 = cadastrarCursoApoio("Design de Moda", 8, 3000);
    String codigoCurso2 = cadastrarCursoApoio("Design de Interiores", 8, 3200);

    service.cadastrarDisciplina(novaDisciplina("Tipografia", 60, 1, codigoCurso1));
    service.cadastrarDisciplina(novaDisciplina("Moulage", 60, 2, codigoCurso1));
    service.cadastrarDisciplina(novaDisciplina("História da Arte", 60, 1, codigoCurso2));

    List<Disciplina> disciplinasCurso1 = service.listarDisciplinasPorCurso(codigoCurso1);

    Assertions.assertEquals(2, disciplinasCurso1.size());
    Assertions.assertTrue(
        disciplinasCurso1.stream().allMatch(d -> d.getCodigoCurso().equals(codigoCurso1)));
  }
}
