package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.UsuarioNaoEncontradoException;
import br.com.classroompb.model.repository.CursoRepository;
import br.com.classroompb.model.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia o vinculo entre curso e coordenador usando o coordenador como fonte unica.
 */
public class VinculoCursoCoordenadorService {

  private final CursoRepository cursoRepository;
  private final UserRepository userRepository;

  /**
   * Cria o servico com os repositorios informados.
   *
   * @param cursoRepository repositorio de cursos.
   * @param userRepository repositorio de usuarios.
   */
  public VinculoCursoCoordenadorService(
      CursoRepository cursoRepository, UserRepository userRepository) {
    this.cursoRepository = cursoRepository;
    this.userRepository = userRepository;
  }

  /**
   * Busca o coordenador responsavel por um curso.
   *
   * @param codigoCurso codigo do curso.
   * @return coordenador responsavel ou null.
   */
  public Coordenador buscarCoordenadorPorCurso(String codigoCurso) {
    validarCursoExistente(codigoCurso);
    Coordenador encontrado = null;

    for (Usuario usuario : userRepository.listar(TipoUsuario.COORDENADOR)) {
      if (usuario instanceof Coordenador coordenador
          && iguais(coordenador.getCodigoCurso(), codigoCurso)) {
        if (encontrado != null) {
          throw new EntradaInvalidaException("Mais de um coordenador vinculado ao mesmo curso.");
        }
        encontrado = coordenador;
      }
    }

    return encontrado;
  }

  /**
   * Lista coordenadores que ainda nao administram curso.
   *
   * @return coordenadores sem curso.
   */
  public List<Coordenador> listarCoordenadoresSemCurso() {
    List<Coordenador> coordenadores = new ArrayList<>();
    for (Usuario usuario : userRepository.listar(TipoUsuario.COORDENADOR)) {
      if (usuario instanceof Coordenador coordenador
          && !possuiValor(coordenador.getCodigoCurso())) {
        coordenadores.add(coordenador);
      }
    }
    return coordenadores;
  }

  /**
   * Vincula um coordenador a um curso.
   *
   * @param matricula matricula do coordenador.
   * @param codigoCurso codigo do curso.
   */
  public void vincular(String matricula, String codigoCurso) {
    validarCursoExistente(codigoCurso);
    Coordenador coordenador = buscarCoordenador(matricula);

    if (iguais(coordenador.getCodigoCurso(), codigoCurso)) {
      validarExclusividade(codigoCurso, coordenador.getMatricula());
      return;
    }

    if (possuiValor(coordenador.getCodigoCurso())) {
      throw new EntradaInvalidaException("Coordenador ja administra outro curso.");
    }

    validarExclusividade(codigoCurso, coordenador.getMatricula());
    coordenador.setCodigoCurso(codigoCurso.trim());
    userRepository.atualizarUsuario(coordenador);
  }

  /**
   * Valida se o coordenador existe e ainda esta disponivel.
   *
   * @param matricula matricula do coordenador.
   */
  public void validarCoordenadorDisponivel(String matricula) {
    Coordenador coordenador = buscarCoordenador(matricula);
    if (possuiValor(coordenador.getCodigoCurso())) {
      throw new EntradaInvalidaException("Coordenador ja administra outro curso.");
    }
  }

  /**
   * Desvincula um coordenador de seu curso.
   *
   * @param matricula matricula do coordenador.
   */
  public void desvincular(String matricula) {
    Coordenador coordenador = buscarCoordenador(matricula);
    coordenador.setCodigoCurso(null);
    userRepository.atualizarUsuario(coordenador);
  }

  /**
   * Valida se um curso esta disponivel para receber coordenador.
   *
   * @param codigoCurso codigo do curso.
   */
  public void validarCursoDisponivel(String codigoCurso) {
    validarCursoExistente(codigoCurso);
    validarExclusividade(codigoCurso, null);
  }

  private void validarExclusividade(String codigoCurso, String matriculaIgnorada) {
    for (Usuario usuario : userRepository.listar(TipoUsuario.COORDENADOR)) {
      if (usuario instanceof Coordenador coordenador
          && iguais(coordenador.getCodigoCurso(), codigoCurso)
          && !iguais(coordenador.getMatricula(), matriculaIgnorada)) {
        throw new EntradaInvalidaException("Curso ja possui coordenador.");
      }
    }
  }

  private void validarCursoExistente(String codigoCurso) {
    if (!possuiValor(codigoCurso)) {
      throw new EntradaInvalidaException("Codigo do curso deve ser informado.");
    }

    Curso curso = cursoRepository.buscarPorCodigo(codigoCurso.trim());
    if (curso == null) {
      throw new EntradaInvalidaException("Curso informado nao encontrado.");
    }
  }

  private Coordenador buscarCoordenador(String matricula) {
    if (!possuiValor(matricula)) {
      throw new EntradaInvalidaException("Matricula do coordenador deve ser informada.");
    }

    try {
      Usuario usuario = userRepository.buscarPorMatricula(matricula.trim());
      if (!(usuario instanceof Coordenador coordenador)) {
        throw new EntradaInvalidaException("Usuario informado nao e coordenador.");
      }
      return coordenador;
    } catch (UsuarioNaoEncontradoException e) {
      throw new EntradaInvalidaException("Coordenador informado nao encontrado.");
    }
  }

  private boolean possuiValor(String valor) {
    return valor != null && !valor.isBlank();
  }

  private boolean iguais(String primeiro, String segundo) {
    return possuiValor(primeiro)
        && possuiValor(segundo)
        && primeiro.trim().equalsIgnoreCase(segundo.trim());
  }
}
