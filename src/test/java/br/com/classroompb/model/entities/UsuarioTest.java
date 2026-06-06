package br.com.classroompb.model.entities;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.entities.Usuario.Administrador;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.entities.Usuario.Coordenador;
import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;

public class UsuarioTest {

    @Test
    public void deveInstaciarAlunoCorretamente(){

        Aluno aluno = new Aluno("Arthur", "arthur@email.com","al00", "123", TipoUsuario.ALUNO);

        Assertions.assertEquals(Aluno.class, aluno.getClass());
    }

    @Test
    public void deveInstaciarAlunoCorretamenteConstrutorSemTipoDeUsuario(){

        Aluno aluno = new Aluno("Arthur", "arthur@email.com","al00", "123");

        Assertions.assertEquals(Aluno.class, aluno.getClass());
    }

    @Test
    public void deveInstaciarProfessorCorretamente(){

        Professor professor = new Professor("Arthur", "arthur@email.com", "pr00","123", TipoUsuario.PROFESSOR);

        Assertions.assertEquals(Professor.class, professor.getClass());
    }

    @Test
    public void deveInstaciarProfessorCorretamenteConstrutorSemTipoDeUsuario(){

        Professor professor = new Professor("Arthur", "arthur@email.com", "pr00","123");

        Assertions.assertEquals(Professor.class, professor.getClass());
    }

    @Test
    public void deveInstaciarCoordenadorCorretamente(){

        Coordenador coordenador = new Coordenador("Arthur", "arthur@email.com","co00", "123", TipoUsuario.COORDENADOR);

        Assertions.assertEquals(Coordenador.class, coordenador.getClass());
    }

    @Test
    public void deveInstaciarCoordenadorCorretamenteConstrutorSemTipoDeUsuario(){

        Coordenador coordenador = new Coordenador("Arthur", "arthur@email.com","co00", "123");

        Assertions.assertEquals(Coordenador.class, coordenador.getClass());
    }

    @Test
    public void deveInstaciarAdministradorCorretamente(){

        Administrador adm = new Administrador("Arthur", "arthur@email.com", "adm00","123", TipoUsuario.ADMINISTRADOR);

        Assertions.assertEquals(Administrador.class, adm.getClass());
    }

    @Test
    public void deveInstaciarAdministradorCorretamenteConstrutorSemTipoDeUsuario(){

        Administrador adm = new Administrador("Arthur", "arthur@email.com", "adm00","123");

        Assertions.assertEquals(Administrador.class, adm.getClass());
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarAlunoComDadosInvalidos(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Aluno("", "", ""));

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarProfessorComDadosInvalidos(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Professor("", "", ""));

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarCoordenadorComDadosInvalidos(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Coordenador("", "", ""));

    }

    @Test
    public void deveLancarEntradaInvalidaExceptionAoCriarAdministradorComDadosInvalidos(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> new Administrador("", "", ""));

    }

    @Test
    public void deveRetornarNomeDeUsuarioCorretamente(){
        Usuario aluno = new Aluno("Arthur", "arthur@email.com", "123");

        Assertions.assertEquals("Arthur", aluno.getNome());
    }

    @Test
    public void deveRetornarEmailDeUsuarioCorretamente(){
        Usuario aluno = new Aluno("Arthur", "arthur@email.com", "123");

        Assertions.assertEquals("arthur@email.com", aluno.getEmail());
    }

    @Test
    public void deveRetornarSenhaDeUsuarioCorretamente(){
        Usuario aluno = new Aluno("Arthur", "arthur@email.com", "123");

        Assertions.assertEquals("123", aluno.getSenha());
    }

    @Test
    public void deveRetornarTipoDeUsuarioCorretamente(){
        Usuario aluno = new Aluno("Arthur", "arthur@email.com", "123");

        Assertions.assertEquals(TipoUsuario.ALUNO, aluno.getTipoUsuario());
    }

    @Test
    public void deveRetornarMatriculaDeUsuarioCorretamente(){
        Usuario aluno = new Aluno("Arthur", "arthur@email.com", "al00","123");

        Assertions.assertEquals("al00", aluno.getMatricula());
    }

    @Test
    public void deveRetornarDisciplinasConcluidasPorAlunoCadastrado(){

        Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

        List<String> disciplinasConcluidas = aluno.getDisciplinasConcluidas();

        Assertions.assertEquals(0, disciplinasConcluidas.size());
    }

    @Test
    public void deveAdicionarDisciplinaConcluidaEmAlunoCorretamente(){

        Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

        Disciplina disciplina = new Disciplina("Engenharia de Software II", 60, 6, 30, "cur00", new ArrayList<>());

        aluno.getDisciplinasConcluidas().add(disciplina.getCodigo());

        List<String> disciplinasConcluidas = aluno.getDisciplinasConcluidas();

        Assertions.assertEquals(1, disciplinasConcluidas.size());
        Assertions.assertEquals(disciplina.getCodigo(), disciplinasConcluidas.getFirst());
    }

    @Test
    public void deveRetornarTurmasMatriculadasPorAlunoCadastrado(){
        Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

        List<String> turmasMatriculadas = aluno.getTurmasMatriculadas();

        Assertions.assertEquals(0, turmasMatriculadas.size());
    }

    @Test
    public void deveAdicionarTurmaMatriculadaEmAlunoCorretamente(){
        Aluno aluno = new Aluno("Arthur", "arthur@email.com", "123");

        Turma turma = new Turma("cur00","dis00", "2026.1", "pr01", 45, "Seg 08:00-10:00" ,"Lab5");

        List<String> turmasMatriculadas = aluno.getTurmasMatriculadas();
        turmasMatriculadas.add(turma.getCodigo());

        Assertions.assertEquals(1, turmasMatriculadas.size());
        Assertions.assertEquals(turma.getCodigo(), turmasMatriculadas.getFirst());
    }


}
