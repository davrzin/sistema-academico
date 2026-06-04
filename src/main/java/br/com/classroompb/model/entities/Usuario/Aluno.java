package br.com.classroompb.model.entities.Usuario;

import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.enums.TipoUsuario;

import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {

    private List<Disciplina> disciplinasConcluidas;
    private List<Turma> turmasMatriculadas;

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

    public List<Disciplina> getDisciplinasConcluidas(){
        return disciplinasConcluidas;
    }

    public void setTurmasMatriculadas(){
        this.turmasMatriculadas = new ArrayList<>();
    }

    public List<Turma> getTurmasMatriculadas(){
        return turmasMatriculadas;
    }

}
