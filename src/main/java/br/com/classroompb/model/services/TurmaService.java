package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;

public class TurmaService {

    private static final Path DIRETORIO_TURMAS = Path.of(System.getProperty("user.dir"), "turmas");
    private static final Path DIRETORIO_DISCIPLINAS = Path.of(System.getProperty("user.dir"), "disciplinas");
    private static final Path DIRETORIO_PERIODOS = Path.of(System.getProperty("user.dir"), "periodos");
    private static final Path DIRETORIO_USUARIOS = Path.of(System.getProperty("user.dir"), "usuarios");

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final PeriodoLetivoRepository periodoLetivoRepository;
    private final UserRepository userRepository;

    public TurmaService() {
        this.turmaRepository = new TurmaRepository(new ObjectMapper(), DIRETORIO_TURMAS.toString());
        this.disciplinaRepository = new DisciplinaRepository(new ObjectMapper(), DIRETORIO_DISCIPLINAS.toString());
        this.periodoLetivoRepository = new PeriodoLetivoRepository(new ObjectMapper(), DIRETORIO_PERIODOS.toString());
        this.userRepository = new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString());
    }

    public TurmaService(
            TurmaRepository turmaRepository,
            DisciplinaRepository disciplinaRepository,
            PeriodoLetivoRepository periodoLetivoRepository,
            UserRepository userRepository
    ) {
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.periodoLetivoRepository = periodoLetivoRepository;
        this.userRepository = userRepository;
    }

    public void ofertarTurma(Turma turma) {
        validarTurma(turma);
        validarDisciplinaExistente(turma.getCodigoDisciplina());
        validarPeriodoLetivoExistente(turma.getPeriodoLetivo());
        validarProfessorResponsavel(turma.getMatriculaProfessor());
        validarConflitoHorarioProfessor(turma);

        turma.setCodigo(gerarCodigoTurma());
        turmaRepository.salvarTurma(turma);
    }

    public List<Turma> listarTurmas() {
        return turmaRepository.listarTurmas();
    }

    private void validarTurma(Turma turma) {
        if (turma == null) {
            throw new EntradaInvalidaException("Turma não pode ser null.");
        }

        turma.validarDadosBasicos();
    }

    private void validarDisciplinaExistente(String codigoDisciplina) {
        Disciplina disciplina = disciplinaRepository.buscarPorCodigo(codigoDisciplina);

        if (disciplina == null) {
            throw new EntradaInvalidaException("Disciplina não encontrada.");
        }
    }

    private void validarPeriodoLetivoExistente(String periodoLetivo) {
        for (PeriodoLetivo periodo : periodoLetivoRepository.listarPeriodos()) {
            if (periodo.getPeriodo() != null && periodo.getPeriodo().equalsIgnoreCase(periodoLetivo.trim())) {
                return;
            }
        }

        throw new EntradaInvalidaException("Período letivo não encontrado.");
    }

    private void validarProfessorResponsavel(String matriculaProfessor) {
        if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
            throw new EntradaInvalidaException("Turma deve possuir professor responsável.");
        }

        try {
            userRepository.buscarPorMatricula(matriculaProfessor, TipoUsuario.PROFESSOR);
        } catch (UsuarioNaoEncontradoException e) {
            throw new EntradaInvalidaException("Professor responsável não encontrado.");
        }
    }

    private void validarConflitoHorarioProfessor(Turma novaTurma) {
        List<Turma> turmasDoProfessor = turmaRepository.buscarPorProfessor(novaTurma.getMatriculaProfessor());

        for (Turma turmaCadastrada : turmasDoProfessor) {
            boolean mesmoPeriodo = turmaCadastrada.getPeriodoLetivo() != null
                    && turmaCadastrada.getPeriodoLetivo().equalsIgnoreCase(novaTurma.getPeriodoLetivo());
            boolean mesmoHorario = turmaCadastrada.getHorario() != null
                    && turmaCadastrada.getHorario().equalsIgnoreCase(novaTurma.getHorario());

            if (mesmoPeriodo && mesmoHorario) {
                throw new EntradaInvalidaException("Professor já possui turma nesse horário.");
            }
        }
    }

    private String gerarCodigoTurma() {
        int contador = turmaRepository.listarTurmas().size();
        String codigo;

        do {
            codigo = "tur" + String.format("%02d", contador);
            contador++;
        } while (turmaRepository.buscarPorCodigo(codigo) != null);

        return codigo;
    }
}
