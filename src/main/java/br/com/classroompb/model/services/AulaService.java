package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import br.com.classroompb.model.repository.TurmaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Servico responsavel pelas operacoes de aula.
 */
public class AulaService {
  private static final Path DIRETORIO_AULAS = PersistenciaPaths.AULAS;

  private final AulaRepository aulaRepository;

  /**
   * Cria o servico de aulas com dependencias padrao.
   */
  public AulaService() {
    this.aulaRepository = new AulaRepository(new ObjectMapper(), DIRETORIO_AULAS.toString());
  }

  /**
   * Cria o servico de aulas com dependencias informadas.
   *
   * @param aulaRepository repositorio de aulas.
   * @param turmaRepository repositorio de turmas.
   */
  public AulaService(AulaRepository aulaRepository, TurmaRepository turmaRepository) {
    this.aulaRepository = aulaRepository;
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

    // -------------

    aulaRepository.salvarAula(aula);
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
