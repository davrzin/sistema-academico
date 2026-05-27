package br.com.classroompb.model.services;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.Curso;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.CursoRepository;


public class CursoService {

    private static final Path DIRETORIO_PERIODOS = Path.of(System.getProperty("user.dir"), "cursos");
    private final CursoRepository repository;

    public CursoService(){
        this.repository = new CursoRepository(new ObjectMapper(), DIRETORIO_PERIODOS.toString());
    }

    public CursoService(CursoRepository cursoRepository){
        this.repository = cursoRepository;
    }

    public Curso cadastrarCurso(String nome,int quantidadePeriodos,int cargaHorariaTotal) {
        String codigo = gerarCodigoCurso();

        if (validarCodigoCurso(codigo) && validarNomeCurso(nome) && validarQuantidadePeriodos(quantidadePeriodos) && validarCargaHoraria(cargaHorariaTotal) && validarExistenciaCurso(codigo, nome)) {
            Curso novoCurso = new Curso(codigo, nome, quantidadePeriodos, cargaHorariaTotal);
            repository.salvarCurso(novoCurso);
            return novoCurso;
        }

        return null;
    }

    public boolean validarExistenciaCurso(String codigo, String nome){
        Curso cursoCodigo = repository.buscarPorCodigo(codigo);
        Curso cursoNome = repository.buscarPorNome(nome);

        if (cursoCodigo == null && cursoNome == null){return true;}

        return false;
    }

    public boolean validarCodigoCurso(String codigo) {
        try {
            if (codigo.isBlank()){throw new EntradaInvalidaException("Código do curso não pode ser vazio.");}
        } catch (NullPointerException e){
            throw new EntradaInvalidaException("Código do curso não pode ser null.");
        }

        return true;
    }

    public boolean validarNomeCurso(String nome) {
        try{
            if (nome.isBlank()) {throw new EntradaInvalidaException( "Nome do curso não pode ser vazio.");}
        } catch (NullPointerException e){
            throw new EntradaInvalidaException("Nome do curso não pode ser null.");
        }

        return true;
    }

    public boolean validarQuantidadePeriodos(int quantidadePeriodos){
        if (quantidadePeriodos <= 0) {throw new EntradaInvalidaException("Quantidade de períodos inválida.");}

        return true;
    }

    public boolean validarCargaHoraria(int cargaHoraria){
        if (cargaHoraria <= 0) {throw new EntradaInvalidaException( "Carga horária inválida.");}

        return true;
    }

    private String gerarCodigoCurso() {
        long quantidade = repository.listarCursos().stream().count();

        return "cur" + String.format("%02d", quantidade);
    }

    public List<Curso> listarCursos() {
        return repository.listarCursos();
    }
}
