package br.com.classroompb.model.services;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioAlunoTurma;
import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioReprovacaoDisciplina;
import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioAlunosTurma;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioOcupacaoVagas;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;

/**
 * Testes do servico de relatorios academicos.
 */
public class RelatorioAcademicoServiceTest {

  private static final String CODIGO_CURSO = "cur00";

  @TempDir Path tempDir;

  /**
   * Limpa os arquivos gerados pelos testes.
   */
  @AfterEach
  public void tearDown() {
    apagarDiretorio("turmas");
    apagarDiretorio("disciplinas");
    apagarDiretorio("periodos");
    apagarDiretorio("usuarios");
    apagarDiretorio("boletins");
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

  private TurmaRepository criarTurmaRepository() {
    return new TurmaRepository(new ObjectMapper(), tempDir.resolve("turmas").toString());
  }

  private DisciplinaRepository criarDisciplinaRepository() {
    return new DisciplinaRepository(new ObjectMapper(), tempDir.resolve("disciplinas").toString());
  }

  private PeriodoLetivoRepository criarPeriodoLetivoRepository() {
    return new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
  }

  private UserRepository criarUserRepository() {
    return new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
  }

  private BoletimRepository criarBoletimRepository() {
    return new BoletimRepository(new ObjectMapper(), tempDir.resolve("boletins").toString());
  }

  /**
   * Conjunto de dependencias necessarias para montar os servicos de um cenario de teste.
   */
  private static final class Ambiente {
    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final PeriodoLetivoRepository periodoLetivoRepository;
    private final UserRepository userRepository;
    private final BoletimRepository boletimRepository;
    private final TurmaService turmaService;
    private final UsuarioService usuarioService;
    private final BoletimService boletimService;
    private final RelatorioAcademicoService relatorioAcademicoService;

    private Ambiente(
        TurmaRepository turmaRepository,
        DisciplinaRepository disciplinaRepository,
        PeriodoLetivoRepository periodoLetivoRepository,
        UserRepository userRepository,
        BoletimRepository boletimRepository,
        AulaRepository aulaRepository) {
      this.turmaRepository = turmaRepository;
      this.disciplinaRepository = disciplinaRepository;
      this.periodoLetivoRepository = periodoLetivoRepository;
      this.userRepository = userRepository;
      this.boletimRepository = boletimRepository;
      this.usuarioService = new UsuarioService(userRepository);
      this.turmaService =
          new TurmaService(
              turmaRepository,
              disciplinaRepository,
              periodoLetivoRepository,
              userRepository,
              boletimRepository,
              aulaRepository);
      this.boletimService =
          new BoletimService(boletimRepository, turmaRepository, periodoLetivoRepository);
      this.relatorioAcademicoService =
          new RelatorioAcademicoService(turmaService, usuarioService, boletimService);
    }
  }

  private Ambiente criarAmbiente() {
    TurmaRepository turmaRepository = criarTurmaRepository();
    DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
    PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
    UserRepository userRepository = criarUserRepository();
    BoletimRepository boletimRepository = criarBoletimRepository();
    AulaRepository aulaRepository =
        new AulaRepository(new ObjectMapper(), tempDir.resolve("aulas").toString());
    return new Ambiente(
        turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository,
        boletimRepository, aulaRepository);
  }

  private Professor criarProfessor(String nome, String email, String senha, String matricula) {
    Professor professor = new Professor(nome, email, senha);
    professor.setMatricula(matricula);
    professor.setCodigoCurso(CODIGO_CURSO);
    return professor;
  }

  private Aluno criarAluno(String nome, String email, String matricula, String senha) {
    return new Aluno(nome, email, matricula, senha, CODIGO_CURSO);
  }

  private Coordenador criarCoordenador() {
    return new Coordenador("Carlos", "carlos@email.com", "co00", "senha123", CODIGO_CURSO);
  }

  private void prepararDadosBasicos(Ambiente ambiente) {
    ambiente.disciplinaRepository.salvarDisciplina(
        new Disciplina("dis00", "Algoritmos", 60, 1, 4, CODIGO_CURSO, List.of()));
    PeriodoLetivo periodoAtivo = new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026");
    periodoAtivo.setPeriodoAtivo(true);
    ambiente.periodoLetivoRepository.salvarPeriodoLetivo(periodoAtivo);

    Professor professor = criarProfessor("João", "joao@email.com", "senha123", "pr00");
    ambiente.userRepository.salvarUsuario(professor);
  }

  private Turma criarEOfertarTurma(Ambiente ambiente, int limiteVagas) {
    Turma turma =
        new Turma("dis00", "2026.2", "pr00", limiteVagas, "SEG 08:00-10:00", "LAB 01");
    ambiente.turmaService.ofertarTurma(turma);
    return turma;
  }

  /**
   * Rebusca a turma no repositorio, pois operacoes como matricula de aluno
   * atualizam apenas a copia persistida, nao a referencia original em memoria.
   */
  private Turma buscarTurmaAtualizada(Ambiente ambiente, String codigoTurma) {
    return ambiente.turmaRepository.buscarTurmaPorCodigo(codigoTurma);
  }

  // ---- listarTurmasDoCoordenador ----

  @Test
  public void deveListarTurmasDoCursoDoCoordenador() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);

