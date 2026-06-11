package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.TurmaNaoEncontradaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class TurmaServiceTest {

    @TempDir
    Path tempDir;

    @AfterEach
    public void tearDown() {
        apagarDiretorio("turmas");
        apagarDiretorio("disciplinas");
        apagarDiretorio("periodos");
        apagarDiretorio("usuarios");
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

    private TurmaService criarService(
            TurmaRepository turmaRepository,
            DisciplinaRepository disciplinaRepository,
            PeriodoLetivoRepository periodoLetivoRepository,
            UserRepository userRepository
    ) {
        return new TurmaService(
                turmaRepository,
                disciplinaRepository,
                periodoLetivoRepository,
                userRepository
        );
    }

    private void prepararDadosBasicos(
            DisciplinaRepository disciplinaRepository,
            PeriodoLetivoRepository periodoLetivoRepository,
            UserRepository userRepository
    ) {
        disciplinaRepository.salvarDisciplina(new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur00", List.of()));
        periodoLetivoRepository.salvarPeriodoLetivo(new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));

        Professor professor = new Professor("João", "joao@email.com", "senha123");
        professor.setMatricula("pr00");
        userRepository.salvarUsuario(professor);
    }

    @Test
    public void deveOfertarTurmaComProfessorResponsavel() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

        service.ofertarTurma(turma);

        Assertions.assertEquals("tur00", turma.getCodigo());
        Assertions.assertEquals(1, turmaRepository.listarTurmas().size());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoOfertarTurmaNull() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.ofertarTurma(null)
        );
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionQuandoProfessorNaoForInformado() {
        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> new Turma("dis00", "2026.2", "", 30, "SEG 08:00-10:00", "LAB 01")
        );
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionQuandoProfessorNaoExistir() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();

        disciplinaRepository.salvarDisciplina(new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur00", List.of()));
        periodoLetivoRepository.salvarPeriodoLetivo(new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        Turma turma = new Turma("dis00", "2026.2", "pr99", 30, "SEG 08:00-10:00", "LAB 01");

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.ofertarTurma(turma)
        );
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionQuandoMatriculaForDeAluno() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();

        disciplinaRepository.salvarDisciplina(new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur00", List.of()));
        periodoLetivoRepository.salvarPeriodoLetivo(new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));

        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
        aluno.setMatricula("al00");
        userRepository.salvarUsuario(aluno);

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        Turma turma = new Turma("dis00", "2026.2", "al00", 30, "SEG 08:00-10:00", "LAB 01");

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.ofertarTurma(turma)
        );
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionQuandoDisciplinaNaoExistir() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();

        periodoLetivoRepository.salvarPeriodoLetivo(new PeriodoLetivo("2026.2", "01/08/2026", "20/12/2026"));
        Professor professor = new Professor("João", "joao@email.com", "senha123");
        professor.setMatricula("pr00");
        userRepository.salvarUsuario(professor);

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        Turma turma = new Turma("dis99", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.ofertarTurma(turma)
        );
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionQuandoPeriodoLetivoNaoExistir() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();

        disciplinaRepository.salvarDisciplina(new Disciplina("dis00", "Algoritmos", 60, 1, 4, "cur00", List.of()));
        Professor professor = new Professor("João", "joao@email.com", "senha123");
        professor.setMatricula("pr00");
        userRepository.salvarUsuario(professor);

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.ofertarTurma(turma)
        );
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionQuandoProfessorTiverChoqueDeHorario() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

        Turma turmaComConflito = new Turma("dis01", "2026.2", "pr00", 25, "SEG 08:00-10:00", "LAB 02");

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.ofertarTurma(turmaComConflito)
        );
    }

    @Test
    public void deveAlterarTurma() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        Professor professor = new Professor("Pedro", "pedro@email.com", "senha123");
        professor.setMatricula("pr01");
        userRepository.salvarUsuario(professor);

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

        Turma turmaAtualizada = new Turma("dis01", "2026.2", "pr01", 40, "TER 10:00-12:00", "LAB 02");
        service.alterarTurma("tur00", turmaAtualizada);

        Turma turmaEncontrada = turmaRepository.buscarPorCodigo("tur00");

        Assertions.assertEquals("dis01", turmaEncontrada.getCodigoDisciplina());
        Assertions.assertEquals("pr01", turmaEncontrada.getMatriculaProfessor());
        Assertions.assertEquals(40, turmaEncontrada.getLimiteVagas());
        Assertions.assertEquals("LAB 02", turmaEncontrada.getSala());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoAlterarTurmaInexistente() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        Turma turmaAtualizada = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

        Assertions.assertThrows(
                TurmaNaoEncontradaException.class,
                () -> service.alterarTurma("tur99", turmaAtualizada)
        );
    }

    @Test
    public void deveIgnorarPropriaTurmaAoValidarConflitoNaAlteracao() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

        Turma turmaAtualizada = new Turma("dis00", "2026.2", "pr00", 35, "SEG 08:00-10:00", "LAB 02");

        Assertions.assertDoesNotThrow(() -> service.alterarTurma("tur00", turmaAtualizada));
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoAlterarTurmaGerandoConflitoDeHorario() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
        service.ofertarTurma(new Turma("dis01", "2026.2", "pr00", 30, "TER 10:00-12:00", "LAB 02"));

        Turma turmaAtualizadaComConflito = new Turma("dis01", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 03");

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.alterarTurma("tur01", turmaAtualizadaComConflito)
        );
    }

    @Test
    public void deveCancelarTurma() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

        service.cancelarTurma("tur00");

        Assertions.assertEquals(0, turmaRepository.listarTurmas().size());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCancelarTurmaInexistente() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.cancelarTurma("tur99")
        );
    }

    @Test
    public void deveListarTurmasPorCodigo(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        Turma turma1 = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
        Turma turma2 = new Turma("dis01", "2026.2", "pr00", 30, "TER 10:00-12:00", "LAB 02");

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        service.ofertarTurma(turma1);
        service.ofertarTurma(turma2);

        Turma turmaBuscada = service.buscarTurmaPorCodigo(turma1.getCodigo());

        Assertions.assertNotNull(turmaBuscada);
        Assertions.assertEquals(turmaBuscada.getCodigo(), turma1.getCodigo());
    }

    @Test
    public void deveLancarTurmaNaoEncontradaException(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);


        Assertions.assertThrows(TurmaNaoEncontradaException.class, () -> service.buscarTurmaPorCodigo("f23ff"));


    }

    @Test
    public void deveListarTurmasPorProfessor() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
        service.ofertarTurma(new Turma("dis01", "2026.2", "pr00", 30, "TER 10:00-12:00", "LAB 02"));

        List<Turma> turmas = service.listarTurmasPorProfessor("pr00");

        Assertions.assertEquals(2, turmas.size());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoListarTurmasComProfessorNull(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Assertions.assertThrows(EntradaInvalidaException.class, () -> service.listarTurmasPorProfessor(null));
    }

    @Test
    public void deveListarTurmasPorPeriodoLetivo() {
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);
        service.ofertarTurma(new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

        List<Turma> turmas = service.listarTurmasPorPeriodoLetivo("2026.2");

        Assertions.assertEquals(1, turmas.size());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoListarTurmasComPeriodoNulo(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Assertions.assertThrows(EntradaInvalidaException.class, () -> service.listarTurmasPorPeriodoLetivo(null));
    }

    @Test
    public void deveCadastrarAlunoCorretamenteEmTurma(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
        service.ofertarTurma(turma);

        Aluno aluno = new Aluno( "Maria", "maria@email.com", "al00", "senha123");
        userRepository.salvarUsuario(aluno);

        service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

        Turma turmaAtualizada = service.buscarTurmaPorCodigo(turma.getCodigo());

        Assertions.assertEquals(1, turmaAtualizada.getMatriculados().size());
        Assertions.assertEquals("al00", turmaAtualizada.getMatriculados().getFirst());
        Assertions.assertEquals(1, aluno.getTurmasMatriculadas().size());
        Assertions.assertEquals("tur00", aluno.getTurmasMatriculadas().getFirst());
    }

    @Test
    public void deveLancarcEntradaInvalidaExceptionEmCadastrarAlunoComTurmaNull(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Aluno aluno = new Aluno( "Maria", "maria@email.com", "al00", "senha123");
        userRepository.salvarUsuario(aluno);

        Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarAlunoEmTurma(null, aluno));

    }

    @Test
    public void deveLancarTurmaNaoEncontradaExceptionEmCadastrarAlunoComTurmaInexistente(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Aluno aluno = new Aluno( "Maria", "maria@email.com", "al00", "senha123");
        userRepository.salvarUsuario(aluno);

        Assertions.assertThrows(TurmaNaoEncontradaException.class, () -> service.cadastrarAlunoEmTurma("123ds", aluno));

    }

    @Test
    public void deveLancarNullPointerExceptionExceptionEmCadastrarAlunoComAlunoNull(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
        service.ofertarTurma(turma);

        Assertions.assertThrows(NullPointerException.class, () -> service.cadastrarAlunoEmTurma(turma.getCodigo(), null));

    }

    @Test
    public void deveCadastrarAlunoCorretamenteNaListaDeEspera(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 1, "SEG 08:00-10:00", "LAB 01");
        service.ofertarTurma(turma);

        Aluno aluno = new Aluno( "Maria", "maria@email.com", "al00", "senha123");
        userRepository.salvarUsuario(aluno);

        Aluno aluno2 = new Aluno( "Joao", "joao@email.com", "al01", "senha123");
        userRepository.salvarUsuario(aluno);

        service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);
        service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno2);

        Turma turmaAtualizada = service.buscarTurmaPorCodigo(turma.getCodigo());

        Assertions.assertEquals(1, turmaAtualizada.getListaEspera().size());
    }

    @Test
    public void deveCancelarAlunoTurmaCorretamente(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
        service.ofertarTurma(turma);

        Aluno aluno = new Aluno( "Maria", "maria@email.com", "al00", "senha123");
        userRepository.salvarUsuario(aluno);

        service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

        service.cancelarAlunoTurma(turma.getCodigo(), aluno);

        Turma turmaAtualizada = service.buscarTurmaPorCodigo(turma.getCodigo());

        Assertions.assertEquals(0, aluno.getTurmasMatriculadas().size());
        Assertions.assertEquals(0, turma.getMatriculados().size());
    }

    @Test
    public void deveLancarcEntradaInvalidaExceptionEmCancelarAlunoComTurmaNull(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
        service.ofertarTurma(turma);

        Aluno aluno = new Aluno( "Maria", "maria@email.com", "al00", "senha123");
        userRepository.salvarUsuario(aluno);

        service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

        Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cancelarAlunoTurma(null, aluno));

    }

    @Test
    public void deveLancarcEntradaInvalidaExceptionEmCancelarAlunoComTurmaInexistente(){
        TurmaRepository turmaRepository = criarTurmaRepository();
        DisciplinaRepository disciplinaRepository = criarDisciplinaRepository();
        PeriodoLetivoRepository periodoLetivoRepository = criarPeriodoLetivoRepository();
        UserRepository userRepository = criarUserRepository();
        prepararDadosBasicos(disciplinaRepository, periodoLetivoRepository, userRepository);
        disciplinaRepository.salvarDisciplina(new Disciplina("dis01", "Estrutura de Dados", 60, 2, 4, "cur00", List.of()));

        TurmaService service = criarService(turmaRepository, disciplinaRepository, periodoLetivoRepository, userRepository);

        Turma turma = new Turma("dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
        service.ofertarTurma(turma);

        Aluno aluno = new Aluno( "Maria", "maria@email.com", "al00", "senha123");
        userRepository.salvarUsuario(aluno);

        service.cadastrarAlunoEmTurma(turma.getCodigo(), aluno);

        Assertions.assertThrows(TurmaNaoEncontradaException.class, () -> service.cancelarAlunoTurma("asda", aluno));

    }
}
