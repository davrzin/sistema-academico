package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class TurmaRepositoryTest {

    @TempDir
    Path tempDir;

    @AfterEach
    public void tearDown() {
        File diretorio = tempDir.resolve("turmas").toFile();
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

    private TurmaRepository criarRepository() {
        return new TurmaRepository(
                new ObjectMapper(),
                tempDir.resolve("turmas").toString()
        );
    }

    @Test
    public void deveRetornarObjectMapper() {
        TurmaRepository repository = criarRepository();

        Assertions.assertNotNull(repository.getObjectMapper());
    }

    @Test
    public void deveRetornarDiretorioDasTurmas() {
        TurmaRepository repository = criarRepository();

        Assertions.assertNotNull(repository.getDiretorioTurmas());
    }

    @Test
    public void deveSalvarTurmaEmArquivo() {
        TurmaRepository repository = criarRepository();
        Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

        repository.salvarTurma(turma);

        File arquivo = tempDir.resolve("turmas").resolve("turmas.json").toFile();

        Assertions.assertTrue(arquivo.exists());
        Assertions.assertEquals(1, repository.listarTurmas().size());
        Assertions.assertEquals("tur00", repository.listarTurmas().get(0).getCodigo());
    }

    @Test
    public void deveLancarIllegalArgumentExceptionAoSalvarTurmaNull() {
        TurmaRepository repository = criarRepository();

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> repository.salvarTurma(null)
        );
    }

    @Test
    public void deveListarNenhumaTurmaQuandoArquivoNaoExiste() {
        TurmaRepository repository = criarRepository();

        Assertions.assertEquals(0, repository.listarTurmas().size());
    }

    @Test
    public void deveBuscarTurmaPorCodigo() {
        TurmaRepository repository = criarRepository();
        Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

        repository.salvarTurma(turma);

        Turma turmaEncontrada = repository.buscarPorCodigo("tur00");

        Assertions.assertNotNull(turmaEncontrada);
        Assertions.assertEquals("dis00", turmaEncontrada.getCodigoDisciplina());
    }

    @Test
    public void naoDeveEncontrarTurmaComCodigoInexistente() {
        TurmaRepository repository = criarRepository();
        Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");

        repository.salvarTurma(turma);

        Assertions.assertNull(repository.buscarPorCodigo("tur99"));
    }

    @Test
    public void deveBuscarTurmasPorProfessor() {
        TurmaRepository repository = criarRepository();
        repository.salvarTurma(new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
        repository.salvarTurma(new Turma("tur01", "dis01", "2026.2", "pr00", 25, "TER 08:00-10:00", "LAB 02"));
        repository.salvarTurma(new Turma("tur02", "dis02", "2026.2", "pr01", 20, "QUA 08:00-10:00", "LAB 03"));

        List<Turma> turmasDoProfessor = repository.buscarPorProfessor("pr00");

        Assertions.assertEquals(2, turmasDoProfessor.size());
    }

    @Test
    public void deveBuscarTurmasPorPeriodoLetivo() {
        TurmaRepository repository = criarRepository();
        repository.salvarTurma(new Turma("tur00", "dis00", "2026.1", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
        repository.salvarTurma(new Turma("tur01", "dis01", "2026.2", "pr00", 25, "TER 08:00-10:00", "LAB 02"));
        repository.salvarTurma(new Turma("tur02", "dis02", "2026.2", "pr01", 20, "QUA 08:00-10:00", "LAB 03"));

        List<Turma> turmasDoPeriodo = repository.buscarPorPeriodoLetivo("2026.2");

        Assertions.assertEquals(2, turmasDoPeriodo.size());
    }

    @Test
    public void deveAtualizarTurmaEmArquivo() {
        TurmaRepository repository = criarRepository();
        Turma turma = new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01");
        Turma turmaAtualizada = new Turma("tur00", "dis01", "2026.2", "pr01", 40, "TER 10:00-12:00", "LAB 02");

        repository.salvarTurma(turma);
        boolean atualizou = repository.atualizarTurma(turmaAtualizada);

        Turma turmaEncontrada = repository.buscarPorCodigo("tur00");

        Assertions.assertTrue(atualizou);
        Assertions.assertEquals(1, repository.listarTurmas().size());
        Assertions.assertEquals("dis01", turmaEncontrada.getCodigoDisciplina());
        Assertions.assertEquals("pr01", turmaEncontrada.getMatriculaProfessor());
        Assertions.assertEquals(40, turmaEncontrada.getLimiteVagas());
        Assertions.assertEquals("LAB 02", turmaEncontrada.getSala());
    }

    @Test
    public void naoDeveAtualizarTurmaInexistente() {
        TurmaRepository repository = criarRepository();
        Turma turmaAtualizada = new Turma("tur99", "dis01", "2026.2", "pr01", 40, "TER 10:00-12:00", "LAB 02");

        boolean atualizou = repository.atualizarTurma(turmaAtualizada);

        Assertions.assertFalse(atualizou);
    }

    @Test
    public void deveRemoverTurmaPorCodigo() {
        TurmaRepository repository = criarRepository();
        repository.salvarTurma(new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));
        repository.salvarTurma(new Turma("tur01", "dis01", "2026.2", "pr01", 25, "TER 08:00-10:00", "LAB 02"));

        boolean removeu = repository.removerTurmaPorCodigo("tur00");

        Assertions.assertTrue(removeu);
        Assertions.assertEquals(1, repository.listarTurmas().size());
        Assertions.assertNull(repository.buscarPorCodigo("tur00"));
        Assertions.assertNotNull(repository.buscarPorCodigo("tur01"));
    }

    @Test
    public void naoDeveRemoverTurmaInexistente() {
        TurmaRepository repository = criarRepository();
        repository.salvarTurma(new Turma("tur00", "dis00", "2026.2", "pr00", 30, "SEG 08:00-10:00", "LAB 01"));

        boolean removeu = repository.removerTurmaPorCodigo("tur99");

        Assertions.assertFalse(removeu);
        Assertions.assertEquals(1, repository.listarTurmas().size());
    }
}
