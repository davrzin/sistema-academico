package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioAlunoTurma;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioAlunosTurma;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servico responsavel por montar relatorios academicos.
 */
public class RelatorioAcademicoService {

  private final TurmaService turmaService;
  private final UsuarioService usuarioService;

  /**
   * Cria o servico de relatorios com dependencias padrao.
   */
  public RelatorioAcademicoService() {
    this.turmaService = new TurmaService();
    this.usuarioService = new UsuarioService();
  }

  /**
   * Cria o servico de relatorios com dependencias informadas.
   *
   * @param turmaService servico de turmas.
   * @param usuarioService servico de usuarios.
   */
  public RelatorioAcademicoService(TurmaService turmaService, UsuarioService usuarioService) {
    this.turmaService = turmaService;
    this.usuarioService = usuarioService;
  }

  /**
   * Lista as turmas do curso do coordenador.
   *
   * @param coordenador coordenador logado.
   * @return turmas do curso.
   */
  public List<Turma> listarTurmasDoCoordenador(Coordenador coordenador) {
    validarCoordenador(coordenador);
    return turmaService.listarTurmasPorCurso(coordenador.getCodigoCurso());
  }

  /**
   * Monta o relatorio de alunos matriculados em uma turma.
   *
   * @param coordenador coordenador logado.
   * @param turma turma selecionada.
   * @return relatorio montado.
   */
  public RelatorioAlunosTurma gerarRelatorioAlunosTurma(
      Coordenador coordenador, Turma turma) {
    validarCoordenador(coordenador);
    validarTurma(turma);
    turmaService.validarTurmaPertenceAoCurso(turma.getCodigo(), coordenador.getCodigoCurso());

    RelatorioAlunosTurma relatorio = new RelatorioAlunosTurma();
    List<ItemRelatorioAlunoTurma> alunos = montarItensAlunos(turma);

    relatorio.setCodigoTurma(turma.getCodigo());
    relatorio.setNomeDisciplina(buscarNomeDisciplina(turma));
    relatorio.setPeriodoLetivo(turma.getPeriodoLetivo());
    relatorio.setNomeProfessor(buscarNomeProfessor(turma));
    relatorio.setLimiteVagas(turma.getLimiteVagas());
    relatorio.setTotalMatriculados(alunos.size());
    relatorio.setAlunos(alunos);

    return relatorio;
  }

  /**
   * Busca o nome da disciplina de uma turma.
   *
   * @param turma turma consultada.
   * @return nome da disciplina.
   */
  public String buscarNomeDisciplina(Turma turma) {
    if (turma == null || turma.getCodigoDisciplina() == null) {
      return "Disciplina não encontrada";
    }

    String nomeDisciplina = turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina());

    if (nomeDisciplina == null
        || nomeDisciplina.isBlank()
        || nomeDisciplina.equalsIgnoreCase(turma.getCodigoDisciplina())) {
      return "Disciplina não encontrada";
    }

    return nomeDisciplina;
  }

  /**
   * Busca o nome do professor de uma turma.
   *
   * @param turma turma consultada.
   * @return nome do professor.
   */
  public String buscarNomeProfessor(Turma turma) {
    if (turma == null || turma.getMatriculaProfessor() == null) {
      return "Professor não encontrado";
    }

    String nomeProfessor = turmaService.buscarNomeProfessor(turma.getMatriculaProfessor());

    if (nomeProfessor == null
        || nomeProfessor.isBlank()
        || nomeProfessor.equalsIgnoreCase(turma.getMatriculaProfessor())) {
      return "Professor não encontrado";
    }

    return nomeProfessor;
  }

  private List<ItemRelatorioAlunoTurma> montarItensAlunos(Turma turma) {
    List<ItemRelatorioAlunoTurma> alunos = new ArrayList<>();

    if (turma.getMatriculados() == null) {
      return alunos;
    }

    for (String matriculaAluno : turma.getMatriculados()) {
      alunos.add(montarItemAluno(matriculaAluno));
    }

    return alunos;
  }

  private ItemRelatorioAlunoTurma montarItemAluno(String matriculaAluno) {
    ItemRelatorioAlunoTurma item = new ItemRelatorioAlunoTurma();
    item.setMatricula(matriculaAluno);

    try {
      Aluno aluno = usuarioService.buscarAlunoPorMatricula(matriculaAluno);
      item.setNome(aluno.getNome());
      item.setEmail(aluno.getEmail());
    } catch (RuntimeException e) {
      item.setNome("Aluno não encontrado");
      item.setEmail("-");
    }

    return item;
  }

  private void validarCoordenador(Coordenador coordenador) {
    if (coordenador == null
        || coordenador.getCodigoCurso() == null
        || coordenador.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Coordenador não está vinculado a nenhum curso.");
    }
  }

  private void validarTurma(Turma turma) {
    if (turma == null || turma.getCodigo() == null || turma.getCodigo().isBlank()) {
      throw new EntradaInvalidaException("Turma inválida para geração de relatório.");
    }
  }
}
