package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Avaliacao;
import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.AvaliacaoRepository;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.DiarioRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes unitarios focados na User Story de Fechamento do Diario e Consolidação.
 *
 * <p>Inclui testes de regressão para os refatoramentos "valida carga horaria no fechamento do
 * diario" e "transfere fechamento do diario para professor". Os servicos auxiliares (avaliacoes
 * e aulas) sao construidos sobre os mesmos repositorios do diario de teste, para que o
 * fechamento enxergue corretamente os dados cadastrados no cenario.
 */
public class FechamentoDiarioTest {

  private static final String CODIGO_CURSO = "cur00";

  @TempDir Path tempDir;

  private DiarioRepository diarioRepository;
  private TurmaRepository turmaRepository;
  private DisciplinaRepository disciplinaRepository;
  private UserRepository userRepository;
  private BoletimRepository boletimRepository;
  private PeriodoLetivoRepository periodoLetivoRepository;
  private AulaRepository aulaRepository;

  private DiarioService diarioService;
  private BoletimService boletimService;
  private AvaliacaoService avaliacaoService;
  private AulaService aulaService;

  /**
   * Prepara os repositórios e serviços utilizados nos testes de fechamento de diário.
   */
  @BeforeEach
  public void setUp() {
    ObjectMapper mapper = new ObjectMapper();

    diarioRepository = new DiarioRepository(mapper, tempDir.resolve("diarios").toString());
    turmaRepository = new TurmaRepository(mapper, tempDir.resolve("turmas").toString());
    disciplinaRepository =
        new DisciplinaRepository(mapper, tempDir.resolve("disciplinas").toString());
    userRepository = new UserRepository(mapper, tempDir.resolve("usuarios").toString());
    boletimRepository = new BoletimRepository(mapper, tempDir.resolve("boletins").toString());
    periodoLetivoRepository =
        new PeriodoLetivoRepository(mapper, tempDir.resolve("periodos").toString());
    aulaRepository = new AulaRepository(mapper, tempDir.resolve("aulas").toString());

    // O AvaliacaoService e o AulaService precisam compartilhar os mesmos repositorios de
    // diario e turma usados pelo DiarioService, senao o fechamento nao enxerga os dados
    // cadastrados no cenario de teste (cada um leria de um diretorio de persistencia diferente).
    avaliacaoService =
        new AvaliacaoService(
            new AvaliacaoRepository(mapper, tempDir.resolve("diarios").toString()),
            diarioRepository,
            turmaRepository,
            userRepository);
    aulaService = new AulaService(aulaRepository, turmaRepository, diarioRepository);

    diarioService =
        new DiarioService(
            diarioRepository,
            turmaRepository,
            disciplinaRepository,
            userRepository,
            avaliacaoService,
            aulaService);
    boletimService =
        new BoletimService(
            boletimRepository, turmaRepository, periodoLetivoRepository, diarioRepository);

    periodoLetivoRepository.salvarPeriodoLetivo(
        new PeriodoLetivo("2026.2", "01/07/2026", "30/11/2026"));
  }

  /**
   * Remove os diretórios temporários criados durante os testes.
   */
  @AfterEach
  public void tearDown() {
    apagarDiretorio("diarios");
    apagarDiretorio("turmas");
    apagarDiretorio("disciplinas");
    apagarDiretorio("usuarios");
    apagarDiretorio("boletins");
    apagarDiretorio("periodos");
    apagarDiretorio("aulas");
  }

  private void apagarDiretorio(String nomeDiretorio) {
    File diretorio = tempDir.resolve(nomeDiretorio).toFile();
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

  private void prepararAmbienteCompleto() {
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, CODIGO_CURSO, List.of()));

    Professor professor = new Professor("João", "pr00@email.com", "senha123");
    professor.setMatricula("pr00");
    professor.setCodigoCurso(CODIGO_CURSO);
    userRepository.salvarUsuario(professor);

