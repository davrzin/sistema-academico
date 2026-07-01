package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.GestaoAcademica.Boletim;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoletimRepository {
    private static final String DIRETORIO_BOLETINS = "boletins";

    private ObjectMapper objectMapper;
    private final String diretorioBoletins;

    public BoletimRepository(ObjectMapper objectMapper){
        this(objectMapper, DIRETORIO_BOLETINS);
    }

    public BoletimRepository(ObjectMapper objectMapper, String diretorioBoletins){
        this.objectMapper = objectMapper;
        this.diretorioBoletins = diretorioBoletins;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getDiretorioBoletins() {
        return diretorioBoletins;
    }

    public void salvarBoletim(Boletim boletim) {
        if(boletim == null){
            throw new EntradaInvalidaException("Boletim não pode ser nulo.");
        }

        List<Boletim> boletins = listarBoletins();
        boletins.add(boletim);
        salvarListaBoletins(boletins);

    }

    private void salvarListaBoletins(List<Boletim> boletins){
        String caminhoArquivo = getCaminhoArquivo();

        try {
            objectMapper.writeValue(new File(caminhoArquivo), boletins);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar boletins.", e);
        }
    }

    public void atualizarBoletins(Boletim boletimAtualizado){
        if(boletimAtualizado == null){
            throw new EntradaInvalidaException("Boletim não pode ser null.");
        }

        List<Boletim> boletins = listarBoletins();

        for(int i = 0; i < boletins.size(); i++){
            Boletim boletim = boletins.get(i);

            if(boletim.getIdBoletim().equalsIgnoreCase(boletimAtualizado.getIdBoletim())){
                boletins.set(i, boletimAtualizado);
                salvarListaBoletins(boletins);
            }
        }
    }

    public List<Boletim> listarBoletins(){
        File arquivo = new File(getCaminhoArquivo());

        if(!arquivo.exists()){
            return new ArrayList<>();
        }

        try{
            return lerBoletins(arquivo, Boletim.class);

        }catch(IOException e){
            throw new PersistenciaException("Erro ao ler boletins", e);
        }
    }

    public Boletim buscarBoletimPorCodigo(String codigo){
        validarCodigo(codigo);
        List<Boletim> boletins = this.listarBoletins();

        for(Boletim boletim : boletins){
            if(boletim.getIdBoletim().equalsIgnoreCase(codigo)){
                return boletim;
            }
        }

        return null;
    }

    public List<Boletim> buscarBoletinsPorTurma(String codigoTurma){
        validarCodigo(codigoTurma);
        List<Boletim> boletins = listarBoletins();

        List<Boletim> boletinsMesmaTurma = new ArrayList<>();

        for(Boletim boletim : boletins){
            if(boletim.getCodigoTurma().equalsIgnoreCase(codigoTurma)){
                boletinsMesmaTurma.add(boletim);
            }
        }

        return boletinsMesmaTurma;
    }

    public List<Boletim> buscarBoletinsPorAluno(String matriculaAluno){
        validarCodigo(matriculaAluno);
        List<Boletim> boletins = listarBoletins();

        List<Boletim> boletinsPorAluno = new ArrayList<>();

        for(Boletim boletin : boletins){
            if(boletin.getMatriculaAluno().equalsIgnoreCase(matriculaAluno)){
                boletinsPorAluno.add(boletin);
            }
        }

        return boletinsPorAluno;
    }

    private void validarCodigo(String codigo){
        if(codigo == null || codigo.isBlank()){
            throw new EntradaInvalidaException("Código não pode ser null.");
        }
    }


    private  <T extends Boletim> List<Boletim> lerBoletins(File arquivo, Class<T> tipo) throws IOException{

        return new ArrayList<>(
                objectMapper.readValue(
                        arquivo,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)
                )
        );
    }


    private String getCaminhoArquivo(){
        File diretorio = new File(diretorioBoletins);

        if(!diretorio.exists()){
            diretorio.mkdirs();
        }

        return new File(diretorio, "boletins.json").getPath();
    }
}
