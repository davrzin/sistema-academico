package br.com.classroompb.model.entities.GestaoAcademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

public class Turma {

    private String codigo;
    private String codigoDisciplina;
    private String periodoLetivo;
    private String matriculaProfessor;
    private int limiteVagas;
    private String horario;
    private String sala;

    public Turma() {
    }

    public Turma(String codigoDisciplina, String periodoLetivo, String matriculaProfessor, int limiteVagas, String horario, String sala) {
        setCodigoDisciplina(codigoDisciplina);
        setPeriodoLetivo(periodoLetivo);
        setMatriculaProfessor(matriculaProfessor);
        setLimiteVagas(limiteVagas);
        setHorario(horario);
        setSala(sala);
    }

    public Turma(String codigo, String codigoDisciplina, String periodoLetivo, String matriculaProfessor, int limiteVagas, String horario, String sala) {
        setCodigo(codigo);
        setCodigoDisciplina(codigoDisciplina);
        setPeriodoLetivo(periodoLetivo);
        setMatriculaProfessor(matriculaProfessor);
        setLimiteVagas(limiteVagas);
        setHorario(horario);
        setSala(sala);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        validarCampoObrigatorio(codigo, "Código da turma não pode ser vazio.");
        this.codigo = codigo;
    }

    public String getCodigoDisciplina() {
        return codigoDisciplina;
    }

    public void setCodigoDisciplina(String codigoDisciplina) {
        validarCampoObrigatorio(codigoDisciplina, "Código da disciplina não pode ser vazio.");
        this.codigoDisciplina = codigoDisciplina;
    }

    public String getPeriodoLetivo() {
        return periodoLetivo;
    }

    public void setPeriodoLetivo(String periodoLetivo) {
        validarCampoObrigatorio(periodoLetivo, "Período letivo da turma não pode ser vazio.");
        this.periodoLetivo = periodoLetivo;
    }

    public String getMatriculaProfessor() {
        return matriculaProfessor;
    }

    public void setMatriculaProfessor(String matriculaProfessor) {
        validarCampoObrigatorio(matriculaProfessor, "Turma deve possuir professor responsável.");
        this.matriculaProfessor = matriculaProfessor;
    }

    public int getLimiteVagas() {
        return limiteVagas;
    }

    public void setLimiteVagas(int limiteVagas) {
        validarLimiteVagas(limiteVagas);
        this.limiteVagas = limiteVagas;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        validarCampoObrigatorio(horario, "Horário da turma não pode ser vazio.");
        this.horario = horario;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        validarCampoObrigatorio(sala, "Sala da turma não pode ser vazia.");
        this.sala = sala;
    }

    public void validarDadosBasicos() {
        validarCampoObrigatorio(codigoDisciplina, "Código da disciplina não pode ser vazio.");
        validarCampoObrigatorio(periodoLetivo, "Período letivo da turma não pode ser vazio.");
        validarCampoObrigatorio(matriculaProfessor, "Turma deve possuir professor responsável.");
        validarLimiteVagas(limiteVagas);
        validarCampoObrigatorio(horario, "Horário da turma não pode ser vazio.");
        validarCampoObrigatorio(sala, "Sala da turma não pode ser vazia.");
    }

    private void validarCampoObrigatorio(String valor, String mensagemErro) {
        if (valor == null || valor.isBlank()) {
            throw new EntradaInvalidaException(mensagemErro);
        }
    }

    private void validarLimiteVagas(int limiteVagas) {
        if (limiteVagas <= 0) {
            throw new EntradaInvalidaException("Limite de vagas da turma deve ser maior que zero.");
        }
    }

    @Override
    public String toString() {
        return """
                ┌────────────────────────────────────────┐
                │                 TURMA                  │
                ├────────────────────────────────────────┤
                │ Código              : %s
                │ Disciplina          : %s
                │ Período Letivo      : %s
                │ Professor           : %s
                │ Limite de Vagas     : %d
                │ Horário             : %s
                │ Sala                : %s
                └────────────────────────────────────────┘
                """.formatted(codigo, codigoDisciplina, periodoLetivo, matriculaProfessor, limiteVagas, horario, sala);
    }
}
