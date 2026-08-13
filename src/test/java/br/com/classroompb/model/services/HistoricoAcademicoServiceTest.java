package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.ItemHistoricoAcademico;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.HistoricoAcademicoRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes do servico de historico academico.
 *
 * <p>Regressão do refactor "consulta historico consolidado persistido" (16c686a): o historico
 * deixou de ser calculado em tempo real a partir dos boletins e passou a exigir que cada
 * resultado final seja explicitamente registrado (persistido) via {@code
 * registrarResultadoConsolidado} antes de aparecer em {@code listarHistoricoAluno}.
 */
public class HistoricoAcademicoServiceTest {

  @TempDir Path tempDir;

  private TurmaRepository turmaRepository;
  private DisciplinaRepository disciplinaRepository;
  private HistoricoAcademicoRepository historicoRepository;
  private HistoricoAcademicoService historicoService;
  private Aluno aluno;

  /**
   * Prepara os repositories temporarios e o servico de historico.
   */
  @BeforeEach
  public void preparar() {
    ObjectMapper mapper = new ObjectMapper();
    turmaRepository = new TurmaRepository(mapper, tempDir.resolve("turmas").toString());
    disciplinaRepository =
        new DisciplinaRepository(mapper, tempDir.resolve("disciplinas").toString());
    historicoRepository =
        new HistoricoAcademicoRepository(mapper, tempDir.resolve("historicos").toString());

    Turma turma =
        new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
    turmaRepository.salvarTurma(turma);

    PeriodoLetivoRepository periodoLetivoRepository =
        new PeriodoLetivoRepository(mapper, tempDir.resolve("periodos").toString());
    UserRepository userRepository =
        new UserRepository(mapper, tempDir.resolve("usuarios").toString());

    // TurmaService so precisa resolver nome/codigo de disciplina e a propria turma para montar
    // o item de historico; as demais dependencias (boletins, aulas, diarios, avaliacoes) usam
    // o construtor de 4 argumentos, que as cria com caminhos de persistencia padrao mas nao
    // sao exercitadas por estes testes.
    TurmaService turmaService =
        new TurmaService(
            turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

    historicoService =
        new HistoricoAcademicoService(new BoletimService(), turmaService, historicoRepository);

    aluno = new Aluno("Aluno", "aluno@email.com", "senha", "cur00");
    aluno.setMatricula("al00");
  }

  private Boletim criarBoletimConsolidado(
      String codigoTurma, Float primeiraNota, Float segundaNota, Double frequencia) {
    Boletim boletim = new Boletim("al00", codigoTurma);
    boletim.setIdBoletim("bol00");
    boletim.setPrimeiraNota(primeiraNota);
    boletim.setSegundaNota(segundaNota);
    boletim.setFrequencia(frequencia);
    return boletim;
  }

  @Test
  public void deveRegistrarResultadoConsolidadoNoHistorico() {
    Boletim boletim = criarBoletimConsolidado("tur00", 8.0f, 6.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.registrarResultadoConsolidado(aluno, boletim);

    Assertions.assertEquals("al00", item.getMatriculaAluno());
    Assertions.assertEquals("tur00", item.getCodigoTurma());
    Assertions.assertEquals(1, historicoRepository.listarHistoricos().size());
  }

  @Test
  public void naoDeveExporResultadoNaoRegistradoExplicitamente() {
    // Regressão principal do refactor: mesmo com um boletim consolidado existente, o item so
    // deve aparecer no historico apos ser explicitamente registrado.
    List<ItemHistoricoAcademico> historico = historicoService.listarHistoricoAluno(aluno);

    Assertions.assertTrue(historico.isEmpty());
  }

  @Test
  public void deveListarResultadoAposRegistroExplicito() {
    Boletim boletim = criarBoletimConsolidado("tur00", 8.0f, 6.0f, 80.0);
    historicoService.registrarResultadoConsolidado(aluno, boletim);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals(7.0, item.getNotaFinal());
    Assertions.assertEquals("Aprovado", item.getSituacao());
  }

  @Test
  public void deveAtualizarRegistroExistenteAoInvesDeDuplicar() {
    Boletim primeiraConsolidacao = criarBoletimConsolidado("tur00", 5.0f, 5.0f, 80.0);
    historicoService.registrarResultadoConsolidado(aluno, primeiraConsolidacao);

    Boletim consolidacaoCorrigida = criarBoletimConsolidado("tur00", 9.0f, 9.0f, 80.0);
    historicoService.registrarResultadoConsolidado(aluno, consolidacaoCorrigida);

    List<ItemHistoricoAcademico> historico = historicoService.listarHistoricoAluno(aluno);

    Assertions.assertEquals(1, historico.size());
    Assertions.assertEquals(9.0, historico.getFirst().getNotaFinal());
    Assertions.assertEquals("Aprovado", historico.getFirst().getSituacao());
  }

  @Test
  public void deveLancarExcecaoAoRegistrarBoletimSemNotasCompletas() {
    Boletim boletimIncompleto = criarBoletimConsolidado("tur00", 8.0f, null, 80.0);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> historicoService.registrarResultadoConsolidado(aluno, boletimIncompleto));
  }

  @Test
  public void deveLancarExcecaoAoRegistrarBoletimSemFrequenciaCalculada() {
    Boletim boletimSemFrequencia = criarBoletimConsolidado("tur00", 8.0f, 6.0f, null);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> historicoService.registrarResultadoConsolidado(aluno, boletimSemFrequencia));
  }

