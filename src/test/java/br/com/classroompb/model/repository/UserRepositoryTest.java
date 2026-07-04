package br.com.classroompb.model.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UserRepositoryTest {

  @TempDir Path tempDir;

  @AfterEach
  void tearDown() {
    File diretorio = tempDir.resolve("usuarios").toFile();
    File[] arquivos = diretorio.listFiles();

    if (arquivos != null) {
      for (File arquivo : arquivos) {
        arquivo.delete();
      }
    }

    if (diretorio.exists() && diretorio.isDirectory()) {
      diretorio.delete();
    }
  }

  private UserRepository criarRepository() {
    return new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
  }

  @Test
  void deveSalvarAlunoEmArquivoProprio() {
    UserRepository repository = criarRepository();

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    repository.salvarUsuario(aluno);

    File arquivo = arquivoDe(TipoUsuario.ALUNO);

    assertTrue(arquivo.exists());
    assertEquals(1, repository.listar(TipoUsuario.ALUNO).size());
    assertEquals("Maria", repository.listar(TipoUsuario.ALUNO).get(0).getNome());
    assertEquals("al00", repository.listar(TipoUsuario.ALUNO).get(0).getMatricula());
  }

  @Test
  void deveSalvarProfessorEmArquivoProprio() {
    final UserRepository repository = criarRepository();

    Professor professor = new Professor("João", "joao@email.com", "senha123");
    professor.setMatricula("pr00");

    repository.salvarUsuario(professor);

    File arquivo = arquivoDe(TipoUsuario.PROFESSOR);

    assertTrue(arquivo.exists());
    assertEquals(1, repository.listar(TipoUsuario.PROFESSOR).size());
    assertEquals("João", repository.listar(TipoUsuario.PROFESSOR).get(0).getNome());
    assertEquals("pr00", repository.listar(TipoUsuario.PROFESSOR).get(0).getMatricula());
  }

  @Test
  void deveSalvarCoordenadorEmArquivoProprio() {
    UserRepository repository = criarRepository();

    Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "senha123");
    coordenador.setMatricula("co00");

    repository.salvarUsuario(coordenador);

    File arquivo = arquivoDe(TipoUsuario.COORDENADOR);

    assertTrue(arquivo.exists());
    assertEquals(1, repository.listar(TipoUsuario.COORDENADOR).size());
    assertEquals("Ana", repository.listar(TipoUsuario.COORDENADOR).get(0).getNome());
    assertEquals("co00", repository.listar(TipoUsuario.COORDENADOR).get(0).getMatricula());
  }

  @Test
  void deveSalvarAdministradorEmArquivoProprio() {
    UserRepository repository = criarRepository();

    Administrador administrador = new Administrador("Carlos", "carlos@email.com", "senha123");
    administrador.setMatricula("ad00");

    repository.salvarUsuario(administrador);

    File arquivo = arquivoDe(TipoUsuario.ADMINISTRADOR);

    assertTrue(arquivo.exists());
    assertEquals(1, repository.listar(TipoUsuario.ADMINISTRADOR).size());
    assertEquals("Carlos", repository.listar(TipoUsuario.ADMINISTRADOR).get(0).getNome());
    assertEquals("ad00", repository.listar(TipoUsuario.ADMINISTRADOR).get(0).getMatricula());
  }

  @Test
  void deveLancarIllegalArgumentExceptionAoSalvarUsuarioNull() {
    UserRepository repository = criarRepository();

    assertThrows(IllegalArgumentException.class, () -> repository.salvarUsuario(null));
  }

  @Test
  void deveEncontrarUsuario() {
    UserRepository repository = criarRepository();

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    Professor professor = new Professor("João", "joao@email.com", "senha123");
    professor.setMatricula("pr00");

    repository.salvarUsuario(aluno);
    repository.salvarUsuario(professor);

    assertNotNull(repository.encontrarUsuario("maria@email.com", "senha123"));
    assertNotNull(repository.encontrarUsuario("joao@email.com", "senha123"));
  }

  @Test
  void deveLancarExceptionDeUsuarioNaoEncontrado() {
    UserRepository repository = criarRepository();

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    repository.salvarUsuario(aluno);

    assertThrows(
        UsuarioNaoEncontradoException.class,
        () -> repository.encontrarUsuario("joaquim@gmail.com", "s123"));
  }

  @Test
  void deveListarUsuariosDeTodosOsTipos() {
    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    Professor professor = new Professor("João", "joao@email.com", "senha123");
    professor.setMatricula("pr00");

    Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "senha123");
    coordenador.setMatricula("co00");

    Administrador administrador = new Administrador("Carlos", "carlos@email.com", "senha123");
    administrador.setMatricula("ad00");

    UserRepository repository = criarRepository();

    repository.salvarUsuario(aluno);
    repository.salvarUsuario(professor);
    repository.salvarUsuario(coordenador);
    repository.salvarUsuario(administrador);

    List<Usuario> usuarios = repository.listar();

    assertEquals(4, usuarios.size());
    assertTrue(
        usuarios.stream()
            .anyMatch(
                u -> u.getMatricula().equals("al00") && u.getTipoUsuario() == TipoUsuario.ALUNO));
    assertTrue(
        usuarios.stream()
            .anyMatch(
                u ->
                    u.getMatricula().equals("pr00")
                        && u.getTipoUsuario() == TipoUsuario.PROFESSOR));
    assertTrue(
        usuarios.stream()
            .anyMatch(
                u ->
                    u.getMatricula().equals("co00")
                        && u.getTipoUsuario() == TipoUsuario.COORDENADOR));
    assertTrue(
        usuarios.stream()
            .anyMatch(
                u ->
                    u.getMatricula().equals("ad00")
                        && u.getTipoUsuario() == TipoUsuario.ADMINISTRADOR));
  }

  @Test
  public void deveListarUsuariosDeTipoEspecifico() {

    UserRepository repository = criarRepository();

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    Aluno aluno2 = new Aluno("Joao", "Joao@email.com", "senha123");
    aluno2.setMatricula("a101");

    repository.salvarUsuario(aluno);
    repository.salvarUsuario(aluno2);

    List<Usuario> usuarios = repository.listar(aluno.getTipoUsuario());

    Assertions.assertEquals(2, usuarios.size());
    Assertions.assertEquals(aluno.getNome(), usuarios.getFirst().getNome());
    Assertions.assertEquals(aluno2.getNome(), usuarios.getLast().getNome());
  }

  @Test
  public void deveRemoverUsuarioCorretamente() {
    UserRepository repository = criarRepository();

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    repository.salvarUsuario(aluno);

    Usuario usuarioRemovido =
        repository.removerPorMatricula(aluno.getMatricula(), aluno.getTipoUsuario());

    Assertions.assertNotNull(usuarioRemovido);
  }

  @Test
  public void deveLancarUsuarioNaoEncontradoException() {
    UserRepository repository = criarRepository();

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    repository.salvarUsuario(aluno);

    Assertions.assertThrows(
        UsuarioNaoEncontradoException.class,
        () -> repository.removerPorMatricula("al01", aluno.getTipoUsuario()));
  }

  @Test
  void deveBuscarUsuarioPorMatriculaEmTodosOsTipos() {
    UserRepository repository = criarRepository();

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    Professor professor = new Professor("João", "joao@email.com", "senha123");
    professor.setMatricula("pr00");

    repository.salvarUsuario(aluno);
    repository.salvarUsuario(professor);

    assertEquals("Maria", repository.buscarPorMatricula("al00").getNome());
    assertEquals("João", repository.buscarPorMatricula("pr00").getNome());
  }

  @Test
  void deveBuscarUsuarioPorMatriculaFiltrandoPeloTipo() {
    UserRepository repository = criarRepository();

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    Professor professor = new Professor("João", "joao@email.com", "senha123");
    professor.setMatricula("pr00");

    repository.salvarUsuario(aluno);
    repository.salvarUsuario(professor);

    Usuario alunoEncontrado = repository.buscarPorMatricula("al00", TipoUsuario.ALUNO);
    Usuario professorEncontrado = repository.buscarPorMatricula("pr00", TipoUsuario.PROFESSOR);

    assertEquals(TipoUsuario.ALUNO, alunoEncontrado.getTipoUsuario());
    assertEquals(TipoUsuario.PROFESSOR, professorEncontrado.getTipoUsuario());
  }

  @Test
  void deveLancarExceptionAoBuscarMatriculaInexistente() {
    UserRepository repository = criarRepository();

    Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
    aluno.setMatricula("al00");

    repository.salvarUsuario(aluno);

    assertThrows(UsuarioNaoEncontradoException.class, () -> repository.buscarPorMatricula("al99"));
  }

  @Test
  void deveLancarIllegalArgumentExceptionAoBuscarMatriculaVazia() {
    UserRepository repository = criarRepository();

    assertThrows(IllegalArgumentException.class, () -> repository.buscarPorMatricula(" "));
  }

  private File arquivoDe(TipoUsuario tipoUsuario) {
    return tempDir.resolve("usuarios").resolve(tipoUsuario.name().toLowerCase() + ".json").toFile();
  }
}
