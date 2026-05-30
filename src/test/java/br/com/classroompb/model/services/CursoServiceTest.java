package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.GestaoAcademica.Curso;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.CursoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class CursoServiceTest {



    @TempDir
    Path tempDir;

    CursoRepository repository;
    CursoService service;

    @BeforeEach
    public void criarVariaveis(){
         repository = criarCursoRepository();
         service = criarCursoService(repository);

    }
    @AfterEach
    public void tearDown(){
        File diretorio = tempDir.resolve("cursos").toFile();
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

    private CursoRepository criarCursoRepository(){
        return new CursoRepository(
                new ObjectMapper(),
                tempDir.resolve("periodos").toString()
        );
    }

    private CursoService criarCursoService(CursoRepository cursoRepository){
        return new CursoService(cursoRepository);
    }

    @Test
    public void deveCadastrarCurso(){

        Curso curso = new Curso("Ciência da Computação", 8, 3000);

        service.cadastrarCurso(curso);

        Assertions.assertEquals(1, repository.listarCursos().size());
        Assertions.assertEquals(curso.getNome(), repository.listarCursos().getFirst().getNome());

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCadastrarCursoNull(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarCurso(null));
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCadastrarCursoJaExistente(){
        Curso curso = new Curso("Ciência da Computação", 8, 3000);

        Curso curso2 = new Curso("Ciência da Computação", 8, 3000);

        service.cadastrarCurso(curso);

        Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarCurso(curso2));

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionQuandoNomeJaExistir(){
        Curso curso = new Curso("Ciência da Computação", 8, 3000);

        Curso curso2 = new Curso("Ciência da Computação", 10, 4000);

        service.cadastrarCurso(curso);

        Assertions.assertThrows(EntradaInvalidaException.class, () -> service.cadastrarCurso(curso2));

    }

    @Test
    public void deveListarTodosOsCursosCadastrados(){
        Curso curso = new Curso("Ciência da Computação", 8, 3000);

        Curso curso2 = new Curso("Medicina", 10, 4000);

        Curso curso3 = new Curso("Farmácia", 10, 4000);


        service.cadastrarCurso(curso);
        service.cadastrarCurso(curso2);
        service.cadastrarCurso(curso3);

        List<Curso> listaCursos = service.listarCursos();

        Assertions.assertEquals(3, listaCursos.size());
        Assertions.assertEquals(curso.getNome(), listaCursos.getFirst().getNome());
        Assertions.assertEquals(curso2.getNome(), listaCursos.get(1).getNome());
        Assertions.assertEquals(curso3.getNome(), listaCursos.getLast().getNome());


    }

    @Test
    public void deveRetornarListaVaziaSeNaoHouverCursosCadastrados(){

        List<Curso> listaVaziaCursos = service.listarCursos();

        Assertions.assertTrue(listaVaziaCursos.isEmpty());
    }
}
