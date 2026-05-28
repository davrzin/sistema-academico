package br.com.classroompb.model.entities.GestaoAcademica;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import br.com.classroompb.model.exception.EntradaInvalidaException;

public class PeriodoLetivo {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String periodo;
    private String dataInicio;
    private String dataFim;
    private boolean periodoAtivo;

    public PeriodoLetivo() {
    }

    public PeriodoLetivo(String periodo, String dataInicio, String dataFim) {
        setPeriodo(periodo);
        setDataInicio(dataInicio);
        setDataFim(dataFim);
        validarIntervaloDatas(dataInicio, dataFim);
        this.periodoAtivo = false;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        validarPeriodo(periodo);
        this.periodo = periodo;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        validarData(dataInicio, "Data de início inválida.");
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        validarData(dataFim, "Data de fim inválida.");
        this.dataFim = dataFim;
    }

    public boolean getPeriodoAtivo() {
        return periodoAtivo;
    }

    public void setPeriodoAtivo(boolean periodoAtivo) {
        this.periodoAtivo = periodoAtivo;
    }

    public void validarDadosBasicos() {
        validarPeriodo(periodo);
        validarData(dataInicio, "Data de início inválida.");
        validarData(dataFim, "Data de fim inválida.");
        validarIntervaloDatas(dataInicio, dataFim);
    }

    private void validarPeriodo(String periodo) {
        if (periodo == null || periodo.isBlank()) {
            throw new EntradaInvalidaException("Período letivo não pode ser vazio.");
        }

        if (!periodo.matches("\\d{4}\\.\\d+")) {
            throw new EntradaInvalidaException("Formato de período inválido. Use o formato 2024.1, por exemplo.");
        }
    }

    private void validarData(String data, String mensagemErro) {
        if (data == null || data.isBlank()) {
            throw new EntradaInvalidaException(mensagemErro);
        }

        try {
            LocalDate.parse(data, FORMATADOR_DATA);
        } catch (DateTimeParseException e) {
            throw new EntradaInvalidaException("Formato de data inválido. Use o formato dd/MM/yyyy.");
        }
    }

    private void validarIntervaloDatas(String dataInicio, String dataFim) {
        LocalDate inicio = LocalDate.parse(dataInicio, FORMATADOR_DATA);
        LocalDate fim = LocalDate.parse(dataFim, FORMATADOR_DATA);

        if (!fim.isAfter(inicio)) {
            throw new EntradaInvalidaException("A data final deve ser posterior à data inicial.");
        }
    }
}
