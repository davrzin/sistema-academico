package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.GestaoAcademica.Boletim;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoletimTest {

    private Boletim boletimTeste;

    @BeforeEach
    public void criarBoletim(){
        boletimTeste = new Boletim("al00", "tur00");
    }

    @Test
    public void deveCriarBoletimComConstrutorVazio(){
        Boletim boletim = new Boletim();

        Assertions.assertNotNull((boletim));
    }

    @Test
    public void deveCriarBoletimComConstrutorPadrao(){
        Boletim boletim = new Boletim("al00", "tur00");

        Assertions.assertNotNull(boletim);
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarBoletimComEntradaInvalida(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Boletim(null, null));
    }

    @Test
    public void deveRetonarMatriculaDeAlunoCorretamente(){
        String matriculaEsperada = "al00";

        Assertions.assertEquals(matriculaEsperada, boletimTeste.getMatriculaAluno());
    }

    @Test
    public void deveRetornarCodigoTurmaCorretamente(){
        String codigoEsperado = "tur00";

        Assertions.assertEquals(codigoEsperado, boletimTeste.getCodigoTurma());
    }

    @Test
    public void deveAlterarMatriculaDoAlunoCorretamente(){
        boletimTeste.setMatriculaAluno("al01");

        String matriculaEsperada = "al01";

        Assertions.assertEquals(matriculaEsperada, boletimTeste.getMatriculaAluno());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoAlterarMatriculaAlunoComNull(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimTeste.setMatriculaAluno(null));
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoAlterarMatriculaAlunoComEntradaVazia(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimTeste.setMatriculaAluno(""));
    }

    @Test
    public void deveAlterarCodigoTurmaCorretamente(){
        boletimTeste.setCodigoTurma("tur01");

        String codigoEsperado = "tur01";

        Assertions.assertEquals(codigoEsperado, boletimTeste.getCodigoTurma());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoAlterarCodigoTurmaComNull(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimTeste.setCodigoTurma(null));
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoAlterarCodigoTurmaComEntradaVazia(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimTeste.setCodigoTurma(null));
    }

    @Test
    public void deveAlterarPrimeiraNotaCorretamente(){
        boletimTeste.setPrimeiraNota(10);

        float notaEsperada = 10;

        Assertions.assertEquals(notaEsperada, boletimTeste.getPrimeiraNota());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptioAoAlterarPrimeiraNotaComNotaInvalida(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimTeste.setPrimeiraNota(20));
    }

    @Test
    public void deveAlterarSegundaNotaCorretamente(){
        boletimTeste.setSegundaNota(10);

        float notaEsperada = 10;

        Assertions.assertEquals(notaEsperada, boletimTeste.getSegundaNota());
    }

    @Test
    public void develancarEntradaInvalidaExceptionAoAlterarSegundaNotaComNotaInvalida(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimTeste.setSegundaNota(42));
    }

    @Test
    public void deveAlterarFrequenciaCorretamente(){
        boletimTeste.setFrequencia(100.0);

        double frequenciaEsperada = 100.0;

        Assertions.assertEquals(frequenciaEsperada, boletimTeste.getFrequencia());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoAlterarFrequenciaComDadoInvalido(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> boletimTeste.setFrequencia(412));
    }

    @Test
    public void deveCalcularFrequenciaMáximaSemNenhumaAula(){
        double frequenciaEsperada = 100.0;

        Assertions.assertEquals(frequenciaEsperada, boletimTeste.calcularFrequencia(0,0));
    }

    @Test
    public void deveCalcularFrequenciaCorretamente(){
        double frequenciaEsperada = 90.0;

        Assertions.assertEquals(frequenciaEsperada, boletimTeste.calcularFrequencia(3, 30));
    }

}
