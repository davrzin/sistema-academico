package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Aula;
import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.AlunoNaoCumprePreRequisitosException;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.TurmaNaoEncontradaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.BoletimRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PeriodoLetivoRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servico responsavel pelas operacoes de turma.
 */
public class TurmaService {

  private static final Path DIRETORIO_TURMAS = PersistenciaPaths.TURMAS;
  private static final Path DIRETORIO_DISCIPLINAS = PersistenciaPaths.DISCIPLINAS;
  private static final Path DIRETORIO_PERIODOS = PersistenciaPaths.PERIODOS;
  private static final Path DIRETORIO_USUARIOS = PersistenciaPaths.USUARIOS;
  private static final Path DIRETORIO_BOLETINS = PersistenciaPaths.BOLETINS;
  private static final Path DIRETORIO_AULAS = PersistenciaPaths.AULAS;

  private final TurmaRepository turmaRepository;
  private final DisciplinaRepository disciplinaRepository;
  private final PeriodoLetivoRepository periodoLetivoRepository;
  private final UserRepository userRepository;
  private final BoletimRepository boletimRepository;
  private final AulaRepository aulaRepository;
  private final BoletimService boletimService;

  private UsuarioService usuarioService;

  /**
   * Cria o servico de turmas com dependencias principais.
   *
   * @param turmaRepository repositorio de turmas.
   * @param disciplinaRepository repositorio de disciplinas.
   * @param periodoLetivoRepository repositorio de periodos letivos.
   * @param userRepository repositorio de usuarios.
   */
  public TurmaService(
      TurmaRepository turmaRepository,
      DisciplinaRepository disciplinaRepository,
      PeriodoLetivoRepository periodoLetivoRepository,
      UserRepository userRepository) {
    this.turmaRepository = turmaRepository;
    this.disciplinaRepository = disciplinaRepository;
    this.periodoLetivoRepository = periodoLetivoRepository;
    this.userRepository = userRepository;
    this.boletimRepository =
        new BoletimRepository(new ObjectMapper(), DIRETORIO_BOLETINS.toString());
    this.aulaRepository = new AulaRepository(new ObjectMapper(), DIRETORIO_AULAS.toString());
    this.boletimService =
        new BoletimService(
            this.boletimRepository, this.turmaRepository, this.periodoLetivoRepository);
    this.usuarioService = new UsuarioService(userRepository);
  }

