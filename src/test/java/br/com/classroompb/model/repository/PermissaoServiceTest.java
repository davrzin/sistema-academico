package br.com.classroompb.model.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import br.com.classroompb.model.entities.Administrador;
import br.com.classroompb.model.entities.Aluno;
import br.com.classroompb.model.entities.Coordenador;
import br.com.classroompb.model.entities.Professor;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.services.PermissaoService;

class PermissaoServiceTest {

    @Test
    void devePermitirAcessoParaAluno() {
        Aluno aluno = new Aluno("Maria","maria@email.com",
        "123",
        "senha123",TipoUsuario.ALUNO);
        assertDoesNotThrow(() ->PermissaoService.validarPermissao(aluno,TipoUsuario.ALUNO));
    }

    @Test
    void devePermitirAcessoParaProfessor() {
        Professor professor = new Professor("João","joao@email.com",
                "234",
                "senha123",
                TipoUsuario.PROFESSOR
        );
        assertDoesNotThrow(() -> PermissaoService.validarPermissao(professor,
                TipoUsuario.PROFESSOR));
    }

    @Test
    void devePermitirAcessoParaCoordenador() {
        Coordenador coordenador = new Coordenador("Ana","ana@email.com",
                "345",
                "senha123",
                TipoUsuario.COORDENADOR
        );
        assertDoesNotThrow(() -> PermissaoService.validarPermissao(coordenador, 
                TipoUsuario.COORDENADOR));
    }

    @Test
    void devePermitirAcessoParaAdministrador() {
        Administrador administrador = new Administrador("Carlos","carlos@email.com",
                "456",
                "senha123",
                TipoUsuario.ADMINISTRADOR
        );
        assertDoesNotThrow(() -> PermissaoService.validarPermissao(administrador, 
                TipoUsuario.ADMINISTRADOR));
    }

    @Test
    void deveNegarAcessoDeAlunoParaProfessor() {
        Aluno aluno = new Aluno("Maria","maria@email.com",
                "123",
                "senha123",
                TipoUsuario.ALUNO
        );
        assertThrows(RuntimeException.class, () -> 
                PermissaoService.validarPermissao(aluno,
                TipoUsuario.PROFESSOR));
    }

    @Test
    void deveNegarAcessoDeProfessorParaAdministrador() {
        Professor professor = new Professor("João","joao@email.com",
                "234",
                "senha123",
                TipoUsuario.PROFESSOR
        );
        assertThrows(RuntimeException.class, () ->
                PermissaoService.validarPermissao(
                professor,
                TipoUsuario.ADMINISTRADOR));
    }

    @Test
    void deveNegarAcessoDeAlunoParaCoordenador() {
        Aluno aluno = new Aluno("Maria","maria@email.com",
                "123",
                "senha123",
                TipoUsuario.ALUNO
        );
        assertThrows(RuntimeException.class, () ->
                PermissaoService.validarPermissao(
                aluno,
                TipoUsuario.COORDENADOR));
    }

    @Test
    void deveNegarAcessoDeProfessorParaCoordenador() {
        Professor professor = new Professor("João","joao@email.com",
                "234",
                "senha123",
                TipoUsuario.PROFESSOR
        );
        assertThrows(RuntimeException.class, () ->
                PermissaoService.validarPermissao(
                professor,
                TipoUsuario.COORDENADOR));
    }
}