package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.Disciplina;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.DiarioNaoEncontradoException;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.TurmaNaoEncontradaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.DiarioRepository;
import br.com.classroompb.model.repository.DisciplinaRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Servico responsavel pelas operacoes de diario de turma.
 *
 * <p>Um diario esta sempre associado a uma turma existente e a um professor responsavel
 * cadastrado no sistema. Uma mesma turma pode possuir um ou mais diarios.
 */
public class DiarioService {

  private static final Path DIRETORIO_DIARIOS = PersistenciaPaths.DIARIOS;
  private static final Path DIRETORIO_TURMAS = PersistenciaPaths.TURMAS;
  private static final Path DIRETORIO_DISCIPLINAS = PersistenciaPaths.DISCIPLINAS;
  private static final Path DIRETORIO_USUARIOS = PersistenciaPaths.USUARIOS;

  private final DiarioRepository diarioRepository;
  private final TurmaRepository turmaRepository;
  private final DisciplinaRepository disciplinaRepository;
  private final UserRepository userRepository;

  /**
   * Cria o servico de diarios com dependencias padrao.
   */
  public DiarioService() {
    this.diarioRepository = new DiarioRepository(new ObjectMapper(), DIRETORIO_DIARIOS.toString());
    this.turmaRepository = new TurmaRepository(new ObjectMapper(), DIRETORIO_TURMAS.toString());
    this.disciplinaRepository =
        new DisciplinaRepository(new ObjectMapper(), DIRETORIO_DISCIPLINAS.toString());
    this.userRepository = new UserRepository(new ObjectMapper(), DIRETORIO_USUARIOS.toString());
  }

  /**
   * Cria o servico de diarios com todas as dependencias informadas.
   *
   * @param diarioRepository repositorio de diarios.
   * @param turmaRepository repositorio de turmas.
   * @param disciplinaRepository repositorio de disciplinas.
   * @param userRepository repositorio de usuarios.
   */
  public DiarioService(
      DiarioRepository diarioRepository,
      TurmaRepository turmaRepository,
      DisciplinaRepository disciplinaRepository,
      UserRepository userRepository) {
    this.diarioRepository = diarioRepository;
    this.turmaRepository = turmaRepository;
    this.disciplinaRepository = disciplinaRepository;
    this.userRepository = userRepository;
  }

  /**
   * Cadastra um novo diario para uma turma.
   *
   * @param diario diario a ser cadastrado.
   */
  public void cadastrarDiario(Diario diario) {
    validarDiario(diario);
    validarTurmaExistente(diario.getCodigoTurma());
    validarProfessorResponsavel(diario.getMatriculaProfessor());

    diario.setSituacao(SituacaoDiario.ATIVO);
    diario.setCodigo(gerarCodigoDiario());
    diarioRepository.salvarDiario(diario);
  }

  /**
   * Cadastra um novo diario para uma turma do curso do coordenador.
   *
   * @param diario diario a ser cadastrado.
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   */
  public void cadastrarDiario(Diario diario, String codigoCursoCoordenador) {
    validarCodigoCursoCoordenador(codigoCursoCoordenador);
    validarDiario(diario);
    validarTurmaDoCurso(diario.getCodigoTurma(), codigoCursoCoordenador);
    validarProfessorDoCurso(diario.getMatriculaProfessor(), codigoCursoCoordenador);

    diario.setSituacao(SituacaoDiario.ATIVO);
    diario.setCodigo(gerarCodigoDiario());
    diarioRepository.salvarDiario(diario);
  }

  /**
   * Altera um diario cadastrado.
   *
   * @param codigo codigo do diario.
   * @param diarioAtualizado diario com dados atualizados.
   */
  public void alterarDiario(String codigo, Diario diarioAtualizado) {
    validarCodigoDiario(codigo);

    Diario diarioCadastrado = diarioRepository.buscarDiarioPorCodigo(codigo);

    if (diarioCadastrado == null) {
      throw new DiarioNaoEncontradoException();
    }

    validarDiario(diarioAtualizado);
    validarTurmaExistente(diarioAtualizado.getCodigoTurma());
    validarProfessorResponsavel(diarioAtualizado.getMatriculaProfessor());

    diarioAtualizado.setCodigo(diarioCadastrado.getCodigo());

    boolean atualizou = diarioRepository.atualizarDiario(diarioAtualizado);

    if (!atualizou) {
      throw new EntradaInvalidaException("Não foi possível alterar o diário.");
    }
  }

