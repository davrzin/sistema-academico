package br.com.classroompb.model.services;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.ExistePeriodoAtivoException;
import br.com.classroompb.model.exception.PeriodoLetivoExistenteException;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;

public class PeriodoLetivoService {

    private static final Path DIRETORIO_PERIODOS = Path.of(System.getProperty("user.dir"), "periodos");
    private final PeriodoLetivoRepository repository;

    public PeriodoLetivoService() {
        this.repository = new PeriodoLetivoRepository(new ObjectMapper(), DIRETORIO_PERIODOS.toString());
    }

    public PeriodoLetivoService(PeriodoLetivoRepository periodoLetivoRepository) {
        this.repository = periodoLetivoRepository;
    }

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
        boolean periodoJaExiste = repository.existePeriodoComDados(
                periodoLetivo.getPeriodo(),
                periodoLetivo.getDataInicio(),
                periodoLetivo.getDataFim()
        );

        if (periodoJaExiste) {
            throw new PeriodoLetivoExistenteException();
        }
    }

    public List<PeriodoLetivo> listarPeriodosLetivos() {
        return repository.listarPeriodos();
    }

    public boolean ativarPeriodoLetivo(PeriodoLetivo periodo, int indicePeriodoEscolhido) {
        if (periodo == null) {
            throw new EntradaInvalidaException("Período letivo não pode ser null.");
        }

        if (!repository.existePeriodoLetivoAtivo()) {
            periodo.setPeriodoAtivo(true);
            return repository.updatePeriodoLetivo(periodo, indicePeriodoEscolhido);
        }

        throw new ExistePeriodoAtivoException();
    }

    public boolean desativarPeriodoLetivo(PeriodoLetivo periodo, int indicePeriodoEscolhido) {
        if (periodo == null) {
            throw new EntradaInvalidaException("Período letivo não pode ser null.");
        }

        periodo.setPeriodoAtivo(false);
        return repository.updatePeriodoLetivo(periodo, indicePeriodoEscolhido);
    }
}
