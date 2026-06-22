package br.com.classroompb.model.entities.GestaoAcademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class Aula {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String id;
    private String codigoTurma;
    private String data;
    private String horario;
    private Map<String, Boolean> presencas;

    public Aula() {
    }

    public Aula(String id, String codigoTurma, String data, String horario, Map<String, Boolean> presencas) {
        setId(id);
        setCodigoTurma(codigoTurma);
        setData(data);
        setHorario(horario);
        setPresencas(presencas);
    }

    public Aula(String id, String codigoTurma, String data, String horario) {
        setId(id);
        setCodigoTurma(codigoTurma);
        setData(data);
        setHorario(horario);
        setPresencas(new HashMap<>());
    }

    public String getId(){
        return this.id; 
    } 

    public void setId(String id){
        validarCodigo(id);
        this.id = id;
    }

    public String getCodigoTurma(){
        return this.codigoTurma; 
    } 

    public void setCodigoTurma(String codigoTurma){
        validarCodigoTurma(codigoTurma);
        this.codigoTurma = codigoTurma;
    }

    public String getData(){
        return this.data; 
    } 

    public void setData(String data){
        validarData(data);
        this.data = data;
    }

    public String getHorario(){
        return this.horario; 
    } 

    public void setHorario(String horario){
        validarHorario(horario);
        this.horario = horario;
    }

    public Map<String, Boolean> getPresencas(){
        return this.presencas; 
    } 

    public void setPresencas(Map<String, Boolean> presencas){
        validarPresencas(presencas);
        this.presencas = presencas;
    }

    private void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new EntradaInvalidaException("Código da aula não pode ser vazio.");
        }
    }

    private void validarCodigoTurma(String codigoTurma) {
        if (codigoTurma == null || codigoTurma.isBlank()) {
            throw new EntradaInvalidaException("Código da turma não pode ser vazio.");
        }
    }

    private void validarData(String data){
        if(data == null || data.isBlank()){
            throw new EntradaInvalidaException("Data não pode ser vazia");
        }

        try {
            LocalDate.parse(data, FORMATADOR_DATA);
        } catch (DateTimeParseException e) {
            throw new EntradaInvalidaException("Formato de data inválido. Use o formato dd/MM/yyyy.");
        }
    }

    private void validarHorario(String horario){
        if(horario == null || horario.isBlank()){
            throw new EntradaInvalidaException("Horario não pode ser vazio");
        }
    }

    private void validarPresencas(Map<String, Boolean> presencas){

        if(presencas == null){
            throw new EntradaInvalidaException("Atributo não pode ser nulo");
        }
    }
}
