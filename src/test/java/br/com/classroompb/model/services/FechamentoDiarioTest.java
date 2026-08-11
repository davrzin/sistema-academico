package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Avaliacao;
import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.DiarioRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
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
 * Testes unitarios focados na User Story de Fechamento do Diario e Consolidação.
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

  private DiarioService diarioService;
  private BoletimService boletimService;
  private AvaliacaoService avaliacaoService;

  @BeforeEach
  public void setUp() {
    diarioRepository =
        new DiarioRepository(new ObjectMapper(), tempDir.resolve("diarios").toString());
    turmaRepository =
        new TurmaRepository(new ObjectMapper(), tempDir.resolve("turmas").toString());
    disciplinaRepository =
        new DisciplinaRepository(new ObjectMapper(), tempDir.resolve("disciplinas").toString());
    userRepository =
        new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
    boletimRepository =
        new BoletimRepository(new ObjectMapper(), tempDir.resolve("boletins").toString());
    periodoLetivoRepository =
        new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());

    diarioService =
        new DiarioService(
            diarioRepository, turmaRepository, disciplinaRepository, userRepository);
    boletimService =
        new BoletimService(
            boletimRepository, turmaRepository, periodoLetivoRepository, diarioRepository);
    avaliacaoService = new AvaliacaoService();

    periodoLetivoRepository.salvarPeriodoLetivo(
        new PeriodoLetivo("2026.2", "01/07/2026", "30/11/2026"));
  }

  @AfterEach
  public void tearDown() {
    apagarDiretorio("diarios");
    apagarDiretorio("turmas");
    apagarDiretorio("disciplinas");
    apagarDiretorio("usuarios");
    apagarDiretorio("boletins");
    apagarDiretorio("periodos");
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

    Turma turma =
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turma.getMatriculados().add("al00");
    turmaRepository.salvarTurma(turma);
  }

  @Test
  public void deveLancarExcecaoAoEncerrarDiarioSemAvaliacoes() {
    prepararAmbienteCompleto();
    Diario diario =
        new Diario("tur00", "Diário Teórico", "pr00", "SEG 08:00", "LAB 01", 60);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> diarioService.encerrarDiario(diario.getCodigo(), CODIGO_CURSO));
  }

  @Test
  public void deveEncerrarDiarioEAlterarSituacaoParaEncerrado() {
    prepararAmbienteCompleto();
    Diario diario =
        new Diario("tur00", "Diário Teórico", "pr00", "SEG 08:00", "LAB 01", 60);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);

    Avaliacao avaliacao = new Avaliacao(diario.getCodigo(), "P1", 1.0, 1, 10.0);
    avaliacaoService.cadastrarAvaliacao(avaliacao, CODIGO_CURSO);

    diarioService.encerrarDiario(diario.getCodigo(), CODIGO_CURSO);

    Diario diarioEncerrado = diarioService.buscarDiarioPorCodigo(diario.getCodigo());
    Assertions.assertEquals(SituacaoDiario.ENCERRADO, diarioEncerrado.getSituacao());
  }

  @Test
  public void deveBloquearLancamentoDeNotasQuandoDiarioEstiverEncerrado() {
    prepararAmbienteCompleto();

    Diario diario =
        new Diario("tur00", "Diário Teórico", "pr00", "SEG 08:00", "LAB 01", 60);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);

    Avaliacao avaliacao = new Avaliacao(diario.getCodigo(), "P1", 1.0, 1, 10.0);
    avaliacaoService.cadastrarAvaliacao(avaliacao, CODIGO_CURSO);

    Boletim boletim = new Boletim("al00", "tur00");
    boletimService.criarBoletim(boletim);

    diarioService.encerrarDiario(diario.getCodigo(), CODIGO_CURSO);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> boletimService.lancarNotas("tur00", "al00", 9.0f, 9.0f, "pr00"));
  }

  @Test
  public void deveConsolidarMediaFinalNoBoletimAoLancarNotas() {
    prepararAmbienteCompleto();

    Diario diario =
        new Diario("tur00", "Diário Teórico", "pr00", "SEG 08:00", "LAB 01", 60);
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

    Diario diario =
        new Diario("tur00", "Diário Teórico", "pr00", "SEG 08:00", "LAB 01", 60);
    diarioService.cadastrarDiario(diario, CODIGO_CURSO);

    Avaliacao avaliacao = new Avaliacao(diario.getCodigo(), "P1", 1.0, 1, 10.0);
    avaliacaoService.cadastrarAvaliacao(avaliacao, CODIGO_CURSO);

    diarioService.encerrarDiario(diario.getCodigo(), CODIGO_CURSO);

    List<Diario> diariosGuardados = diarioService.listarDiariosPorTurma("tur00");
    Assertions.assertEquals(1, diariosGuardados.size());
    Assertions.assertEquals(SituacaoDiario.ENCERRADO, diariosGuardados.get(0).getSituacao());
  }
}