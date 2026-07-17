package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioAlunoTurma;
import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioReprovacaoDisciplina;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioAlunosTurma;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioOcupacaoVagas;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.enums.SituacaoAcademica;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servico responsavel por montar relatorios academicos.
 */
public class RelatorioAcademicoService {

  private final TurmaService turmaService;
  private final UsuarioService usuarioService;
  private final BoletimService boletimService;
  private final SituacaoAcademicaService situacaoAcademicaService;

  /**
   * Cria o servico de relatorios com dependencias padrao.
   */
  public RelatorioAcademicoService() {
    this(new TurmaService(), new UsuarioService(), new BoletimService());
  }

  /**
   * Cria o servico de relatorios com dependencias informadas.
   *
   * @param turmaService servico de turmas.
   * @param usuarioService servico de usuarios.
   */
  public RelatorioAcademicoService(TurmaService turmaService, UsuarioService usuarioService) {
    this(turmaService, usuarioService, new BoletimService());
  }

  /**
   * Cria o servico de relatorios com todas as dependencias informadas.
   *
   * @param turmaService servico de turmas.
   * @param usuarioService servico de usuarios.
   * @param boletimService servico de boletins.
   */
  public RelatorioAcademicoService(
      TurmaService turmaService,
      UsuarioService usuarioService,
      BoletimService boletimService) {
    this.turmaService = turmaService;
    this.usuarioService = usuarioService;
    this.boletimService = boletimService;
    this.situacaoAcademicaService = new SituacaoAcademicaService();
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
   * Monta o relatorio de ocupacao de vagas de uma turma.
   *
   * @param coordenador coordenador logado.
   * @param turma turma consultada.
   * @return relatorio montado.
   */
  public RelatorioOcupacaoVagas gerarRelatorioOcupacaoVagas(
      Coordenador coordenador, Turma turma) {
    validarCoordenador(coordenador);
    validarTurma(turma);
    turmaService.validarTurmaPertenceAoCurso(turma.getCodigo(), coordenador.getCodigoCurso());

    int limiteVagas = turma.getLimiteVagas();
    int vagasOcupadas = turma.getMatriculados() == null ? 0 : turma.getMatriculados().size();
    int vagasDisponiveis = Math.max(limiteVagas - vagasOcupadas, 0);
    double percentualOcupacao = vagasOcupadas * 100.0 / limiteVagas;

    RelatorioOcupacaoVagas relatorio = new RelatorioOcupacaoVagas();
    relatorio.setCodigoTurma(turma.getCodigo());
    relatorio.setNomeDisciplina(buscarNomeDisciplina(turma));
    relatorio.setPeriodoLetivo(turma.getPeriodoLetivo());
    relatorio.setLimiteVagas(limiteVagas);
    relatorio.setVagasOcupadas(vagasOcupadas);
    relatorio.setVagasDisponiveis(vagasDisponiveis);
    relatorio.setPercentualOcupacao(percentualOcupacao);

    return relatorio;
  }

  /**
   * Monta o relatorio de reprovacao agrupado por disciplina.
   *
   * @param coordenador coordenador logado.
   * @return itens do relatorio.
   */
  public List<ItemRelatorioReprovacaoDisciplina> gerarRelatorioReprovacaoPorDisciplina(
      Coordenador coordenador) {
    Map<String, ItemRelatorioReprovacaoDisciplina> itensPorDisciplina =
        new LinkedHashMap<>();

    for (Turma turma : listarTurmasDoCoordenador(coordenador)) {
      ItemRelatorioReprovacaoDisciplina item =
          itensPorDisciplina.computeIfAbsent(
              turma.getCodigoDisciplina(), codigo -> criarItemReprovacao(turma));

      for (Boletim boletim : boletimService.buscarBoletinsPorTurma(turma.getCodigo())) {
        contabilizarSituacao(item, situacaoAcademicaService.determinar(boletim));
      }
    }

    List<ItemRelatorioReprovacaoDisciplina> itens =
        new ArrayList<>(itensPorDisciplina.values());
    itens.forEach(this::calcularTotaisReprovacao);
    itens.sort(comparadorReprovacao());
    return itens;
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

  private ItemRelatorioReprovacaoDisciplina criarItemReprovacao(Turma turma) {
    ItemRelatorioReprovacaoDisciplina item = new ItemRelatorioReprovacaoDisciplina();
    item.setCodigoDisciplina(turma.getCodigoDisciplina());
    item.setNomeDisciplina(buscarNomeDisciplina(turma));
    return item;
  }

  private void contabilizarSituacao(
      ItemRelatorioReprovacaoDisciplina item, SituacaoAcademica situacao) {
    switch (situacao) {
      case APROVADO:
        item.setTotalResultadosFinais(item.getTotalResultadosFinais() + 1);
        break;
      case REPROVADO_POR_NOTA:
        item.setTotalResultadosFinais(item.getTotalResultadosFinais() + 1);
        item.setTotalReprovadosPorNota(item.getTotalReprovadosPorNota() + 1);
        break;
      case REPROVADO_POR_FALTA:
        item.setTotalResultadosFinais(item.getTotalResultadosFinais() + 1);
        item.setTotalReprovadosPorFalta(item.getTotalReprovadosPorFalta() + 1);
        break;
      case EM_ANDAMENTO:
      case EM_RECUPERACAO:
        break;
      default:
        break;
    }
  }

  private void calcularTotaisReprovacao(ItemRelatorioReprovacaoDisciplina item) {
    int totalReprovados =
        item.getTotalReprovadosPorNota() + item.getTotalReprovadosPorFalta();
    item.setTotalReprovados(totalReprovados);

    if (item.getTotalResultadosFinais() == 0) {
      item.setPercentualReprovacao(0.0);
      return;
    }

    item.setPercentualReprovacao(
        totalReprovados * 100.0 / item.getTotalResultadosFinais());
  }

  private Comparator<ItemRelatorioReprovacaoDisciplina> comparadorReprovacao() {
    return Comparator.comparing(
        (ItemRelatorioReprovacaoDisciplina item) ->
            valorOrdenacao(item.getNomeDisciplina()),
        String.CASE_INSENSITIVE_ORDER)
        .thenComparing(
            item -> valorOrdenacao(item.getCodigoDisciplina()),
            String.CASE_INSENSITIVE_ORDER);
  }

  private String valorOrdenacao(String valor) {
    return valor == null ? "" : valor;
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
