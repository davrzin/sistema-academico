package br.com.classroompb.model.services;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.Curso;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.CursoRepository;

public class CursoService {

    private static final Path DIRETORIO_CURSOS = Path.of(System.getProperty("user.dir"), "cursos");
    private final CursoRepository repository;

    public CursoService() {
        this.repository = new CursoRepository(new ObjectMapper(), DIRETORIO_CURSOS.toString());
    }

    public CursoService(CursoRepository cursoRepository) {
        this.repository = cursoRepository;
    }

    public void cadastrarCurso(Curso curso) {
        validarCurso(curso);

        String codigo = gerarCodigoCurso();
        curso.setCodigo(codigo);

        validarExistenciaCurso(curso.getCodigo(), curso.getNome());

        repository.salvarCurso(curso);
    }

    private void validarCurso(Curso curso) {
        if (curso == null) {
            throw new EntradaInvalidaException("Curso não pode ser null.");
        }

        curso.validarDadosBasicos();
    }

    private void validarExistenciaCurso(String codigo, String nome) {
        Curso cursoCodigo = repository.buscarPorCodigo(codigo);
        Curso cursoNome = repository.buscarPorNome(nome);

        if (cursoCodigo != null) {
            throw new EntradaInvalidaException("Já existe um curso cadastrado com esse código.");
        }

        if (cursoNome != null) {
            throw new EntradaInvalidaException("Já existe um curso cadastrado com esse nome.");
        }
    }

    private String gerarCodigoCurso() {
        int contador = repository.listarCursos().size();
        String codigo;

        do {
            codigo = "cur" + String.format("%02d", contador);
            contador++;
        } while (repository.buscarPorCodigo(codigo) != null);

        return codigo;
    }

    public List<Curso> listarCursos() {
        return repository.listarCursos();
    }
}
