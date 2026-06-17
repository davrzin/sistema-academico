package br.com.classroompb.model.entities.GestaoAcademica;

import java.time.LocalDate;
import java.util.Map;

public class Aula {
    private String id;
    private String codigoTurma;
    private LocalDate data;
    private String horario;
    private Map<String, Boolean> presencas;

    public Aula() {
    }

    public Aula(String id, String codigoTurma, LocalDate data, String horario, Map<String, Boolean> presencas) {
        setId(id);
        setCodigoTurma(codigoTurma);
        setData(data);
        setHorario(horario);
        setPresencas(presencas);
    }

    public String getId(){
        return this.id; 
    } 

    public void setId(String id){
        this.id = id;
    }

    public String getCodigoTurma(){
        return this.codigoTurma; 
    } 

    public void setCodigoTurma(String codigoTurma){
        this.codigoTurma = codigoTurma;
    }

    public LocalDate getData(){
        return this.data; 
    } 

    public void setData(LocalDate data){
        this.data = data;
    }

    public String horario(){
        return this.horario; 
    } 

    public void setHorario(String horario){
        this.horario = horario;
    }

    public Map<String, Boolean> getPresencas(){
        return this.presencas; 
    } 

    public void setPresencas(Map<String, Boolean> presencas){
        this.presencas = presencas;
    }
}
