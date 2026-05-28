package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

public class PeriodoLetivoRepositoryTest {

    @TempDir
    Path tempDir;

    @AfterEach
    public void tearDown() {
        File diretorio = tempDir.resolve("periodos").toFile();
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

    private PeriodoLetivoRepository criarRepository() {
        return new PeriodoLetivoRepository(
                new ObjectMapper(),
                tempDir.resolve("periodos").toString()
        );
    }

    @Test
    public void deveRetornarObjectMapper() {
        PeriodoLetivoRepository repository = criarRepository();

        Assertions.assertNotNull(repository.getObjectMapper());
    }

    @Test
    public void deveRetornarDiretorioParaArquivo() {
        PeriodoLetivoRepository repository = criarRepository();

        Assertions.assertNotNull(repository.getDiretorioPeriodoLetivo());
    }

    @Test
    public void deveCadastrarPeriodoLetivoEmArquivo() {
        PeriodoLetivoRepository repository = criarRepository();
        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        boolean salvou = repository.salvarPeriodoLetivo(periodo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();

        Assertions.assertTrue(salvou);
        Assertions.assertTrue(arquivo.exists());
        Assertions.assertEquals(1, repository.listarPeriodos().size());
    }

    @Test
    public void deveLancarIllegalArgumentExceptionAoSalvarPeriodoLetivoNull() {
        PeriodoLetivoRepository repository = criarRepository();

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> repository.salvarPeriodoLetivo(null)
        );
    }

    @Test
    public void deveAtualizarPeriodoLetivoEmArquivo() {
        PeriodoLetivoRepository repository = criarRepository();

        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");
        PeriodoLetivo periodoNovo = new PeriodoLetivo("2026.2", "03/02/2026", "16/09/2026");

        repository.salvarPeriodoLetivo(periodo);

        boolean atualizou = repository.updatePeriodoLetivo(periodoNovo, 0);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();

        Assertions.assertTrue(atualizou);
        Assertions.assertTrue(arquivo.exists());
        Assertions.assertEquals(1, repository.listarPeriodos().size());
        Assertions.assertEquals("2026.2", repository.listarPeriodos().get(0).getPeriodo());
    }

    @Test
    public void deveLancarIllegalArgumentExceptionAoAtualizarPeriodoLetivoNull() {
        PeriodoLetivoRepository repository = criarRepository();

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> repository.updatePeriodoLetivo(null, 0)
        );
    }

    @Test
    public void deveListarTodosOsPeriodosSalvos() {
        PeriodoLetivoRepository repository = criarRepository();

        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");
        PeriodoLetivo periodoNovo = new PeriodoLetivo("2026.2", "03/10/2026", "15/12/2026");

        repository.salvarPeriodoLetivo(periodo);
        repository.salvarPeriodoLetivo(periodoNovo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();

        Assertions.assertTrue(arquivo.exists());
        Assertions.assertEquals(2, repository.listarPeriodos().size());
    }

    @Test
    public void deveListarNenhumPeriodoQuandoArquivoNaoExiste() {
        PeriodoLetivoRepository repository = criarRepository();

        Assertions.assertEquals(0, repository.listarPeriodos().size());
    }

    @Test
    public void deveRetornarTrueQuandoExistirPeriodoComMesmoPeriodo() {
        PeriodoLetivoRepository repository = criarRepository();

        repository.salvarPeriodoLetivo(new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026"));

        boolean existe = repository.existePeriodoComDados("2026.1", "03/02/2026", "16/09/2026");

        Assertions.assertTrue(existe);
    }

    @Test
    public void deveRetornarTrueQuandoExistirPeriodoComMesmaDataInicio() {
        PeriodoLetivoRepository repository = criarRepository();

        repository.salvarPeriodoLetivo(new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026"));

        boolean existe = repository.existePeriodoComDados("2026.2", "02/02/2026", "16/09/2026");

        Assertions.assertTrue(existe);
    }

    @Test
    public void deveRetornarTrueQuandoExistirPeriodoComMesmaDataFim() {
        PeriodoLetivoRepository repository = criarRepository();

        repository.salvarPeriodoLetivo(new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026"));

        boolean existe = repository.existePeriodoComDados("2026.2", "03/02/2026", "15/09/2026");

        Assertions.assertTrue(existe);
    }

    @Test
    public void deveRetornarFalseQuandoNaoExistirPeriodoComDadosIguais() {
        PeriodoLetivoRepository repository = criarRepository();

        repository.salvarPeriodoLetivo(new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026"));

        boolean existe = repository.existePeriodoComDados("2026.2", "03/02/2026", "16/09/2026");

        Assertions.assertFalse(existe);
    }

    @Test
    public void deveEncontrarPeriodoLetivoBuscado() {
        PeriodoLetivoRepository repository = criarRepository();

        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");
        repository.salvarPeriodoLetivo(periodo);

        PeriodoLetivo periodoProcurado = repository.buscarPeriodoLetivo(
                "2026.1",
                "02/02/2026",
                "15/09/2026"
        );

        Assertions.assertNotNull(periodoProcurado);
        Assertions.assertEquals(periodo.getPeriodo(), periodoProcurado.getPeriodo());
        Assertions.assertEquals(periodo.getDataInicio(), periodoProcurado.getDataInicio());
        Assertions.assertEquals(periodo.getDataFim(), periodoProcurado.getDataFim());
    }

    @Test
    public void naoDeveEncontrarPeriodoLetivoBuscado() {
        PeriodoLetivoRepository repository = criarRepository();

        repository.salvarPeriodoLetivo(new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026"));

        PeriodoLetivo periodoProcurado = repository.buscarPeriodoLetivo(
                "2027.1",
                "20/02/2027",
                "20/10/2027"
        );

        Assertions.assertNull(periodoProcurado);
    }

    @Test
    public void deveEncontrarPeriodoLetivoAtivo() {
        PeriodoLetivoRepository repository = criarRepository();

        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");
        periodo.setPeriodoAtivo(true);

        repository.salvarPeriodoLetivo(periodo);

        Assertions.assertTrue(repository.existePeriodoLetivoAtivo());
    }

    @Test
    public void naoDeveEncontrarPeriodoLetivoAtivo() {
        PeriodoLetivoRepository repository = criarRepository();

        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");
        repository.salvarPeriodoLetivo(periodo);

        Assertions.assertFalse(repository.existePeriodoLetivoAtivo());
    }
}
