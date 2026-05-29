package br.com.classroompb.ui.tela;

import java.util.List;
import java.util.Scanner;

import br.com.classroompb.model.entities.Usuario.Administrador;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.entities.Usuario.Coordenador;
import br.com.classroompb.model.entities.Usuario.Professor;
import br.com.classroompb.model.entities.Usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.services.UsuarioService;

public class UsuarioTela {

    private final Scanner scanner;
    private final UsuarioService usuarioService = new UsuarioService();

    public UsuarioTela(Scanner scanner) {
        this.scanner = scanner;
    }

    public void cadastrarUsuario() {
        try {
            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            TipoUsuario tipoUsuario = escolherTipoUsuario();

            Usuario novoUsuario = criarUsuarioPorTipo(tipoUsuario, nome, email, senha);

            usuarioService.cadastrarUsuario(novoUsuario);

            System.out.println("Usuário cadastrado com sucesso!");
            System.out.println("Matrícula gerada: " + novoUsuario.getMatricula());

        } catch (RuntimeException e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    public void buscarUsuarioPorMatricula(Usuario usuarioLogado) {
        System.out.print("Digite a matrícula: ");
        String matricula = scanner.nextLine();

        try {
            Usuario usuarioEncontrado = usuarioService.buscarUsuarioPorMatricula(usuarioLogado, matricula);
            exibirUsuario(usuarioEncontrado);

        } catch (RuntimeException e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
        }
    }

    public void listarUsuarios(Usuario usuarioLogado) {
        try {
            List<Usuario> usuarios = usuarioService.listarUsuarios(usuarioLogado);
            exibirListaUsuarios(usuarios);

        } catch (RuntimeException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }
    }

    public void removerUsuario(Usuario usuarioLogado) {
        System.out.print("Digite a matrícula do usuário que deseja remover: ");
        String matricula = scanner.nextLine();

        System.out.print("Tem certeza que deseja remover esse usuário? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (!confirmacao.equalsIgnoreCase("S")) {
            System.out.println("Remoção cancelada.");
            return;
        }

        try {
            Usuario usuarioRemovido = usuarioService.removerUsuarioPorMatricula(usuarioLogado, matricula);

            System.out.println("Usuário removido com sucesso.");
            System.out.println("Matrícula removida: " + usuarioRemovido.getMatricula());

        } catch (RuntimeException e) {
            System.out.println("Erro ao remover usuário: " + e.getMessage());
        }
    }

    public void atualizarUsuario(Usuario usuarioLogado) {
        System.out.print("Digite a matrícula do usuário que deseja atualizar: ");
        String matricula = scanner.nextLine();

        System.out.println("Digite os novos dados. Pressione ENTER para manter o valor atual.");

        System.out.print("Novo nome: ");
        String novoNome = scanner.nextLine();

        System.out.print("Novo email: ");
        String novoEmail = scanner.nextLine();

        System.out.print("Nova senha: ");
        String novaSenha = scanner.nextLine();

        try {
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(
                    usuarioLogado,
                    matricula,
                    novoNome,
                    novoEmail,
                    novaSenha
            );

            System.out.println("Usuário atualizado com sucesso.");
            exibirUsuario(usuarioAtualizado);

        } catch (RuntimeException e) {
            System.out.println("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    private void exibirListaUsuarios(List<Usuario> usuarios) {
        if (usuarios == null || usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        for (Usuario usuario : usuarios) {
            exibirUsuario(usuario);
        }
    }

    private TipoUsuario escolherTipoUsuario() {
        while (true) {
            System.out.println("""
                    Escolha o tipo de usuário:
                    1 - Administrador
                    2 - Coordenador
                    3 - Professor
                    4 - Aluno
                    """);

            System.out.print("Digite uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    return TipoUsuario.ADMINISTRADOR;

                case "2":
                    return TipoUsuario.COORDENADOR;

                case "3":
                    return TipoUsuario.PROFESSOR;

                case "4":
                    return TipoUsuario.ALUNO;

                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    private Usuario criarUsuarioPorTipo(
            TipoUsuario tipoUsuario,
            String nome,
            String email,
            String senha
    ) {
        switch (tipoUsuario) {
            case ALUNO:
                return new Aluno(nome, email, senha);

            case PROFESSOR:
                return new Professor(nome, email, senha);

            case COORDENADOR:
                return new Coordenador(nome, email, senha);

            case ADMINISTRADOR:
                return new Administrador(nome, email, senha);

            default:
                throw new IllegalArgumentException("Tipo de usuário inválido.");
        }
    }

    private void exibirUsuario(Usuario usuario) {
        System.out.println("\nUsuário:");
        System.out.println("Nome: " + usuario.getNome());
        System.out.println("E-mail: " + usuario.getEmail());
        System.out.println("Matrícula: " + usuario.getMatricula());
        System.out.println("Tipo: " + usuario.getTipoUsuario());
        System.out.println();
    }
}
