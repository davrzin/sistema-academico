package br.com.classroompb.model.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import br.com.classroompb.model.entities.Usuario.Administrador;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.entities.Usuario.Coordenador;
import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.services.PermissaoService;

class PermissaoServiceTest {

    @Test
    void devePermitirAcessoParaAluno() {
        Aluno aluno = new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO);
        assertDoesNotThrow(() -> PermissaoService.validarPermissao(aluno, TipoUsuario.ALUNO));
    }

    @Test
    void devePermitirAcessoParaProfessor() {
        Professor professor = new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR);
        assertDoesNotThrow(() -> PermissaoService.validarPermissao(professor, TipoUsuario.PROFESSOR));
    }

    @Test
    void devePermitirAcessoParaCoordenador() {
        Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR);
        assertDoesNotThrow(() -> PermissaoService.validarPermissao(coordenador, TipoUsuario.COORDENADOR));
    }

    @Test
    void devePermitirAcessoParaAdministrador() {
        Administrador administrador = new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR);
        assertDoesNotThrow(() -> PermissaoService.validarPermissao(administrador, TipoUsuario.ADMINISTRADOR));
    }

    @Test
    void deveNegarAcessoDeAlunoParaProfessor() {
        Aluno aluno = new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO);
        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissao(aluno, TipoUsuario.PROFESSOR));
    }

    @Test
    void deveNegarAcessoDeProfessorParaAdministrador() {
        Professor professor = new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR);
        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissao(professor, TipoUsuario.ADMINISTRADOR));
    }

    @Test
    void deveNegarAcessoDeAlunoParaCoordenador() {
        Aluno aluno = new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO);
        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissao(aluno, TipoUsuario.COORDENADOR));
    }

    @Test
    void deveNegarAcessoDeProfessorParaCoordenador() {
        Professor professor = new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR);
        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissao(professor, TipoUsuario.COORDENADOR));
    }

    @Test
    void devePermitirAdministradorBuscarAlunoProfessorECoordenadorPorMatricula() {
        Administrador administrador = new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR);
        Aluno aluno = new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO);
        Professor professor = new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR);
        Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR);

        assertDoesNotThrow(() -> PermissaoService.validarPermissaoBuscaPorMatricula(administrador, aluno));
        assertDoesNotThrow(() -> PermissaoService.validarPermissaoBuscaPorMatricula(administrador, professor));
        assertDoesNotThrow(() -> PermissaoService.validarPermissaoBuscaPorMatricula(administrador, coordenador));
    }

    @Test
    void deveNegarAdministradorBuscarOutroAdministradorPorMatricula() {
        Administrador administradorLogado = new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR);
        Administrador administradorEncontrado = new Administrador("Bruna", "bruna@email.com", "999", "senha123", TipoUsuario.ADMINISTRADOR);

        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissaoBuscaPorMatricula(administradorLogado, administradorEncontrado));
    }

    @Test
    void devePermitirCoordenadorBuscarAlunoEProfessorPorMatricula() {
        Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR);
        Aluno aluno = new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO);
        Professor professor = new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR);

        assertDoesNotThrow(() -> PermissaoService.validarPermissaoBuscaPorMatricula(coordenador, aluno));
        assertDoesNotThrow(() -> PermissaoService.validarPermissaoBuscaPorMatricula(coordenador, professor));
    }

    @Test
    void deveNegarCoordenadorBuscarAdministradorOuOutroCoordenadorPorMatricula() {
        Coordenador coordenadorLogado = new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR);
        Administrador administrador = new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR);
        Coordenador coordenadorEncontrado = new Coordenador("Bruna", "bruna@email.com", "999", "senha123", TipoUsuario.COORDENADOR);

        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissaoBuscaPorMatricula(coordenadorLogado, administrador));
        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissaoBuscaPorMatricula(coordenadorLogado, coordenadorEncontrado));
    }

    @Test
    void devePermitirProfessorBuscarAlunoPorMatricula() {
        Professor professor = new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR);
        Aluno aluno = new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO);

        assertDoesNotThrow(() -> PermissaoService.validarPermissaoBuscaPorMatricula(professor, aluno));
    }

    @Test
    void deveNegarProfessorBuscarProfessorCoordenadorOuAdministradorPorMatricula() {
        Professor professorLogado = new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR);
        Professor professorEncontrado = new Professor("Pedro", "pedro@email.com", "789", "senha123", TipoUsuario.PROFESSOR);
        Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR);
        Administrador administrador = new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR);

        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissaoBuscaPorMatricula(professorLogado, professorEncontrado));
        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissaoBuscaPorMatricula(professorLogado, coordenador));
        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissaoBuscaPorMatricula(professorLogado, administrador));
    }

    @Test
    void deveNegarAlunoBuscarUsuarioPorMatricula() {
        Aluno alunoLogado = new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO);
        Aluno alunoEncontrado = new Aluno("Pedro", "pedro@email.com", "789", "senha123", TipoUsuario.ALUNO);

        assertThrows(RuntimeException.class, () -> PermissaoService.validarPermissaoBuscaPorMatricula(alunoLogado, alunoEncontrado));
    }
}