    List<Turma> turmas =
        ambiente.relatorioAcademicoService.listarTurmasDoCoordenador(criarCoordenador());

    Assertions.assertEquals(1, turmas.size());
    Assertions.assertEquals(turma.getCodigo(), turmas.get(0).getCodigo());
  }

  @Test
  public void deveLancarExcecaoAoListarTurmasComCoordenadorNull() {
    Ambiente ambiente = criarAmbiente();

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> ambiente.relatorioAcademicoService.listarTurmasDoCoordenador(null));
  }

  @Test
  public void deveLancarExcecaoAoListarTurmasComCoordenadorSemCurso() {
    Ambiente ambiente = criarAmbiente();
    Coordenador coordenadorSemCurso = new Coordenador("Ana", "ana@email.com", "co01", "senha123");

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () -> ambiente.relatorioAcademicoService.listarTurmasDoCoordenador(coordenadorSemCurso));
  }

  // ---- gerarRelatorioAlunosTurma ----

  @Test
  public void deveGerarRelatorioDeAlunosDaTurmaComAlunoMatriculado() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);
    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    ambiente.userRepository.salvarUsuario(aluno);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);
    Turma turmaAtualizada = buscarTurmaAtualizada(ambiente, turma.getCodigo());

    RelatorioAlunosTurma relatorio =
        ambiente.relatorioAcademicoService.gerarRelatorioAlunosTurma(
            criarCoordenador(), turmaAtualizada);

    Assertions.assertEquals(turma.getCodigo(), relatorio.getCodigoTurma());
    Assertions.assertEquals("Algoritmos", relatorio.getNomeDisciplina());
    Assertions.assertEquals("2026.2", relatorio.getPeriodoLetivo());
    Assertions.assertEquals("João", relatorio.getNomeProfessor());
    Assertions.assertEquals(30, relatorio.getLimiteVagas());
    Assertions.assertEquals(1, relatorio.getTotalMatriculados());

    ItemRelatorioAlunoTurma item = relatorio.getAlunos().get(0);
    Assertions.assertEquals("al00", item.getMatricula());
    Assertions.assertEquals("Maria", item.getNome());
    Assertions.assertEquals("maria@email.com", item.getEmail());
  }

  @Test
  public void deveGerarRelatorioDeAlunosDaTurmaSemMatriculados() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);

    RelatorioAlunosTurma relatorio =
        ambiente.relatorioAcademicoService.gerarRelatorioAlunosTurma(
            criarCoordenador(), turma);

    Assertions.assertEquals(0, relatorio.getTotalMatriculados());
    Assertions.assertTrue(relatorio.getAlunos().isEmpty());
  }

  @Test
  public void deveLancarExcecaoAoGerarRelatorioDeAlunosComTurmaNull() {
    Ambiente ambiente = criarAmbiente();

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            ambiente.relatorioAcademicoService.gerarRelatorioAlunosTurma(
                criarCoordenador(), null));
  }

  @Test
  public void deveLancarExcecaoAoGerarRelatorioDeAlunosComTurmaDeOutroCurso() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    ambiente.disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Redes", 60, 1, 4, "cur99", List.of()));
    Professor professorOutroCurso =
        criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    ambiente.userRepository.salvarUsuario(professorOutroCurso);
    Turma turmaOutroCurso =
        new Turma("dis01", "2026.2", "pr01", 30, "TER 10:00-12:00", "LAB 02");
    ambiente.turmaService.ofertarTurma(turmaOutroCurso);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            ambiente.relatorioAcademicoService.gerarRelatorioAlunosTurma(
                criarCoordenador(), turmaOutroCurso));
  }

  // ---- gerarRelatorioOcupacaoVagas ----

  @Test
  public void deveCalcularOcupacaoDeVagasComTurmaParcialmenteOcupada() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 4);
    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    ambiente.userRepository.salvarUsuario(aluno);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);
    Turma turmaAtualizada = buscarTurmaAtualizada(ambiente, turma.getCodigo());

    RelatorioOcupacaoVagas relatorio =
        ambiente.relatorioAcademicoService.gerarRelatorioOcupacaoVagas(
            criarCoordenador(), turmaAtualizada);

    Assertions.assertEquals(4, relatorio.getLimiteVagas());
    Assertions.assertEquals(1, relatorio.getVagasOcupadas());
    Assertions.assertEquals(3, relatorio.getVagasDisponiveis());
    Assertions.assertEquals(25.0, relatorio.getPercentualOcupacao(), 0.001);
  }

  @Test
  public void deveCalcularOcupacaoDeVagasComTurmaSemMatriculados() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 10);

    RelatorioOcupacaoVagas relatorio =
        ambiente.relatorioAcademicoService.gerarRelatorioOcupacaoVagas(
            criarCoordenador(), turma);

    Assertions.assertEquals(0, relatorio.getVagasOcupadas());
    Assertions.assertEquals(10, relatorio.getVagasDisponiveis());
    Assertions.assertEquals(0.0, relatorio.getPercentualOcupacao(), 0.001);
  }

  @Test
  public void naoDeveGerarVagasDisponiveisNegativasComTurmaLotada() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 1);
    Aluno alunoMatriculado = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    Aluno alunoListaEspera = criarAluno("Ana", "ana@email.com", "al01", "senha123");
    ambiente.userRepository.salvarUsuario(alunoMatriculado);
    ambiente.userRepository.salvarUsuario(alunoListaEspera);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), alunoMatriculado);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), alunoListaEspera);
    Turma turmaAtualizada = buscarTurmaAtualizada(ambiente, turma.getCodigo());

    RelatorioOcupacaoVagas relatorio =
        ambiente.relatorioAcademicoService.gerarRelatorioOcupacaoVagas(
            criarCoordenador(), turmaAtualizada);

    Assertions.assertEquals(1, relatorio.getVagasOcupadas());
    Assertions.assertEquals(0, relatorio.getVagasDisponiveis());
  }

  // ---- gerarRelatorioReprovacaoPorDisciplina ----

  @Test
  public void deveContabilizarReprovacaoPorNotaEPorFalta() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);

    Aluno alunoAprovado = criarAluno("Ana", "ana@email.com", "al00", "senha123");
    Aluno alunoReprovadoNota = criarAluno("Bia", "bia@email.com", "al01", "senha123");
    Aluno alunoReprovadoFalta = criarAluno("Caio", "caio@email.com", "al02", "senha123");
    ambiente.userRepository.salvarUsuario(alunoAprovado);
    ambiente.userRepository.salvarUsuario(alunoReprovadoNota);
    ambiente.userRepository.salvarUsuario(alunoReprovadoFalta);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), alunoAprovado);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), alunoReprovadoNota);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), alunoReprovadoFalta);

    lancarBoletimCompleto(ambiente, turma.getCodigo(), "al00", 8.0f, 8.0f, 90.0);
    lancarBoletimCompleto(ambiente, turma.getCodigo(), "al01", 2.0f, 3.0f, 90.0);
    lancarBoletimCompleto(ambiente, turma.getCodigo(), "al02", 8.0f, 8.0f, 40.0);

    List<ItemRelatorioReprovacaoDisciplina> itens =
        ambiente.relatorioAcademicoService.gerarRelatorioReprovacaoPorDisciplina(
            criarCoordenador());

    Assertions.assertEquals(1, itens.size());
    ItemRelatorioReprovacaoDisciplina item = itens.get(0);
    Assertions.assertEquals("dis00", item.getCodigoDisciplina());
    Assertions.assertEquals("Algoritmos", item.getNomeDisciplina());
    Assertions.assertEquals(3, item.getTotalResultadosFinais());
    Assertions.assertEquals(1, item.getTotalReprovadosPorNota());
    Assertions.assertEquals(1, item.getTotalReprovadosPorFalta());
    Assertions.assertEquals(2, item.getTotalReprovados());
    Assertions.assertEquals(66.666, item.getPercentualReprovacao(), 0.01);
  }

  @Test
  public void naoDeveContabilizarBoletinsEmAndamentoNoTotalDeResultados() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);
    Aluno aluno = criarAluno("Ana", "ana@email.com", "al00", "senha123");
    ambiente.userRepository.salvarUsuario(aluno);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);
    ambiente.boletimService.criarBoletimSeNaoExistir("al00", turma.getCodigo());

    List<ItemRelatorioReprovacaoDisciplina> itens =
        ambiente.relatorioAcademicoService.gerarRelatorioReprovacaoPorDisciplina(
            criarCoordenador());

    ItemRelatorioReprovacaoDisciplina item = itens.get(0);
    Assertions.assertEquals(0, item.getTotalResultadosFinais());
    Assertions.assertEquals(0.0, item.getPercentualReprovacao(), 0.001);
  }

  @Test
  public void deveRetornarListaVaziaDeReprovacaoQuandoCoordenadorNaoTemTurmas() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);

    List<ItemRelatorioReprovacaoDisciplina> itens =
        ambiente.relatorioAcademicoService.gerarRelatorioReprovacaoPorDisciplina(
            criarCoordenador());

    Assertions.assertTrue(itens.isEmpty());
  }

  @Test
  public void deveOrdenarRelatorioDeReprovacaoPorNomeDaDisciplina() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    ambiente.disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Banco de Dados", 60, 2, 4, CODIGO_CURSO, List.of()));
    Professor professor2 = criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    ambiente.userRepository.salvarUsuario(professor2);

    criarEOfertarTurma(ambiente, 30);
    Turma turmaBancoDados =
        new Turma("dis01", "2026.2", "pr01", 30, "TER 10:00-12:00", "LAB 02");
    ambiente.turmaService.ofertarTurma(turmaBancoDados);

    List<ItemRelatorioReprovacaoDisciplina> itens =
        ambiente.relatorioAcademicoService.gerarRelatorioReprovacaoPorDisciplina(
            criarCoordenador());

    Assertions.assertEquals(2, itens.size());
    Assertions.assertEquals("Algoritmos", itens.get(0).getNomeDisciplina());
    Assertions.assertEquals("Banco de Dados", itens.get(1).getNomeDisciplina());
  }

  private void lancarBoletimCompleto(
      Ambiente ambiente,
      String codigoTurma,
      String matriculaAluno,
      float primeiraNota,
      float segundaNota,
      double frequencia) {
    Boletim boletim = ambiente.boletimService.criarBoletimSeNaoExistir(matriculaAluno, codigoTurma);
    boletim.setPrimeiraNota(primeiraNota);
    boletim.setSegundaNota(segundaNota);
    boletim.setFrequencia(frequencia);
    ambiente.boletimRepository.atualizarBoletins(boletim);
  }

  // ---- buscarNomeDisciplina / buscarNomeProfessor ----

  @Test
  public void deveBuscarNomeDaDisciplinaExistente() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);

    Assertions.assertEquals(
        "Algoritmos", ambiente.relatorioAcademicoService.buscarNomeDisciplina(turma));
  }

  @Test
  public void deveRetornarDisciplinaNaoEncontradaQuandoCodigoNaoExiste() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turmaComDisciplinaInexistente =
        new Turma("dis99", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertEquals(
        "Disciplina não encontrada",
        ambiente.relatorioAcademicoService.buscarNomeDisciplina(turmaComDisciplinaInexistente));
  }

  @Test
  public void deveRetornarDisciplinaNaoEncontradaQuandoTurmaForNull() {
    Ambiente ambiente = criarAmbiente();

    Assertions.assertEquals(
        "Disciplina não encontrada",
        ambiente.relatorioAcademicoService.buscarNomeDisciplina(null));
  }

  @Test
  public void deveBuscarNomeDoProfessorExistente() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);

    Assertions.assertEquals(
        "João", ambiente.relatorioAcademicoService.buscarNomeProfessor(turma));
  }

  @Test
  public void deveRetornarProfessorNaoEncontradoQuandoTurmaForNull() {
    Ambiente ambiente = criarAmbiente();

    Assertions.assertEquals(
        "Professor não encontrado",
        ambiente.relatorioAcademicoService.buscarNomeProfessor(null));
  }

  @Test
  public void deveRetornarProfessorNaoEncontradoQuandoMatriculaNaoExiste() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turmaComProfessorInexistente =
        new Turma("dis00", "2026.2", "pr99", 30, "SEG 08:00-10:00", "LAB 01");

    Assertions.assertEquals(
        "Professor não encontrado",
        ambiente.relatorioAcademicoService.buscarNomeProfessor(turmaComProfessorInexistente));
  }

  // ---- construtores ----

  @Test
  public void deveCriarServicoComConstrutorPadrao() {
    Assertions.assertNotNull(new RelatorioAcademicoService());
  }

  @Test
  public void deveCriarServicoComConstrutorDeDuasDependencias() {
    Ambiente ambiente = criarAmbiente();

    Assertions.assertNotNull(
        new RelatorioAcademicoService(ambiente.turmaService, ambiente.usuarioService));
  }

  // ---- validacoes de coordenador adicionais ----

  @Test
  public void deveLancarExcecaoAoListarTurmasComCoordenadorDeCodigoCursoEmBranco() {
    Ambiente ambiente = criarAmbiente();
    Coordenador coordenadorCursoEmBranco =
        new Coordenador("Ana", "ana@email.com", "co01", "senha123", "  ");

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            ambiente.relatorioAcademicoService.listarTurmasDoCoordenador(
                coordenadorCursoEmBranco));
  }

  @Test
  public void deveLancarExcecaoAoGerarRelatorioDeAlunosComCoordenadorNull() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            ambiente.relatorioAcademicoService.gerarRelatorioAlunosTurma(null, turma));
  }

  @Test
  public void deveGerarRelatorioDeAlunosComAlunoRemovidoDaBase() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);
    Aluno aluno = criarAluno("Maria", "maria@email.com", "al00", "senha123");
    ambiente.userRepository.salvarUsuario(aluno);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);
    ambiente.userRepository.removerPorMatricula("al00", TipoUsuario.ALUNO);
    Turma turmaAtualizada = buscarTurmaAtualizada(ambiente, turma.getCodigo());

    RelatorioAlunosTurma relatorio =
        ambiente.relatorioAcademicoService.gerarRelatorioAlunosTurma(
            criarCoordenador(), turmaAtualizada);

    ItemRelatorioAlunoTurma item = relatorio.getAlunos().get(0);
    Assertions.assertEquals("al00", item.getMatricula());
    Assertions.assertEquals("Aluno não encontrado", item.getNome());
    Assertions.assertEquals("-", item.getEmail());
  }

  @Test
  public void deveLancarExcecaoAoGerarRelatorioDeOcupacaoComCoordenadorNull() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            ambiente.relatorioAcademicoService.gerarRelatorioOcupacaoVagas(null, turma));
  }

  @Test
  public void deveLancarExcecaoAoGerarRelatorioDeOcupacaoComTurmaNull() {
    Ambiente ambiente = criarAmbiente();

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            ambiente.relatorioAcademicoService.gerarRelatorioOcupacaoVagas(
                criarCoordenador(), null));
  }

  @Test
  public void deveLancarExcecaoAoGerarRelatorioDeOcupacaoComTurmaDeOutroCurso() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    ambiente.disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Redes", 60, 1, 4, "cur99", List.of()));
    Professor professorOutroCurso =
        criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    ambiente.userRepository.salvarUsuario(professorOutroCurso);
    Turma turmaOutroCurso =
        new Turma("dis01", "2026.2", "pr01", 30, "TER 10:00-12:00", "LAB 02");
    ambiente.turmaService.ofertarTurma(turmaOutroCurso);

    Assertions.assertThrows(
        EntradaInvalidaException.class,
        () ->
            ambiente.relatorioAcademicoService.gerarRelatorioOcupacaoVagas(
                criarCoordenador(), turmaOutroCurso));
  }

  @Test
  public void naoDeveContabilizarBoletinsEmRecuperacaoNoTotalDeReprovados() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma = criarEOfertarTurma(ambiente, 30);
    Aluno alunoEmRecuperacao = criarAluno("Ana", "ana@email.com", "al00", "senha123");
    ambiente.userRepository.salvarUsuario(alunoEmRecuperacao);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma.getCodigo(), alunoEmRecuperacao);
    lancarBoletimCompleto(ambiente, turma.getCodigo(), "al00", 5.0f, 5.0f, 90.0);

    List<ItemRelatorioReprovacaoDisciplina> itens =
        ambiente.relatorioAcademicoService.gerarRelatorioReprovacaoPorDisciplina(
            criarCoordenador());

    ItemRelatorioReprovacaoDisciplina item = itens.get(0);
    Assertions.assertEquals(0, item.getTotalResultadosFinais());
    Assertions.assertEquals(0, item.getTotalReprovados());
    Assertions.assertEquals(0.0, item.getPercentualReprovacao(), 0.001);
  }

  @Test
  public void deveAgruparReprovacaoDeMultiplasTurmasDaMesmaDisciplina() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    Turma turma1 = criarEOfertarTurma(ambiente, 30);
    Professor professor2 = criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    ambiente.userRepository.salvarUsuario(professor2);
    Turma turma2 = new Turma("dis00", "2026.2", "pr01", 30, "TER 10:00-12:00", "LAB 02");
    ambiente.turmaService.ofertarTurma(turma2);

    Aluno alunoAprovado = criarAluno("Bia", "bia@email.com", "al00", "senha123");
    Aluno alunoReprovado = criarAluno("Caio", "caio@email.com", "al01", "senha123");
    ambiente.userRepository.salvarUsuario(alunoAprovado);
    ambiente.userRepository.salvarUsuario(alunoReprovado);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma1.getCodigo(), alunoAprovado);
    ambiente.turmaService.cadastrarAlunoEmTurma(turma2.getCodigo(), alunoReprovado);

    lancarBoletimCompleto(ambiente, turma1.getCodigo(), "al00", 8.0f, 8.0f, 90.0);
    lancarBoletimCompleto(ambiente, turma2.getCodigo(), "al01", 2.0f, 2.0f, 90.0);

    List<ItemRelatorioReprovacaoDisciplina> itens =
        ambiente.relatorioAcademicoService.gerarRelatorioReprovacaoPorDisciplina(
            criarCoordenador());

    Assertions.assertEquals(1, itens.size());
    ItemRelatorioReprovacaoDisciplina item = itens.get(0);
    Assertions.assertEquals(2, item.getTotalResultadosFinais());
    Assertions.assertEquals(1, item.getTotalReprovadosPorNota());
    Assertions.assertEquals(1, item.getTotalReprovados());
  }

  @Test
  public void deveDesempatarOrdenacaoPorCodigoDaDisciplinaQuandoNomesForemIguais() {
    Ambiente ambiente = criarAmbiente();
    prepararDadosBasicos(ambiente);
    ambiente.disciplinaRepository.salvarDisciplina(
        new Disciplina("dis01", "Algoritmos", 60, 2, 4, CODIGO_CURSO, List.of()));
    Professor professor2 = criarProfessor("Pedro", "pedro@email.com", "senha123", "pr01");
    ambiente.userRepository.salvarUsuario(professor2);

    criarEOfertarTurma(ambiente, 30);
    Turma turmaDisciplinaDuplicada =
        new Turma("dis01", "2026.2", "pr01", 30, "TER 10:00-12:00", "LAB 02");
    ambiente.turmaService.ofertarTurma(turmaDisciplinaDuplicada);

    List<ItemRelatorioReprovacaoDisciplina> itens =
        ambiente.relatorioAcademicoService.gerarRelatorioReprovacaoPorDisciplina(
            criarCoordenador());

    Assertions.assertEquals(2, itens.size());
    Assertions.assertEquals("dis00", itens.get(0).getCodigoDisciplina());
    Assertions.assertEquals("dis01", itens.get(1).getCodigoDisciplina());
  }
}
