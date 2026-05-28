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

    private static final Path DIRETORIO_DISCIPLINAS = Path.of(System.getProperty("user.dir"), "disciplinas");
    private static final Path DIRETORIO_CURSOS = Path.of(System.getProperty("user.dir"), "cursos");

    private final DisciplinaRepository repository;
    private final CursoRepository cursoRepository;

    public DisciplinaService() {
        this.repository = new DisciplinaRepository(new ObjectMapper(), DIRETORIO_DISCIPLINAS.toString());
        this.cursoRepository = new CursoRepository(new ObjectMapper(), DIRETORIO_CURSOS.toString());
    }

    public DisciplinaService(DisciplinaRepository repository, CursoRepository cursoRepository) {
        this.repository = repository;
        this.cursoRepository = cursoRepository;
    }

    public void cadastrarDisciplina(Disciplina disciplina) {
        validarDisciplina(disciplina);

        String codigo = gerarCodigoDisciplina();
        disciplina.setCodigo(codigo);

        validarExistenciaDisciplina(disciplina.getCodigo(), disciplina.getNome());
        validarCurso(disciplina);
        validarPreRequisitos(disciplina.getPreRequisitos());

        repository.salvarDisciplina(disciplina);
    }

    private void validarDisciplina(Disciplina disciplina) {
        if (disciplina == null) {
            throw new EntradaInvalidaException("Disciplina não pode ser null.");
        }

        disciplina.validarDadosBasicos();
    }

    private void validarExistenciaDisciplina(String codigo, String nome) {
        Disciplina disciplinaCodigo = repository.buscarPorCodigo(codigo);
        Disciplina disciplinaNome = repository.buscarPorNome(nome);

        if (disciplinaCodigo != null) {
            throw new EntradaInvalidaException("Já existe uma disciplina cadastrada com esse código.");
        }

        if (disciplinaNome != null) {
            throw new EntradaInvalidaException("Já existe uma disciplina cadastrada com esse nome.");
        }
    }

    private void validarCurso(Disciplina disciplina) {
        Curso curso = cursoRepository.buscarPorCodigo(disciplina.getCodigoCurso());

        if (curso == null) {
            throw new EntradaInvalidaException("Curso não encontrado.");
        }

        if (disciplina.getPeriodo() > curso.getQuantidadePeriodos()) {
            throw new EntradaInvalidaException("O período da disciplina não pode ser maior que a quantidade de períodos do curso.");
        }
    }

    private void validarPreRequisitos(List<String> preRequisitos) {
        if (preRequisitos == null || preRequisitos.isEmpty()) {
            return;
        }

        for (String codigoDisciplina : preRequisitos) {
            Disciplina disciplina = repository.buscarPorCodigo(codigoDisciplina);

            if (disciplina == null) {
                throw new EntradaInvalidaException("Pré-requisito " + codigoDisciplina + " não encontrado.");
            }
        }
    }

    private String gerarCodigoDisciplina() {
        int contador = repository.listarDisciplinas().size();
        String codigo;

        do {
            codigo = "dis" + String.format("%02d", contador);
            contador++;
        } while (repository.buscarPorCodigo(codigo) != null);

        return codigo;
    }

    public List<Disciplina> listarDisciplinas() {
        return repository.listarDisciplinas();
    }

    public List<Disciplina> listarDisciplinasPorCurso(String codigoCurso) {
        return repository.buscarPorCurso(codigoCurso);
    }
}
