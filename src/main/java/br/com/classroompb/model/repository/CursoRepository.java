package br.com.classroompb.model.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.Curso;
import br.com.classroompb.model.exception.PersistenciaException;

public class CursoRepository {

    private static final String DIRETORIO_CURSOS = "cursos";

    private ObjectMapper objectMapper;
    private final String diretorioCursos;

    public CursoRepository(ObjectMapper objectMapper) {
        this(objectMapper, DIRETORIO_CURSOS);
    }

    public CursoRepository(ObjectMapper objectMapper, String diretorioCursos) {
        this.objectMapper = objectMapper;
        this.diretorioCursos = diretorioCursos;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getDiretorioCursos() {
        return diretorioCursos;
    }

    public boolean salvarCurso(Curso curso) {
        if (curso == null) {
            throw new IllegalArgumentException("Curso não pode ser nulo.");
        }

        String caminhoArquivo = this.getCaminhoArquivo();

        List<Curso> cursos = this.listarCursos();

        cursos.add(curso);

        try {
            this.objectMapper.writeValue(new File(caminhoArquivo),cursos);

            return true;
        } catch (IOException e){
            throw new PersistenciaException("Erro ao salvar curso.", e);
        }
    }

    public List<Curso> listarCursos() {

        File arquivo = new File(getCaminhoArquivo());

        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try {
            return lerCursos(
                    arquivo,
                    Curso.class
            );
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler cursos.", e);
        }
    }

    public Curso buscarPorCodigo(String codigo) {
        List<Curso> cursos = this.listarCursos();
        for (Curso curso : cursos) {
            if (curso.getCodigo().equalsIgnoreCase(codigo)) {
                return curso;
            }
        }
        return null;
    }

    public Curso buscarPorNome(String nome) {
        List<Curso> cursos = this.listarCursos();
        for (Curso curso : cursos) {
            if (curso.getNome().equalsIgnoreCase(nome)) {
                return curso;
            }
        }
        return null;
    }

    private String getCaminhoArquivo() {
        File diretorio = new File(diretorioCursos);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
        return new File(diretorio,"cursos.json").getPath();
    }

    private <T extends Curso> List<Curso> lerCursos(File arquivo,Class<T> tipo) throws IOException {
        return new ArrayList<>(objectMapper.readValue(arquivo,objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
    }
}