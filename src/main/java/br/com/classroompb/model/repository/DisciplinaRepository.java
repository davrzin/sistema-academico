package br.com.classroompb.model.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.exception.PersistenciaException;

public class DisciplinaRepository {

    private static final String DIRETORIO_DISCIPLINAS = "disciplinas";

    private ObjectMapper objectMapper;
    private final String diretorioDisciplinas;

    public DisciplinaRepository(ObjectMapper objectMapper) {
        this(objectMapper, DIRETORIO_DISCIPLINAS);
    }

    public DisciplinaRepository(ObjectMapper objectMapper, String diretorioDisciplinas) {
        this.objectMapper = objectMapper;
        this.diretorioDisciplinas = diretorioDisciplinas;
    }

    public ObjectMapper getObjectMapper(){
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public String getDiretorioDisciplinas(){
        return diretorioDisciplinas;
    }

    public void salvarDisciplina(Disciplina disciplina){

        if(disciplina == null){throw new IllegalArgumentException("Disciplina não pode ser nula.");}

        String caminhoArquivo = this.getCaminhoArquivo();
        List<Disciplina> disciplinas = this.listarDisciplinas();
        disciplinas.add(disciplina);

        try {
            this.objectMapper.writeValue(new File(caminhoArquivo), disciplinas);
        } catch(IOException e){
            throw new PersistenciaException("Erro ao salvar disciplina.", e);
        }
    }

    public List<Disciplina> listarDisciplinas() {
        File arquivo = new File(this.getCaminhoArquivo());

        if(!arquivo.exists()){
            return new ArrayList<>();
        }

        try{
            return this.lerDisciplinas(arquivo, Disciplina.class);
        } catch(IOException e){
            throw new PersistenciaException("Erro ao ler disciplinas.", e);
        }
    }

    public Disciplina buscarPorCodigo(String codigo) {
        List<Disciplina> disciplinas = this.listarDisciplinas();

        for(Disciplina disciplina : disciplinas){
            if(disciplina.getCodigo().equalsIgnoreCase(codigo)){
                return disciplina;
            }
        }

        return null;
    }

    public Disciplina buscarPorNome(String nome) {

        List<Disciplina> disciplinas = this.listarDisciplinas();

        for(Disciplina disciplina : disciplinas){
            if(disciplina.getNome().equalsIgnoreCase(nome)){
                return disciplina;
            }
        }

        return null;
    }

    public List<Disciplina> buscarPorCurso(String codigoCurso) {
        List<Disciplina> disciplinas = this.listarDisciplinas();
        List<Disciplina> disciplinasCurso = new ArrayList<>();

        for (Disciplina disciplina : disciplinas){
            if (disciplina.getCodigoCurso().equalsIgnoreCase(codigoCurso)){
                disciplinasCurso.add(disciplina);
            }
        }

        return disciplinasCurso;
    }

    private String getCaminhoArquivo() {
        File diretorio =new File(diretorioDisciplinas);

        if (!diretorio.exists()){
            diretorio.mkdirs();
        }

        return new File(diretorio, "disciplinas.json").getPath();
    }

    private <T extends Disciplina>
    List<Disciplina> lerDisciplinas(File arquivo, Class<T> tipo) throws IOException {
        return new ArrayList<>(
                objectMapper.readValue(arquivo,objectMapper.getTypeFactory().constructCollectionType(List.class,tipo))
        );
    }
}