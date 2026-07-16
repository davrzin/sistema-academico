package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioUsuario;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioGeralUsuarios;
import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.model.services.RelatorioUsuarioService;

/**
 * Tela de interacao para relatorios de usuarios.
 */
public class RelatorioUsuarioTela {

  private final RelatorioUsuarioService relatorioService = new RelatorioUsuarioService();

  /**
   * Cria a tela de relatorios de usuarios.
   */
  public RelatorioUsuarioTela() {}

  /**
   * Exibe o relatorio geral de usuarios cadastrados.
   *
   * @param administradorLogado administrador logado.
   */
  public void gerarRelatorioGeralUsuarios(Administrador administradorLogado) {
    try {
      RelatorioGeralUsuarios relatorio =
          relatorioService.gerarRelatorioGeralUsuarios(administradorLogado);
      exibirRelatorio(relatorio);
    } catch (RuntimeException e) {
      System.out.println("Erro ao gerar relatório de usuários: " + e.getMessage());
    }
  }

  private void exibirRelatorio(RelatorioGeralUsuarios relatorio) {
    System.out.println("Relatório geral de usuários cadastrados");
    System.out.println();
    System.out.println("Resumo:");
    System.out.println("Total geral: " + relatorio.getTotalGeral());
    System.out.println("Administradores: " + relatorio.getTotalAdministradores());
    System.out.println("Coordenadores: " + relatorio.getTotalCoordenadores());
    System.out.println("Professores: " + relatorio.getTotalProfessores());
    System.out.println("Alunos: " + relatorio.getTotalAlunos());
    System.out.println();
    System.out.println("Usuários cadastrados:");

    if (relatorio.getUsuarios().isEmpty()) {
      System.out.println();
      System.out.println("Nenhum usuário cadastrado.");
      return;
    }

    for (int i = 0; i < relatorio.getUsuarios().size(); i++) {
      System.out.println();
      exibirUsuario(i + 1, relatorio.getUsuarios().get(i));
    }
  }

  private void exibirUsuario(int numero, ItemRelatorioUsuario usuario) {
    System.out.println(numero + " - " + formatarValor(usuario.getTipoUsuario()));
    System.out.println("    Nome: " + formatarValor(usuario.getNome()));
    System.out.println("    Matrícula: " + formatarValor(usuario.getMatricula()));
    System.out.println("    Email: " + formatarValor(usuario.getEmail()));
    System.out.println("    Curso: " + formatarCurso(usuario.getCodigoCurso()));
  }

  private String formatarValor(String valor) {
    if (valor == null || valor.isBlank()) {
      return "-";
    }
    return valor;
  }

  private String formatarCurso(String codigoCurso) {
    if (codigoCurso == null || codigoCurso.isBlank()) {
      return "Não se aplica";
    }
    return codigoCurso;
  }
}
