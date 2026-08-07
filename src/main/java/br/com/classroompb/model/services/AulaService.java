package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.DiarioRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import br.com.classroompb.model.repository.TurmaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servico responsavel pelas operacoes de aula.
 */
public class AulaService {
  private static final int HORAS_POR_AULA = 2;
  private static final Path DIRETORIO_AULAS = PersistenciaPaths.AULAS;
  private static final Path DIRETORIO_DIARIOS = PersistenciaPaths.DIARIOS;
  private static final Path DIRETORIO_TURMAS = PersistenciaPaths.TURMAS;

  private final AulaRepository aulaRepository;
  private final DiarioRepository diarioRepository;
  private final TurmaRepository turmaRepository;

  /**
   * Cria o servico de aulas com dependencias padrao.
   */
  public AulaService() {
    this.aulaRepository = new AulaRepository(new ObjectMapper(), DIRETORIO_AULAS.toString());
    this.diarioRepository =
        new DiarioRepository(new ObjectMapper(), DIRETORIO_DIARIOS.toString());
    this.turmaRepository =
        new TurmaRepository(new ObjectMapper(), DIRETORIO_TURMAS.toString());
  }

  /**
   * Cria o servico de aulas com dependencias informadas.
   *
   * @param aulaRepository repositorio de aulas.
   * @param turmaRepository repositorio de turmas.
   */
  public AulaService(AulaRepository aulaRepository, TurmaRepository turmaRepository) {
    this.aulaRepository = aulaRepository;
    this.turmaRepository = turmaRepository;
    this.diarioRepository =
        new DiarioRepository(new ObjectMapper(), DIRETORIO_DIARIOS.toString());
  }

  /**
   * Cria o servico de aulas com todas as dependencias informadas.
   *
   * @param aulaRepository repositorio de aulas.
   * @param turmaRepository repositorio de turmas.
   * @param diarioRepository repositorio de diarios.
   */
  public AulaService(
      AulaRepository aulaRepository, TurmaRepository turmaRepository,
      DiarioRepository diarioRepository) {
    this.aulaRepository = aulaRepository;
    this.turmaRepository = turmaRepository;
    this.diarioRepository = diarioRepository;
  }

  /**
   * Gera uma aula para a turma informada.
   *
   * @param turma turma da aula.
   * @return aula gerada.
   */
  public Aula gerarAula(Turma turma) {
    return new Aula(
        gerarCodigoAula(),
        turma.getCodigo(),
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        turma.getHorario());
  }

  /**
   * Gera uma aula vinculada ao diario informado.
   *
   * @param diario diario da aula.
   * @param presencas presencas registradas.
   * @return aula gerada.
   */
  public Aula gerarAula(Diario diario, Map<String, Boolean> presencas) {
    if (diario == null) {
      throw new EntradaInvalidaException("Diario da aula nao pode ser nulo.");
    }
    if (diario.getCodigo() == null || diario.getCodigo().isBlank()) {
      throw new EntradaInvalidaException("Codigo do diario da aula nao pode ser vazio.");
    }

    return new Aula(
        gerarCodigoAula(),
        diario.getCodigoTurma(),
        diario.getCodigo(),
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        diario.getHorario(),
        presencas);
  }

  /**
   * Salva uma aula.
   *
   * @param aula aula a ser salva.
   */
  public void salvarAula(Aula aula) {
    validarDadosDaAula(aula);
    if (aula.getCodigoDiario() != null) {
      throw new EntradaInvalidaException(
          "Professor responsavel deve ser informado para aula vinculada a diario.");
    }
    validarDiarioNaoFechado(aula.getCodigoTurma());
    aulaRepository.salvarAula(aula);
  }

  /**
   * Salva uma aula registrada por um professor, validando que a turma da aula pertence a esse
   * professor.
   *
   * @param aula aula a ser salva.
   * @param matriculaProfessor matricula do professor que esta registrando a aula.
   */
  public void salvarAula(Aula aula, String matriculaProfessor) {
    validarDadosDaAula(aula);

    if (aula.getCodigoDiario() == null) {
      validarDiarioNaoFechado(aula.getCodigoTurma());
      validarProfessorResponsavelPelaTurma(aula.getCodigoTurma(), matriculaProfessor);
    } else {
      validarAulaVinculadaAoDiario(aula, matriculaProfessor);
    }

    aulaRepository.salvarAula(aula);
  }

