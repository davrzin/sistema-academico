package br.com.classroompb.model.entities;

import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testes das entidades de usuario.
 */
public class UsuarioTest {

  @Test
  public void deveInstaciarAlunoCorretamente() {

    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "al00", "123", TipoUsuario.ALUNO);

    Assertions.assertEquals(Aluno.class, aluno.getClass());
  }

  @Test
  public void deveInstaciarAlunoCorretamenteConstrutorSemTipoDeUsuario() {

    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "al00", "123");

    Assertions.assertEquals(Aluno.class, aluno.getClass());
  }

  @Test
  public void deveInstaciarProfessorCorretamente() {

    Professor professor =
        new Professor("Arthur", "arthur@email.com", "pr00", "123", TipoUsuario.PROFESSOR);

    Assertions.assertEquals(Professor.class, professor.getClass());
  }

  @Test
  public void deveInstaciarProfessorCorretamenteConstrutorSemTipoDeUsuario() {

    Professor professor = new Professor("Arthur", "arthur@email.com", "pr00", "123");

    Assertions.assertEquals(Professor.class, professor.getClass());
  }

  @Test
  public void deveInstaciarCoordenadorCorretamente() {

    Coordenador coordenador =
        new Coordenador("Arthur", "arthur@email.com", "co00", "123", TipoUsuario.COORDENADOR);

    Assertions.assertEquals(Coordenador.class, coordenador.getClass());
  }

  @Test
  public void deveInstaciarCoordenadorCorretamenteConstrutorSemTipoDeUsuario() {

    Coordenador coordenador = new Coordenador("Arthur", "arthur@email.com", "co00", "123");

    Assertions.assertEquals(Coordenador.class, coordenador.getClass());
  }

  @Test
  public void deveInstaciarAdministradorCorretamente() {

    Administrador adm =
        new Administrador("Arthur", "arthur@email.com", "adm00", "123", TipoUsuario.ADMINISTRADOR);

    Assertions.assertEquals(Administrador.class, adm.getClass());
  }

  @Test
  public void deveInstaciarAdministradorCorretamenteConstrutorSemTipoDeUsuario() {

    Administrador adm = new Administrador("Arthur", "arthur@email.com", "adm00", "123");

    Assertions.assertEquals(Administrador.class, adm.getClass());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCriarAlunoComDadosInvalidos() {

    Assertions.assertThrows(EntradaInvalidaException.class, () -> new Aluno("", "", ""));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCriarProfessorComDadosInvalidos() {

    Assertions.assertThrows(EntradaInvalidaException.class, () -> new Professor("", "", ""));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCriarCoordenadorComDadosInvalidos() {

    Assertions.assertThrows(EntradaInvalidaException.class, () -> new Coordenador("", "", ""));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoCriarAdministradorComDadosInvalidos() {

    Assertions.assertThrows(EntradaInvalidaException.class, () -> new Administrador("", "", ""));
  }

  @Test
  public void deveRetornarNomeDeUsuarioCorretamente() {
    Usuario aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertEquals("Arthur", aluno.getNome());
  }

  @Test
  public void deveRetornarEmailDeUsuarioCorretamente() {
    Usuario aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertEquals("arthur@email.com", aluno.getEmail());
  }

  @Test
  public void deveRetornarSenhaDeUsuarioCorretamente() {
    Usuario aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertEquals("123", aluno.getSenha());
  }

  @Test
  public void deveRetornarTipoDeUsuarioCorretamente() {
    Usuario aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertEquals(TipoUsuario.ALUNO, aluno.getTipoUsuario());
  }

  @Test
  public void deveRetornarMatriculaDeUsuarioCorretamente() {
    Usuario aluno = new Aluno("Arthur", "arthur@email.com", "al00", "123");

    Assertions.assertEquals("al00", aluno.getMatricula());
  }

  @Test
  public void deveRetornarDisciplinasConcluidasPorAlunoCadastrado() {

    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    List<String> disciplinasConcluidas = aluno.getDisciplinasConcluidas();

    Assertions.assertEquals(0, disciplinasConcluidas.size());
  }

  @Test
  public void deveAdicionarDisciplinaConcluidaEmAlunoCorretamente() {

    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Disciplina disciplina =
        new Disciplina("Engenharia de Software II", 60, 6, 30, "cur00", new ArrayList<>());

    aluno.getDisciplinasConcluidas().add(disciplina.getCodigo());

    List<String> disciplinasConcluidas = aluno.getDisciplinasConcluidas();

    Assertions.assertEquals(1, disciplinasConcluidas.size());
    Assertions.assertEquals(disciplina.getCodigo(), disciplinasConcluidas.getFirst());
  }

  @Test
  public void deveRetornarTurmasMatriculadasPorAlunoCadastrado() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    List<String> turmasMatriculadas = aluno.getTurmasMatriculadas();

    Assertions.assertEquals(0, turmasMatriculadas.size());
  }

  @Test
  public void deveAtualizarNomeDeUsuarioCorretamenteViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    aluno.setNome("Arthur Barbosa");

    Assertions.assertEquals("Arthur Barbosa", aluno.getNome());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirNomeNuloViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setNome(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirNomeEmBrancoViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setNome("   "));
  }

  @Test
  public void deveAtualizarEmailDeUsuarioCorretamenteViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    aluno.setEmail("novo@email.com");

    Assertions.assertEquals("novo@email.com", aluno.getEmail());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirEmailNuloViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setEmail(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirEmailEmBrancoViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setEmail("   "));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirEmailSemArrobaViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setEmail("arthur.com"));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirEmailSemPontoViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setEmail("arthur@email"));
  }

  @Test
  public void deveAtualizarMatriculaDeUsuarioCorretamenteViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "al00", "123");

    aluno.setMatricula("al01");

    Assertions.assertEquals("al01", aluno.getMatricula());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirMatriculaNulaViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "al00", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setMatricula(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirMatriculaEmBrancoViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "al00", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setMatricula("   "));
  }

  @Test
  public void deveAtualizarSenhaDeUsuarioCorretamenteViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    aluno.setSenha("novaSenha");

    Assertions.assertEquals("novaSenha", aluno.getSenha());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirSenhaNulaViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setSenha(null));
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirSenhaEmBrancoViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setSenha("   "));
  }

  @Test
  public void deveAtualizarTipoDeUsuarioCorretamenteViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    aluno.setTipoUsuario(TipoUsuario.ADMINISTRADOR);

    Assertions.assertEquals(TipoUsuario.ADMINISTRADOR, aluno.getTipoUsuario());
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoDefinirTipoDeUsuarioNuloViaSetter() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, () -> aluno.setTipoUsuario(null));
  }

  @Test
  public void deveDeixarMatriculaNulaQuandoUsuarioForCriadoSemMatricula() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertNull(aluno.getMatricula());
  }

  @Test
  public void naoDeveLancarExcecaoAoValidarDadosBasicosDeUsuarioCompleto() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertDoesNotThrow(aluno::validarDadosBasicos);
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoValidarDadosBasicosDeUsuarioVazio() {
    Aluno aluno = new Aluno();

    Assertions.assertThrows(EntradaInvalidaException.class, aluno::validarDadosBasicos);
  }

  @Test
  public void naoDeveLancarExcecaoAoValidarDadosComMatriculaDeUsuarioCompleto() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "al00", "123");

    Assertions.assertDoesNotThrow(aluno::validarDadosComMatricula);
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoValidarDadosComMatriculaSemMatricula() {
    Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

    Assertions.assertThrows(EntradaInvalidaException.class, aluno::validarDadosComMatricula);
  }

  @Test
  public void deveLancarEntradaInvalidaExceptionAoValidarDadosComMatriculaDeUsuarioVazio() {
    Aluno aluno = new Aluno();

    Assertions.assertThrows(EntradaInvalidaException.class, aluno::validarDadosComMatricula);
  }
}
