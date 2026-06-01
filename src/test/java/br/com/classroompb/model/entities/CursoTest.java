package br.com.classroompb.model.entities;
 
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import br.com.classroompb.model.entities.GestaoAcademica.Curso;
import br.com.classroompb.model.exception.EntradaInvalidaException;
 
public class CursoTest {
 
    @Test
    public void deveCriarCursoComConstrutorVazio() {
        Curso curso = new Curso();
        Assertions.assertEquals(Curso.class, curso.getClass());
    }
 
    @Test
    public void deveCriarCursoComTresParametros() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertEquals(Curso.class, curso.getClass());
    }
 
    @Test
    public void deveCriarCursoComQuatroParametros() {
        Curso curso = new Curso("ESW001", "Engenharia de Software", 8, 3200);
        Assertions.assertEquals(Curso.class, curso.getClass());
    }
 
    @Test
    public void deveLancarExceptionNoConstrutorQuandoNomeVazio() {
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> new Curso("", 8, 3200));
    }
 
    @Test
    public void deveLancarExceptionNoConstrutorQuandoQuantidadePeriodosZero() {
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> new Curso("Engenharia de Software", 0, 3200));
    }
 
    @Test
    public void deveLancarExceptionNoConstrutorQuandoCargaHorariaZero() {
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> new Curso("Engenharia de Software", 8, 0));
    }

 
    @Test
    public void deveRetornarCodigo() {
        Curso curso = new Curso("ESW001", "Engenharia de Software", 8, 3200);
        Assertions.assertEquals("ESW001", curso.getCodigo());
    }
 
    @Test
    public void deveAlterarCodigo() {
        Curso curso = new Curso("ESW001", "Engenharia de Software", 8, 3200);
        curso.setCodigo("ESW002");
        Assertions.assertEquals("ESW002", curso.getCodigo());
    }
 
    @Test
    public void deveLancarExceptionEmSetCodigoVazio() {
        Curso curso = new Curso("ESW001", "Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setCodigo(""));
    }
 
    @Test
    public void deveLancarExceptionEmSetCodigoNulo() {
        Curso curso = new Curso("ESW001", "Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setCodigo(null));
    }
  
    @Test
    public void deveRetornarNome() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertEquals("Engenharia de Software", curso.getNome());
    }
 
    @Test
    public void deveAlterarNome() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        curso.setNome("Ciência da Computação");
        Assertions.assertEquals("Ciência da Computação", curso.getNome());
    }
 
    @Test
    public void deveLancarExceptionEmSetNomeVazio() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setNome(""));
    }
 
    @Test
    public void deveLancarExceptionEmSetNomeNulo() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setNome(null));
    }
 
    @Test
    public void deveLancarExceptionEmSetNomeMuitoCurto() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setNome("AB"));
    }
 
    @Test
    public void deveLancarExceptionEmSetNomeMuitoLongo() {
        String nomeLongo = "A".repeat(201);
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setNome(nomeLongo));
    }
  
    @Test
    public void deveRetornarQuantidadePeriodos() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertEquals(8, curso.getQuantidadePeriodos());
    }
 
    @Test
    public void deveAlterarQuantidadePeriodos() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        curso.setQuantidadePeriodos(10);
        Assertions.assertEquals(10, curso.getQuantidadePeriodos());
    }
 
    @Test
    public void deveLancarExceptionEmSetQuantidadePeriodosZero() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setQuantidadePeriodos(0));
    }
 
    @Test
    public void deveLancarExceptionEmSetQuantidadePeriodosNegativo() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setQuantidadePeriodos(-1));
    }
 
    @Test
    public void deveLancarExceptionEmSetQuantidadePeriodosAcimaDoLimite() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setQuantidadePeriodos(14));
    }
 
    @Test
    public void deveAceitarQuantidadePeriodosNoLimiteMaximo() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        curso.setQuantidadePeriodos(13);
        Assertions.assertEquals(13, curso.getQuantidadePeriodos());
    }
  
    @Test
    public void deveRetornarCargaHorariaTotal() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertEquals(3200, curso.getCargaHorariaTotal());
    }
 
    @Test
    public void deveAlterarCargaHorariaTotal() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        curso.setCargaHorariaTotal(4000);
        Assertions.assertEquals(4000, curso.getCargaHorariaTotal());
    }
 
    @Test
    public void deveLancarExceptionEmSetCargaHorariaZero() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setCargaHorariaTotal(0));
    }
 
    @Test
    public void deveLancarExceptionEmSetCargaHorariaNegativa() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setCargaHorariaTotal(-100));
    }
 
    @Test
    public void deveLancarExceptionEmSetCargaHorariaAcimaDoLimite() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertThrows(EntradaInvalidaException.class,
                () -> curso.setCargaHorariaTotal(10001));
    }
 
    @Test
    public void deveAceitarCargaHorariaNoLimiteMaximo() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        curso.setCargaHorariaTotal(10000);
        Assertions.assertEquals(10000, curso.getCargaHorariaTotal());
    }
  
    @Test
    public void deveValidarDadosBasicosComDadosValidos() {
        Curso curso = new Curso("Engenharia de Software", 8, 3200);
        Assertions.assertDoesNotThrow(curso::validarDadosBasicos);
    }
  
    @Test
    public void deveRetornarToStringNaoNulo() {
        Curso curso = new Curso("ESW001", "Engenharia de Software", 8, 3200);
        Assertions.assertNotNull(curso.toString());
    }
 
    @Test
    public void deveConterNomeNoToString() {
        Curso curso = new Curso("ESW001", "Engenharia de Software", 8, 3200);
        Assertions.assertTrue(curso.toString().contains("Engenharia de Software"));
    }
}