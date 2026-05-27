package br.com.classroompb.model.entities.GestaoAcademica;

public class PeriodoLetivo {

    private String periodo;

    private String dataInicio;

    private String dataFim;

    private boolean periodoAtivo;

    public PeriodoLetivo(){}
    public PeriodoLetivo(String periodo, String dataInicio, String dataFim){
        this.periodo = periodo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.periodoAtivo = false;
    }

    public String getPeriodo(){
        return this.periodo;
    }

    public void setPeriodo(String periodo){
        this.periodo = periodo;
    }

    public String getDataInicio(){
        return this.dataInicio;
    }

    public void setDataInicio(String dataInicio){
        this.dataInicio = dataInicio;
    }

    public String getDataFim(){
        return this.dataFim;
    }

    public void setDataFim(String dataFim){
        this.dataFim = dataFim;
    }

    public boolean getPeriodoAtivo(){
        return this.periodoAtivo;
    }

    public void setPeriodoAtivo(boolean periodoAtivo){
        this.periodoAtivo = periodoAtivo;
    }
}
