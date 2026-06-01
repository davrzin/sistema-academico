package br.com.classroompb.model.entities.GestaoAcademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

public class Curso {
    private String codigo;
    private String nome;
    private int quantidadePeriodos;
    private int cargaHorariaTotal;

    public Curso() {
    }

    public Curso(String nome, int quantidadePeriodos, int cargaHorariaTotal) {
        setNome(nome);
        setQuantidadePeriodos(quantidadePeriodos);
        setCargaHorariaTotal(cargaHorariaTotal);
    }

    public Curso(String codigo, String nome, int quantidadePeriodos, int cargaHorariaTotal) {
        setCodigo(codigo);
        setNome(nome);
        setQuantidadePeriodos(quantidadePeriodos);
        setCargaHorariaTotal(cargaHorariaTotal);
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

    public int getQuantidadePeriodos() {
        return quantidadePeriodos;
    }

    public void setQuantidadePeriodos(int quantidadePeriodos) {
        validarQuantidadePeriodos(quantidadePeriodos);
        this.quantidadePeriodos = quantidadePeriodos;
    }

    public int getCargaHorariaTotal() {
        return cargaHorariaTotal;
    }

    public void setCargaHorariaTotal(int cargaHorariaTotal) {
        validarCargaHorariaTotal(cargaHorariaTotal);
        this.cargaHorariaTotal = cargaHorariaTotal;
    }

    public void validarDadosBasicos() {
        validarNome(nome);
        validarQuantidadePeriodos(quantidadePeriodos);
        validarCargaHorariaTotal(cargaHorariaTotal);
    }

    private void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new EntradaInvalidaException("Código do curso não pode ser vazio.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new EntradaInvalidaException("Nome do curso não pode ser vazio.");
        }

        if (nome.length() < 3) {
            throw new EntradaInvalidaException(
                "Nome do curso deve possuir pelo menos 3 caracteres."
            );
        }

        if (nome.length() > 200) {
            throw new EntradaInvalidaException(
                "Nome do curso deve possuir no máximo 100 caracteres."
            );
        }
    }

    private void validarQuantidadePeriodos(int quantidadePeriodos){
        if (quantidadePeriodos <= 0){
            throw new EntradaInvalidaException("Quantidade de períodos inválida.");
        }

        if (quantidadePeriodos > 13){
            throw new EntradaInvalidaException(
                "A quantidade de períodos não pode ultrapassar 13."
            );
        }
    }

    private void validarCargaHorariaTotal(int cargaHorariaTotal) {
        if (cargaHorariaTotal <= 0){
            throw new EntradaInvalidaException("Carga horária inválida.");
        }

        if (cargaHorariaTotal > 10000){
            throw new EntradaInvalidaException(
                "Carga horária inválida."
            );
        }
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
