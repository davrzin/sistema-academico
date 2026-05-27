package br.com.classroompb.model.entities.GestaoAcademica;

public class Curso {
    private String codigo;
    private String nome;
    private int quantidadePeriodos;
    private int cargaHorariaTotal;

    public Curso() {
    }

    public Curso(String codigo, String nome, int quantidadePeriodos, int cargaHorariaTotal) {
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadePeriodos = quantidadePeriodos;
        this.cargaHorariaTotal = cargaHorariaTotal;
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

    public int getQuantidadePeriodos() {
        return quantidadePeriodos;
    }

    public void setQuantidadePeriodos(int quantidadePeriodos) {
        this.quantidadePeriodos = quantidadePeriodos;
    }

    public int getCargaHorariaTotal() {
        return cargaHorariaTotal;
    }

    public void setCargaHorariaTotal(int cargaHorariaTotal) {
        this.cargaHorariaTotal = cargaHorariaTotal;
    }

    @Override
    public String toString() {
        return """
            ┌───────────────────────────────────────┐
            │                 CURSO                 │
            ├───────────────────────────────────────┤
            │ Código        : %s
            │ Nome          : %s
            │ Períodos      : %d
            │ Carga Horária : %dh
            └───────────────────────────────────────┘
            """.formatted(codigo, nome, quantidadePeriodos, cargaHorariaTotal);
    }
}
