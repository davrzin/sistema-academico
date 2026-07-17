package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.gestaoacademica.ItemRelatorioUsuario;
import br.com.classroompb.model.entities.gestaoacademica.RelatorioGeralUsuarios;
import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testes do servico de relatorios de usuarios.
 */
public class RelatorioUsuarioServiceTest {

  private static final Usuario ADMIN_LOGADO =
      new Administrador("Admin Logado", "admin.logado@teste.com", "adm00", "123456");

  /**
   * UsuarioService de teste que devolve uma lista fixa de usuarios, sem depender
   * de repositorios reais.
   */
  private static class UsuarioServiceFalso extends UsuarioService {

    private final List<Usuario> usuarios;

    UsuarioServiceFalso(List<Usuario> usuarios) {
      super(null, null);
      this.usuarios = usuarios;
    }

    @Override
    public List<Usuario> listarUsuarios(Usuario usuarioLogado) {
      return usuarios;
    }
  }

  private RelatorioUsuarioService criarServico(List<Usuario> usuarios) {
    return new RelatorioUsuarioService(new UsuarioServiceFalso(usuarios));
  }

  @Test
  public void deveCriarRelatorioUsuarioServiceComConstrutorPadrao() {
    RelatorioUsuarioService servico = new RelatorioUsuarioService();

    Assertions.assertNotNull(servico);
  }

  @Test
  public void deveCriarRelatorioUsuarioServiceComDependenciaInformada() {
    RelatorioUsuarioService servico = criarServico(new ArrayList<>());

    Assertions.assertNotNull(servico);
  }

  @Test
  public void deveGerarRelatorioVazioQuandoNaoHaUsuarios() {
    RelatorioUsuarioService servico = criarServico(new ArrayList<>());

    RelatorioGeralUsuarios relatorio = servico.gerarRelatorioGeralUsuarios(ADMIN_LOGADO);

    Assertions.assertEquals(0, relatorio.getTotalGeral());
    Assertions.assertEquals(0, relatorio.getTotalAdministradores());
    Assertions.assertEquals(0, relatorio.getTotalCoordenadores());
    Assertions.assertEquals(0, relatorio.getTotalProfessores());
    Assertions.assertEquals(0, relatorio.getTotalAlunos());
    Assertions.assertTrue(relatorio.getUsuarios().isEmpty());
  }

  @Test
  public void deveContarCorretamenteCadaTipoDeUsuario() {
    Administrador administrador =
        new Administrador("Ana Admin", "ana.admin@teste.com", "adm01", "123456");
    Coordenador coordenador =
        new Coordenador("Bruno Coord", "bruno.coord@teste.com", "coo01", "123456", "cur00");
    Professor professor =
        new Professor("Carlos Prof", "carlos.prof@teste.com", "pro01", "123456", "cur00");
    Aluno aluno1 = new Aluno("Duda Aluna", "duda.aluna@teste.com", "alu01", "123456", "cur00");
    Aluno aluno2 = new Aluno("Elias Aluno", "elias.aluno@teste.com", "alu02", "123456", "cur01");

    List<Usuario> usuarios = List.of(administrador, coordenador, professor, aluno1, aluno2);
    RelatorioUsuarioService servico = criarServico(usuarios);

    RelatorioGeralUsuarios relatorio = servico.gerarRelatorioGeralUsuarios(ADMIN_LOGADO);

    Assertions.assertEquals(5, relatorio.getTotalGeral());
    Assertions.assertEquals(1, relatorio.getTotalAdministradores());
    Assertions.assertEquals(1, relatorio.getTotalCoordenadores());
    Assertions.assertEquals(1, relatorio.getTotalProfessores());
    Assertions.assertEquals(2, relatorio.getTotalAlunos());
    Assertions.assertEquals(5, relatorio.getUsuarios().size());
  }

  @Test
  public void deveMontarItensComOsDadosDoUsuario() {
    Aluno aluno = new Aluno("Duda Aluna", "duda.aluna@teste.com", "alu01", "123456", "cur00");

    RelatorioUsuarioService servico = criarServico(List.of(aluno));

    RelatorioGeralUsuarios relatorio = servico.gerarRelatorioGeralUsuarios(ADMIN_LOGADO);
    ItemRelatorioUsuario item = relatorio.getUsuarios().get(0);

    Assertions.assertEquals("ALUNO", item.getTipoUsuario());
    Assertions.assertEquals("Duda Aluna", item.getNome());
    Assertions.assertEquals("alu01", item.getMatricula());
    Assertions.assertEquals("duda.aluna@teste.com", item.getEmail());
    Assertions.assertEquals("cur00", item.getCodigoCurso());
  }

