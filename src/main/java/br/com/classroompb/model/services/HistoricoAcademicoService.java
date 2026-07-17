package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.ItemHistoricoAcademico;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.util.Comparator;
import java.util.List;

/**
 * Servico responsavel por montar o historico academico a partir dos boletins.
 */
public class HistoricoAcademicoService {

  private final BoletimService boletimService;
  private final TurmaService turmaService;
  private final SituacaoAcademicaService situacaoAcademicaService;

  /**
   * Cria o servico de historico academico com dependencias padrao.
   */
  public HistoricoAcademicoService() {
    this(new BoletimService(), new TurmaService());
  }

  /**
   * Cria o servico de historico academico com dependencias informadas.
   *
   * @param boletimService servico de boletins.
   * @param turmaService servico de turmas.
   */
  public HistoricoAcademicoService(BoletimService boletimService, TurmaService turmaService) {
    this.boletimService = boletimService;
    this.turmaService = turmaService;
    this.situacaoAcademicaService = new SituacaoAcademicaService();
  }

  /**
   * Lista o historico academico de um aluno.
   *
   * @param aluno aluno consultado.
   * @return itens do historico academico.
   */
  public List<ItemHistoricoAcademico> listarHistoricoAluno(Aluno aluno) {
    validarAluno(aluno);

    return boletimService.buscarBoletinsPorAluno(aluno.getMatricula()).stream()
        .map(boletim -> montarItemHistorico(aluno, boletim))
        .sorted(comparadorHistorico())
        .toList();
  }

  private void validarAluno(Aluno aluno) {
    if (aluno == null || aluno.getMatricula() == null || aluno.getMatricula().isBlank()) {
      throw new EntradaInvalidaException("Aluno invalido para consulta de historico.");
    }
  }

  private ItemHistoricoAcademico montarItemHistorico(Aluno aluno, Boletim boletim) {
    Turma turma = buscarTurma(boletim);
    ItemHistoricoAcademico item = new ItemHistoricoAcademico();

    item.setMatriculaAluno(aluno.getMatricula());
    item.setNomeAluno(aluno.getNome());
    item.setCodigoTurma(boletim.getCodigoTurma());
    item.setPeriodoLetivo(buscarPeriodoLetivo(turma));
    item.setCodigoDisciplina(buscarCodigoDisciplina(turma));
    item.setNomeDisciplina(buscarNomeDisciplina(turma));
    item.setNomeProfessor(buscarNomeProfessor(turma));
    item.setNotaFinal(calcularNotaFinal(boletim));
    item.setFrequencia(buscarFrequencia(boletim));
    item.setSituacao(situacaoAcademicaService.determinar(boletim).getDescricao());

    return item;
  }

  private Turma buscarTurma(Boletim boletim) {
    try {
      return turmaService.buscarTurmaPorCodigo(boletim.getCodigoTurma());
    } catch (RuntimeException e) {
      return null;
    }
  }

  private String buscarPeriodoLetivo(Turma turma) {
    if (turma == null || turma.getPeriodoLetivo() == null || turma.getPeriodoLetivo().isBlank()) {
      return "-";
    }

    return turma.getPeriodoLetivo();
  }

  private String buscarCodigoDisciplina(Turma turma) {
    if (turma == null || turma.getCodigoDisciplina() == null) {
      return "-";
    }

    return turma.getCodigoDisciplina();
  }

  private String buscarNomeDisciplina(Turma turma) {
    if (turma == null || turma.getCodigoDisciplina() == null) {
      return "Disciplina nao encontrada";
    }

    String nomeDisciplina = turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina());

    if (nomeDisciplina == null
        || nomeDisciplina.isBlank()
        || nomeDisciplina.equalsIgnoreCase(turma.getCodigoDisciplina())) {
      return "Disciplina nao encontrada";
    }

    return nomeDisciplina;
  }

  private String buscarNomeProfessor(Turma turma) {
    if (turma == null || turma.getMatriculaProfessor() == null) {
      return "Professor nao encontrado";
    }

    String nomeProfessor = turmaService.buscarNomeProfessor(turma.getMatriculaProfessor());

    if (nomeProfessor == null
        || nomeProfessor.isBlank()
        || nomeProfessor.equalsIgnoreCase(turma.getMatriculaProfessor())) {
      return "Professor nao encontrado";
    }

    return nomeProfessor;
  }

  private Double calcularNotaFinal(Boletim boletim) {
    Float media = boletim.calcularMediaFinal();
    return media == null ? null : media.doubleValue();
  }

  private Double buscarFrequencia(Boletim boletim) {
    return boletim.getFrequencia();
  }

  private Comparator<ItemHistoricoAcademico> comparadorHistorico() {
    return Comparator.comparing(
        (ItemHistoricoAcademico item) -> valorOrdenacao(item.getPeriodoLetivo()),
        String.CASE_INSENSITIVE_ORDER)
        .thenComparing(
            item -> valorOrdenacao(item.getNomeDisciplina()), String.CASE_INSENSITIVE_ORDER)
        .thenComparing(
            item -> valorOrdenacao(item.getCodigoTurma()), String.CASE_INSENSITIVE_ORDER);
  }

  private String valorOrdenacao(String valor) {
    return valor == null ? "" : valor;
  }
}