  /**
   * Lista somente as aulas vinculadas ao diario informado.
   *
   * @param codigoDiario codigo do diario.
   * @return aulas do diario.
   */
  public List<Aula> listarAulasPorDiario(String codigoDiario) {
    if (codigoDiario == null || codigoDiario.isBlank()) {
      throw new EntradaInvalidaException("Codigo do diario nao pode ser vazio.");
    }

    Diario diario = diarioRepository.buscarDiarioPorCodigo(codigoDiario);
    if (diario == null) {
      throw new EntradaInvalidaException("Diario nao encontrado.");
    }

    if (!diarioValidoParaFrequencia(diario)) {
      return new ArrayList<>();
    }

    return aulaRepository.buscarAulasPorDiario(codigoDiario);
  }

  /**
   * Lista as aulas dos diarios validos associados a uma turma.
   *
   * @param codigoTurma codigo da turma.
   * @return aulas vinculadas a diarios ativos ou encerrados.
   */
  public List<Aula> listarAulasValidasPorTurma(String codigoTurma) {
    if (codigoTurma == null || codigoTurma.isBlank()) {
      throw new EntradaInvalidaException("Codigo da turma nao pode ser vazio.");
    }

    List<Aula> aulas = new ArrayList<>();
    for (Diario diario : diarioRepository.buscarDiariosPorTurma(codigoTurma)) {
      if (diarioValidoParaFrequencia(diario)) {
        aulas.addAll(aulaRepository.buscarAulasPorDiario(diario.getCodigo()));
      }
    }
    return aulas;
  }

  /**
   * Calcula as horas ministradas considerando duas horas-aula por registro.
   *
   * @param aulas aulas consideradas.
   * @return total de horas ministradas.
   */
  public int calcularHorasMinistradas(List<Aula> aulas) {
    return aulas == null ? 0 : aulas.size() * HORAS_POR_AULA;
  }

  /**
   * Calcula as faltas-hora de um aluno.
   *
   * @param matriculaAluno matricula do aluno.
   * @param aulas aulas consideradas.
   * @return total de faltas-hora.
   */
  public int calcularFaltasHora(String matriculaAluno, List<Aula> aulas) {
    if (matriculaAluno == null || matriculaAluno.isBlank()) {
      throw new EntradaInvalidaException("Matricula do aluno nao pode ser vazia.");
    }

    int faltasHora = 0;
    if (aulas == null) {
      return faltasHora;
    }
    for (Aula aula : aulas) {
      Map<String, Boolean> presencas = aula.getPresencas();
      if (presencas == null || !Boolean.TRUE.equals(presencas.get(matriculaAluno))) {
        faltasHora += HORAS_POR_AULA;
      }
    }
    return faltasHora;
  }

  /**
   * Calcula a frequencia de um aluno em horas-aula.
   *
   * @param matriculaAluno matricula do aluno.
   * @param aulas aulas consideradas.
   * @return percentual de frequencia, ou {@code null} quando nao ha aulas.
   */
  public Double calcularFrequencia(String matriculaAluno, List<Aula> aulas) {
    int horasMinistradas = calcularHorasMinistradas(aulas);
    if (horasMinistradas == 0) {
      return null;
    }
    int faltasHora = calcularFaltasHora(matriculaAluno, aulas);
    return (horasMinistradas - faltasHora) * 100.0 / horasMinistradas;
  }

  private boolean diarioValidoParaFrequencia(Diario diario) {
    return diario != null
        && (diario.getSituacao() == SituacaoDiario.ATIVO
            || diario.getSituacao() == SituacaoDiario.ENCERRADO);
  }

  private void validarDadosDaAula(Aula aula) {
    // VALIDAÇÕES...
    if (aula == null) {
      throw new EntradaInvalidaException("A aula não pode ser nula.");
    }

    if (aula.getCodigoTurma() == null || aula.getCodigoTurma().isBlank()) {
      throw new EntradaInvalidaException("Código da turma inválido.");
    }

    if (aula.getData() == null) {
      throw new EntradaInvalidaException("Data da aula inválida.");
    }

    if (aula.getHorario() == null || aula.getHorario().isBlank()) {
      throw new EntradaInvalidaException("Horário inválido.");
    }

    if (aula.getPresencas() == null || aula.getPresencas().isEmpty()) {
      throw new EntradaInvalidaException("A aula deve possuir registros de frequência.");
    }
  }

