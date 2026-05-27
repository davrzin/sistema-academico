package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.exception.PeriodoLetivoExistenteException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

public class PeriodoLetivoTest {

    @TempDir
    Path tempDir;

    @AfterEach
    public void tearDown(){
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


    @Test
    public void deveRetornarObjectMapper(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());

        Assertions.assertNotNull(repository.getObjectMapper());
    }

    @Test
    public void deveRetornarDiretorioParaArquivo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());

        Assertions.assertNotNull(repository.getDiretorioPeriodoLetivo());
    }
    @Test
    public void deveCadastrarPeriodoLetivoEmArquivo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        repository.salvarPeriodoLetivo(periodo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();
        Assertions.assertTrue(arquivo.exists());
        Assertions.assertEquals(1, repository.listarPeriodos().size());
    }

    @Test
    public void deveLancarIllegalArgumentExceptionEmSalvarPeriodoLetivo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());

        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.salvarPeriodoLetivo(null));
    }

    @Test
    public void deveLancarPersistenciaExceptionEmSalvarPeriodoLetivo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.salvarPeriodoLetivo(null));
    }

    @Test
    public void deveAtualizarPeriodoLetivoEmArquivo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");
        PeriodoLetivo periodoNovo = new PeriodoLetivo("2026.2", "02/02/2026", "15/09/2026");

        repository.salvarPeriodoLetivo(periodo);
        repository.updatePeriodoLetivo(periodoNovo, 0);



        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();
        Assertions.assertTrue(arquivo.exists());
        Assertions.assertEquals(1, repository.listarPeriodos().size());
        Assertions.assertEquals(periodoNovo.getPeriodo(), repository.listarPeriodos().get(0).getPeriodo());
    }

    @Test
    public void deveLancarIllegalArgumentExceptionEmAtualizarPeriodoLetivo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());

        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.salvarPeriodoLetivo(null));
    }

    @Test
    public void deveLancarPersistenciaExceptionEmAtualizarPeriodoLetivo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.salvarPeriodoLetivo(null));
    }

    @Test
    public void deveListarTodosOsPeriodosSalvos(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");
        PeriodoLetivo periodoNovo = new PeriodoLetivo("2026.2", "02/02/2026", "15/09/2026");

        repository.salvarPeriodoLetivo(periodo);
        repository.salvarPeriodoLetivo(periodoNovo);



        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();
        Assertions.assertTrue(arquivo.exists());
        Assertions.assertEquals(2, repository.listarPeriodos().size());
    }

    @Test
    public void deveListarNenhumPeriodo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());

        Assertions.assertEquals(0, repository.listarPeriodos().size());
    }

    @Test
    public void deveLancarPeriodoLetivoExistenteExceptionComTodosOsAtributosIguais(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        repository.salvarPeriodoLetivo(periodo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();


        Assertions.assertTrue(arquivo.exists());
        Assertions.assertThrows(PeriodoLetivoExistenteException.class,() -> repository.validarAtributosExistentes("2026.1", "02/02/2026", "15/09/2026"));
    }

    @Test
    public void deveLancarPeriodoLetivoExistenteExceptionComDoisAtributosIguais(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.2", "02/02/2026", "15/09/2026");

        repository.salvarPeriodoLetivo(periodo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();


        Assertions.assertTrue(arquivo.exists());
        Assertions.assertThrows(PeriodoLetivoExistenteException.class,() -> repository.validarAtributosExistentes("2026.1", "02/02/2026", "15/09/2026"));
    }

    @Test
    public void deveLancarPeriodoLetivoExistenteExceptionComUmAtributoIgual(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.2", "03/02/2026", "15/09/2026");

        repository.salvarPeriodoLetivo(periodo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();


        Assertions.assertTrue(arquivo.exists());
        Assertions.assertThrows(PeriodoLetivoExistenteException.class,() -> repository.validarAtributosExistentes("2026.1", "02/02/2026", "15/09/2026"));
    }

    @Test
    public void deveRetornarFalseEmValidarAtributosExistentes(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.2", "03/02/2026", "15/08/2026");

        repository.salvarPeriodoLetivo(periodo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();

        boolean existeAtributosIguais = repository.validarAtributosExistentes("2026.1", "02/02/2026", "15/09/2026");

        Assertions.assertTrue(arquivo.exists());
        Assertions.assertFalse(existeAtributosIguais);
    }

    @Test
    public void deveEncontrarPeriodoLetivoBuscado(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        repository.salvarPeriodoLetivo(periodo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();
        PeriodoLetivo periodoProcurado = repository.buscarPeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        Assertions.assertTrue(arquivo.exists());
        Assertions.assertEquals(periodo.getPeriodo(), periodoProcurado.getPeriodo());
        Assertions.assertEquals(periodo.getDataInicio(), periodoProcurado.getDataInicio());
        Assertions.assertEquals(periodo.getDataFim(), periodoProcurado.getDataFim());
    }

    @Test
    public void naoDeveEncontrarPeriodoLetivoBuscado(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        repository.salvarPeriodoLetivo(periodo);

        PeriodoLetivo periodoProcurado = repository.buscarPeriodoLetivo("2027.1", "20/02/2027", "20/10/2027");

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();
        Assertions.assertTrue(arquivo.exists());
        Assertions.assertNull( periodoProcurado);
    }

    @Test
    public void deveEncontrarPeriodoLetivoAtivo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        periodo.setPeriodoAtivo(true);
        repository.salvarPeriodoLetivo(periodo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();
        Assertions.assertTrue(arquivo.exists());
        Assertions.assertTrue(repository.existePeriodoLetivoAtivo());
    }

    @Test
    public void naoDeveEncontrarPeriodoLetivoAtivo(){
        PeriodoLetivoRepository repository = new PeriodoLetivoRepository(new ObjectMapper(), tempDir.resolve("periodos").toString());
        PeriodoLetivo periodo = new PeriodoLetivo("2026.1", "02/02/2026", "15/09/2026");

        repository.salvarPeriodoLetivo(periodo);

        File arquivo = tempDir.resolve("periodos").resolve("periodos_letivos.json").toFile();
        Assertions.assertTrue(arquivo.exists());
        Assertions.assertFalse(repository.existePeriodoLetivoAtivo());
    }

}
