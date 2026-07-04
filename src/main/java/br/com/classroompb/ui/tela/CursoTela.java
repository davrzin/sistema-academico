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
      System.out.println("Informe o nome do curso:");
      String nome = scanner.nextLine();

      System.out.println("Informe a quantidade de períodos:");
      int quantidadePeriodos = Integer.parseInt(scanner.nextLine());

      System.out.println("Informe a carga horária total:");
      int cargaHorariaTotal = Integer.parseInt(scanner.nextLine());

      String matriculaCoordenador = selecionarCoordenadorDoCurso();

      Curso novoCurso =
          new Curso(nome, quantidadePeriodos, cargaHorariaTotal, matriculaCoordenador);
      cursoService.cadastrarCurso(novoCurso);

      System.out.println("Curso cadastrado com sucesso.");
      System.out.println("Código gerado: " + novoCurso.getCodigo());

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

    for (Curso curso : cursos) {
      exibirCurso(curso);
    }
  }

  private void exibirCurso(Curso curso) {
    System.out.println("\nCurso:");
    System.out.println("Código: " + curso.getCodigo());
    System.out.println("Nome: " + curso.getNome());
    System.out.println("Quantidade de períodos: " + curso.getQuantidadePeriodos());
    System.out.println("Carga horária total: " + curso.getCargaHorariaTotal() + "h");
    System.out.println("Matricula do coordenador: " + curso.getMatriculaCoordenador());
    System.out.println();
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
    System.out.println("0 - Deixar sem coordenador por enquanto");

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

  private int lerOpcaoCoordenador(int quantidadeCoordenadores) {
    System.out.print("Escolha o numero do coordenador ou 0 para deixar sem coordenador: ");
    String entrada = scanner.nextLine();
    int opcao;

    try {
      opcao = Integer.parseInt(entrada);
    } catch (NumberFormatException e) {
      throw new EntradaInvalidaException("Opcao invalida. Digite o numero de uma opcao listada.");
    }

    if (opcao < 0 || opcao > quantidadeCoordenadores) {
      throw new EntradaInvalidaException("Opcao invalida. Escolha uma opcao da lista.");
    }

    return opcao;
  }
}
