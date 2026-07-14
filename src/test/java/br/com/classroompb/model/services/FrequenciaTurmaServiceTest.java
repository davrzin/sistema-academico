package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.BoletimRepository;
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

/** Testes isolados da atualizacao de frequencia da turma. */
public class FrequenciaTurmaServiceTest {

  @TempDir Path tempDir;

  private TurmaRepository turmaRepository;
  private BoletimRepository boletimRepository;
  private AulaRepository aulaRepository;
  private TurmaService turmaService;

  /** Prepara repositories temporarios e dados basicos. */
  @BeforeEach
  public void preparar() {
    ObjectMapper mapper = new ObjectMapper();
    turmaRepository = new TurmaRepository(mapper, tempDir.resolve("turmas").toString());
    boletimRepository = new BoletimRepository(mapper, tempDir.resolve("boletins").toString());
    aulaRepository = new AulaRepository(mapper, tempDir.resolve("aulas").toString());
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
    turmaService =
        new TurmaService(
            turmaRepository,
            disciplinaRepository,
            new PeriodoLetivoRepository(mapper, tempDir.resolve("periodos").toString()),
            new UserRepository(mapper, tempDir.resolve("usuarios").toString()),
            boletimRepository,
            aulaRepository);
  }

  @Test
  public void deveManterFrequenciaPendenteSemAulas() {
    turmaService.atualizarFrequenciaTurma("tur00");

    Assertions.assertNull(boletimAtualizado().getFrequencia());
  }

  @Test
  public void devePersistirFrequenciaZeroComAusenciaTotal() {
    Map<String, Boolean> presencas = new HashMap<>();
    presencas.put("al00", false);
    aulaRepository.salvarAula(new Aula("aul00", "tur00", "10/07/2026", "08:00", presencas));
    Turma turma = turmaRepository.buscarTurmaPorCodigo("tur00");
    turma.getAulas().add("aul00");
    turmaRepository.atualizarTurma(turma);

    turmaService.atualizarFrequenciaTurma("tur00");

    Assertions.assertEquals(0.0, boletimAtualizado().getFrequencia());
  }

  private Boletim boletimAtualizado() {
    return boletimRepository.buscarBoletinsPorTurma("tur00").getFirst();
  }
}
