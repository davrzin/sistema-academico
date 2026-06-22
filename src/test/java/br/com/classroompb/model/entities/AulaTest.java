package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.GestaoAcademica.Aula;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class AulaTest {

    Aula aulaTeste;

    @BeforeEach
    public void criarAula(){
        aulaTeste = new Aula("aula00", "tur00", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "SEX 08:00-10:00", new HashMap<>());
    }

    @Test
    public void deveCriarAulaComConstrutorVazio(){


        Aula aula = new Aula();

        Assertions.assertNotNull(aula);
    }

    @Test
    public void deveCriarAulaComConstrutorSemPresencas(){
        Aula aula = new Aula("aula00", "tur00", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "SEX 08:00-10:00");

        Assertions.assertNotNull(aula);
    }

    @Test
    public void deveLancarNullPointerExceptionAoCriarAulaComAtributoNull(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Aula(null, null, null, null));
    }

    @Test
    public void deveCriarAulaComConstrutorCompleto(){
        Aula aula = new Aula("aula00", "tur00", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "SEX 08:00-10:00", new HashMap<>());

        Assertions.assertNotNull(aula);
    }

    @Test
    public void deveLancarNullPointerExceptionComConstrutorCompleto(){
        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Aula(null, null, null, null, null));
    }

    @Test
    public void deveRetornarCodigoAula(){

        Assertions.assertEquals("aula00", aulaTeste.getId());
    }

    @Test
    public void deveRetornarCodigoTurma(){

        Assertions.assertEquals("tur00", aulaTeste.getCodigoTurma());
    }

    @Test
    public void deveRetornarDataAula(){

        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), aulaTeste.getData());
    }

    @Test
    public void deveRetornarHorarioAula(){

        Assertions.assertEquals("SEX 08:00-10:00", aulaTeste.getHorario());
    }

    @Test
    public void deveRetornarPresencasAula(){

        Assertions.assertNotNull(aulaTeste.getPresencas());

    }

    @Test
    public void deveAlterarCodigoAula(){
        aulaTeste.setId("aul00");

        Assertions.assertEquals("aul00", aulaTeste.getId());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionEmAlterarCodigoAula(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaTeste.setId(null));

    }

    @Test
    public void deveAlterarCodigoTurma(){
        aulaTeste.setCodigoTurma("tur01");

        Assertions.assertEquals("tur01", aulaTeste.getCodigoTurma());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionEmAlterarCodigoTurma(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaTeste.setCodigoTurma(null));

    }

    @Test
    public void deveAlterarData(){
        aulaTeste.setData("21/06/2026");

        Assertions.assertEquals("21/06/2026", aulaTeste.getData());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionEmAlterarData(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaTeste.setData(null));

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionEmAlterarDataFormatoInvalido(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaTeste.setData("asdada"));
    }

    @Test
    public void deveAlterarCodigoHorario(){
        aulaTeste.setHorario("TER 07:00-09:00");

        Assertions.assertEquals("TER 07:00-09:00", aulaTeste.getHorario());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionEmAlterarHorario(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaTeste.setHorario(null));

    }
}
