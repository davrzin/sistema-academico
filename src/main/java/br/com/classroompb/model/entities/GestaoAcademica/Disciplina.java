package br.com.classroompb.model.entities.GestaoAcademica;

import java.util.ArrayList;
import java.util.List;

import br.com.classroompb.model.exception.EntradaInvalidaException;

public class Disciplina {
    private String codigo;
    private String nome;
    private int cargaHoraria;
    private int periodo;
    private int creditos;
    private String codigoCurso;
    private List<String> preRequisitos;

    public Disciplina() {
        this.preRequisitos = new ArrayList<>();
    }

    public Disciplina(String nome, int cargaHoraria, int periodo, int creditos, String codigoCurso, List<String> preRequisitos) {
        setNome(nome);
        setCargaHoraria(cargaHoraria);
        setPeriodo(periodo);
        setCreditos(creditos);
        setCodigoCurso(codigoCurso);
        setPreRequisitos(preRequisitos);
    }

    public Disciplina( String codigo, String nome, int cargaHoraria, int periodo, int creditos, String codigoCurso, List<String> preRequisitos) {
        setCodigo(codigo);
        setNome(nome);
        setCargaHoraria(cargaHoraria);
        setPeriodo(periodo);
        setCreditos(creditos);
        setCodigoCurso(codigoCurso);
        setPreRequisitos(preRequisitos);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        validarCodigo(codigo);
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        validarNome(nome);
        this.nome = nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        validarCargaHoraria(cargaHoraria);
        this.cargaHoraria = cargaHoraria;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        validarPeriodo(periodo);
        this.periodo = periodo;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        validarCodigoCurso(codigoCurso);
        this.codigoCurso = codigoCurso;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        validarCreditos(creditos);
        this.creditos = creditos;
    }

    public List<String> getPreRequisitos() {
        return preRequisitos;
    }

    public void setPreRequisitos(List<String> preRequisitos) {
        validarPreRequisitosBasicos(preRequisitos);

        if (preRequisitos == null) {
            this.preRequisitos = new ArrayList<>();
        } else {
            this.preRequisitos = new ArrayList<>(preRequisitos);
        }
    }

    public void validarDadosBasicos() {
        validarNome(nome);
        validarCargaHoraria(cargaHoraria);
        validarPeriodo(periodo);
        validarCreditos(creditos);
        validarCodigoCurso(codigoCurso);
        validarPreRequisitosBasicos(preRequisitos);
    }

    private void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new EntradaInvalidaException("Código da disciplina não pode ser vazio.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new EntradaInvalidaException("Nome da disciplina não pode ser vazio.");
        }
    }

    private void validarCargaHoraria(int cargaHoraria) {
        if (cargaHoraria <= 0) {
            throw new EntradaInvalidaException("Carga horária da disciplina inválida.");
        }
    }

    private void validarPeriodo(int periodo) {
        if (periodo <= 0) {
            throw new EntradaInvalidaException("Período da disciplina inválido.");
        }
    }

    private void validarCreditos(int creditos) {
        if (creditos <= 0) {
            throw new EntradaInvalidaException("Quantidade de créditos inválida.");
        }
    }

    private void validarCodigoCurso(String codigoCurso) {
        if (codigoCurso == null || codigoCurso.isBlank()) {
            throw new EntradaInvalidaException("Código do curso da disciplina não pode ser vazio.");
        }
    }

    private void validarPreRequisitosBasicos(List<String> preRequisitos) {
        if (preRequisitos == null) {
            return;
        }

        for (String codigoDisciplina : preRequisitos) {
            if (codigoDisciplina == null || codigoDisciplina.isBlank()) {
                throw new EntradaInvalidaException("Código de pré-requisito inválido.");
            }
        }
    }

    @Override
    public String toString() {
        return """
                ┌────────────────────────────────────────┐
                │              DISCIPLINA                │
                ├────────────────────────────────────────┤
                │ Código        : %s
                │ Nome          : %s
                │ Carga Horária : %dh
                │ Período       : %d
                │ Curso         : %s
                │ Créditos      : %d
                │ Pré-requisitos: %s
                └────────────────────────────────────────┘
                """.formatted(codigo, nome, cargaHoraria, periodo, codigoCurso, creditos, preRequisitos);
    }
}