    br.com.classroompb.model.entities.usuario.Aluno aluno =
        new br.com.classroompb.model.entities.usuario.Aluno(
            "Aluno", "al00@email.com", "senha123", CODIGO_CURSO);
    aluno.setMatricula("al00");
    userRepository.salvarUsuario(aluno);

    Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turma.getMatriculados().add("al00");
    turmaRepository.salvarTurma(turma);
  }

  private Diario criarDiario(int cargaHoraria) {
    return new Diario("tur00", "Diário Teórico", "pr00", "SEG 08:00", "LAB 01", cargaHoraria);
  }

  private void registrarAvaliacaoPadrao(Diario diario) {
    Avaliacao avaliacao = new Avaliacao(diario.getCodigo(), "P1", 1.0, 1, 10.0);
    avaliacaoService.cadastrarAvaliacao(avaliacao, "pr00");
  }

  /**
   * Cadastra uma avaliacao padrao no diario e lanca a nota do aluno "al00", deixando o diario
   * apto a ser encerrado (sem avaliacoes/notas pendentes).
   */
  private void registrarAvaliacaoComNotaLancada(Diario diario) {
    Avaliacao avaliacao = new Avaliacao(diario.getCodigo(), "P1", 1.0, 1, 10.0);
    avaliacaoService.cadastrarAvaliacao(avaliacao, "pr00");
    avaliacaoService.lancarNota(avaliacao.getCodigo(), "al00", 8.0, "pr00");
  }

  /**
   * Registra, para o diario informado, aulas suficientes para cumprir exatamente a carga
   * horaria configurada (2 horas por aula).
   *
   * @param diario diario que recebera as aulas.
   */
  private void registrarAulasCompletas(Diario diario) {
    int quantidadeAulas = diario.getCargaHoraria() / 2;
    for (int i = 0; i < quantidadeAulas; i++) {
      Map<String, Boolean> presencas = new HashMap<>();
      presencas.put("al00", true);
      Aula aula = aulaService.gerarAula(diario, presencas);
      aulaService.salvarAula(aula, "pr00");
    }
  }

  @Test
  public void deveLancarExcecaoAoEncerrarDiarioSemAvaliacoes() {
    prepararAmbienteCompleto();
    Diario diario = criarDiario(4);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);
    registrarAulasCompletas(diario);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> diarioService.encerrarDiario(diario.getCodigo(), "pr00"));
  }

  @Test
  public void deveEncerrarDiarioAlterarSituacaoParaEncerrado() {
    prepararAmbienteCompleto();
    Diario diario = criarDiario(4);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);
    registrarAulasCompletas(diario);
    registrarAvaliacaoComNotaLancada(diario);

    diarioService.encerrarDiario(diario.getCodigo(), "pr00");

    Diario diarioEncerrado = diarioService.buscarDiarioPorCodigo(diario.getCodigo());
    Assertions.assertEquals(SituacaoDiario.ENCERRADO, diarioEncerrado.getSituacao());
  }

  @Test
  public void deveBloquearLancamentoDeNotasQuandoDiarioEstiverEncerrado() {
    prepararAmbienteCompleto();
    Diario diario = criarDiario(4);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);
    registrarAulasCompletas(diario);
    registrarAvaliacaoComNotaLancada(diario);

    Boletim boletim = new Boletim("al00", "tur00");
    boletimService.criarBoletim(boletim);

    diarioService.encerrarDiario(diario.getCodigo(), "pr00");

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarNotas("tur00", "al00", 9.0f, 9.0f, "pr00"));
  }

  @Test
  public void deveConsolidarMediaFinalNoBoletimAoLancarNotas() {
    prepararAmbienteCompleto();
    Diario diario = criarDiario(4);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);

    Boletim boletim = new Boletim("al00", "tur00");
    boletimService.criarBoletim(boletim);

    boletimService.lancarNotas("tur00", "al00", 8.0f, 10.0f, "pr00");

    Boletim boletimConsolidado = boletimService.buscarBoletimPorAlunoTurma("al00", "tur00");
    Assertions.assertNotNull(boletimConsolidado);
    Assertions.assertEquals(9.0f, boletimConsolidado.calcularMediaFinal());
  }

  @Test
  public void deveManterHistoricoDoDiarioEncerradoNoRepositorio() {
    prepararAmbienteCompleto();
    Diario diario = criarDiario(4);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);
    registrarAulasCompletas(diario);
    registrarAvaliacaoComNotaLancada(diario);

    diarioService.encerrarDiario(diario.getCodigo(), "pr00");

    List<Diario> diariosGuardados = diarioService.listarDiariosPorTurma("tur00");
    Assertions.assertEquals(1, diariosGuardados.size());
    Assertions.assertEquals(SituacaoDiario.ENCERRADO, diariosGuardados.get(0).getSituacao());
  }

  // --- Regressão: refactor "valida carga horaria no fechamento do diario" (005bc21) ---

  @Test
  public void deveLancarExcecaoAoEncerrarDiarioComCargaHorariaIncompleta() {
    prepararAmbienteCompleto();
    Diario diario = criarDiario(4);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);
    registrarAvaliacaoPadrao(diario);
    // Apenas uma aula (2h) registrada, mas o diario exige 4h.

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("al00", true);
    Aula aula = aulaService.gerarAula(diario, presencas);
    aulaService.salvarAula(aula, "pr00");

    EntradaInvalidaException excecao =
        Assertions.assertThrows(
            EntradaInvalidaException.class,
            () -> diarioService.encerrarDiario(diario.getCodigo(), "pr00"));

    Assertions.assertTrue(excecao.getMessage().toLowerCase().contains("incompleta"));
  }

  @Test
  public void deveLancarExcecaoAoEncerrarDiarioSemNenhumaAulaRegistrada() {
    prepararAmbienteCompleto();
    Diario diario = criarDiario(4);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);
    registrarAvaliacaoPadrao(diario);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> diarioService.encerrarDiario(diario.getCodigo(), "pr00"));
  }

  @Test
  public void devePermitirEncerrarDiarioQuandoCargaHorariaForCumpridaExatamente() {
    prepararAmbienteCompleto();
    Diario diario = criarDiario(6);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);
    registrarAulasCompletas(diario);
    registrarAvaliacaoComNotaLancada(diario);

    diarioService.encerrarDiario(diario.getCodigo(), "pr00");

    Diario diarioEncerrado = diarioService.buscarDiarioPorCodigo(diario.getCodigo());
    Assertions.assertEquals(SituacaoDiario.ENCERRADO, diarioEncerrado.getSituacao());
  }

  @Test
  public void naoDevePermitirRegistrarAulaAlemDaCargaHorariaDoDiario() {
    prepararAmbienteCompleto();
    Diario diario = criarDiario(2);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);
    registrarAulasCompletas(diario);

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("al00", true);
    Aula aulaExcedente = aulaService.gerarAula(diario, presencas);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> aulaService.salvarAula(aulaExcedente, "pr00"));
  }
}
