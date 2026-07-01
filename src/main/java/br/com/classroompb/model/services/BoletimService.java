package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.GestaoAcademica.Boletim;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.BoletimRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;

public class BoletimService {

    private static final Path DIRETORIO_BOLETINS = Path.of(System.getProperty("user.dir"), "boletins");
    private final BoletimRepository repository;

    public BoletimService(){
        this.repository = new BoletimRepository(new ObjectMapper(), DIRETORIO_BOLETINS.toString());
    }

    public BoletimService(BoletimRepository repository){
        this.repository = repository;
    }

    public void criarBoletim(Boletim boletim){
        validarBoletim(boletim);

        String codigo = gerarCodigoBoletim();
        boletim.setIdBoletim(codigo);

        repository.salvarBoletim(boletim);
    }

    public List<Boletim> buscarBoletinsPorAluno(String matriculaAluno){
        if(matriculaAluno == null || matriculaAluno.isBlank()){
            throw new EntradaInvalidaException("Matrícula do aluno não pode ser null.");
        }

        return repository.buscarBoletinsPorAluno(matriculaAluno);
    }

    private void validarBoletim(Boletim boletim){
        if(boletim == null){
            throw new EntradaInvalidaException("Boletim não pode ser null.");
        }
    }

    private String gerarCodigoBoletim(){
        int contador = repository.listarBoletins().size();
        String codigo;

        do{
            codigo = "cur" + String.format("%02d", contador);
            contador++;
        } while(repository.buscarBoletimPorCodigo(codigo) != null);

        return codigo;
    }
}
