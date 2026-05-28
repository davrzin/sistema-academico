package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
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

    public PeriodoLetivoRepository(ObjectMapper objectMapper, String diretorioPeriodo) {
        this.objectMapper = objectMapper;
        this.diretorioPeriodos = diretorioPeriodo;
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getDiretorioPeriodoLetivo() {
        return this.diretorioPeriodos;
    }

    public boolean salvarPeriodoLetivo(PeriodoLetivo periodoLetivo) {
        if (periodoLetivo == null) {
            throw new IllegalArgumentException("Período letivo não pode ser nulo.");
        }

        String caminhoArquivo = this.getCaminhoArquivo();

        List<PeriodoLetivo> periodos = this.listarPeriodos();
        periodos.add(periodoLetivo);

        try {
            this.objectMapper.writeValue(new File(caminhoArquivo), periodos);
            return true;
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao adicionar período letivo.", e);
        }
    }

    public boolean updatePeriodoLetivo(PeriodoLetivo periodoAtualizado, int indicePeriodoEscolhido) {
        if (periodoAtualizado == null) {
            throw new IllegalArgumentException("Período letivo não pode ser nulo.");
        }

        List<PeriodoLetivo> listaPeriodos = this.listarPeriodos();
        File arquivo = new File(this.getCaminhoArquivo());

        PeriodoLetivo periodoAntigo = listaPeriodos.get(indicePeriodoEscolhido);

        try {
            this.objectMapper.updateValue(periodoAntigo, periodoAtualizado);
            this.objectMapper.writeValue(arquivo, listaPeriodos);

            return true;
        } catch (IOException e) {
            throw new PersistenciaException("Falha ao atualizar período letivo.", e);
        }
    }

    public List<PeriodoLetivo> listarPeriodos() {
        File arquivo = new File(this.getCaminhoArquivo());

        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try {
            return this.lerPeriodos(arquivo, PeriodoLetivo.class);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler períodos.", e);
        }
    }

    public boolean existePeriodoComDados(String periodo, String dataInicio, String dataFim) {
        List<PeriodoLetivo> periodos = this.listarPeriodos();

        for (PeriodoLetivo periodoLetivo : periodos) {
            if (
                    periodoLetivo.getPeriodo().equals(periodo)
                            || periodoLetivo.getDataInicio().equals(dataInicio)
                            || periodoLetivo.getDataFim().equals(dataFim)
            ) {
                return true;
            }
        }

        return false;
    }

    public PeriodoLetivo buscarPeriodoLetivo(String periodo, String dataInicio, String dataFim) {
        List<PeriodoLetivo> periodos = this.listarPeriodos();

        for (PeriodoLetivo periodoLetivo : periodos) {
            if (
                    periodoLetivo.getPeriodo().equals(periodo)
                            && periodoLetivo.getDataInicio().equals(dataInicio)
                            && periodoLetivo.getDataFim().equals(dataFim)
            ) {
                return periodoLetivo;
            }
        }

        return null;
    }

    public boolean existePeriodoLetivoAtivo() {
        List<PeriodoLetivo> listaPeriodos = this.listarPeriodos();

        for (PeriodoLetivo periodo : listaPeriodos) {
            if (periodo.getPeriodoAtivo()) {
                return true;
            }
        }

        return false;
    }

    private String getCaminhoArquivo() {
        File diretorio = new File(diretorioPeriodos);

        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        return new File(diretorio, "periodos_letivos.json").getPath();
    }

    private <T extends PeriodoLetivo> List<PeriodoLetivo> lerPeriodos(File arquivo, Class<T> tipo) throws IOException {
        return new ArrayList<>(
                objectMapper.readValue(
                        arquivo,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)
                )
        );
    }
}
