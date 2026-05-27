package br.com.classroompb.model.entities.GestaoAcademica;

public class Disciplina {
    private String codigo;
    private String nome;
    private int cargaHoraria;
    private int periodo;
    private String codigoCurso;

    public Disciplina() {
    }

    public Disciplina(
            String codigo,
            String nome,
            int cargaHoraria,
            int periodo,
            String codigoCurso
    ) {

        this.codigo = codigo;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.periodo = periodo;
        this.codigoCurso = codigoCurso;
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

    @Override
    public String toString() {
        return """
            ┌───────────────────────────────────────┐
            │             DISCIPLINA                │
            ├───────────────────────────────────────┤
            │ Código        : %s
            │ Nome          : %s
            │ Carga Horária : %dh
            │ Período       : %d
            │ Curso         : %s
            └───────────────────────────────────────┘
            """.formatted(codigo, nome, cargaHoraria, periodo, codigoCurso);
    }
}
