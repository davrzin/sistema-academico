package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.CursoService;
import br.com.classroompb.model.services.UsuarioService;
import java.util.List;
import java.util.Scanner;

/**
 * Tela de interacao para operacoes de curso.
 */
public class CursoTela {

  private final Scanner scanner;
  private final CursoService cursoService = new CursoService();
  private final UsuarioService usuarioService = new UsuarioService();

  /**
   * Cria a tela de cursos.
   *
   * @param scanner leitor de entrada.
   */
  public CursoTela(Scanner scanner) {
    this.scanner = scanner;
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

      String matriculaCoordenador = selecionarCoordenadorDoCurso();

      Curso novoCurso =
          new Curso(nome, quantidadePeriodos, cargaHorariaTotal, matriculaCoordenador);
      cursoService.cadastrarCurso(novoCurso);

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
    System.out.println(
        "    Coordenador: " + buscarNomeCoordenador(curso.getMatriculaCoordenador()));
  }

  private String selecionarCoordenadorDoCurso() {
    List<Coordenador> coordenadores = usuarioService.listarCoordenadores();

    if (coordenadores == null || coordenadores.isEmpty()) {
      System.out.println("Nenhum coordenador cadastrado. O curso sera cadastrado sem coordenador.");
      return null;
    }

    listarCoordenadoresParaSelecao(coordenadores);
    int opcao = lerOpcaoCoordenador(coordenadores.size());

    if (opcao == 0) {
      return null;
    }

    Coordenador coordenador = coordenadores.get(opcao - 1);

    if (coordenador.getCodigoCurso() != null && !coordenador.getCodigoCurso().isBlank()) {
      throw new EntradaInvalidaException("Coordenador ja esta vinculado a outro curso.");
    }

    return coordenador.getMatricula();
  }

  private void listarCoordenadoresParaSelecao(List<Coordenador> coordenadores) {
    System.out.println("Coordenadores cadastrados:");
    System.out.println("0 - Cancelar cadastro");

    for (int i = 0; i < coordenadores.size(); i++) {
      Coordenador coordenador = coordenadores.get(i);
      String cursoVinculado = coordenador.getCodigoCurso();

      if (cursoVinculado == null || cursoVinculado.isBlank()) {
        cursoVinculado = "sem curso vinculado";
      } else {
        cursoVinculado = "vinculado ao curso " + cursoVinculado;
      }

      System.out.println(
          (i + 1)
              + " - "
              + coordenador.getNome()
              + " | Matricula: "
              + coordenador.getMatricula()
              + " | "
              + cursoVinculado);
    }
  }

  private String buscarNomeCoordenador(String matriculaCoordenador) {
    if (matriculaCoordenador == null || matriculaCoordenador.isBlank()) {
      return "-";
    }

    for (Coordenador coordenador : usuarioService.listarCoordenadores()) {
      if (matriculaCoordenador.equalsIgnoreCase(coordenador.getMatricula())) {
        return coordenador.getNome();
      }
    }

    return matriculaCoordenador;
  }

  private int lerOpcaoCoordenador(int quantidadeCoordenadores) {
    int opcao =
        EntradaTela.lerOpcaoOuCancelar(
            scanner,
            "Escolha o numero do coordenador ou 0 para cancelar: ",
            quantidadeCoordenadores);

    if (opcao == 0) {
      throw new EntradaTela.EntradaCanceladaException();
    }

    return opcao;
  }
}
