package br.com.classroompb.model.entities.GestaoAcademica;

import br.com.classroompb.model.exception.EntradaInvalidaException;

public class Boletim {

    private String idBoletim;
    private String matriculaAluno;
    private String codigoTurma;
    private float primeiraNota;
    private float segundaNota;
    private double frequencia;

    public Boletim(){}

    public Boletim(String matriculaAluno, String codigoTurma){
        this.matriculaAluno = matriculaAluno;
        this.codigoTurma = codigoTurma;
    }


    public String getIdBoletim() {
        return idBoletim;
    }

    public void setIdBoletim(String idBoletim) {
        this.idBoletim = idBoletim;
    }

    public String getMatriculaAluno() {
        return matriculaAluno;
    }

    public void setMatriculaAluno(String matriculaAluno) {
        this.matriculaAluno = matriculaAluno;
    }

    public String getCodigoTurma() {
        return codigoTurma;
    }

    public void setCodigoTurma(String codigoTurma) {
        this.codigoTurma = codigoTurma;
    }

    public float getPrimeiraNota() {
        return primeiraNota;
    }

    public void setPrimeiraNota(float primeiraNota) {
        this.primeiraNota = primeiraNota;
    }

    public float getSegundaNota() {
        return segundaNota;
    }

    public void setSegundaNota(float segundaNota) {
        this.segundaNota = segundaNota;
    }

    public double getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(double frequencia) {
        this.frequencia = frequencia;
    }

    public void validarDadosBasicos(){
        validarAluno(matriculaAluno);
        validarDisciplina(codigoTurma);

    }

    public void calcularFrequencia(int quantidadeDeFaltas, int quantidadeDeAulas){

        System.out.println(quantidadeDeFaltas);
        System.out.println(quantidadeDeAulas);

        if(quantidadeDeAulas == 0){
            setFrequencia(100.00);
            return;
        }

        double frequencia = (double) ((quantidadeDeFaltas) * 100) / quantidadeDeAulas;


        setFrequencia(100.00 - frequencia);
    }

    private void validarAluno(String matriculaAluno){
        if(matriculaAluno == null){
            throw new EntradaInvalidaException("Aluno não pode ser null.");
        }
    }

    private void validarDisciplina(String codigoTurma){
        if(codigoTurma == null){
            throw new EntradaInvalidaException("Disciplina não pode ser null.");
        }
    }

    @Override
    public String toString() {
        return "Boletim{" +
                "primeiraNota=" + primeiraNota +
                ", segundaNota=" + segundaNota +
                ", frequencia=" + frequencia +
                '}';
    }
}
