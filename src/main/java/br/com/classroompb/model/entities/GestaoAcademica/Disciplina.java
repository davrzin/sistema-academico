package br.com.classroompb.model.entities.GestaoAcademica;

import java.util.List;

public class Disciplina {
    private String codigo;
    private String nome;
    private int cargaHoraria;
    private int periodo;
    private int creditos;
    private String codigoCurso;
    private List<String> preRequisitos;

    public Disciplina() {
    }

    public Disciplina(
            String codigo,
            String nome,
            int cargaHoraria,
            int periodo,
            int creditos,
            String codigoCurso,
            List<String> preRequisitos
    ) {

        this.codigo = codigo;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.periodo = periodo;
        this.creditos = creditos;
        this.codigoCurso = codigoCurso;
        this.preRequisitos = preRequisitos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public int getCreditos(){
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public List<String> getPreRequisitos(){
        return preRequisitos;
    }

    public void setPreRequisitos(List<String> preRequisitos) {
        this.preRequisitos = preRequisitos;
    }

    @Override
    public String toString() {
        return """
                ┌────────────────────────────────────────┐
                │              DISCIPLINA                 │
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
