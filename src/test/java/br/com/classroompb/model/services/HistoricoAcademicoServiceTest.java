package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.ItemHistoricoAcademico;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Testes isolados do historico academico. */
public class HistoricoAcademicoServiceTest {

  @TempDir Path tempDir;

  private BoletimRepository boletimRepository;
  private TurmaRepository turmaRepository;
  private DisciplinaRepository disciplinaRepository;
  private UserRepository userRepository;
  private HistoricoAcademicoService historicoService;
  private Aluno aluno;

  /** Prepara os repositories temporarios e o servico de historico. */
  @BeforeEach
  public void preparar() {
    ObjectMapper mapper = new ObjectMapper();
    boletimRepository = new BoletimRepository(mapper, tempDir.resolve("boletins").toString());
    turmaRepository = new TurmaRepository(mapper, tempDir.resolve("turmas").toString());
    disciplinaRepository =
        new DisciplinaRepository(mapper, tempDir.resolve("disciplinas").toString());
    userRepository = new UserRepository(mapper, tempDir.resolve("usuarios").toString());

    Turma turma =
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turmaRepository.salvarTurma(turma);

    TurmaService turmaService =
        new TurmaService(
            turmaRepository,
            disciplinaRepository,
            new PeriodoLetivoRepository(mapper, tempDir.resolve("periodos").toString()),
            userRepository,
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
    salvarBoletim("bol00", "tur00", 8.0f, null, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertNull(item.getNotaFinal());
    Assertions.assertEquals("Em andamento", item.getSituacao());
  }

  @Test
  public void deveUsarMediaComSituacaoDoBoletim() {
    salvarBoletim("bol00", "tur00", 8.0f, 6.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals(7.0, item.getNotaFinal());
    Assertions.assertEquals("Aprovado", item.getSituacao());
  }

  @Test
  public void deveClassificarComoReprovadoPorNota() {
    salvarBoletim("bol00", "tur00", 2.0f, 2.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals(2.0, item.getNotaFinal());
    Assertions.assertEquals("Reprovado por nota", item.getSituacao());
  }

  @Test
  public void deveClassificarComoEmRecuperacao() {
    salvarBoletim("bol00", "tur00", 5.0f, 5.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals(5.0, item.getNotaFinal());
    Assertions.assertEquals("Em recuperação", item.getSituacao());
  }

  @Test
  public void deveClassificarComoReprovadoPorFaltaMesmoComBoaNota() {
    salvarBoletim("bol00", "tur00", 9.0f, 9.0f, 50.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals(9.0, item.getNotaFinal());
    Assertions.assertEquals("Reprovado por falta", item.getSituacao());
  }

  @Test
  public void deveLancarExcecaoQuandoAlunoForNulo() {
    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> historicoService.listarHistoricoAluno(null));
  }

  @Test
  public void deveLancarExcecaoQuandoMatriculaDoAlunoForNula() {
    // Construtor de 3 argumentos nao define matricula, permanecendo nula.
    Aluno alunoSemMatricula = new Aluno("Aluno", "aluno@email.com", "senha");

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> historicoService.listarHistoricoAluno(alunoSemMatricula));
  }

  @Test
  public void deveLancarExcecaoQuandoMatriculaDoAlunoForVazia() throws Exception {
    // O setter de matricula valida contra valores em branco, entao o campo e forcado
    // via reflexao para simular um registro carregado sem matricula preenchida
    // (ex: desserializacao que ignore os validadores do setter).
    Field campoMatricula = aluno.getClass().getSuperclass().getDeclaredField("matricula");
    campoMatricula.setAccessible(true);
    campoMatricula.set(aluno, "   ");

    Assertions.assertThrows(
        EntradaInvalidaException.class, () -> historicoService.listarHistoricoAluno(aluno));
  }

  @Test
  public void deveRetornarListaVaziaQuandoAlunoNaoPossuiBoletins() {
    List<ItemHistoricoAcademico> historico = historicoService.listarHistoricoAluno(aluno);

    Assertions.assertTrue(historico.isEmpty());
  }

  @Test
  public void devePreencherDadosDoAlunoDaTurmaNoItem() {
    salvarBoletim("bol00", "tur00", 8.0f, 6.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals("al00", item.getMatriculaAluno());
    Assertions.assertEquals("Aluno", item.getNomeAluno());
    Assertions.assertEquals("tur00", item.getCodigoTurma());
    Assertions.assertEquals("2026.2", item.getPeriodoLetivo());
    Assertions.assertEquals("dis00", item.getCodigoDisciplina());
  }

  @Test
  public void deveExibirNomeDaDisciplinaQuandoCadastrada() {
    disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Engenharia de Software", 60, 5, 4, "cur00", new ArrayList<>()));
    salvarBoletim("bol00", "tur00", 8.0f, 6.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals("Engenharia de Software", item.getNomeDisciplina());
  }

  @Test
  public void deveExibirDisciplinaNaoEncontradaQuandoDisciplinaNaoCadastrada() {
    salvarBoletim("bol00", "tur00", 8.0f, 6.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals("Disciplina nao encontrada", item.getNomeDisciplina());
  }

  @Test
  public void deveExibirNomeDoProfessorQuandoCadastrado() {
    Professor professor = new Professor("Professor Ana", "ana@email.com", "senha");
    professor.setMatricula("pr00");
    userRepository.salvarUsuario(professor);
    salvarBoletim("bol00", "tur00", 8.0f, 6.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals("Professor Ana", item.getNomeProfessor());
  }

  @Test
  public void deveExibirProfessorNaoEncontradoQuandoProfessorNaoCadastrado() {
    salvarBoletim("bol00", "tur00", 8.0f, 6.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals("Professor nao encontrado", item.getNomeProfessor());
  }

  @Test
  public void devePreencherCamposPadraoQuandoTurmaNaoForEncontrada() {
    Boletim boletim = new Boletim("al00", "turXX");
    boletim.setIdBoletim("bolXX");
    boletim.setPrimeiraNota(8.0f);
    boletim.setSegundaNota(6.0f);
    boletim.setFrequencia(80.0);
    boletimRepository.salvarBoletim(boletim);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals("-", item.getPeriodoLetivo());
    Assertions.assertEquals("-", item.getCodigoDisciplina());
    Assertions.assertEquals("Disciplina nao encontrada", item.getNomeDisciplina());
    Assertions.assertEquals("Professor nao encontrado", item.getNomeProfessor());
    Assertions.assertEquals(7.0, item.getNotaFinal());
  }

  @Test
  public void deveOrdenarHistoricoPorPeriodoDisciplinaTurma() {
    Turma turmaPeriodoAnterior =
        new Turma("tur01", "dis00", "2026.1", "pr00", 30, "TER 08:00-10:00", "LAB 02");
    turmaRepository.salvarTurma(turmaPeriodoAnterior);

    salvarBoletim("bol00", "tur00", 8.0f, 6.0f, 80.0);
    salvarBoletim("bol01", "tur01", 8.0f, 6.0f, 80.0);

    List<ItemHistoricoAcademico> historico = historicoService.listarHistoricoAluno(aluno);

    Assertions.assertEquals(2, historico.size());
    Assertions.assertEquals("2026.1", historico.get(0).getPeriodoLetivo());
    Assertions.assertEquals("2026.2", historico.get(1).getPeriodoLetivo());
  }

  private void salvarBoletim(
      String idBoletim,
      String codigoTurma,
      Float primeiraNota,
      Float segundaNota,
      Double frequencia) {
    Boletim boletim = new Boletim("al00", codigoTurma);
    boletim.setIdBoletim(idBoletim);
    boletim.setPrimeiraNota(primeiraNota);
    boletim.setSegundaNota(segundaNota);
    boletim.setFrequencia(frequencia);
    boletimRepository.salvarBoletim(boletim);
  }
}
