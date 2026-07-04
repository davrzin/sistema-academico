package br.com.classroompb.model.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.enums.TipoUsuario;
import org.junit.jupiter.api.Test;

class PermissaoServiceTest {

  @Test
  void devePermitirAcessoParaAluno() {
    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    assertDoesNotThrow(() -> PermissaoService.validarPermissao(aluno, TipoUsuario.ALUNO));
  }

  @Test
  void devePermitirAcessoParaProfessor() {
    Professor professor = new Professor("João", "joao@email.com", "senha123");
    professor.setMatricula("pr00");

    assertDoesNotThrow(() -> PermissaoService.validarPermissao(professor, TipoUsuario.PROFESSOR));
  }

  @Test
  void devePermitirAcessoParaCoordenador() {
    Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "senha123");
    coordenador.setMatricula("co00");

    assertDoesNotThrow(
        () -> PermissaoService.validarPermissao(coordenador, TipoUsuario.COORDENADOR));
  }

  @Test
  void devePermitirAcessoParaAdministrador() {
    Administrador administrador = new Administrador("Carlos", "carlos@email.com", "senha123");
    administrador.setMatricula("ad00");

    assertDoesNotThrow(
        () -> PermissaoService.validarPermissao(administrador, TipoUsuario.ADMINISTRADOR));
  }

  @Test
  void deveNegarPermissaoQuandoUsuarioForNull() {
    assertThrows(
        RuntimeException.class, () -> PermissaoService.validarPermissao(null, TipoUsuario.ALUNO));
  }

  @Test
  void deveNegarAcessoQuandoTipoForDiferente() {
    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    assertThrows(
        RuntimeException.class,
        () -> PermissaoService.validarPermissao(aluno, TipoUsuario.PROFESSOR));
  }

  @Test
  void devePermitirAdministradorBuscarAlunoProfessorCoordenadorPorMatricula() {
    Administrador administrador = new Administrador("Carlos", "carlos@email.com", "senha123");
    administrador.setMatricula("ad00");

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    Professor professor = new Professor("João", "joao@email.com", "senha123");
    professor.setMatricula("pr00");

    Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "senha123");
    coordenador.setMatricula("co00");

    assertDoesNotThrow(
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(administrador, aluno));
    assertDoesNotThrow(
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(administrador, professor));
    assertDoesNotThrow(
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(administrador, coordenador));
  }

  @Test
  void deveNegarAdministradorBuscarOutroAdministradorPorMatricula() {
    Administrador administradorLogado = new Administrador("Carlos", "carlos@email.com", "senha123");
    administradorLogado.setMatricula("ad00");

    Administrador administradorEncontrado =
        new Administrador("Bruna", "bruna@email.com", "senha123");
    administradorEncontrado.setMatricula("ad01");

    assertThrows(
        RuntimeException.class,
        () ->
            PermissaoService.validarPermissaoBuscaPorMatricula(
                administradorLogado, administradorEncontrado));
  }

  @Test
  void devePermitirCoordenadorBuscarAlunoProfessorPorMatricula() {
    Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "senha123");
    coordenador.setMatricula("co00");

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    Professor professor = new Professor("João", "joao@email.com", "senha123");
    professor.setMatricula("pr00");

    assertDoesNotThrow(
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(coordenador, aluno));
    assertDoesNotThrow(
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(coordenador, professor));
  }

  @Test
  void deveNegarCoordenadorBuscarAdministradorOuOutroCoordenadorPorMatricula() {
    Coordenador coordenadorLogado = new Coordenador("Ana", "ana@email.com", "senha123");
    coordenadorLogado.setMatricula("co00");

    Administrador administrador = new Administrador("Carlos", "carlos@email.com", "senha123");
    administrador.setMatricula("ad00");

    Coordenador coordenadorEncontrado = new Coordenador("Bruna", "bruna@email.com", "senha123");
    coordenadorEncontrado.setMatricula("co01");

    assertThrows(
        RuntimeException.class,
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(coordenadorLogado, administrador));
    assertThrows(
        RuntimeException.class,
        () ->
            PermissaoService.validarPermissaoBuscaPorMatricula(
                coordenadorLogado, coordenadorEncontrado));
  }

  @Test
  void devePermitirProfessorBuscarAlunoPorMatricula() {
    Professor professor = new Professor("João", "joao@email.com", "senha123");
    professor.setMatricula("pr00");

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    assertDoesNotThrow(() -> PermissaoService.validarPermissaoBuscaPorMatricula(professor, aluno));
  }

  @Test
  void deveNegarProfessorBuscarProfessorCoordenadorOuAdministradorPorMatricula() {
    Professor professorLogado = new Professor("João", "joao@email.com", "senha123");
    professorLogado.setMatricula("pr00");

    Professor professorEncontrado = new Professor("Pedro", "pedro@email.com", "senha123");
    professorEncontrado.setMatricula("pr01");

    Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "senha123");
    coordenador.setMatricula("co00");

    Administrador administrador = new Administrador("Carlos", "carlos@email.com", "senha123");
    administrador.setMatricula("ad00");

    assertThrows(
        RuntimeException.class,
        () ->
            PermissaoService.validarPermissaoBuscaPorMatricula(
                professorLogado, professorEncontrado));
    assertThrows(
        RuntimeException.class,
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(professorLogado, coordenador));
    assertThrows(
        RuntimeException.class,
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(professorLogado, administrador));
  }

  @Test
  void deveNegarAlunoBuscarUsuarioPorMatricula() {
    Aluno alunoLogado = new Aluno("Maria", "maria@email.com", "senha123");
    alunoLogado.setMatricula("al00");

    Aluno alunoEncontrado = new Aluno("Pedro", "pedro@email.com", "senha123");
    alunoEncontrado.setMatricula("al01");

    assertThrows(
        RuntimeException.class,
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(alunoLogado, alunoEncontrado));
  }

  @Test
  void deveNegarBuscaQuandoUsuarioLogadoOuEncontradoForNull() {
    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    assertThrows(
        RuntimeException.class,
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(null, aluno));
    assertThrows(
        RuntimeException.class,
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(aluno, null));
  }

  @Test
  void deveNegarBuscaDoProprioUsuario() {
    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    assertThrows(
        RuntimeException.class,
        () -> PermissaoService.validarPermissaoBuscaPorMatricula(aluno, aluno));
  }

  @Test
  void deveValidarRegrasDoPodeBuscarPorMatricula() {
    assertTrue(
        PermissaoService.podeBuscarPorMatricula(TipoUsuario.ADMINISTRADOR, TipoUsuario.ALUNO));
    assertTrue(
        PermissaoService.podeBuscarPorMatricula(TipoUsuario.COORDENADOR, TipoUsuario.PROFESSOR));
    assertTrue(PermissaoService.podeBuscarPorMatricula(TipoUsuario.PROFESSOR, TipoUsuario.ALUNO));

    assertFalse(PermissaoService.podeBuscarPorMatricula(TipoUsuario.ALUNO, TipoUsuario.PROFESSOR));
    assertFalse(PermissaoService.podeBuscarPorMatricula(null, TipoUsuario.ALUNO));
    assertFalse(PermissaoService.podeBuscarPorMatricula(TipoUsuario.ALUNO, null));
  }
}