  @Test
  public void deveLancarExcecaoAoRegistrarComAlunoNulo() {
    Boletim boletim = criarBoletimConsolidado("tur00", 8.0f, 6.0f, 80.0);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> historicoService.registrarResultadoConsolidado(null, boletim));
  }

  @Test
  public void deveClassificarComoReprovadoPorNota() {
    Boletim boletim = criarBoletimConsolidado("tur00", 2.0f, 2.0f, 80.0);
    historicoService.registrarResultadoConsolidado(aluno, boletim);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals(2.0, item.getNotaFinal());
    Assertions.assertEquals("Reprovado por nota", item.getSituacao());
  }

  @Test
  public void deveClassificarComoEmRecuperacao() {
    Boletim boletim = criarBoletimConsolidado("tur00", 5.0f, 5.0f, 80.0);
    historicoService.registrarResultadoConsolidado(aluno, boletim);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals(5.0, item.getNotaFinal());
    Assertions.assertEquals("Em recuperação", item.getSituacao());
  }

  @Test
  public void deveClassificarComoReprovadoPorFaltaMesmoComBoaNota() {
    Boletim boletim = criarBoletimConsolidado("tur00", 9.0f, 9.0f, 50.0);
    historicoService.registrarResultadoConsolidado(aluno, boletim);

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
  public void devePreencherDadosDoAlunoDaTurmaNoItem() {
    Boletim boletim = criarBoletimConsolidado("tur00", 8.0f, 6.0f, 80.0);
    historicoService.registrarResultadoConsolidado(aluno, boletim);

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
        new Disciplina("dis00", "Engenharia de Software", 60, 5, 4, "cur00", List.of()));
    Boletim boletim = criarBoletimConsolidado("tur00", 8.0f, 6.0f, 80.0);
    historicoService.registrarResultadoConsolidado(aluno, boletim);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals("Engenharia de Software", item.getNomeDisciplina());
  }

  @Test
  public void deveExibirDisciplinaNaoEncontradaQuandoDisciplinaNaoCadastrada() {
    Boletim boletim = criarBoletimConsolidado("tur00", 8.0f, 6.0f, 80.0);
    historicoService.registrarResultadoConsolidado(aluno, boletim);

    ItemHistoricoAcademico item = historicoService.listarHistoricoAluno(aluno).getFirst();

    Assertions.assertEquals("Disciplina nao encontrada", item.getNomeDisciplina());
  }

  @Test
  public void devePreencherCamposPadraoQuandoTurmaNaoForEncontrada() {
    Boletim boletim = criarBoletimConsolidado("turXX", 8.0f, 6.0f, 80.0);

    ItemHistoricoAcademico item = historicoService.registrarResultadoConsolidado(aluno, boletim);

    Assertions.assertEquals("-", item.getPeriodoLetivo());
    Assertions.assertEquals("-", item.getCodigoDisciplina());
    Assertions.assertEquals("Disciplina nao encontrada", item.getNomeDisciplina());
    Assertions.assertEquals(7.0, item.getNotaFinal());
  }

  @Test
  public void deveOrdenarHistoricoPorPeriodoDisciplinaTurma() {
    Turma turmaPeriodoAnterior =
        new Turma("tur01", "dis00", "2026.1", "pr00", 30, "TER 08:00-10:00", "LAB 02");
    turmaRepository.salvarTurma(turmaPeriodoAnterior);

    historicoService.registrarResultadoConsolidado(
        aluno, criarBoletimConsolidado("tur00", 8.0f, 6.0f, 80.0));
    historicoService.registrarResultadoConsolidado(
        aluno, criarBoletimConsolidado("tur01", 8.0f, 6.0f, 80.0));

    List<ItemHistoricoAcademico> historico = historicoService.listarHistoricoAluno(aluno);

    Assertions.assertEquals(2, historico.size());
    Assertions.assertEquals("2026.1", historico.get(0).getPeriodoLetivo());
    Assertions.assertEquals("2026.2", historico.get(1).getPeriodoLetivo());
  }
}
