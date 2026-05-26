package br.com.classroompb.model.entities.GestaoAcademica;

public class PeriodoLetivo {

    private String periodo;

    private String dataInicio;

    private String dataFim;

    public PeriodoLetivo(String periodo, String dataInicio, String dataFim){
        this.periodo = periodo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
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
}
