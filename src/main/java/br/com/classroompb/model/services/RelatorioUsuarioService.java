package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioUsuario;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioGeralUsuarios;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Servico responsavel pelos relatorios de usuarios.
 */
public class RelatorioUsuarioService {

  private final UsuarioService usuarioService;

  /**
   * Cria o servico de relatorios com dependencia padrao.
   */
  public RelatorioUsuarioService() {
    this(new UsuarioService());
  }

  /**
   * Cria o servico de relatorios com dependencia informada.
   *
   * @param usuarioService servico de usuarios.
   */
  public RelatorioUsuarioService(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  /**
   * Gera o relatorio geral de usuarios cadastrados.
   *
   * @param administradorLogado usuario que solicitou o relatorio.
   * @return relatorio geral de usuarios.
   */
  public RelatorioGeralUsuarios gerarRelatorioGeralUsuarios(Usuario administradorLogado) {
    List<Usuario> usuarios = usuarioService.listarUsuarios(administradorLogado);
    List<ItemRelatorioUsuario> itens = new ArrayList<>();

    for (Usuario usuario : usuarios) {
      itens.add(montarItem(usuario));
    }

    itens.sort(criarComparador());

    RelatorioGeralUsuarios relatorio = new RelatorioGeralUsuarios();
    relatorio.setTotalGeral(itens.size());
    relatorio.setTotalAdministradores(contarTipo(itens, TipoUsuario.ADMINISTRADOR));
    relatorio.setTotalCoordenadores(contarTipo(itens, TipoUsuario.COORDENADOR));
    relatorio.setTotalProfessores(contarTipo(itens, TipoUsuario.PROFESSOR));
    relatorio.setTotalAlunos(contarTipo(itens, TipoUsuario.ALUNO));
    relatorio.setUsuarios(itens);
    return relatorio;
  }

  private ItemRelatorioUsuario montarItem(Usuario usuario) {
    ItemRelatorioUsuario item = new ItemRelatorioUsuario();
    item.setTipoUsuario(usuario.getTipoUsuario().name());
    item.setNome(usuario.getNome());
    item.setMatricula(usuario.getMatricula());
    item.setEmail(usuario.getEmail());
    item.setCodigoCurso(buscarCodigoCurso(usuario));
    return item;
  }

  private String buscarCodigoCurso(Usuario usuario) {
    if (usuario instanceof Aluno aluno) {
      return aluno.getCodigoCurso();
    }
    if (usuario instanceof Professor professor) {
      return professor.getCodigoCurso();
    }
    if (usuario instanceof Coordenador coordenador) {
      return coordenador.getCodigoCurso();
    }
    return null;
  }

  private int contarTipo(List<ItemRelatorioUsuario> itens, TipoUsuario tipoUsuario) {
    int total = 0;
    for (ItemRelatorioUsuario item : itens) {
      if (tipoUsuario.name().equals(item.getTipoUsuario())) {
        total++;
      }
    }
    return total;
  }

  private Comparator<ItemRelatorioUsuario> criarComparador() {
    return Comparator.comparingInt(this::ordemTipo)
        .thenComparing(this::nomeParaOrdenacao, String.CASE_INSENSITIVE_ORDER)
        .thenComparing(this::matriculaParaOrdenacao, String.CASE_INSENSITIVE_ORDER);
  }

  private int ordemTipo(ItemRelatorioUsuario item) {
    return switch (TipoUsuario.valueOf(item.getTipoUsuario())) {
      case ADMINISTRADOR -> 1;
      case COORDENADOR -> 2;
      case PROFESSOR -> 3;
      case ALUNO -> 4;
    };
  }

  private String nomeParaOrdenacao(ItemRelatorioUsuario item) {
    if (item.getNome() == null || item.getNome().isBlank()) {
      return matriculaParaOrdenacao(item);
    }
    return item.getNome();
  }

  private String matriculaParaOrdenacao(ItemRelatorioUsuario item) {
    return item.getMatricula() == null ? "" : item.getMatricula();
  }
}
