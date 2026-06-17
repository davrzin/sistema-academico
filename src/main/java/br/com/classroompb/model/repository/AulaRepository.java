package br.com.classroompb.model.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.Aula;
import br.com.classroompb.model.exception.PersistenciaException;

public class AulaRepository {
     private static final String DIRETORIO_AULAS = "aulas";

    private ObjectMapper objectMapper;
    private final String diretorioAulas;

    public AulaRepository(ObjectMapper objectMapper) {
        this(objectMapper, DIRETORIO_AULAS);
    }

    public AulaRepository(ObjectMapper objectMapper, String diretorioAulas) {
        this.objectMapper = objectMapper;
        this.diretorioAulas = diretorioAulas;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getDiretorioAulas() {
        return diretorioAulas;
    }

    public void salvarAula(Aula aula) {
        if (aula == null) {
            throw new IllegalArgumentException("Aula não pode ser nula.");
        }

        List<Aula> aulas = listarAulas();
        aulas.add(aula);
        salvarListaAulas(aulas);
    }

    public boolean atualizarAula(Aula aulaAtualizada) {
        if (aulaAtualizada == null) {
            throw new IllegalArgumentException("Aula não pode ser nula.");
        }

        List<Aula> aulas = listarAulas();

        for (int i = 0; i < aulas.size(); i++) {
            Aula aula = aulas.get(i);

            if (aula.getId() != null && aula.getId().equalsIgnoreCase(aulaAtualizada.getId())) {
                aulas.set(i, aulaAtualizada);
                salvarListaAulas(aulas);
                return true;
            }
        }

        return false;
    }

    private void salvarListaAulas(List<Aula> aulas) {
        String caminhoArquivo = getCaminhoArquivo();

        try {
            objectMapper.writeValue(new File(caminhoArquivo), aulas);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar aulas.", e);
        }
    }

    public List<Aula> listarAulas() {
        File arquivo = new File(getCaminhoArquivo());

        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try {
            return lerAulas(arquivo, Aula.class);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler aulas.", e);
        }
    }

    public Aula buscarAulaPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        for (Aula aula : listarAulas()) {
            if (aula.getId() != null && aula.getId().trim().equalsIgnoreCase(id.trim())) {
                return aula;
            }
        }

        return null;
    }

    private String getCaminhoArquivo() {
        File diretorio = new File(diretorioAulas);

        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        return new File(diretorio, "aulas.json").getPath();
    }

    private <T extends Aula> List<Aula> lerAulas(File arquivo, Class<T> tipo) throws IOException {
        return new ArrayList<>(
                objectMapper.readValue(
                        arquivo,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)
                )
        );
    }
}