  private void validarAulaVinculadaAoDiario(Aula aula, String matriculaProfessor) {
    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      throw new EntradaInvalidaException("Matricula do professor logado nao pode ser vazia.");
    }

    Diario diario = diarioRepository.buscarDiarioPorCodigo(aula.getCodigoDiario());

    if (diario == null) {
      throw new EntradaInvalidaException("Diario da aula nao encontrado.");
    }

    if (diario.getMatriculaProfessor() == null
        || !diario.getMatriculaProfessor().equalsIgnoreCase(matriculaProfessor.trim())) {
      throw new EntradaInvalidaException(
          "Professor nao pode registrar aula em diario de outro professor.");
    }

    if (diario.getSituacao() != SituacaoDiario.ATIVO) {
      throw new EntradaInvalidaException(
          "Nao e possivel registrar aula em diario encerrado ou cancelado.");
    }

    validarLimiteCargaHoraria(diario);

    Turma turma = turmaRepository.buscarTurmaPorCodigo(diario.getCodigoTurma());

    if (turma == null) {
      throw new EntradaInvalidaException("Turma associada ao diario nao encontrada.");
    }

    if (!turma.getCodigo().equalsIgnoreCase(aula.getCodigoTurma().trim())) {
      throw new EntradaInvalidaException("Aula nao pertence a turma associada ao diario.");
    }
  }

  private void validarLimiteCargaHoraria(Diario diario) {
    int quantidadeAulas = aulaRepository.buscarAulasPorDiario(diario.getCodigo()).size();
    int horasAposNovoRegistro = (quantidadeAulas + 1) * HORAS_POR_AULA;

    if (horasAposNovoRegistro > diario.getCargaHoraria()) {
      throw new EntradaInvalidaException(
          "Nao e possivel registrar aula alem da carga horaria do diario.");
    }
  }

  /**
   * Valida se a turma da aula pertence ao professor que esta tentando registra-la.
   *
   * @param codigoTurma codigo da turma da aula.
   * @param matriculaProfessor matricula do professor logado.
   */
  private void validarProfessorResponsavelPelaTurma(
      String codigoTurma, String matriculaProfessor) {
    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      throw new EntradaInvalidaException("Matrícula do professor logado não pode ser vazia.");
    }

    Turma turma = turmaRepository.buscarTurmaPorCodigo(codigoTurma);

    if (turma == null) {
      throw new EntradaInvalidaException("Turma não encontrada.");
    }

    if (turma.getMatriculaProfessor() == null
        || !turma.getMatriculaProfessor().equalsIgnoreCase(matriculaProfessor.trim())) {
      throw new EntradaInvalidaException(
          "Professor não pode registrar aula em turma de outro professor.");
    }
  }

  /**
   * Valida se a turma possui um diario fechado (encerrado ou cancelado) que impeça o registro
   * de novas aulas. Turmas sem nenhum diario cadastrado nao sao bloqueadas.
   *
   * @param codigoTurma codigo da turma da aula.
   */
  private void validarDiarioNaoFechado(String codigoTurma) {
    List<Diario> diariosDaTurma = diarioRepository.buscarDiariosPorTurma(codigoTurma);

    if (diariosDaTurma.isEmpty()) {
      return;
    }

    boolean possuiDiarioAtivo = false;

    for (Diario diario : diariosDaTurma) {
      if (diario.getSituacao() == SituacaoDiario.ATIVO) {
        possuiDiarioAtivo = true;
        break;
      }
    }

    if (!possuiDiarioAtivo) {
      throw new EntradaInvalidaException(
          "Não é possível registrar aula: o diário desta turma está fechado.");
    }
  }

  private String gerarCodigoAula() {
    int contador = aulaRepository.listarAulas().size();
    String id;

    do {
      id = "aul" + String.format("%02d", contador);
      contador++;
    } while (aulaRepository.buscarAulaPorId(id) != null);

    return id;
  }
}
