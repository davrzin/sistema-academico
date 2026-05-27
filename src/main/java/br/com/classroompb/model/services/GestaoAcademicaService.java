package br.com.classroompb.model.services;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;

public class GestaoAcademicaService {

    private static final Path DIRETORIO_PERIODOS = Path.of(System.getProperty("user.dir"), "periodos");
    private final PeriodoLetivoRepository repository;

    public GestaoAcademicaService(){
        this.repository = new PeriodoLetivoRepository(new ObjectMapper(), DIRETORIO_PERIODOS.toString());
    }

    public GestaoAcademicaService(PeriodoLetivoRepository periodoLetivoRepository){
        this.repository = periodoLetivoRepository;
    }

    public PeriodoLetivo cadastrarPeriodoLetivo(String periodo, String dataInicio, String dataFim){

        if(this.validarPeriodoLetivo(periodo) && this.validarDataInicioFimPeriodo(dataInicio, dataFim) && this.validarExistenciaPeriodoLetivo(periodo, dataInicio, dataFim)){

            PeriodoLetivo novoPeriodo = new PeriodoLetivo(periodo, dataInicio, dataFim);

            repository.salvarPeriodoLetivo(novoPeriodo);

            return novoPeriodo;
        }
        return null;
    }

    public boolean validarExistenciaPeriodoLetivo(String periodo, String dataInicio, String dataFim){
        //
        PeriodoLetivo periodoLetivo = repository.buscarPeriodoLetivo(periodo, dataInicio, dataFim);

        if(periodoLetivo == null){
            return true;
        }

        return false;
    }

    public boolean validarPeriodoLetivo(String periodo){

        try{
            if(periodo.isBlank()){
                throw new EntradaInvalidaException("Entradas completamente em branco não são aceitas.");
            }
        }catch(NullPointerException e){
            throw new EntradaInvalidaException("Entradas null não são aceitas!");
        }

        if(!periodo.matches("[\\d\\.]+")){
            throw new EntradaInvalidaException("Formato de período inválido");
        }
        return true;

    }

    public boolean validarDataInicioFimPeriodo(String dataInicio, String dataFim){
        try{

            String formatoData = "dd/MM/yyyy";

            DateTimeFormatter formater = DateTimeFormatter.ofPattern(formatoData);

            LocalDate.parse(dataInicio, formater);
            LocalDate.parse(dataFim, formater);

            return true;

        }catch(DateTimeParseException e){
            throw new EntradaInvalidaException("Formato de data inválida.");
        }
    }

}
