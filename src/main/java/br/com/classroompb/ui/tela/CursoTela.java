package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.CursoService;
import java.util.List;
import java.util.Scanner;

/**
 * Tela de interacao para operacoes de curso.
 */
public class CursoTela {

  private final Scanner scanner;
  private final CursoService cursoService;

  /**
   * Cria a tela de cursos.
   *
   * @param scanner leitor de entrada.
   */
  public CursoTela(Scanner scanner) {
    this(scanner, new CursoService());
  }

  /**
   * Cria a tela de cursos com servico informado.
   *
   * @param scanner leitor de entrada.
   * @param cursoService servico de cursos.
   */
  public CursoTela(Scanner scanner, CursoService cursoService) {
    this.scanner = scanner;
    this.cursoService = cursoService;
  }

  /**
   * Solicita os dados para cadastro de curso.
   */
  public void cadastrarCurso() {
    try {
      String nome =
          EntradaTela.lerTextoObrigatorioOuCancelar(
              scanner, "Informe o nome do curso: ", "Nome");

      int quantidadePeriodos =
          EntradaTela.lerInteiroPositivoOuCancelar(
              scanner, "Informe a quantidade de periodos: ");

      int cargaHorariaTotal =
          EntradaTela.lerInteiroPositivoOuCancelar(
              scanner, "Informe a carga horaria total: ");

      String matricula = selecionarCoordenadorDoCurso();

      Curso novoCurso = new Curso(nome, quantidadePeriodos, cargaHorariaTotal);
      cursoService.cadastrarCurso(novoCurso, matricula);

      System.out.println("Curso cadastrado com sucesso.");
      System.out.println("Código gerado: " + novoCurso.getCodigo());

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Cadastro de curso cancelado.");

    } catch (PersistenciaException | EntradaInvalidaException e) {
      System.out.println("Ocorreu um erro ao cadastrar curso: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Valor numérico inválido.");
    }
  }

  /**
   * Lista os cursos cadastrados no terminal.
   */
  public void listarCursos() {
    List<Curso> cursos = cursoService.listarCursos();
    exibirListaCursos(cursos);
  }

  private void exibirListaCursos(List<Curso> cursos) {
    if (cursos == null || cursos.isEmpty()) {
      System.out.println("Nenhum curso cadastrado.");
      return;
    }

    for (int i = 0; i < cursos.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirCurso(i + 1, cursos.get(i));
    }
  }

  private void exibirCurso(int numero, Curso curso) {
    System.out.println(numero + " - " + curso.getNome());
    System.out.println("    Codigo interno: " + curso.getCodigo());
    System.out.println("    Periodos: " + curso.getQuantidadePeriodos());
    System.out.println("    Carga horaria: " + curso.getCargaHorariaTotal() + "h");
    System.out.println("    Coordenador: " + buscarNomeCoordenador(curso.getCodigo()));
  }

  private String selecionarCoordenadorDoCurso() {
    List<Coordenador> coordenadores = cursoService.listarCoordenadoresSemCurso();

    listarCoordenadoresParaSelecao(coordenadores);
    int opcaoSemCoordenador = coordenadores.size() + 1;
    int opcao = lerOpcaoCoordenador(opcaoSemCoordenador);

    if (opcao == opcaoSemCoordenador) {
      return null;
    }

    return coordenadores.get(opcao - 1).getMatricula();
  }

  private void listarCoordenadoresParaSelecao(List<Coordenador> coordenadores) {
    if (coordenadores.isEmpty()) {
      System.out.println("Nenhum coordenador disponivel para vinculacao.");
    } else {
      System.out.println("Coordenadores disponiveis:");
    }
    System.out.println();

    for (int i = 0; i < coordenadores.size(); i++) {
      Coordenador coordenador = coordenadores.get(i);
      System.out.println((i + 1) + " - " + coordenador.getNome());
      System.out.println("    Matricula: " + coordenador.getMatricula());
      System.out.println("    Email: " + coordenador.getEmail());
      System.out.println();
    }

    System.out.println((coordenadores.size() + 1) + " - Cadastrar curso sem coordenador");
    System.out.println("0 - Voltar");
    System.out.println();
  }

  private String buscarNomeCoordenador(String codigoCurso) {
    Coordenador coordenador = cursoService.buscarCoordenadorPorCurso(codigoCurso);
    return coordenador == null ? "Não definido" : coordenador.getNome();
  }

  private int lerOpcaoCoordenador(int opcaoSemCoordenador) {
    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner, "Informe uma opcao: ", opcaoSemCoordenador);

    if (opcao == 0) {
      throw new EntradaTela.EntradaCanceladaException();
    }

    return opcao;
  }
}
