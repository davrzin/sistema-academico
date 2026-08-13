package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.DiarioRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes isolados da atualizacao de frequencia da turma.
 *
 * <p>Regressão do refactor "remove dependencias ativas de dados legados da turma" (2bf2f7a) e
 * "calcula frequencia por horas aula" (6b31621): a frequencia deixou de considerar aulas
 * anexadas diretamente a lista legada {@code turma.aulas} e passou a exigir que as aulas
 * estejam vinculadas a um diario ativo/encerrado da turma.
 */
public class FrequenciaTurmaServiceTest {

  @TempDir Path tempDir;

  private TurmaRepository turmaRepository;
  private BoletimRepository boletimRepository;
  private AulaRepository aulaRepository;
  private DiarioRepository diarioRepository;
  private TurmaService turmaService;
  private AulaService aulaService;

  /** Prepara repositories temporarios e dados basicos. */
  @BeforeEach
  public void preparar() {
    ObjectMapper mapper = new ObjectMapper();
    turmaRepository = new TurmaRepository(mapper, tempDir.resolve("turmas").toString());
    boletimRepository = new BoletimRepository(mapper, tempDir.resolve("boletins").toString());
    aulaRepository = new AulaRepository(mapper, tempDir.resolve("aulas").toString());
    diarioRepository = new DiarioRepository(mapper, tempDir.resolve("diarios").toString());
    DisciplinaRepository disciplinaRepository =
        new DisciplinaRepository(mapper, tempDir.resolve("disciplinas").toString());
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Calculo", 30, 1, 2, "cur00", List.of()));
    Turma turma =
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turma.getMatriculados().add("al00");
    turmaRepository.salvarTurma(turma);
    Boletim boletim = new Boletim("al00", "tur00");
    boletim.setIdBoletim("bol00");
    boletimRepository.salvarBoletim(boletim);
    aulaService = new AulaService(aulaRepository, turmaRepository, diarioRepository);
    turmaService =
        new TurmaService(
            turmaRepository,
            disciplinaRepository,
            new PeriodoLetivoRepository(mapper, tempDir.resolve("periodos").toString()),
            new UserRepository(mapper, tempDir.resolve("usuarios").toString()),
            boletimRepository,
            aulaRepository,
            diarioRepository);
  }

  @Test
  public void deveManterFrequenciaPendenteSemAulas() {
    turmaService.atualizarFrequenciaTurma("tur00");

    Assertions.assertNull(boletimAtualizado().getFrequencia());
  }

  @Test
  public void devePersistirFrequenciaZeroComAusenciaTotal() {
    Diario diario =
        new Diario(
            "dia00", "tur00", "Diário de aulas teóricas", "pr00", "SEG 08:00-10:00", "LAB 01", 2,
            SituacaoDiario.ATIVO);
    diarioRepository.salvarDiario(diario);

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("al00", false);
    Aula aula = aulaService.gerarAula(diario, presencas);
    aulaService.salvarAula(aula, "pr00");

    turmaService.atualizarFrequenciaTurma("tur00");

    Assertions.assertEquals(0.0, boletimAtualizado().getFrequencia());
  }

  @Test
  public void naoDeveConsiderarAulaAnexadaApenasNaListaLegadaDaTurma() {
    // Regressão principal: uma aula presente apenas em turma.aulas (fluxo legado, sem diario
    // vinculado) nao deve mais influenciar a frequencia calculada.
    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("al00", false);
    aulaRepository.salvarAula(new Aula("aul00", "tur00", "10/07/2026", "08:00", presencas));
    Turma turma = turmaRepository.buscarTurmaPorCodigo("tur00");
    turma.getAulas().add("aul00");
    turmaRepository.atualizarTurma(turma);

    turmaService.atualizarFrequenciaTurma("tur00");

    Assertions.assertNull(boletimAtualizado().getFrequencia());
  }

  @Test
  public void naoDeveConsiderarAulaDeDiarioCanceladoNoCalculoDeFrequencia() {
    Diario diario =
        new Diario(
            "dia01", "tur00", "Diário cancelado", "pr00", "SEG 08:00-10:00", "LAB 01", 2,
            SituacaoDiario.ATIVO);
    diarioRepository.salvarDiario(diario);

    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("al00", true);
    Aula aula = aulaService.gerarAula(diario, presencas);
    aulaService.salvarAula(aula, "pr00");

    diario.setSituacao(SituacaoDiario.CANCELADO);
    diarioRepository.atualizarDiario(diario);

    turmaService.atualizarFrequenciaTurma("tur00");

    Assertions.assertNull(boletimAtualizado().getFrequencia());
  }

  private Boletim boletimAtualizado() {
    return boletimRepository.buscarBoletinsPorTurma("tur00").getFirst();
  }
}
