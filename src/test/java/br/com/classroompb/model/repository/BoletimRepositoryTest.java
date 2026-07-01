package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.GestaoAcademica.Boletim;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class BoletimRepositoryTest {

    @TempDir
    Path tempDir;

    private BoletimRepository boletimRepository;
    private Boletim boletim;

    @BeforeEach
    public void criarVariaveis(){
        boletimRepository = new BoletimRepository(new ObjectMapper(), tempDir.resolve("boletins").toString());
        boletim = new Boletim("al00", "tur00");
    }

    @AfterEach
    public void tearDown(){
        File diretorio = tempDir.resolve("aulas").toFile();
        File[] arquivos = diretorio.listFiles();


        if(arquivos != null){
            for(File arquivo : arquivos){
                arquivo.delete();
            }
        }

        if(diretorio.exists() && diretorio.isDirectory()){
            diretorio.delete();
        }
    }

    @Test
    public void deveCriarRepositorySomenteComMapper(){

        BoletimRepository boletimRepository1 = new BoletimRepository(new ObjectMapper());

        Assertions.assertNotNull(boletimRepository1);
    }

    @Test
    public void deveCriarRepositoryComConstrutorCompleto(){
        BoletimRepository boletimRepository1 = new BoletimRepository(new ObjectMapper(), tempDir.resolve("boletins").toString());

        Assertions.assertNotNull(boletimRepository1);
    }

    @Test
    public void deveRetornarObjectMapperCorretamente(){

        Assertions.assertEquals(ObjectMapper.class, boletimRepository.getObjectMapper().getClass());
    }

    @Test
    public void deveRetornarCaminhoDoArquivoCorretamente(){

        String caminhoEsperado = tempDir.resolve("boletins").toString();

        Assertions.assertEquals(caminhoEsperado, boletimRepository.getDiretorioBoletins());
    }

    @Test
    public void deveListarBoletinsCorretamente(){

        List<Boletim> boletins = boletimRepository.listarBoletins();

        Assertions.assertEquals(0, boletins.size());
    }

    @Test
    public void deveSalvarBoletimCorretamente(){

        boletimRepository.salvarBoletim(boletim);

        List<Boletim> boletins = boletimRepository.listarBoletins();

        Assertions.assertEquals(1, boletins.size());
        Assertions.assertEquals(boletim.getMatriculaAluno(), boletins.getFirst().getMatriculaAluno());
        Assertions.assertEquals(boletim.getCodigoTurma(), boletins.getFirst().getCodigoTurma());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoSalvarBoletimComBoletimNull(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimRepository.salvarBoletim(null));
    }

    @Test
    public void deveBuscarBoletimPorCodigoCorretamente(){

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorCodigoNull(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimRepository.buscarBoletimPorCodigo(null));
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorCodigoVazio(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimRepository.buscarBoletimPorCodigo(""));
    }

    @Test
    public void deveBuscarBoletimPorAlunoCorretamente(){

        boletimRepository.salvarBoletim(boletim);

        List<Boletim> boletinsluno = boletimRepository.buscarBoletinsPorAluno("al00");

        Assertions.assertEquals(1, boletinsluno.size());
        Assertions.assertEquals(boletim.getMatriculaAluno(), boletinsluno.getFirst().getMatriculaAluno());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorMatriculaAlunoNull(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimRepository.buscarBoletinsPorAluno(null));
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorMatriculaAlunoVazio(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimRepository.buscarBoletinsPorAluno(""));
    }

    @Test
    public void deveBuscarBoletimPorTurmaCorretamente(){

        boletimRepository.salvarBoletim(boletim);

        List<Boletim> boletinsluno = boletimRepository.buscarBoletinsPorTurma("tur00");

        Assertions.assertEquals(1, boletinsluno.size());
        Assertions.assertEquals(boletim.getCodigoTurma(), boletinsluno.getFirst().getCodigoTurma());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorCodigoTurmaNull(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimRepository.buscarBoletinsPorTurma(null));
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoBuscarBoletimPorCodigoTurmaVazio(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimRepository.buscarBoletinsPorTurma(""));
    }

}