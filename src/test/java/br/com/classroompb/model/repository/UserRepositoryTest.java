package br.com.classroompb.model.repository;

import java.io.File;
import java.util.List;

import java.nio.file.Path;

import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.Administrador;
import br.com.classroompb.model.entities.Aluno;
import br.com.classroompb.model.entities.Coordenador;
import br.com.classroompb.model.entities.Professor;
import br.com.classroompb.model.entities.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @TempDir
    Path tempDir;

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

    @Test
    void deveSalvarAlunoEmArquivoProprio() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
        Aluno aluno = new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO);

        repository.salvarUsuario(aluno);

        File arquivo = arquivoDe(TipoUsuario.ALUNO);
        assertTrue(arquivo.exists());
        assertEquals(1, repository.listar(TipoUsuario.ALUNO).size());
        assertEquals("Maria", repository.listar(TipoUsuario.ALUNO).get(0).getNome());
        assertEquals("123", repository.listar(TipoUsuario.ALUNO).get(0).getMatricula());
    }

    @Test
    void deveSalvarProfessorEmArquivoProprio() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
        Professor professor = new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR);

        repository.salvarUsuario(professor);

        File arquivo = arquivoDe(TipoUsuario.PROFESSOR);
        assertTrue(arquivo.exists());
        assertEquals(1, repository.listar(TipoUsuario.PROFESSOR).size());
        assertEquals("João", repository.listar(TipoUsuario.PROFESSOR).get(0).getNome());
        assertEquals("234", repository.listar(TipoUsuario.PROFESSOR).get(0).getMatricula());
    }

    @Test
    void deveSalvarCoordenadorEmArquivoProprio() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
        Coordenador coordenador = new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR);

        repository.salvarUsuario(coordenador);

        File arquivo = arquivoDe(TipoUsuario.COORDENADOR);
        assertTrue(arquivo.exists());
        assertEquals(1, repository.listar(TipoUsuario.COORDENADOR).size());
        assertEquals("Ana", repository.listar(TipoUsuario.COORDENADOR).get(0).getNome());
        assertEquals("345", repository.listar(TipoUsuario.COORDENADOR).get(0).getMatricula());
    }

    @Test
    void deveSalvarAdministradorEmArquivoProprio() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());
        Administrador administrador = new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR);

        repository.salvarUsuario(administrador);

        File arquivo = arquivoDe(TipoUsuario.ADMINISTRADOR);
        assertTrue(arquivo.exists());
        assertEquals(1, repository.listar(TipoUsuario.ADMINISTRADOR).size());
        assertEquals("Carlos", repository.listar(TipoUsuario.ADMINISTRADOR).get(0).getNome());
        assertEquals("456", repository.listar(TipoUsuario.ADMINISTRADOR).get(0).getMatricula());
    }

    @Test
    void deveEncontrarUsuario() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());

        repository.salvarUsuario(new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO));
        repository.salvarUsuario(new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR));
        repository.salvarUsuario(new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR));
        repository.salvarUsuario(new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR));

        assertNotNull(repository.encontrarUsuario("maria@email.com", "senha123"));
        assertNotNull(repository.encontrarUsuario("joao@email.com", "senha123"));
        assertNotNull(repository.encontrarUsuario("ana@email.com", "senha123"));
        assertNotNull(repository.encontrarUsuario("carlos@email.com", "senha123"));
    }

    @Test
    void deveLancarExceptionDeUsuarioNaoEncontrado() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());

        repository.salvarUsuario(new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO));
        repository.salvarUsuario(new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR));
        repository.salvarUsuario(new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR));
        repository.salvarUsuario(new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR));

        assertThrows(UsuarioNaoEncontradoException.class, () -> repository.encontrarUsuario("joaquim@gmail.com", "s123"));
    }

    @Test
    void deveListarUsuariosDeTodosOsTipos() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());

        repository.salvarUsuario(new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO));
        repository.salvarUsuario(new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR));
        repository.salvarUsuario(new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR));
        repository.salvarUsuario(new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR));

        List<Usuario> usuarios = repository.listar();

        assertEquals(4, usuarios.size());
        assertTrue(usuarios.stream().anyMatch(u -> u.getMatricula().equals("123") && u.getTipoUsuario() == TipoUsuario.ALUNO));
        assertTrue(usuarios.stream().anyMatch(u -> u.getMatricula().equals("234") && u.getTipoUsuario() == TipoUsuario.PROFESSOR));
        assertTrue(usuarios.stream().anyMatch(u -> u.getMatricula().equals("345") && u.getTipoUsuario() == TipoUsuario.COORDENADOR));
        assertTrue(usuarios.stream().anyMatch(u -> u.getMatricula().equals("456") && u.getTipoUsuario() == TipoUsuario.ADMINISTRADOR));
    }

    @Test
    void deveBuscarUsuarioPorMatriculaEmTodosOsTipos() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());

        repository.salvarUsuario(new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO));
        repository.salvarUsuario(new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR));
        repository.salvarUsuario(new Coordenador("Ana", "ana@email.com", "345", "senha123", TipoUsuario.COORDENADOR));
        repository.salvarUsuario(new Administrador("Carlos", "carlos@email.com", "456", "senha123", TipoUsuario.ADMINISTRADOR));

        assertEquals("Maria", repository.buscarPorMatricula("123").getNome());
        assertEquals("João", repository.buscarPorMatricula("234").getNome());
        assertEquals("Ana", repository.buscarPorMatricula("345").getNome());
        assertEquals("Carlos", repository.buscarPorMatricula("456").getNome());
    }

    @Test
    void deveBuscarUsuarioPorMatriculaFiltrandoPeloTipo() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());

        repository.salvarUsuario(new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO));
        repository.salvarUsuario(new Professor("João", "joao@email.com", "234", "senha123", TipoUsuario.PROFESSOR));

        Usuario alunoEncontrado = repository.buscarPorMatricula("123", TipoUsuario.ALUNO);
        Usuario professorEncontrado = repository.buscarPorMatricula("234", TipoUsuario.PROFESSOR);

        assertEquals(TipoUsuario.ALUNO, alunoEncontrado.getTipoUsuario());
        assertEquals(TipoUsuario.PROFESSOR, professorEncontrado.getTipoUsuario());
    }

    @Test
    void deveLancarExceptionAoBuscarMatriculaInexistente() {
        UserRepository repository = new UserRepository(new ObjectMapper(), tempDir.resolve("usuarios").toString());

        repository.salvarUsuario(new Aluno("Maria", "maria@email.com", "123", "senha123", TipoUsuario.ALUNO));

        assertThrows(UsuarioNaoEncontradoException.class, () -> repository.buscarPorMatricula("999"));
    }

    private File arquivoDe(TipoUsuario tipoUsuario) {
        return tempDir.resolve("usuarios").resolve(tipoUsuario.name().toLowerCase() + ".json").toFile();
    }
}