  /**
   * Altera um diario de uma turma do curso do coordenador.
   *
   * @param codigo codigo do diario.
   * @param diarioAtualizado diario com dados atualizados.
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   */
  public void alterarDiario(String codigo, Diario diarioAtualizado, String codigoCursoCoordenador) {
    validarCodigoCursoCoordenador(codigoCursoCoordenador);
    validarDiarioPertenceAoCurso(codigo, codigoCursoCoordenador);
    validarDiario(diarioAtualizado);
    validarTurmaDoCurso(diarioAtualizado.getCodigoTurma(), codigoCursoCoordenador);
    validarProfessorDoCurso(diarioAtualizado.getMatriculaProfessor(), codigoCursoCoordenador);

    Diario diarioCadastrado = buscarDiarioPorCodigo(codigo);
    diarioAtualizado.setCodigo(diarioCadastrado.getCodigo());
    diarioAtualizado.setSituacao(diarioCadastrado.getSituacao());

    boolean atualizou = diarioRepository.atualizarDiario(diarioAtualizado);

    if (!atualizou) {
      throw new EntradaInvalidaException("Não foi possível alterar o diário.");
    }
  }

  /**
   * Altera a situacao de um diario cadastrado.
   *
   * @param codigo codigo do diario.
   * @param novaSituacao nova situacao do diario.
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   */
  public void alterarSituacaoDiario(
      String codigo, SituacaoDiario novaSituacao, String codigoCursoCoordenador) {
    validarCodigoCursoCoordenador(codigoCursoCoordenador);
    validarDiarioPertenceAoCurso(codigo, codigoCursoCoordenador);

    if (novaSituacao == null) {
      throw new EntradaInvalidaException("Situação do diário não pode ser vazia.");
    }

    Diario diario = buscarDiarioPorCodigo(codigo);
    diario.setSituacao(novaSituacao);

    boolean atualizou = diarioRepository.atualizarDiario(diario);

    if (!atualizou) {
      throw new EntradaInvalidaException("Não foi possível alterar a situação do diário.");
    }
  }

  /**
   * Cancela um diario pelo codigo.
   *
   * @param codigo codigo do diario.
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   */
  public void cancelarDiario(String codigo, String codigoCursoCoordenador) {
    alterarSituacaoDiario(codigo, SituacaoDiario.CANCELADO, codigoCursoCoordenador);
  }

  /**
   * Encerra um diario pelo codigo.
   *
   * @param codigo codigo do diario.
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   */
  public void encerrarDiario(String codigo, String codigoCursoCoordenador) {
    alterarSituacaoDiario(codigo, SituacaoDiario.ENCERRADO, codigoCursoCoordenador);
  }

  /**
   * Busca um diario pelo codigo.
   *
   * @param codigo codigo do diario.
   * @return diario encontrado.
   * @throws DiarioNaoEncontradoException quando o diario nao e encontrado.
   */
  public Diario buscarDiarioPorCodigo(String codigo) throws DiarioNaoEncontradoException {
    validarCodigoDiario(codigo);

    Diario diario = diarioRepository.buscarDiarioPorCodigo(codigo);

    if (diario == null) {
      throw new DiarioNaoEncontradoException();
    }

    return diario;
  }

  /**
   * Lista todos os diarios cadastrados.
   *
   * @return lista de diarios cadastrados.
   */
  public List<Diario> listarDiarios() {
    return diarioRepository.listarDiarios();
  }

  /**
   * Lista os diarios de uma turma.
   *
   * @param codigoTurma codigo da turma.
   * @return lista de diarios da turma.
   */
  public List<Diario> listarDiariosPorTurma(String codigoTurma) {
    validarTurmaExistente(codigoTurma);
    return diarioRepository.buscarDiariosPorTurma(codigoTurma);
  }

  /**
   * Lista os diarios das turmas do curso do coordenador.
   *
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   * @return lista de diarios do curso.
   */
  public List<Diario> listarDiariosPorCurso(String codigoCursoCoordenador) {
    validarCodigoCursoCoordenador(codigoCursoCoordenador);

    List<Diario> diariosDoCurso = new ArrayList<>();

    for (Diario diario : diarioRepository.listarDiarios()) {
      Turma turma = turmaRepository.buscarTurmaPorCodigo(diario.getCodigoTurma());

      if (turma == null) {
        continue;
      }

      Disciplina disciplina = disciplinaRepository.buscarPorCodigo(turma.getCodigoDisciplina());

      if (disciplina != null
          && disciplina.getCodigoCurso() != null
          && disciplina.getCodigoCurso().equalsIgnoreCase(codigoCursoCoordenador.trim())) {
        diariosDoCurso.add(diario);
      }
    }

    return diariosDoCurso;
  }

  /**
   * Lista os diarios de um professor.
   *
   * @param matriculaProfessor matricula do professor.
   * @return lista de diarios do professor.
   */
  public List<Diario> listarDiariosPorProfessor(String matriculaProfessor) {
    return diarioRepository.buscarDiariosPorMatriculaDeProfessor(matriculaProfessor);
  }

