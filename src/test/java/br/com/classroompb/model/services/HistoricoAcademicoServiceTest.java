package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.ItemHistoricoAcademico;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Testes isolados do historico academico. */
public class HistoricoAcademicoServiceTest {

  @TempDir Path tempDir;

  private BoletimRepository boletimRepository;
  private HistoricoAcademicoService historicoService;
  private Aluno aluno;

  /** Prepara os repositories temporarios. */
  @BeforeEach
  public void preparar() {
    ObjectMapper mapper = new ObjectMapper();
    boletimRepository = new BoletimRepository(mapper, tempDir.resolve("boletins").toString());
    TurmaRepository turmaRepository =
        new TurmaRepository(mapper, tempDir.resolve("turmas").toString());
    Turma turma =
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turmaRepository.salvarTurma(turma);
    TurmaService turmaService =
        new TurmaService(
            turmaRepository,
            new DisciplinaRepository(mapper, tempDir.resolve("disciplinas").toString()),
            new PeriodoLetivoRepository(mapper, tempDir.resolve("periodos").toString()),
            new UserRepository(mapper, tempDir.resolve("usuarios").toString()),
            boletimRepository,
            new AulaRepository(mapper, tempDir.resolve("aulas").toString()));
    historicoService =
        new HistoricoAcademicoService(
            new BoletimService(boletimRepository, turmaRepository), turmaService);
    aluno = new Aluno("Aluno", "aluno@email.com", "senha", "cur00");
    aluno.setMatricula("al00");
  }

  @Test
  public void deveManterHistoricoEmAndamentoComUmaNotaPendente() {
    salvarBoletim(8.0f, null, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertNull(item.getNotaFinal());
    Assertions.assertEquals("Em andamento", item.getSituacao());
  }

  @Test
  public void deveUsarMediaComSituacaoDoBoletim() {
    salvarBoletim(8.0f, 6.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals(7.0, item.getNotaFinal());
    Assertions.assertEquals("Aprovado", item.getSituacao());
  }

  private void salvarBoletim(Float primeiraNota, Float segundaNota, Double frequencia) {
    Boletim boletim = new Boletim("al00", "tur00");
    boletim.setIdBoletim("bol00");
    boletim.setPrimeiraNota(primeiraNota);
    boletim.setSegundaNota(segundaNota);
    boletim.setFrequencia(frequencia);
    boletimRepository.salvarBoletim(boletim);
  }
}