  /**
   * Cria o servico de turmas com dependencias padrao.
   */
  public TurmaService() {
    this.turmaRepository = new TurmaRepository(new ObjectMapper(), DIRETORIO_TURMAS.toString());
    this.disciplinaRepository =
        new DisciplinaRepository(new ObjectMapper(), DIRETORIO_DISCIPLINAS.toString());
    this.periodoLetivoRepository =
        new PeriodoLetivoRepository(new ObjectMapper(), DIRETORIO_PERIODOS.toString());
    this.userRepository = new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString());
    this.boletimRepository =
        new BoletimRepository(new ObjectMapper(), DIRETORIO_BOLETINS.toString());
    this.aulaRepository = new AulaRepository(new ObjectMapper(), DIRETORIO_AULAS.toString());
    this.boletimService =
        new BoletimService(
            this.boletimRepository, this.turmaRepository, this.periodoLetivoRepository);
    this.usuarioService = new UsuarioService(this.userRepository);
  }

  /**
   * Cria o servico de turmas com todas as dependencias informadas.
   *
   * @param turmaRepository repositorio de turmas.
   * @param disciplinaRepository repositorio de disciplinas.
   * @param periodoLetivoRepository repositorio de periodos letivos.
   * @param userRepository repositorio de usuarios.
   * @param boletimRepository repositorio de boletins.
   * @param aulaRepository repositorio de aulas.
   */
  public TurmaService(
      TurmaRepository turmaRepository,
      DisciplinaRepository disciplinaRepository,
      PeriodoLetivoRepository periodoLetivoRepository,
      UserRepository userRepository,
      BoletimRepository boletimRepository,
      AulaRepository aulaRepository) {
    this.turmaRepository = turmaRepository;
    this.disciplinaRepository = disciplinaRepository;
    this.periodoLetivoRepository = periodoLetivoRepository;
    this.userRepository = userRepository;
    this.boletimRepository = boletimRepository;
    this.aulaRepository = aulaRepository;
    this.boletimService =
        new BoletimService(
            this.boletimRepository, this.turmaRepository, this.periodoLetivoRepository);
    this.usuarioService = new UsuarioService(userRepository);
  }

  /**
   * Oferta uma nova turma.
   *
   * @param turma turma a ser ofertada.
   */
  public void ofertarTurma(Turma turma) {
    validarTurma(turma);
    validarDisciplinaExistente(turma.getCodigoDisciplina());
    validarPeriodoLetivoExistente(turma.getPeriodoLetivo());
    validarProfessorResponsavel(turma.getMatriculaProfessor());
    validarConflitoHorarioProfessor(turma, null);

    turma.setCodigo(gerarCodigoTurma());
    turmaRepository.salvarTurma(turma);
  }

  /**
   * Oferta uma nova turma para o curso do coordenador.
   *
   * @param turma turma a ser ofertada.
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   */
  public void ofertarTurma(Turma turma, String codigoCursoCoordenador) {
    validarCodigoCursoCoordenador(codigoCursoCoordenador);
    validarTurma(turma);
    validarDisciplinaDoCurso(turma.getCodigoDisciplina(), codigoCursoCoordenador);
    validarPeriodoLetivoExistente(turma.getPeriodoLetivo());
    validarProfessorDoCurso(turma.getMatriculaProfessor(), codigoCursoCoordenador);
    validarConflitoHorarioProfessor(turma, null);

    turma.setCodigo(gerarCodigoTurma());
    turmaRepository.salvarTurma(turma);
  }

  /**
   * Altera uma turma cadastrada.
   *
   * @param codigo codigo da turma.
   * @param turmaAtualizada turma com dados atualizados.
   */
  public void alterarTurma(String codigo, Turma turmaAtualizada) {
    validarCodigoTurma(codigo);

    Turma turmaCadastrada = turmaRepository.buscarTurmaPorCodigo(codigo);

    if (turmaCadastrada == null) {
      throw new TurmaNaoEncontradaException();
    }

    validarTurma(turmaAtualizada);
    validarDisciplinaExistente(turmaAtualizada.getCodigoDisciplina());
    validarPeriodoLetivoExistente(turmaAtualizada.getPeriodoLetivo());
    validarProfessorResponsavel(turmaAtualizada.getMatriculaProfessor());

    turmaAtualizada.setCodigo(turmaCadastrada.getCodigo());
    preservarDadosAcademicosTurma(turmaCadastrada, turmaAtualizada);
    validarConflitoHorarioProfessor(turmaAtualizada, turmaCadastrada.getCodigo());

    boolean atualizou = turmaRepository.atualizarTurma(turmaAtualizada);

    if (!atualizou) {
      throw new EntradaInvalidaException("Não foi possível alterar a turma.");
    }
  }

  /**
   * Altera uma turma do curso do coordenador.
   *
   * @param codigo codigo da turma.
   * @param turmaAtualizada turma com dados atualizados.
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   */
  public void alterarTurma(String codigo, Turma turmaAtualizada, String codigoCursoCoordenador) {
    validarTurmaPertenceAoCurso(codigo, codigoCursoCoordenador);
    validarCodigoCursoCoordenador(codigoCursoCoordenador);
    validarTurma(turmaAtualizada);
    validarDisciplinaDoCurso(turmaAtualizada.getCodigoDisciplina(), codigoCursoCoordenador);
    validarPeriodoLetivoExistente(turmaAtualizada.getPeriodoLetivo());
    validarProfessorDoCurso(turmaAtualizada.getMatriculaProfessor(), codigoCursoCoordenador);

    Turma turmaCadastrada = buscarTurmaPorCodigo(codigo);
    turmaAtualizada.setCodigo(turmaCadastrada.getCodigo());
    preservarDadosAcademicosTurma(turmaCadastrada, turmaAtualizada);
    validarConflitoHorarioProfessor(turmaAtualizada, turmaCadastrada.getCodigo());

    boolean atualizou = turmaRepository.atualizarTurma(turmaAtualizada);

    if (!atualizou) {
      throw new EntradaInvalidaException("Nao foi possivel alterar a turma.");
    }
  }

  /**
   * Cancela uma turma pelo codigo.
   *
   * @param codigo codigo da turma.
   */
  public void cancelarTurma(String codigo) {
    validarCodigoTurma(codigo);

    Turma turma = turmaRepository.buscarTurmaPorCodigo(codigo);

    if (turma == null) {
      throw new EntradaInvalidaException("Turma não encontrada.");
    }

    boolean removeu = turmaRepository.removerTurmaPorCodigo(codigo);

    if (!removeu) {
      throw new EntradaInvalidaException("Não foi possível cancelar a turma.");
    }
  }

  /**
   * Cancela uma turma do curso do coordenador.
   *
   * @param codigo codigo da turma.
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   */
  public void cancelarTurma(String codigo, String codigoCursoCoordenador) {
    validarTurmaPertenceAoCurso(codigo, codigoCursoCoordenador);
    cancelarTurma(codigo);
  }

  /**
   * Busca uma turma pelo codigo.
   *
   * @param codigo codigo da turma.
   * @return turma encontrada.
   * @throws TurmaNaoEncontradaException quando a turma nao e encontrada.
   */
  public Turma buscarTurmaPorCodigo(String codigo) throws TurmaNaoEncontradaException {
    validarCodigoTurma(codigo);

    Turma turma = turmaRepository.buscarTurmaPorCodigo(codigo);

    if (turma == null) {
      throw new TurmaNaoEncontradaException();
    }
    return turma;
  }

  /**
   * Busca uma turma pelo codigo e professor.
   *
   * @param codigo codigo da turma.
   * @param matriculaProfessor matricula do professor.
   * @return turma encontrada.
   * @throws TurmaNaoEncontradaException quando a turma nao e encontrada.
   */
  public Turma buscarTurmaPorCodigo(String codigo, String matriculaProfessor)
      throws TurmaNaoEncontradaException {
    Turma turma = buscarTurmaPorCodigo(codigo);
    validarTurmaPertenceAoProfessor(turma, matriculaProfessor);
    return turma;
  }

  /**
   * Lista todas as turmas cadastradas.
   *
   * @return lista de turmas cadastradas.
   */
  public List<Turma> listarTurmas() {
    return turmaRepository.listarTurmas();
  }

  /**
   * Lista turmas pelo curso.
   *
   * @param codigoCurso codigo do curso.
   * @return lista de turmas do curso.
   */
  public List<Turma> listarTurmasPorCurso(String codigoCurso) {
    validarCodigoCursoCoordenador(codigoCurso);

    List<Turma> turmasCurso = new ArrayList<>();

    for (Turma turma : turmaRepository.listarTurmas()) {
      Disciplina disciplina = disciplinaRepository.buscarPorCodigo(turma.getCodigoDisciplina());

      if (disciplina != null
          && disciplina.getCodigoCurso() != null
          && disciplina.getCodigoCurso().equalsIgnoreCase(codigoCurso.trim())) {
        turmasCurso.add(turma);
      }
    }

    return turmasCurso;
  }

  /**
   * Lista professores pelo curso.
   *
   * @param codigoCurso codigo do curso.
   * @return lista de professores do curso.
   */
  public List<Professor> listarProfessoresPorCurso(String codigoCurso) {
    validarCodigoCursoCoordenador(codigoCurso);

    List<Professor> professoresCurso = new ArrayList<>();

    for (Usuario usuario : userRepository.listar(TipoUsuario.PROFESSOR)) {
      if (usuario instanceof Professor professor
          && professor.getCodigoCurso() != null
          && professor.getCodigoCurso().equalsIgnoreCase(codigoCurso.trim())) {
        professoresCurso.add(professor);
      }
    }

    return professoresCurso;
  }

  /**
   * Busca o periodo letivo ativo.
   *
   * @return periodo letivo ativo.
   */
  public String buscarPeriodoLetivoAtivo() {
    for (PeriodoLetivo periodo : periodoLetivoRepository.listarPeriodos()) {
      if (periodo.getPeriodoAtivo()) {
        return periodo.getPeriodo();
      }
    }

    return null;
  }

  /**
   * Busca o nome da disciplina pelo codigo.
   *
   * @param codigoDisciplina codigo da disciplina.
   * @return nome da disciplina.
   */
  public String buscarNomeDisciplina(String codigoDisciplina) {
    Disciplina disciplina = disciplinaRepository.buscarPorCodigo(codigoDisciplina);
    return disciplina == null ? codigoDisciplina : disciplina.getNome();
  }

  /**
   * Busca o nome do professor pela matricula.
   *
   * @param matriculaProfessor matricula do professor.
   * @return nome do professor.
   */
  public String buscarNomeProfessor(String matriculaProfessor) {
    try {
      Usuario usuario =
          userRepository.buscarPorMatricula(matriculaProfessor, TipoUsuario.PROFESSOR);
      return usuario.getNome();
    } catch (UsuarioNaoEncontradoException e) {
      return matriculaProfessor;
    }
  }

  /**
   * Valida se a turma pertence ao curso.
   *
   * @param codigoTurma codigo da turma.
   * @param codigoCurso codigo do curso.
   */
  public void validarTurmaPertenceAoCurso(String codigoTurma, String codigoCurso) {
    validarCodigoCursoCoordenador(codigoCurso);
    Turma turma = buscarTurmaPorCodigo(codigoTurma);
    validarDisciplinaDoCurso(turma.getCodigoDisciplina(), codigoCurso);
  }

  /**
   * Valida se a turma pertence ao professor.
   *
   * @param codigoTurma codigo da turma.
   * @param matriculaProfessor matricula do professor.
   */
  public void validarTurmaPertenceAoProfessor(String codigoTurma, String matriculaProfessor) {
    Turma turma = buscarTurmaPorCodigo(codigoTurma);
    validarTurmaPertenceAoProfessor(turma, matriculaProfessor);
  }

  private void validarTurmaPertenceAoProfessor(Turma turma, String matriculaProfessor) {
    validarMatriculaProfessorLogado(matriculaProfessor);

    if (turma.getMatriculaProfessor() == null
        || !turma.getMatriculaProfessor().equalsIgnoreCase(matriculaProfessor.trim())) {
      throw new EntradaInvalidaException("Professor nao pode atuar em turma de outro professor.");
    }
  }

  /**
   * Lista turmas pelo professor.
   *
   * @param matriculaProfessor matricula do professor.
   * @return lista de turmas do professor.
   */
  public List<Turma> listarTurmasPorProfessor(String matriculaProfessor) {
    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      throw new EntradaInvalidaException("Matrícula do professor não pode ser vazia.");
    }

    return turmaRepository.buscarTurmaPorMatriculaDeProfessor(matriculaProfessor);
  }

  /**
   * Lista turmas pelo periodo letivo.
   *
   * @param periodoLetivo periodo letivo.
   * @return lista de turmas do periodo.
   */
  public List<Turma> listarTurmasPorPeriodoLetivo(String periodoLetivo) {
    if (periodoLetivo == null || periodoLetivo.isBlank()) {
      throw new EntradaInvalidaException("Período letivo não pode ser vazio.");
    }

    return turmaRepository.buscarTurmaPorPeriodoLetivo(periodoLetivo);
  }

  /**
   * Cadastra um aluno em uma turma.
   *
   * @param codigoTurma codigo da turma.
   * @param alunoLogado aluno logado.
   * @return resultado do cadastro.
   */
  public int cadastrarAlunoEmTurma(String codigoTurma, Aluno alunoLogado) {
    Turma turma = buscarTurmaPorCodigo(codigoTurma);

    validarEntradaAlunoEmTurma(alunoLogado, turma);

    if (validarDisponibilidadeDeTurma(turma)) {
      // SUJEIRO A MUDANÇA
      adicionarAlunoTurma(alunoLogado, turma);
      return 0;
    } else {
      adicionarAlunoListaEspera(alunoLogado, turma);
      return 1;
    }
  }

  /**
   * Cadastra uma nova aula em uma turma.
   *
   * @param aula aula a ser cadastrada.
   * @param codigoTurma codigo da turma.
   */
  public void cadastrarNovaAula(Aula aula, String codigoTurma) {
    Turma turma = buscarTurmaPorCodigo(codigoTurma);

    turma.getAulas().add(aula.getId());

    turmaRepository.atualizarTurma(turma);
  }

  /**
   * Cadastra uma nova aula em turma do professor.
   *
   * @param aula aula a ser cadastrada.
   * @param codigoTurma codigo da turma.
   * @param matriculaProfessor matricula do professor.
   */
  public void cadastrarNovaAula(Aula aula, String codigoTurma, String matriculaProfessor) {
    validarTurmaPertenceAoProfessor(codigoTurma, matriculaProfessor);
    cadastrarNovaAula(aula, codigoTurma);
  }

  /**
   * Verifica se existem alunos matriculados na turma.
   *
   * @param turma turma verificada.
   * @return verdadeiro se existirem alunos matriculados.
   */
  public boolean existeAlunosMatriculados(Turma turma) {

    return turma.getMatriculados().isEmpty();
  }

  /**
   * Atualiza a frequencia de uma turma.
   *
   * @param codigoTurma codigo da turma.
   */
  public void atualizarFrequenciaTurma(String codigoTurma) {
    List<Boletim> boletinsTurma = boletimRepository.buscarBoletinsPorTurma(codigoTurma);

    List<String> codigoAulas = turmaRepository.buscarAulasDeTurma(codigoTurma);

    int quantidadeDeAulas = codigoAulas.size();

    List<Aula> aulasTurma = new ArrayList<>();

    for (String codigoAula : codigoAulas) {
      aulasTurma.add(aulaRepository.buscarAulaPorId(codigoAula));
    }

    for (Boletim boletim : boletinsTurma) {

      int contadorDeFaltas = 0;

      for (Aula aula : aulasTurma) {
        Map<String, Boolean> presencas = aula.getPresencas();

        Boolean estaPresente = presencas.get(boletim.getMatriculaAluno());

        if (!Boolean.TRUE.equals(estaPresente)) {
          contadorDeFaltas += 1;
        }
      }

      Turma turma = turmaRepository.buscarTurmaPorCodigo(boletim.getCodigoTurma());

      Disciplina disciplina = disciplinaRepository.buscarPorCodigo(turma.getCodigoDisciplina());

      boletim.calcularFrequencia(contadorDeFaltas, quantidadeDeAulas, disciplina.getCargaHoraria());
      boletimRepository.atualizarBoletins(boletim);
    }
  }

  /**
   * Atualiza a frequencia de uma turma do professor.
   *
   * @param codigoTurma codigo da turma.
   * @param matriculaProfessor matricula do professor.
   */
  public void atualizarFrequenciaTurma(String codigoTurma, String matriculaProfessor) {
    validarTurmaPertenceAoProfessor(codigoTurma, matriculaProfessor);
    atualizarFrequenciaTurma(codigoTurma);
  }

  private void adicionarAlunoListaEspera(Aluno alunoLogado, Turma turma) {
    validarAlunoNaListaEspera(alunoLogado, turma);

    turma.getListaEspera().add(alunoLogado.getMatricula());
    turmaRepository.atualizarTurma(turma);
  }

  private void adicionarAlunoTurma(Aluno alunoLogado, Turma turma) {
    turma.getMatriculados().add(alunoLogado.getMatricula());
    alunoLogado.getTurmasMatriculadas().add(turma.getCodigo());
    turmaRepository.atualizarTurma(turma);
    userRepository.atualizarUsuario(alunoLogado);
    boletimService.criarBoletimSeNaoExistir(alunoLogado.getMatricula(), turma.getCodigo());
  }

  private void validarAlunoNaListaEspera(Aluno alunoLogado, Turma turma) {
    if (turma.getListaEspera().contains(alunoLogado.getMatricula())) {
      throw new EntradaInvalidaException("Aluno já está na lista de espera.");
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

  private void validarMatriculaProfessorLogado(String matriculaProfessor) {
    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      throw new EntradaInvalidaException("Matricula do professor logado nao pode ser vazia.");
    }
  }

  private void validarCodigoCursoCoordenador(String codigoCurso) {
    if (codigoCurso == null || codigoCurso.isBlank()) {
      throw new EntradaInvalidaException("Coordenador nao esta vinculado a nenhum curso.");
    }
  }

  private void validarDisciplinaExistente(String codigoDisciplina) {
    Disciplina disciplina = disciplinaRepository.buscarPorCodigo(codigoDisciplina);

    if (disciplina == null) {
      throw new EntradaInvalidaException("Disciplina não encontrada.");
    }
  }

  private Disciplina validarDisciplinaDoCurso(String codigoDisciplina, String codigoCurso) {
    Disciplina disciplina = disciplinaRepository.buscarPorCodigo(codigoDisciplina);

    if (disciplina == null) {
      throw new EntradaInvalidaException("Disciplina nao encontrada.");
    }

    if (disciplina.getCodigoCurso() == null
        || !disciplina.getCodigoCurso().equalsIgnoreCase(codigoCurso.trim())) {
      throw new EntradaInvalidaException("Disciplina nao pertence ao curso do coordenador.");
    }

    return disciplina;
  }

  private void validarPeriodoLetivoExistente(String periodoLetivo) {
    for (PeriodoLetivo periodo : periodoLetivoRepository.listarPeriodos()) {
      if (periodo.getPeriodo() != null
          && periodo.getPeriodo().equalsIgnoreCase(periodoLetivo.trim())) {
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

  private Professor validarProfessorDoCurso(String matriculaProfessor, String codigoCurso) {
    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      throw new EntradaInvalidaException("Turma deve possuir professor responsavel.");
    }

    try {
      Professor professor =
          (Professor)
              userRepository.buscarPorMatricula(matriculaProfessor.trim(), TipoUsuario.PROFESSOR);

      if (professor.getCodigoCurso() == null
          || !professor.getCodigoCurso().equalsIgnoreCase(codigoCurso.trim())) {
        throw new EntradaInvalidaException("Professor nao pertence ao curso do coordenador.");
      }

      return professor;
    } catch (UsuarioNaoEncontradoException e) {
      throw new EntradaInvalidaException("Professor responsavel nao encontrado.");
    }
  }

  private void preservarDadosAcademicosTurma(Turma turmaCadastrada, Turma turmaAtualizada) {
    turmaAtualizada.setMatriculados();
    turmaAtualizada.setListaEspera();
    turmaAtualizada.setAulas();

    if (turmaCadastrada.getMatriculados() != null) {
      turmaAtualizada.getMatriculados().addAll(turmaCadastrada.getMatriculados());
    }

    if (turmaCadastrada.getListaEspera() != null) {
      turmaAtualizada.getListaEspera().addAll(turmaCadastrada.getListaEspera());
    }

    if (turmaCadastrada.getAulas() != null) {
      turmaAtualizada.getAulas().addAll(turmaCadastrada.getAulas());
    }
  }

  private void validarConflitoHorarioProfessor(Turma novaTurma, String codigoTurmaIgnorada) {
    List<Turma> turmasDoProfessor =
        turmaRepository.buscarTurmaPorMatriculaDeProfessor(novaTurma.getMatriculaProfessor());

    for (Turma turmaCadastrada : turmasDoProfessor) {
      boolean mesmaTurma =
          codigoTurmaIgnorada != null
              && turmaCadastrada.getCodigo() != null
              && turmaCadastrada.getCodigo().equalsIgnoreCase(codigoTurmaIgnorada);

      if (mesmaTurma) {
        continue;
      }

      boolean mesmoPeriodo =
          turmaCadastrada.getPeriodoLetivo() != null
              && turmaCadastrada.getPeriodoLetivo().equalsIgnoreCase(novaTurma.getPeriodoLetivo());
      boolean mesmoHorario =
          turmaCadastrada.getHorario() != null
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
    validarPeriodoLetivoAtivoParaMatricula(turma);

    Set<String> codigosDisciplinasConcluidas = new HashSet<>(aluno.getDisciplinasConcluidas());

    Disciplina disciplina = disciplinaRepository.buscarPorCodigo(turma.getCodigoDisciplina());

    validarAlunoCursoTurma(aluno, disciplina);
    validarDisciplinasConcluidas(codigosDisciplinasConcluidas, disciplina);
    validarAlunoJaAprovadoNaDisciplina(codigosDisciplinasConcluidas, disciplina);
    validarAlunoJaMatriculado(aluno, turma);
    validarAlunoEmOutraTurmaMesmaDisciplinaPeriodo(aluno, turma);
    validarHorariosDeTurma(aluno, turma);
  }

  private void validarPeriodoLetivoAtivoParaMatricula(Turma turma) {
    String periodoAtivo = buscarPeriodoLetivoAtivo();

    if (periodoAtivo == null
        || turma.getPeriodoLetivo() == null
        || !periodoAtivo.equalsIgnoreCase(turma.getPeriodoLetivo())) {
      throw new EntradaInvalidaException(
          "Periodo letivo da turma nao esta ativo para matricula.");
    }
  }

  private void validarAlunoCursoTurma(Aluno aluno, Disciplina disciplina) {
    if (aluno.getCodigoCurso() == null || aluno.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Aluno nao esta vinculado a nenhum curso.");
    }

    if (disciplina == null) {
      throw new EntradaInvalidaException("Disciplina da turma nao encontrada.");
    }

    if (disciplina.getCodigoCurso() == null
        || !disciplina.getCodigoCurso().equalsIgnoreCase(aluno.getCodigoCurso().trim())) {
      throw new EntradaInvalidaException("Aluno nao pode se matricular em turma de outro curso.");
    }
  }

  private void validarDisciplinasConcluidas(
      Set<String> codigoDisciplinasConcluidas, Disciplina disciplina) {

    List<String> disciplinasPreRequisito = disciplina.getPreRequisitos();

    boolean todasDisciplinasForamConcluidas =
        new HashSet<>(codigoDisciplinasConcluidas.stream().toList())
            .containsAll(disciplinasPreRequisito);

    if (!todasDisciplinasForamConcluidas) {
      throw new AlunoNaoCumprePreRequisitosException(
          "Aluno não possui todos os pré-requisitos para esta disciplina.");
    }
  }

  private void validarHorariosDeTurma(Aluno aluno, Turma turma) {
    for (String codigoTurmaAluno : aluno.getTurmasMatriculadas()) {
      Turma turmaAluno = turmaRepository.buscarTurmaPorCodigo(codigoTurmaAluno);

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

  private void validarAlunoJaAprovadoNaDisciplina(
      Set<String> codigosDisciplinasConcluidas, Disciplina disciplina) {
    if (codigosDisciplinasConcluidas.contains(disciplina.getCodigo())) {
      throw new EntradaInvalidaException(
          "Aluno já foi aprovado na disciplina '"
              + disciplina.getNome()
              + "' e não pode se matricular novamente.");
    }
  }

  private void validarAlunoEmOutraTurmaMesmaDisciplinaPeriodo(Aluno aluno, Turma turmaDestino) {
    for (Turma turmaCadastrada : turmaRepository.listarTurmas()) {
      if (mesmaTurma(turmaCadastrada, turmaDestino)) {
        continue;
      }

      if (possuiMesmaDisciplinaNoMesmoPeriodo(turmaCadastrada, turmaDestino)
          && alunoParticipaDaTurma(aluno, turmaCadastrada)) {
        throw new AlunoNaoCumprePreRequisitosException(
            "Aluno ja esta matriculado ou em lista de espera em outra turma "
                + "desta disciplina neste periodo.");
      }
    }
  }

  private boolean mesmaTurma(Turma primeiraTurma, Turma segundaTurma) {
    if (primeiraTurma == null || segundaTurma == null) {
      return false;
    }

    return primeiraTurma.getCodigo() != null
        && segundaTurma.getCodigo() != null
        && primeiraTurma.getCodigo().equalsIgnoreCase(segundaTurma.getCodigo());
  }

  private boolean possuiMesmaDisciplinaNoMesmoPeriodo(
      Turma turmaCadastrada, Turma turmaDestino) {
    return turmaCadastrada.getCodigoDisciplina() != null
        && turmaDestino.getCodigoDisciplina() != null
        && turmaCadastrada.getPeriodoLetivo() != null
        && turmaDestino.getPeriodoLetivo() != null
        && turmaCadastrada
            .getCodigoDisciplina()
            .equalsIgnoreCase(turmaDestino.getCodigoDisciplina())
        && turmaCadastrada.getPeriodoLetivo().equalsIgnoreCase(turmaDestino.getPeriodoLetivo());
  }

  private boolean alunoParticipaDaTurma(Aluno aluno, Turma turma) {
    String matriculaAluno = aluno.getMatricula();

    boolean estaMatriculado =
        turma.getMatriculados() != null && turma.getMatriculados().contains(matriculaAluno);
    boolean estaNaListaEspera =
        turma.getListaEspera() != null && turma.getListaEspera().contains(matriculaAluno);

    return estaMatriculado || estaNaListaEspera;
  }

  /**
   * Cancela a matricula de um aluno em turma.
   *
   * @param codTurma codigo da turma.
   * @param alunoLogado aluno logado.
   * @return mensagem do resultado.
   */
  public String cancelarAlunoTurma(String codTurma, Aluno alunoLogado) {
    validarCodigoTurma(codTurma);

    Turma turma = buscarTurmaPorCodigo(codTurma);
    String matriculaAluno = alunoLogado.getMatricula();

    validarAlunoMatriculado(turma, matriculaAluno);

    String msgResultado = "";

    turma.getMatriculados().remove(matriculaAluno);
    alunoLogado.getTurmasMatriculadas().remove(codTurma);

    if (!turma.getListaEspera().isEmpty()) {
      String codAlunoListaEspera = turma.getListaEspera().get(0);
      turma.getMatriculados().add(codAlunoListaEspera);

      turma.getListaEspera().remove(0);

      Aluno alunoListaEspera = usuarioService.buscarAlunoPorMatricula(codAlunoListaEspera);
      alunoListaEspera.getTurmasMatriculadas().add(codTurma);

      userRepository.atualizarUsuario(alunoListaEspera);
      boletimService.criarBoletimSeNaoExistir(codAlunoListaEspera, codTurma);

      msgResultado =
          "\n O aluno com matrícula: "
              + codAlunoListaEspera
              + " foi promovido da lista de espera para a turma.";
    }

    turmaRepository.atualizarTurma(turma);
    userRepository.atualizarUsuario(alunoLogado);

    return msgResultado;
  }

  private void validarAlunoMatriculado(Turma turma, String matriculaAluno) {
    if (!turma.getMatriculados().contains(matriculaAluno)) {
      throw new EntradaInvalidaException("Aluno não está matriculado na turma.");
    }
  }

  private String gerarCodigoTurma() {
    int contador = turmaRepository.listarTurmas().size();
    String codigo;

    do {
      codigo = "tur" + String.format("%02d", contador);
      contador++;
    } while (turmaRepository.buscarTurmaPorCodigo(codigo) != null);

    return codigo;
  }
}