  /**
   * Busca o nome da turma associada, no formato "disciplina - periodo".
   *
   * @param codigoTurma codigo da turma.
   * @return descricao amigavel da turma.
   */
  public String buscarDescricaoTurma(String codigoTurma) {
    Turma turma = turmaRepository.buscarTurmaPorCodigo(codigoTurma);

    if (turma == null) {
      return codigoTurma;
    }

    Disciplina disciplina = disciplinaRepository.buscarPorCodigo(turma.getCodigoDisciplina());
    String nomeDisciplina = disciplina == null ? turma.getCodigoDisciplina() : disciplina.getNome();

    return nomeDisciplina + " (" + turma.getPeriodoLetivo() + ")";
  }

  /**
   * Busca o nome do professor pela matricula.
   *
   * @param matriculaProfessor matricula do professor.
   * @return nome do professor.
   */
  public String buscarNomeProfessor(String matriculaProfessor) {
    try {
      return userRepository.buscarPorMatricula(matriculaProfessor, TipoUsuario.PROFESSOR).getNome();
    } catch (UsuarioNaoEncontradoException e) {
      return matriculaProfessor;
    }
  }

  /**
   * Valida se o diario pertence ao curso do coordenador.
   *
   * @param codigoDiario codigo do diario.
   * @param codigoCursoCoordenador codigo do curso do coordenador.
   */
  public void validarDiarioPertenceAoCurso(String codigoDiario, String codigoCursoCoordenador) {
    validarCodigoCursoCoordenador(codigoCursoCoordenador);
    Diario diario = buscarDiarioPorCodigo(codigoDiario);
    validarTurmaDoCurso(diario.getCodigoTurma(), codigoCursoCoordenador);
  }

  private void validarDiario(Diario diario) {
    if (diario == null) {
      throw new EntradaInvalidaException("Diário não pode ser nulo.");
    }

    diario.validarDadosBasicos();
  }

  private void validarCodigoDiario(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new EntradaInvalidaException("Código do diário não pode ser vazio.");
    }
  }

  private void validarCodigoCursoCoordenador(String codigoCurso) {
    if (codigoCurso == null || codigoCurso.isBlank()) {
      throw new EntradaInvalidaException("Coordenador não está vinculado a nenhum curso.");
    }
  }

  private void validarTurmaExistente(String codigoTurma) {
    if (codigoTurma == null || codigoTurma.isBlank()) {
      throw new EntradaInvalidaException("Diário deve estar associado a uma turma.");
    }

    Turma turma = turmaRepository.buscarTurmaPorCodigo(codigoTurma);

    if (turma == null) {
      throw new TurmaNaoEncontradaException();
    }
  }

  private void validarTurmaDoCurso(String codigoTurma, String codigoCurso) {
    if (codigoTurma == null || codigoTurma.isBlank()) {
      throw new EntradaInvalidaException("Diário deve estar associado a uma turma.");
    }

    Turma turma = turmaRepository.buscarTurmaPorCodigo(codigoTurma);

    if (turma == null) {
      throw new TurmaNaoEncontradaException();
    }

    Disciplina disciplina = disciplinaRepository.buscarPorCodigo(turma.getCodigoDisciplina());

    if (disciplina == null
        || disciplina.getCodigoCurso() == null
        || !disciplina.getCodigoCurso().equalsIgnoreCase(codigoCurso.trim())) {
      throw new EntradaInvalidaException("Turma não pertence ao curso do coordenador.");
    }
  }

  private void validarProfessorResponsavel(String matriculaProfessor) {
    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      throw new EntradaInvalidaException("Diário deve possuir professor responsável.");
    }

    try {
      userRepository.buscarPorMatricula(matriculaProfessor, TipoUsuario.PROFESSOR);
    } catch (UsuarioNaoEncontradoException e) {
      throw new EntradaInvalidaException("Professor responsável não encontrado.");
    }
  }

  private void validarProfessorDoCurso(String matriculaProfessor, String codigoCurso) {
    if (matriculaProfessor == null || matriculaProfessor.isBlank()) {
      throw new EntradaInvalidaException("Diário deve possuir professor responsável.");
    }

    try {
      Professor professor =
          (Professor)
              userRepository.buscarPorMatricula(matriculaProfessor.trim(), TipoUsuario.PROFESSOR);

      if (professor.getCodigoCurso() == null
          || !professor.getCodigoCurso().equalsIgnoreCase(codigoCurso.trim())) {
        throw new EntradaInvalidaException("Professor não pertence ao curso do coordenador.");
      }
    } catch (UsuarioNaoEncontradoException e) {
      throw new EntradaInvalidaException("Professor responsável não encontrado.");
    }
  }

  private String gerarCodigoDiario() {
    int contador = diarioRepository.listarDiarios().size();
    String codigo;

    do {
      codigo = "dia" + String.format("%02d", contador);
      contador++;
    } while (diarioRepository.buscarDiarioPorCodigo(codigo) != null);

    return codigo;
  }
}
