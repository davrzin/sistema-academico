package br.com.classroompb.model.services;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.Curso;
import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;

public class DisciplinaService {

    private static final Path DIRETORIO_DISCIPLINAS = Path.of(System.getProperty("user.dir"),"disciplinas");
    private final DisciplinaRepository repository;
    private final CursoRepository cursoRepository;

    public DisciplinaService() {
        this.repository = new DisciplinaRepository(new ObjectMapper(), DIRETORIO_DISCIPLINAS.toString());
        this.cursoRepository = new CursoRepository(new ObjectMapper());
    }

    public DisciplinaService(DisciplinaRepository repository, CursoRepository cursoRepository) {
        this.repository = repository;
        this.cursoRepository = cursoRepository;
    }

    public Disciplina cadastrarDisciplina(String nome, int cargaHoraria, int periodo, int creditos, String codigoCurso, 
        List<String> preRequisitos) {

        String codigo = gerarCodigoDisciplina();
        if(validarCodigo(codigo) && validarNome(nome) && validarCargaHoraria(cargaHoraria) && validarPeriodo(periodo) 
            && validarCurso(codigoCurso)  && validarExistenciaDisciplina(codigo,nome) && validarCreditos(creditos)
            && validarPreRequisitos(preRequisitos)) 
        {
            Disciplina novaDisciplina = new Disciplina(codigo, nome, cargaHoraria, periodo, creditos, codigoCurso, preRequisitos);
            repository.salvarDisciplina(novaDisciplina);
            return novaDisciplina;
        }

        return null;
    }

    public boolean validarExistenciaDisciplina(String codigo, String nome) {
        Disciplina disciplinaCodigo = repository.buscarPorCodigo(codigo);
        Disciplina disciplinaNome = repository.buscarPorNome(nome);
        return disciplinaCodigo == null && disciplinaNome == null;
    }

    public boolean validarCodigo(String codigo) {
        try{
            if (codigo.isBlank()) {
                throw new EntradaInvalidaException("Código inválido.");
            }

        } catch(NullPointerException e){
            throw new EntradaInvalidaException( "Código não pode ser null.");
        }

        return true;
    }

    public boolean validarNome(String nome) {
        try {
            if(nome.isBlank()){ 
                throw new EntradaInvalidaException("Nome inválido.");
            }
        } catch(NullPointerException e){
            throw new EntradaInvalidaException("Nome não pode ser null.");
        }

        return true;
    }

    public boolean validarCargaHoraria(int cargaHoraria) {
        if (cargaHoraria <= 0){
            throw new EntradaInvalidaException("Carga horária inválida.");
        }

        return true;
    }

    public boolean validarPeriodo(int periodo) {
        if (periodo <= 0){
            throw new EntradaInvalidaException("Período inválido.");
        }

        return true;
    }

    public boolean validarCreditos(int creditos) {
        if (creditos <= 0){
            throw new EntradaInvalidaException("Quantidade de créditos inválida.");
        }
        return true;
    }

    public boolean validarCurso(String codigoCurso) {
        Curso curso = cursoRepository.buscarPorCodigo(codigoCurso);
        if(curso == null){
            throw new EntradaInvalidaException("Curso não encontrado.");
        }
        return true;
    }

    public boolean validarPreRequisitos(List<String> preRequisitos) {
        if (preRequisitos == null){return true;}

        for (String codigoDisciplina : preRequisitos){
            if (codigoDisciplina == null || codigoDisciplina.isBlank()) {
                throw new EntradaInvalidaException("Código de pré-requisito inválido.");
            }

            Disciplina disciplina = repository.buscarPorCodigo(codigoDisciplina);

            if (disciplina == null){
                throw new EntradaInvalidaException("Pré-requisito " + codigoDisciplina + " não encontrado.");
            }
        }

        return true;
    }

    private String gerarCodigoDisciplina() {
        long quantidade = repository.listarDisciplinas().size();
        return "dis" + String.format("%02d", quantidade);
    }

    public List<Disciplina> listarDisciplinas() {
        return repository.listarDisciplinas();
    }

    public List<Disciplina> listarDisciplinasPorCurso(String codigoCurso) {
        return repository.buscarPorCurso(codigoCurso);
    }
}