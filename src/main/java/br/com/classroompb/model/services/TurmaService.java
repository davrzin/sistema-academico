package br.com.classroompb.model.services;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.classroompb.model.exception.TurmaNaoEncontradaException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.Disciplina;
import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.AlunoNaoCumprePreRequisitosException;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;

public class TurmaService {

    private static final Path DIRETORIO_TURMAS = Path.of(System.getProperty("user.dir"), "turmas");
    private static final Path DIRETORIO_DISCIPLINAS = Path.of(System.getProperty("user.dir"), "disciplinas");
    private static final Path DIRETORIO_PERIODOS = Path.of(System.getProperty("user.dir"), "periodos");
    private static final Path DIRETORIO_USUARIOS = Path.of(System.getProperty("user.dir"), "usuarios");

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final PeriodoLetivoRepository periodoLetivoRepository;
    private final UserRepository userRepository;

    private UsuarioService usuarioService;

    public TurmaService() {
        this.turmaRepository = new TurmaRepository(new ObjectMapper(), DIRETORIO_TURMAS.toString());
        this.disciplinaRepository = new DisciplinaRepository(new ObjectMapper(), DIRETORIO_DISCIPLINAS.toString());
        this.periodoLetivoRepository = new PeriodoLetivoRepository(new ObjectMapper(), DIRETORIO_PERIODOS.toString());
        this.userRepository = new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString());
    }

    public TurmaService( TurmaRepository turmaRepository, DisciplinaRepository disciplinaRepository, PeriodoLetivoRepository periodoLetivoRepository, UserRepository userRepository) {
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
        validarConflitoHorarioProfessor(turma, null);

        turma.setCodigo(gerarCodigoTurma());
        turmaRepository.salvarTurma(turma);
    }

    public void alterarTurma(String codigo, Turma turmaAtualizada) {
        validarCodigoTurma(codigo);

        Turma turmaCadastrada = turmaRepository.buscarPorCodigo(codigo);

        if (turmaCadastrada == null) {
            throw new TurmaNaoEncontradaException();
        }

        validarTurma(turmaAtualizada);
        validarDisciplinaExistente(turmaAtualizada.getCodigoDisciplina());
        validarPeriodoLetivoExistente(turmaAtualizada.getPeriodoLetivo());
        validarProfessorResponsavel(turmaAtualizada.getMatriculaProfessor());

        turmaAtualizada.setCodigo(turmaCadastrada.getCodigo());
        validarConflitoHorarioProfessor(turmaAtualizada, turmaCadastrada.getCodigo());

        boolean atualizou = turmaRepository.atualizarTurma(turmaAtualizada);

        if (!atualizou) {
            throw new EntradaInvalidaException("Não foi possível alterar a turma.");
        }
    }

    public void cancelarTurma(String codigo) {
        validarCodigoTurma(codigo);

        Turma turma = turmaRepository.buscarPorCodigo(codigo);

        if (turma == null) {
            throw new EntradaInvalidaException("Turma não encontrada.");
        }

        boolean removeu = turmaRepository.removerTurmaPorCodigo(codigo);

        if (!removeu) {
            throw new EntradaInvalidaException("Não foi possível cancelar a turma.");
        }
    }

    public Turma buscarTurmaPorCodigo(String codigo) {
        validarCodigoTurma(codigo);

        Turma turma = turmaRepository.buscarPorCodigo(codigo);

        if (turma == null) {
            throw new TurmaNaoEncontradaException();
        }
        return turma;
    }

    public List<Turma> listarTurmas() {
        return turmaRepository.listarTurmas();
    }

    public List<Turma> listarTurmasPorProfessor(String matriculaProfessor) {
        if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
            throw new EntradaInvalidaException("Matrícula do professor não pode ser vazia.");
        }

        return turmaRepository.buscarPorProfessor(matriculaProfessor);
    }

    public List<Turma> listarTurmasPorPeriodoLetivo(String periodoLetivo) {
        if (periodoLetivo == null || periodoLetivo.isBlank()) {
            throw new EntradaInvalidaException("Período letivo não pode ser vazio.");
        }

        return turmaRepository.buscarPorPeriodoLetivo(periodoLetivo);
    }

    public void cadastrarAlunoEmTurma(String codigoTurma, Aluno alunoLogado){
        Turma turma = buscarTurmaPorCodigo(codigoTurma);

        validarEntradaAlunoEmTurma(alunoLogado, turma);

        if(validarDisponibilidadeDeTurma(turma)){
            //SUJEIRO A MUDANÇA
            adicionarAlunoTurma(alunoLogado, turma);
        }else{
            adicionarAlunoListaEspera(alunoLogado, turma);
        }
    }

    private void adicionarAlunoListaEspera(Aluno alunoLogado, Turma turma){
        validarAlunoNaListaEspera(alunoLogado, turma);

        turma.getListaEspera().add(alunoLogado.getMatricula());
        turmaRepository.atualizarTurma(turma);
    }

    private void adicionarAlunoTurma(Aluno alunoLogado, Turma turma){
        String matriculaAluno = alunoLogado.getMatricula();
        turma.getMatriculados().add(alunoLogado.getMatricula());
        alunoLogado.getTurmasMatriculadas().add(turma.getCodigo());
        turmaRepository.atualizarTurma(turma);
        userRepository.atualizarUsuario(alunoLogado);
    }

    private void validarAlunoNaListaEspera(Aluno alunoLogado, Turma turma){
        if(turma.getListaEspera().contains(alunoLogado.getMatricula())){
            throw new EntradaInvalidaException(
                "Aluno já está na lista de espera."
            );
        }
    }

    private void validarTurma(Turma turma) {
        if (turma == null) {
            throw new EntradaInvalidaException("Turma não pode ser null.");
        }

        turma.validarDadosBasicos();
    }

    private void validarCodigoTurma(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new EntradaInvalidaException("Código da turma não pode ser vazio.");
        }
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

    private void validarConflitoHorarioProfessor(Turma novaTurma, String codigoTurmaIgnorada) {
        List<Turma> turmasDoProfessor = turmaRepository.buscarPorProfessor(novaTurma.getMatriculaProfessor());

        for (Turma turmaCadastrada : turmasDoProfessor) {
            boolean mesmaTurma = codigoTurmaIgnorada != null
                    && turmaCadastrada.getCodigo() != null
                    && turmaCadastrada.getCodigo().equalsIgnoreCase(codigoTurmaIgnorada);

            if (mesmaTurma) {
                continue;
            }

            boolean mesmoPeriodo = turmaCadastrada.getPeriodoLetivo() != null
                    && turmaCadastrada.getPeriodoLetivo().equalsIgnoreCase(novaTurma.getPeriodoLetivo());
            boolean mesmoHorario = turmaCadastrada.getHorario() != null
                    && turmaCadastrada.getHorario().equalsIgnoreCase(novaTurma.getHorario());

            if (mesmoPeriodo && mesmoHorario) {
                throw new EntradaInvalidaException("Professor já possui turma nesse horário.");
            }
        }
    }

    private boolean validarDisponibilidadeDeTurma(Turma turma) {
        return turma.getLimiteVagas() > turma.getMatriculados().size();
    }

        private void validarEntradaAlunoEmTurma(Aluno aluno, Turma turma) {
        Set<String> codigosDisciplinasConcluidas = new HashSet<>(aluno.getDisciplinasConcluidas());

        Disciplina disciplina = disciplinaRepository.buscarPorCodigo(turma.getCodigoDisciplina());

        validarDisciplinasConcluidas(codigosDisciplinasConcluidas, disciplina);
        validarAlunoJaAprovadoNaDisciplina(codigosDisciplinasConcluidas, disciplina);
        validarAlunoJaMatriculado(aluno, turma);
        validarHorariosDeTurma(aluno, turma);
    }

    private void validarDisciplinasConcluidas(Set<String> codigoDisciplinasConcluidas, Disciplina disciplina){

        List<String> disciplinasPreRequisito = disciplina.getPreRequisitos();

        boolean todasDisciplinasForamConcluidas = new HashSet<>(codigoDisciplinasConcluidas.stream().toList()).containsAll(disciplinasPreRequisito);

        if(!todasDisciplinasForamConcluidas){
            throw new AlunoNaoCumprePreRequisitosException("Aluno não possui todos os pré-requisitos para esta disciplina.");
        }
    }

    private void validarHorariosDeTurma(Aluno aluno, Turma turma) {
        for (String codigoTurmaAluno : aluno.getTurmasMatriculadas()) {
            Turma turmaAluno = turmaRepository.buscarPorCodigo(codigoTurmaAluno);

            if (turmaAluno != null && turmaAluno.getHorario().equalsIgnoreCase(turma.getHorario())) {
                throw new AlunoNaoCumprePreRequisitosException("Turmas com choque de horário.");
            }
        }
    }

    private void validarAlunoJaMatriculado(Aluno aluno, Turma turma) {
        for (String turmaAluno : aluno.getTurmasMatriculadas()) {
            if (turmaAluno.equals(turma.getCodigo())) {
                throw new AlunoNaoCumprePreRequisitosException("O aluno já está matriculado nessa turma.");
            }
        }
    }

    private void validarAlunoJaAprovadoNaDisciplina(Set<String> codigosDisciplinasConcluidas, Disciplina disciplina) {
        if (codigosDisciplinasConcluidas.contains(disciplina.getCodigo())) {
            throw new EntradaInvalidaException(
                "Aluno já foi aprovado na disciplina '" + disciplina.getNome() + "' e não pode se matricular novamente."
            );
        }
    }

    public String cancelarAlunoTurma(String codTurma, Aluno alunoLogado){
        String msgResultado = "";

        validarCodigoTurma(codTurma);

        Turma turma = buscarTurmaPorCodigo(codTurma);
        String matriculaAluno = alunoLogado.getMatricula();

        validarAlunoMatriculado(turma, matriculaAluno);

        turma.getMatriculados().remove(matriculaAluno);
        alunoLogado.getTurmasMatriculadas().remove(codTurma);

        if(!turma.getListaEspera().isEmpty()){
            String codAlunoListaEspera = turma.getListaEspera().get(0);
            turma.getMatriculados().add(codAlunoListaEspera);

            turma.getListaEspera().remove(0);

            Aluno alunoListaEspera = usuarioService.buscarAlunoPorMatricula(codAlunoListaEspera);
            alunoListaEspera.getTurmasMatriculadas().add(codTurma);

            userRepository.atualizarUsuario(alunoListaEspera);

            msgResultado = "\n O aluno com matrícula: " + codAlunoListaEspera + " foi promovido da lista de espera para a turma.";
        }

        turmaRepository.atualizarTurma(turma);
        userRepository.atualizarUsuario(alunoLogado);

        return msgResultado;
    }

    private void validarAlunoMatriculado(Turma turma, String matriculaAluno){
        if(!turma.getMatriculados().contains(matriculaAluno)){
            throw new EntradaInvalidaException("Aluno não está matriculado na turma.");
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
