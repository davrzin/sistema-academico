package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.Usuario.*;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UsuarioTest {

    @Test
    public void deveInstaciarAlunoCorretamente(){

        Aluno aluno = new Aluno("Arthur", "arthur@email.com","al00", "123", TipoUsuario.ALUNO);

        Assertions.assertEquals(Aluno.class, aluno.getClass());
    }

    @Test
    public void deveInstaciarAlunoCorretamenteConstrutorSemTipoDeUsuario(){

        Aluno aluno = new Aluno("Arthur", "arthur@email.com","al00", "123");

        Assertions.assertEquals(Aluno.class, aluno.getClass());
    }

    @Test
    public void deveInstaciarProfessorCorretamente(){

        Professor professor = new Professor("Arthur", "arthur@email.com", "pr00","123", TipoUsuario.PROFESSOR);

        Assertions.assertEquals(Professor.class, professor.getClass());
    }

    @Test
    public void deveInstaciarProfessorCorretamenteConstrutorSemTipoDeUsuario(){

        Professor professor = new Professor("Arthur", "arthur@email.com", "pr00","123");

        Assertions.assertEquals(Professor.class, professor.getClass());
    }

    @Test
    public void deveInstaciarCoordenadorCorretamente(){

        Coordenador coordenador = new Coordenador("Arthur", "arthur@email.com","co00", "123", TipoUsuario.COORDENADOR);

        Assertions.assertEquals(Coordenador.class, coordenador.getClass());
    }

    @Test
    public void deveInstaciarCoordenadorCorretamenteConstrutorSemTipoDeUsuario(){

        Coordenador coordenador = new Coordenador("Arthur", "arthur@email.com","co00", "123");

        Assertions.assertEquals(Coordenador.class, coordenador.getClass());
    }

    @Test
    public void deveInstaciarAdministradorCorretamente(){

        Administrador adm = new Administrador("Arthur", "arthur@email.com", "adm00","123", TipoUsuario.ADMINISTRADOR);

        Assertions.assertEquals(Administrador.class, adm.getClass());
    }

    @Test
    public void deveInstaciarAdministradorCorretamenteConstrutorSemTipoDeUsuario(){

        Administrador adm = new Administrador("Arthur", "arthur@email.com", "adm00","123");

        Assertions.assertEquals(Administrador.class, adm.getClass());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarAlunoComDadosInvalidos(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Aluno("", "", ""));

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarProfessorComDadosInvalidos(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Professor("", "", ""));

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarCoordenadorComDadosInvalidos(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Coordenador("", "", ""));

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarAdministradorComDadosInvalidos(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Administrador("", "", ""));

    }


}
