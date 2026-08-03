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
import java.util.List;

/**
 * Servico responsavel pelas operacoes de aula.
 */
public class AulaService {
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
   * Salva uma aula.
   *
   * @param aula aula a ser salva.
   */
  public void salvarAula(Aula aula) {
    validarDadosDaAula(aula);
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
    validarProfessorResponsavelPelaTurma(aula.getCodigoTurma(), matriculaProfessor);
    aulaRepository.salvarAula(aula);
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

    validarDiarioNaoFechado(aula.getCodigoTurma());

    // -------------
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
