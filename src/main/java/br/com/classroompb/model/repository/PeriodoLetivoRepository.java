package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.exception.PeriodoLetivoExistenteException;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PeriodoLetivoRepository {

    private static final String DIRETORIO_PERIODO_LETIVO = "periodos";

    private ObjectMapper objectMapper;
    private final String diretorioPeriodos;

    public PeriodoLetivoRepository(ObjectMapper objectMapper) {
        this(objectMapper, DIRETORIO_PERIODO_LETIVO);
    }

    public PeriodoLetivoRepository(ObjectMapper objectMapper, String diretorioPeriodo){
        this.objectMapper = objectMapper;
        this.diretorioPeriodos = diretorioPeriodo;
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

    }

    public String getDiretorioPeriodoLetivo(){
        return this.diretorioPeriodos;
    }

    public int salvarPeriodoLetivo(PeriodoLetivo periodoLetivo) {

        if (periodoLetivo == null) {
            throw new IllegalArgumentException("Período letivo não pode ser nulo");
        }

        String caminhoArquivo = this.getCaminhoArquivo();

        List<PeriodoLetivo> periodos = this.listarPeriodos();
        periodos.add(periodoLetivo);


        try {
            this.objectMapper.writeValue(new File(caminhoArquivo), periodos);

            return 1;
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao adicionar período letivo", e);
        }

    }

    public List<PeriodoLetivo> listarPeriodos() {
        File diretorio = new File(this.getCaminhoArquivo());

        if (!diretorio.exists() || !diretorio.isDirectory()) {
            return new ArrayList<>();
        }

        File[] arquivos = diretorio.listFiles();

        List<PeriodoLetivo> periodos = new ArrayList<>();

        if (arquivos == null) {
            return periodos;
        }

        try {
            for (File arquivo : arquivos) {

                periodos.addAll(this.lerPeriodos(arquivo, PeriodoLetivo.class));

            }

            return periodos;
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler usuários.", e);
        }

    }

    public PeriodoLetivo buscarPeriodoLetivo(String periodo, String dataInicio, String dataFim){

        List<PeriodoLetivo> periodos = this.listarPeriodos();

        for(PeriodoLetivo periodoLetivo : periodos){
            if(periodoLetivo.getPeriodo() == periodo || periodoLetivo.getDataInicio() == dataInicio || periodoLetivo.getDataFim() == dataFim){
                return periodoLetivo;
            }
        }

        throw new PeriodoLetivoExistenteException();
    }

    private String getCaminhoArquivo() {
        File diretorio = new File(diretorioPeriodos);

        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        return new File(diretorio,"periodos_letivos.json").getPath();
    }


    private <T extends PeriodoLetivo> List<PeriodoLetivo> lerPeriodos(File arquivo, Class<T> tipo) throws IOException {
        return new ArrayList<>(objectMapper.readValue(arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
    }


}
