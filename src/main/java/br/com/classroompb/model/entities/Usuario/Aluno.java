package br.com.classroompb.model.entities.Usuario;

import java.util.ArrayList;
import java.util.List;

import br.com.classroompb.model.enums.TipoUsuario;

public class Aluno extends Usuario {

    private List<String> disciplinasConcluidas;
    private List<String> turmasMatriculadas;

    public Aluno() {
        super();
    }

    public Aluno(String nome, String email, String senha) {
        super(nome, email, senha, TipoUsuario.ALUNO);
        setDisciplinasConcluidas();
        setTurmasMatriculadas();
    }

    public Aluno(String nome, String email, String matricula, String senha) {
        super(nome, email, matricula, senha, TipoUsuario.ALUNO);
        setDisciplinasConcluidas();
        setTurmasMatriculadas();
    }

    public Aluno(String nome, String email, String matricula, String senha, TipoUsuario tipoUsuario) {
        super(nome, email, matricula, senha, TipoUsuario.ALUNO);
        setDisciplinasConcluidas();
        setTurmasMatriculadas();
    }

    public void setDisciplinasConcluidas(){
        this.disciplinasConcluidas = new ArrayList<>();
    }

    public List<String> getDisciplinasConcluidas(){
        return disciplinasConcluidas;
    }

    public void setTurmasMatriculadas(){
        this.turmasMatriculadas = new ArrayList<>();
    }

    public List<String> getTurmasMatriculadas(){
        return turmasMatriculadas;
    }

}
