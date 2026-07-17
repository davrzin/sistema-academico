package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.ExistePeriodoAtivoException;
import br.com.classroompb.model.exception.PeriodoLetivoExistenteException;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.List;

/**
 * Servico responsavel pelas operacoes de periodo letivo.
 */
public class PeriodoLetivoService {

  private static final Path DIRETORIO_PERIODOS = PersistenciaPaths.PERIODOS;
  private final PeriodoLetivoRepository repository;

  /**
   * Cria o servico de periodos letivos com repositorio padrao.
   */
  public PeriodoLetivoService() {
    this.repository =
        new PeriodoLetivoRepository(new ObjectMapper(), DIRETORIO_PERIODOS.toString());
  }

  /**
   * Cria o servico de periodos letivos com repositorio informado.
   *
   * @param periodoLetivoRepository repositorio de periodos letivos.
   */
  public PeriodoLetivoService(PeriodoLetivoRepository periodoLetivoRepository) {
    this.repository = periodoLetivoRepository;
  }

  /**
   * Cadastra um periodo letivo.
   *
   * @param periodoLetivo periodo letivo a ser cadastrado.
   */
  public void cadastrarPeriodoLetivo(PeriodoLetivo periodoLetivo) {
    validarPeriodoLetivo(periodoLetivo);
    validarExistenciaPeriodoLetivo(periodoLetivo);

    repository.salvarPeriodoLetivo(periodoLetivo);
  }

  private void validarPeriodoLetivo(PeriodoLetivo periodoLetivo) {
    if (periodoLetivo == null) {
      throw new EntradaInvalidaException("Período letivo não pode ser null.");
    }

    periodoLetivo.validarDadosBasicos();
  }

  private void validarExistenciaPeriodoLetivo(PeriodoLetivo periodoLetivo) {
    boolean periodoJaExiste =
        repository.existePeriodoComDados(
            periodoLetivo.getPeriodo(), periodoLetivo.getDataInicio(), periodoLetivo.getDataFim());

    if (periodoJaExiste) {
      throw new PeriodoLetivoExistenteException();
    }
  }

  /**
   * Lista todos os periodos letivos cadastrados.
   *
   * @return lista de periodos letivos.
   */
  public List<PeriodoLetivo> listarPeriodosLetivos() {
    return repository.listarPeriodos();
  }

  /**
   * Ativa um periodo letivo.
   *
   * @param periodo periodo letivo a ser ativado.
   * @param indicePeriodoEscolhido indice do periodo escolhido.
   * @return verdadeiro se o periodo foi ativado.
   */
  public boolean ativarPeriodoLetivo(PeriodoLetivo periodo, int indicePeriodoEscolhido) {
    if (periodo == null) {
      throw new EntradaInvalidaException("Período letivo não pode ser null.");
    }

    if (periodo.getPeriodoEncerrado()) {
      throw new EntradaInvalidaException(
          "Não é possível ativar um período letivo já encerrado.");
    }

    if (!repository.existePeriodoLetivoAtivo()) {
      periodo.setPeriodoAtivo(true);
      return repository.updatePeriodoLetivo(periodo, indicePeriodoEscolhido);
    }

    throw new ExistePeriodoAtivoException();
  }

  /**
   * Desativa um periodo letivo.
   *
   * @param periodo periodo letivo a ser desativado.
   * @param indicePeriodoEscolhido indice do periodo escolhido.
   * @return verdadeiro se o periodo foi desativado.
   */
  public boolean desativarPeriodoLetivo(PeriodoLetivo periodo, int indicePeriodoEscolhido) {
    if (periodo == null) {
      throw new EntradaInvalidaException("Período letivo não pode ser null.");
    }

    if (periodo.getPeriodoEncerrado()) {
      throw new EntradaInvalidaException("O período letivo já está encerrado.");
    }

    periodo.setPeriodoAtivo(false);
    periodo.setPeriodoEncerrado(true);
    return repository.updatePeriodoLetivo(periodo, indicePeriodoEscolhido);
  }
}
