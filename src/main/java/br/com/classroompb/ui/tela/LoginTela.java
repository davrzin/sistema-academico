package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.services.UsuarioService;
import br.com.classroompb.ui.menu.MenuAdministrador;
import br.com.classroompb.ui.menu.MenuAluno;
import br.com.classroompb.ui.menu.MenuCoordenador;
import br.com.classroompb.ui.menu.MenuProfessor;
import java.util.Scanner;

/**
 * Tela de interacao para autenticacao de usuarios.
 */
public class LoginTela {

  private final Scanner scanner;
  private final UsuarioService usuarioService = new UsuarioService();

  /**
   * Cria a tela de login.
   *
   * @param scanner leitor de entrada.
   */
  public LoginTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Solicita os dados de login do usuario.
   */
  public void login() {
    System.out.print("Email: ");
    String emailUsuario = scanner.nextLine();

    System.out.print("Senha: ");
    String senhaUsuario = scanner.nextLine();
    System.out.println();

    Usuario usuario = usuarioService.fazerLoginUsuario(emailUsuario, senhaUsuario);

    if (usuario == null) {
      System.out.println("Email ou senha inválidos.");
      return;
    }

    redirecionarUsuario(usuario);
  }

  private void redirecionarUsuario(Usuario usuario) {
    switch (usuario.getTipoUsuario()) {
      case ALUNO:
        MenuAluno menuAluno = new MenuAluno((Aluno) usuario);
        menuAluno.iniciar();
        break;

      case PROFESSOR:
        MenuProfessor menuProfessor = new MenuProfessor((Professor) usuario);
        menuProfessor.iniciar();
        break;

      case COORDENADOR:
        MenuCoordenador menuCoordenador = new MenuCoordenador((Coordenador) usuario);
        menuCoordenador.iniciar();
        break;

      case ADMINISTRADOR:
        MenuAdministrador menuAdmin = new MenuAdministrador((Administrador) usuario);
        menuAdmin.iniciar();
        break;

      default:
        System.out.println("Tipo de usuário inválido.");
    }
  }
}
