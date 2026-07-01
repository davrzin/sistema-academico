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
        setMatriculaAluno(matriculaAluno);
        setCodigoTurma(codigoTurma);
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
        validarMatriculaAluno(matriculaAluno);
        this.matriculaAluno = matriculaAluno;
    }

    public String getCodigoTurma() {
        return codigoTurma;
    }

    public void setCodigoTurma(String codigoTurma) {
        validarCodigoTurma(codigoTurma);
        this.codigoTurma = codigoTurma;
    }

    public float getPrimeiraNota() {
        return primeiraNota;
    }

    public void setPrimeiraNota(float primeiraNota) {
        validarNota(primeiraNota);
        this.primeiraNota = primeiraNota;
    }

    public float getSegundaNota() {
        return segundaNota;
    }

    public void setSegundaNota(float segundaNota) {
        validarNota(segundaNota);
        this.segundaNota = segundaNota;
    }

    public double getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(double frequencia) {
        validarFrequencia(frequencia);
        this.frequencia = frequencia;
    }

    public Double calcularFrequencia(int quantidadeDeFaltas, int quantidadeDeAulas, int cargaHorariaTotal){
        validarQuantidadeDeAulasMinistradas(quantidadeDeAulas, cargaHorariaTotal);

        if(quantidadeDeAulas == 0){
            setFrequencia(100.0);
            return 100.0;
        }

        double frequencia = (double) ((quantidadeDeFaltas) * 100) / quantidadeDeAulas;

        setFrequencia(100.00 - frequencia);
        return 100.0 - frequencia;
    }

    private void validarMatriculaAluno(String matriculaAluno) {
        if(matriculaAluno == null || matriculaAluno.isBlank()){
            throw new EntradaInvalidaException("Aluno não pode ser null.");
        }
    }

    private void validarCodigoTurma(String codigoTurma){
        if(codigoTurma == null || codigoTurma.isBlank()){
            throw new EntradaInvalidaException("Disciplina não pode ser null.");
        }
    }

    private void validarFrequencia(double frequencia){
        if(frequencia < 0 || frequencia > 100.0){
            throw new EntradaInvalidaException("A frequência deve estar entre 0 e 100");
        }
    }

    private void validarNota(float nota){
        if(nota < 0 || nota > 10.0){
            throw new EntradaInvalidaException("A nota deve entrar entre 0 e 10.");
        }
    }

    private void validarQuantidadeDeAulasMinistradas(int quantidadeDeAulas, int cargaHorariaTotal){
        if(cargaHorariaTotal < 30 || cargaHorariaTotal > 90){
            throw new EntradaInvalidaException("Carga horária total deve estar entre 30 e 90");
        }
        if(quantidadeDeAulas < 0 || quantidadeDeAulas > cargaHorariaTotal){
            throw new EntradaInvalidaException("Quantidade de aulas inválida.");
        }
    }

    private void validarQuantidadeDeFaltas(int quantidadeDeFaltas){
        if(quantidadeDeFaltas < 0){
            throw new EntradaInvalidaException("A quantidade de faltas deve ser posistivo.");
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
