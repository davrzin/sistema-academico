package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
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
}
