package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Avaliacao;
import br.com.classroompb.model.entities.gestaoacademica.Diario;
import br.com.classroompb.model.entities.gestaoacademica.NotaAvaliacao;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.enums.SituacaoDiario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.AvaliacaoRepository;
import br.com.classroompb.model.repository.DiarioRepository;
import br.com.classroompb.model.repository.PersistenciaPaths;
import br.com.classroompb.model.repository.TurmaRepository;
import br.com.classroompb.model.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class AvaliacaoService {

  private final AvaliacaoRepository avaliacaoRepository;
  private final DiarioRepository diarioRepository;
  private final TurmaRepository turmaRepository;
  private final UserRepository userRepository;

  public AvaliacaoService() {
    this(
        new AvaliacaoRepository(new ObjectMapper(), PersistenciaPaths.DIARIOS.toString()),
        new DiarioRepository(new ObjectMapper(), PersistenciaPaths.DIARIOS.toString()),
        new TurmaRepository(new ObjectMapper(), PersistenciaPaths.TURMAS.toString()),
        new UserRepository(new ObjectMapper(), PersistenciaPaths.USUARIOS.toString()));
  }

  public AvaliacaoService(
      AvaliacaoRepository avaliacaoRepository,
      DiarioRepository diarioRepository,
      TurmaRepository turmaRepository,
      UserRepository userRepository) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.diarioRepository = diarioRepository;
    this.turmaRepository = turmaRepository;
    this.userRepository = userRepository;
  }

  public void cadastrarAvaliacao(Avaliacao avaliacao, String matriculaProfessor) {
    if (avaliacao == null) {
      throw new EntradaInvalidaException("Avaliação não pode ser vazia.");
    }

    Diario diario = buscarDiarioAtivo(avaliacao.getCodigoDiario());
    validarProfessorResponsavel(diario, matriculaProfessor);

    if (avaliacao.getNotaMaxima() <= 0 || avaliacao.getNotaMaxima() > 10.0) {
      throw new EntradaInvalidaException("Nota máxima inválida (deve ser entre 0.1 e 10.0).");
    }

    int total = avaliacaoRepository.listarAvaliacoes().size();
    avaliacao.setCodigo("avl" + String.format("%02d", total));
    avaliacaoRepository.salvarAvaliacao(avaliacao);
  }

  public List<Avaliacao> listarAvaliacoesPorDiario(String codigoDiario) {
    return avaliacaoRepository.buscarPorDiario(codigoDiario);
  }

  public List<Avaliacao> listarAvaliacoesPorDiarioDoAluno(
      String codigoDiario, String matriculaAluno) {
    Diario diario = buscarDiarioConsultavel(codigoDiario);
    validarAlunoMatriculado(diario, matriculaAluno);
    return avaliacaoRepository.buscarPorDiario(diario.getCodigo());
  }

  public List<Avaliacao> listarAvaliacoesPorDiarioDoProfessor(
      String codigoDiario, String matriculaProfessor) {
    Diario diario = buscarDiarioConsultavel(codigoDiario);
    validarProfessorResponsavel(diario, matriculaProfessor);
    return avaliacaoRepository.buscarPorDiario(diario.getCodigo());
  }

  public void lancarNota(
      String codigoAvaliacao, String matriculaAluno, double nota, String matriculaProfessor) {
    Avaliacao avaliacao = buscarAvaliacaoPorCodigo(codigoAvaliacao);
    Diario diario = buscarDiarioAtivo(avaliacao.getCodigoDiario());
    validarProfessorResponsavel(diario, matriculaProfessor);
    validarAlunoMatriculado(diario, matriculaAluno);

    if (nota < 0 || nota > avaliacao.getNotaMaxima()) {
      throw new EntradaInvalidaException(
          "A nota deve estar entre 0.0 e a nota máxima (" + avaliacao.getNotaMaxima() + ").");
    }

    avaliacaoRepository.salvarNota(
        new NotaAvaliacao(codigoAvaliacao, matriculaAluno.trim(), nota));
  }

  public Double buscarNotaDoAluno(String codigoAvaliacao, String matriculaAluno) {
    return avaliacaoRepository.buscarNotaDoAluno(codigoAvaliacao, matriculaAluno);
  }

  public Double buscarNotaDoAluno(
      String codigoAvaliacao, String codigoDiario, String matriculaAluno) {
    Avaliacao avaliacao = buscarAvaliacaoPorCodigo(codigoAvaliacao);
    Diario diario = buscarDiarioConsultavel(codigoDiario);

    if (!avaliacao.getCodigoDiario().trim().equalsIgnoreCase(diario.getCodigo().trim())) {
      throw new EntradaInvalidaException("Avaliação não pertence ao diário informado.");
    }

    validarAlunoMatriculado(diario, matriculaAluno);
    return avaliacaoRepository.buscarNotaDoAluno(avaliacao.getCodigo(), matriculaAluno.trim());
  }

  public Double buscarNotaDoAlunoParaProfessor(
      String codigoAvaliacao,
      String codigoDiario,
      String matriculaAluno,
      String matriculaProfessor) {
    Avaliacao avaliacao = buscarAvaliacaoPorCodigo(codigoAvaliacao);
    Diario diario = buscarDiarioConsultavel(codigoDiario);
    validarProfessorResponsavel(diario, matriculaProfessor);

    if (!avaliacao.getCodigoDiario().trim().equalsIgnoreCase(diario.getCodigo().trim())) {
      throw new EntradaInvalidaException("Avaliação não pertence ao diário informado.");
    }

    validarAlunoMatriculado(diario, matriculaAluno);
    return avaliacaoRepository.buscarNotaDoAluno(avaliacao.getCodigo(), matriculaAluno.trim());
  }

  private Avaliacao buscarAvaliacaoPorCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new EntradaInvalidaException("Código da avaliação não pode ser vazio.");
    }

    for (Avaliacao avaliacao : avaliacaoRepository.listarAvaliacoes()) {
      if (avaliacao.getCodigo() != null
          && avaliacao.getCodigo().equalsIgnoreCase(codigo.trim())) {
        return avaliacao;
      }
    }
    throw new EntradaInvalidaException("Avaliação não encontrada.");
  }

  private Diario buscarDiarioAtivo(String codigoDiario) {
    Diario diario = diarioRepository.buscarDiarioPorCodigo(codigoDiario);
    if (diario == null) {
      throw new EntradaInvalidaException("Diário não encontrado.");
    }
    if (diario.getSituacao() != SituacaoDiario.ATIVO) {
      throw new EntradaInvalidaException(
          "Não é possível alterar avaliações em diário fechado/cancelado.");
    }
    return diario;
  }

  private Diario buscarDiarioConsultavel(String codigoDiario) {
    Diario diario = diarioRepository.buscarDiarioPorCodigo(codigoDiario);
    if (diario == null) {
      throw new EntradaInvalidaException("Diário não encontrado.");
    }
    if (diario.getSituacao() != SituacaoDiario.ATIVO
        && diario.getSituacao() != SituacaoDiario.ENCERRADO) {
      throw new EntradaInvalidaException("Diário não está disponível para consulta.");
    }
    return diario;
  }

  private void validarProfessorResponsavel(Diario diario, String matriculaProfessor) {
    if (matriculaProfessor == null
        || matriculaProfessor.isBlank()
        || diario.getMatriculaProfessor() == null
        || !diario
            .getMatriculaProfessor()
            .trim()
            .equalsIgnoreCase(matriculaProfessor.trim())) {
      throw new EntradaInvalidaException("Professor não é responsável por este diário.");
    }
  }

  private void validarAlunoMatriculado(Diario diario, String matriculaAluno) {
    if (matriculaAluno == null || matriculaAluno.isBlank()) {
      throw new EntradaInvalidaException("Matrícula do aluno não pode ser vazia.");
    }

    String matriculaNormalizada = matriculaAluno.trim();
    try {
      userRepository.buscarPorMatricula(matriculaNormalizada, TipoUsuario.ALUNO);
    } catch (UsuarioNaoEncontradoException e) {
      throw new EntradaInvalidaException("Aluno não encontrado.");
    }

    Turma turma = turmaRepository.buscarTurmaPorCodigo(diario.getCodigoTurma());
    if (turma == null) {
      throw new EntradaInvalidaException("Turma associada ao diário não encontrada.");
    }

    boolean alunoMatriculado =
        turma.getMatriculados() != null
            && turma.getMatriculados().stream()
                .anyMatch(matricula -> matricula.equalsIgnoreCase(matriculaNormalizada));

    if (!alunoMatriculado) {
      throw new EntradaInvalidaException("Aluno não está matriculado na turma do diário.");
    }
  }
}
