package br.com.classroompb.model.services;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.Usuario.Administrador;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.entities.Usuario.Coordenador;
import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.UserRepository;

public class UsuarioServiceTest {

    @TempDir
    Path tempDir;

    @AfterEach
    public void tearDown() {
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
        return new UserRepository(
                new ObjectMapper(),
                tempDir.resolve("usuarios").toString()
        );
    }

    private UsuarioService criarService(UserRepository repository) {
        return new UsuarioService(repository);
    }

    @Test
    public void deveCadastrarAlunoGerandoMatriculaAutomaticamente() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(aluno);

        Assertions.assertEquals("al00", aluno.getMatricula());
        Assertions.assertEquals(1, repository.listar(TipoUsuario.ALUNO).size());
    }

    @Test
    public void deveCadastrarProfessorGerandoMatriculaAutomaticamente() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Professor professor = new Professor("João", "joao@email.com", "senha123");

        service.cadastrarUsuario(professor);

        Assertions.assertEquals("pr00", professor.getMatricula());
        Assertions.assertEquals(1, repository.listar(TipoUsuario.PROFESSOR).size());
    }

    @Test
    public void deveCadastrarCoordenadorGerandoMatriculaAutomaticamente() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "senha123");

        service.cadastrarUsuario(coordenador);

        Assertions.assertEquals("co00", coordenador.getMatricula());
        Assertions.assertEquals(1, repository.listar(TipoUsuario.COORDENADOR).size());
    }

    @Test
    public void deveCadastrarAdministradorGerandoMatriculaAutomaticamente() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Administrador administrador = new Administrador("Carlos", "carlos@email.com", "senha123");

        service.cadastrarUsuario(administrador);

        Assertions.assertEquals("ad00", administrador.getMatricula());
        Assertions.assertEquals(1, repository.listar(TipoUsuario.ADMINISTRADOR).size());
    }

    @Test
    public void deveGerarMatriculasSequenciaisPorTipoUsuario() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Aluno primeiroAluno = new Aluno("Maria", "maria@email.com", "senha123");
        Aluno segundoAluno = new Aluno("Pedro", "pedro@email.com", "senha123");
        Professor professor = new Professor("João", "joao@email.com", "senha123");

        service.cadastrarUsuario(primeiroAluno);
        service.cadastrarUsuario(segundoAluno);
        service.cadastrarUsuario(professor);

        Assertions.assertEquals("al00", primeiroAluno.getMatricula());
        Assertions.assertEquals("al01", segundoAluno.getMatricula());
        Assertions.assertEquals("pr00", professor.getMatricula());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCadastrarUsuarioNull() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.cadastrarUsuario(null)
        );
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCadastrarEmailDuplicado() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        service.cadastrarUsuario(new Aluno("Maria", "maria@email.com", "senha123"));

        Professor professor = new Professor("João", "maria@email.com", "senha456");

        Assertions.assertThrows(
                EntradaInvalidaException.class,
                () -> service.cadastrarUsuario(professor)
        );
    }

    @Test
    public void deveFazerLoginComEmailESenhaCorretos() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
        service.cadastrarUsuario(aluno);

        Usuario usuarioLogado = service.fazerLoginUsuario("maria@email.com", "senha123");

        Assertions.assertNotNull(usuarioLogado);
        Assertions.assertEquals("Maria", usuarioLogado.getNome());
    }

    @Test
    public void deveRetornarNullQuandoLoginForInvalido() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        service.cadastrarUsuario(new Aluno("Maria", "maria@email.com", "senha123"));

        Usuario usuarioLogado = service.fazerLoginUsuario("maria@email.com", "senhaErrada");

        Assertions.assertNull(usuarioLogado);
    }

    @Test
    public void deveSerPossivelProfessorAtualizarDadosDeUsuarioCorretamente(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Professor professor = new Professor("Sabrina", "sabrina@email.com", "1234");
        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(professor);
        service.cadastrarUsuario(aluno);

        Usuario usuarioAtualizado = service.atualizarUsuario(professor, aluno.getMatricula(), "Joana", "joana@email.com", "321");

        Assertions.assertNotEquals(aluno.getNome(), usuarioAtualizado.getNome());
        Assertions.assertNotEquals(aluno.getEmail(), usuarioAtualizado.getEmail());
        Assertions.assertNotEquals(aluno.getSenha(), usuarioAtualizado.getSenha());

    }

    @Test
    public void deveSerPossivelCoordenadorAtualizarDadosDeUsuarioCorretamente(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Coordenador coordenador = new Coordenador("Sabrina", "sabrina@email.com", "1234");
        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(coordenador);
        service.cadastrarUsuario(aluno);

        Usuario usuarioAtualizado = service.atualizarUsuario(coordenador, aluno.getMatricula(), "Joana", "joana@email.com", "321");

        Assertions.assertNotEquals(aluno.getNome(), usuarioAtualizado.getNome());
        Assertions.assertNotEquals(aluno.getEmail(), usuarioAtualizado.getEmail());
        Assertions.assertNotEquals(aluno.getSenha(), usuarioAtualizado.getSenha());

    }

    @Test
    public void deveSerPossivelAdministradortualizarDadosDeUsuarioCorretamente(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Administrador adm = new Administrador("Sabrina", "sabrina@email.com", "1234");
        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(adm);
        service.cadastrarUsuario(aluno);

        Usuario usuarioAtualizado = service.atualizarUsuario(adm, aluno.getMatricula(), "Joana", "joana@email.com", "321");

        Assertions.assertNotEquals(aluno.getNome(), usuarioAtualizado.getNome());
        Assertions.assertNotEquals(aluno.getEmail(), usuarioAtualizado.getEmail());
        Assertions.assertNotEquals(aluno.getSenha(), usuarioAtualizado.getSenha());

    }

    @Test
    public void deveLancarRuntimeExceptioAoAlunoTentarEditarSeusDados(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(aluno);

        Assertions.assertThrows(RuntimeException.class, () -> service.atualizarUsuario(aluno, aluno.getMatricula(), "Joana", "joana@email.com", "321"));

    }

    @Test
    public void administradorDeveRemoverAlunoPorMatricula(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Administrador adm = new Administrador("Sabrina", "sabrina@email.com", "1234");
        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(adm);
        service.cadastrarUsuario(aluno);

        Usuario usuarioRemovido = service.removerUsuarioPorMatricula(adm, aluno.getMatricula());


        Assertions.assertNotNull(usuarioRemovido);
    }

    @Test
    public void coordenadorDeveRemoverAlunoPorMatricula(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Coordenador coordenador = new Coordenador("Sabrina", "sabrina@email.com", "1234");
        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(coordenador);
        service.cadastrarUsuario(aluno);

        Usuario usuarioRemovido = service.removerUsuarioPorMatricula(coordenador, aluno.getMatricula());


        Assertions.assertNotNull(usuarioRemovido);
    }

    @Test
    public void professorDeveRemoverAlunoPorMatricula(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Professor professor = new Professor("Sabrina", "sabrina@email.com", "1234");
        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(professor);
        service.cadastrarUsuario(aluno);

        Usuario usuarioRemovido = service.removerUsuarioPorMatricula(professor, aluno.getMatricula());


        Assertions.assertNotNull(usuarioRemovido);
    }

    @Test
    public void deveLancarRuntimeExceptionAoAlunoTentarRemoverPropriaConta(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(aluno);

        Assertions.assertThrows(RuntimeException.class, () -> service.removerUsuarioPorMatricula(aluno, aluno.getMatricula()));


    }

    @Test
    public void deveLancarUsuarioNaoEncontradoExceptionAoRemoverUsuarioInexistente(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Professor professor = new Professor("Sabrina", "sabrina@email.com", "1234");

        Assertions.assertThrows(UsuarioNaoEncontradoException.class, () -> service.removerUsuarioPorMatricula(professor, "2fwef234"));


    }

    @Test
    public void deveBuscarUsuarioPorMatriculaRespeitandoPermissao() {
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Administrador administrador = new Administrador("Carlos", "carlos@email.com", "senha123");
        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(administrador);
        service.cadastrarUsuario(aluno);

        Usuario usuarioEncontrado = service.buscarUsuarioPorMatricula(
                administrador,
                aluno.getMatricula()
        );

        Assertions.assertEquals("Maria", usuarioEncontrado.getNome());
    }

    @Test
    public void deveListarTodosOsUsuariosCadastrados(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Administrador administrador = new Administrador("Carlos", "carlos@email.com", "senha123");
        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");
        Aluno aluno2 = new Aluno("Joao", "joao@email.com", "senha123");

        service.cadastrarUsuario(administrador);
        service.cadastrarUsuario(aluno);
        service.cadastrarUsuario(aluno2);

        List<Usuario> usuariosCadastrados = service.listarUsuarios(administrador);

        Assertions.assertEquals(3, usuariosCadastrados.size());

        Assertions.assertTrue(usuariosCadastrados.stream().anyMatch(u -> u.getNome().equals("Carlos")));

        Assertions.assertTrue(usuariosCadastrados.stream().anyMatch(u -> u.getNome().equals("Maria")));

        Assertions.assertTrue(usuariosCadastrados.stream().anyMatch(u -> u.getNome().equals("Joao")));
    }

    @Test
    public void deveLancarRuntimeExceptionComPermissaoErrada(){
        UserRepository repository = criarRepository();
        UsuarioService service = criarService(repository);

        Aluno aluno = new Aluno("Maria", "maria@email.com", "senha123");

        service.cadastrarUsuario(aluno);

        Assertions.assertThrows(RuntimeException.class, () -> service.listarUsuarios(aluno));
    }

}
