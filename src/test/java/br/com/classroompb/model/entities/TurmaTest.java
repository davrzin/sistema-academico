package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TurmaTest {

    private Turma turma;

    @BeforeEach
    public void inicializarTurma(){
        turma = new Turma("T1", "5", "pr00", 40, "09:00-11:00", "B-109");
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarTurmaComDadosInvalidos(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Turma(null, null, null, -9, null, null));
    }

    @Test
    public void deveRetornarCodigoDisciplina(){

        Assertions.assertNotNull(turma.getCodigoDisciplina());
    }

    @Test
    public void deveRetornarPeriodoLetivo(){

        Assertions.assertNotNull(turma.getPeriodoLetivo());
    }

    @Test
    public void deveRetornarMatriculaProfessor(){

        Assertions.assertNotNull(turma.getMatriculaProfessor());
    }

    @Test
    public void deveRetornarLimiteDeVagas(){

        Assertions.assertEquals(40, turma.getLimiteVagas());
    }

    @Test
    public void deveRetornarHorario(){

        Assertions.assertNotNull(turma.getHorario());
    }

    @Test
    public void deveRetornarSala(){

        Assertions.assertNotNull(turma.getSala());
    }

    @Test
    public void deveAtualizarCodigoDisciplina(){
        turma.setCodigoDisciplina("T100");

        Assertions.assertEquals("T100", turma.getCodigoDisciplina());
    }

    @Test
    public void deveAtualizarPeriodoLetivo(){
        turma.setPeriodoLetivo("6");

        Assertions.assertEquals("6", turma.getPeriodoLetivo());
    }

    @Test
    public void deveAtualizarMatriculaProfessor(){
        turma.setMatriculaProfessor("pr100");

        Assertions.assertEquals("pr100", turma.getMatriculaProfessor());
    }

    @Test
    public void deveAtualizarLimiteDeVagas(){
        turma.setLimiteVagas(50);

        Assertions.assertEquals(50, turma.getLimiteVagas());
    }

    @Test
    public void deveAtualizarHorario(){
        turma.setHorario("07:00-09:00");

        Assertions.assertEquals("07:00-09:00", turma.getHorario());
    }

    @Test
    public void deveAtualizarSala(){
        turma.setSala("C-206");

        Assertions.assertEquals("C-206", turma.getSala());
    }
}