  @Test
  public void deveMontarItemDeProfessorComCodigoDeCurso() {
    Professor professor =
        new Professor("Carlos Prof", "carlos.prof@teste.com", "pro01", "123456", "cur00");

    RelatorioUsuarioService servico = criarServico(List.of(professor));

    ItemRelatorioUsuario item =
        servico.gerarRelatorioGeralUsuarios(ADMIN_LOGADO).getUsuarios().get(0);

    Assertions.assertEquals("cur00", item.getCodigoCurso());
  }

  @Test
  public void deveMontarItemDeCoordenadorComCodigoDeCurso() {
    Coordenador coordenador =
        new Coordenador("Bruno Coord", "bruno.coord@teste.com", "coo01", "123456", "cur00");

    RelatorioUsuarioService servico = criarServico(List.of(coordenador));

    ItemRelatorioUsuario item =
        servico.gerarRelatorioGeralUsuarios(ADMIN_LOGADO).getUsuarios().get(0);

    Assertions.assertEquals("cur00", item.getCodigoCurso());
  }

  @Test
  public void deveMontarItemDeAdministradorSemCodigoDeCurso() {
    Administrador administrador =
        new Administrador("Ana Admin", "ana.admin@teste.com", "adm01", "123456");

    RelatorioUsuarioService servico = criarServico(List.of(administrador));

    ItemRelatorioUsuario item =
        servico.gerarRelatorioGeralUsuarios(ADMIN_LOGADO).getUsuarios().get(0);

    Assertions.assertNull(item.getCodigoCurso());
  }

  @Test
  public void deveOrdenarPorTipoDeUsuario() {
    Aluno aluno = new Aluno("Zeca Aluno", "zeca.aluno@teste.com", "alu01", "123456", "cur00");
    Professor professor =
        new Professor("Yara Prof", "yara.prof@teste.com", "pro01", "123456", "cur00");
    Coordenador coordenador =
        new Coordenador("Xico Coord", "xico.coord@teste.com", "coo01", "123456", "cur00");
    Administrador administrador =
        new Administrador("Wilma Admin", "wilma.admin@teste.com", "adm01", "123456");

    List<Usuario> usuarios = List.of(aluno, professor, coordenador, administrador);
    RelatorioUsuarioService servico = criarServico(usuarios);

    List<ItemRelatorioUsuario> itens =
        servico.gerarRelatorioGeralUsuarios(ADMIN_LOGADO).getUsuarios();

    Assertions.assertEquals("ADMINISTRADOR", itens.get(0).getTipoUsuario());
    Assertions.assertEquals("COORDENADOR", itens.get(1).getTipoUsuario());
    Assertions.assertEquals("PROFESSOR", itens.get(2).getTipoUsuario());
    Assertions.assertEquals("ALUNO", itens.get(3).getTipoUsuario());
  }

  @Test
  public void deveOrdenarPorNomeIgnorandoCaixaDentroDoMesmoTipo() {
    Aluno alunoB = new Aluno("bruno", "bruno.aluno@teste.com", "alu02", "123456", "cur00");
    Aluno alunoA = new Aluno("Ana", "ana.aluna@teste.com", "alu01", "123456", "cur00");

    List<Usuario> usuarios = List.of(alunoB, alunoA);
    RelatorioUsuarioService servico = criarServico(usuarios);

    List<ItemRelatorioUsuario> itens =
        servico.gerarRelatorioGeralUsuarios(ADMIN_LOGADO).getUsuarios();

    Assertions.assertEquals("Ana", itens.get(0).getNome());
    Assertions.assertEquals("bruno", itens.get(1).getNome());
  }

  @Test
  public void deveOrdenarPorMatriculaQuandoNomesForemIguais() {
    Aluno aluno1 = new Aluno("Mesmo Nome", "aluno1@teste.com", "alu02", "123456", "cur00");
    Aluno aluno2 = new Aluno("Mesmo Nome", "aluno2@teste.com", "alu01", "123456", "cur00");

    List<Usuario> usuarios = List.of(aluno1, aluno2);
    RelatorioUsuarioService servico = criarServico(usuarios);

    List<ItemRelatorioUsuario> itens =
        servico.gerarRelatorioGeralUsuarios(ADMIN_LOGADO).getUsuarios();

    Assertions.assertEquals("alu01", itens.get(0).getMatricula());
    Assertions.assertEquals("alu02", itens.get(1).getMatricula());
  }
}
