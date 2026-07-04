package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.entities.gestaoacademica.Curso;
import br.com.classroompb.model.entities.gestaoacademica.Turma;
import br.com.classroompb.model.entities.usuario.Administrador;
import br.com.classroompb.model.entities.usuario.Aluno;
import br.com.classroompb.model.entities.usuario.Coordenador;
import br.com.classroompb.model.entities.usuario.Professor;
import br.com.classroompb.model.entities.usuario.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;
import br.com.classroompb.model.services.BoletimService;
import br.com.classroompb.model.services.CursoService;
import br.com.classroompb.model.services.TurmaService;
import br.com.classroompb.model.services.UsuarioService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Tela de interacao para operacoes de usuario.
 */
public class UsuarioTela {

  private final Scanner scanner;
  private final UsuarioService usuarioService = new UsuarioService();
  private final BoletimService boletimService = new BoletimService();
  private final CursoService cursoService = new CursoService();
  private final TurmaService turmaService = new TurmaService();

  /**
   * Cria a tela de usuarios.
   *
   * @param scanner leitor de entrada.
   */
  public UsuarioTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Solicita os dados para cadastro de usuario.
   */
  public void cadastrarUsuario() {
    try {
      System.out.print("Nome: ");
      String nome = scanner.nextLine();

      System.out.print("Email: ");
      String email = scanner.nextLine();

      System.out.print("Senha: ");
      String senha = scanner.nextLine();

      TipoUsuario tipoUsuario = escolherTipoUsuario();

      String codigoCurso = lerCodigoCursoSeNecessario(tipoUsuario);

      Usuario novoUsuario = criarUsuarioPorTipo(tipoUsuario, nome, email, senha, codigoCurso);

      usuarioService.cadastrarUsuario(novoUsuario);

      System.out.println("Usuário cadastrado com sucesso!");
      System.out.println("Matrícula gerada: " + novoUsuario.getMatricula());

    } catch (RuntimeException e) {
      System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
    }
  }

  /**
   * Solicita a matricula para busca de usuario.
   *
   * @param usuarioLogado usuario logado.
   */
  public void buscarUsuarioPorMatricula(Usuario usuarioLogado) {
    if (usuarioLogado instanceof Professor professorLogado) {
      try {
        buscarAlunoDoProfessor(professorLogado);
      } catch (RuntimeException e) {
        System.out.println("Erro ao buscar usuário: " + e.getMessage());
      }
      return;
    }

    System.out.print("Digite a matrícula: ");
    String matricula = scanner.nextLine();

    try {
      Usuario usuarioEncontrado =
          usuarioService.buscarUsuarioPorMatricula(usuarioLogado, matricula);

      if (usuarioLogado instanceof Professor professorLogado) {
        validarAlunoPertenceAoProfessor(professorLogado, usuarioEncontrado);
      }

      exibirUsuario(usuarioEncontrado);

    } catch (RuntimeException e) {
      System.out.println("Erro ao buscar usuário: " + e.getMessage());
    }
  }

  /**
   * Lista usuarios conforme o perfil logado.
   *
   * @param usuarioLogado usuario logado.
   */
  public void listarUsuarios(Usuario usuarioLogado) {
    try {
      if (usuarioLogado instanceof Professor professorLogado) {
        listarAlunosDoProfessor(professorLogado);
        return;
      }

      List<Usuario> usuarios = usuarioService.listarUsuarios(usuarioLogado);
      exibirListaUsuarios(usuarios);

    } catch (RuntimeException e) {
      System.out.println("Erro ao listar usuários: " + e.getMessage());
    }
  }

  /**
   * Solicita a remocao de usuario.
   *
   * @param usuarioLogado usuario logado.
   */
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

  /**
   * Solicita a atualizacao de usuario.
   *
   * @param usuarioLogado usuario logado.
   */
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
      Usuario usuarioAtualizado =
          usuarioService.atualizarUsuario(usuarioLogado, matricula, novoNome, novoEmail, novaSenha);

      System.out.println("Usuário atualizado com sucesso.");
      exibirUsuario(usuarioAtualizado);

    } catch (RuntimeException e) {
      System.out.println("Erro ao atualizar usuário: " + e.getMessage());
    }
  }

  /**
   * Exibe o boletim do aluno.
   *
   * @param aluno aluno consultado.
   */
  public void exibirBoletinAluno(Aluno aluno) {

    List<Boletim> boletins = boletimService.buscarBoletinsPorAluno(aluno.getMatricula());

    if (boletins == null || boletins.isEmpty()) {
      System.out.println("Nenhum boletim encontrado para o aluno.");
      return;
    }

    for (Boletim boletim : boletins) {
      Turma turma = buscarTurmaDoBoletim(boletim);
      double media = calcularMediaBoletim(boletim);

      System.out.println("==================BOLETIM====================");
      System.out.println("Disciplina: " + buscarNomeDisciplinaBoletim(turma, boletim));
      System.out.println("Professor: " + buscarNomeProfessorBoletim(turma));
      System.out.println("Periodo letivo: " + buscarPeriodoLetivoBoletim(turma));
      System.out.println("Turma: " + boletim.getCodigoTurma());
      System.out.println("Nota da primeira unidade: " + boletim.getPrimeiraNota());
      System.out.println("Nota da segunda unidade: " + boletim.getSegundaNota());
      System.out.println("Media: " + String.format("%.2f", media));
      System.out.println("Situacao: " + definirSituacaoBoletim(media));
      System.out.println("Frequência: " + String.format("%.2f", boletim.getFrequencia()) + "%");
    }
  }

  private Turma buscarTurmaDoBoletim(Boletim boletim) {
    try {
      return turmaService.buscarTurmaPorCodigo(boletim.getCodigoTurma());
    } catch (RuntimeException e) {
      return null;
    }
  }

  private String buscarNomeDisciplinaBoletim(Turma turma, Boletim boletim) {
    if (turma == null) {
      return "Turma nao encontrada (" + boletim.getCodigoTurma() + ")";
    }

    return turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina());
  }

  private String buscarNomeProfessorBoletim(Turma turma) {
    if (turma == null) {
      return "Nao informado";
    }

    return turmaService.buscarNomeProfessor(turma.getMatriculaProfessor());
  }

  private String buscarPeriodoLetivoBoletim(Turma turma) {
    if (turma == null) {
      return "Nao informado";
    }

    return turma.getPeriodoLetivo();
  }

  private double calcularMediaBoletim(Boletim boletim) {
    return (boletim.getPrimeiraNota() + boletim.getSegundaNota()) / 2.0;
  }

  private String definirSituacaoBoletim(double media) {
    return media >= 7.0 ? "Aprovado" : "Em andamento/Reprovado";
  }

  private void listarAlunosDoProfessor(Professor professorLogado) {
    List<Turma> turmas = turmaService.listarTurmasPorProfessor(professorLogado.getMatricula());
    boolean encontrouAluno = false;

    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Professor nao possui turmas cadastradas.");
      return;
    }

    for (Turma turma : turmas) {
      if (turma.getMatriculados() == null || turma.getMatriculados().isEmpty()) {
        continue;
      }

      for (String matriculaAluno : turma.getMatriculados()) {
        Aluno aluno = usuarioService.buscarAlunoPorMatricula(matriculaAluno);
        exibirAlunoDoProfessor(aluno, turma);
        encontrouAluno = true;
      }
    }

    if (!encontrouAluno) {
      System.out.println("Nenhum aluno matriculado nas turmas do professor.");
    }
  }

  private void buscarAlunoDoProfessor(Professor professorLogado) {
    List<Turma> turmas = turmaService.listarTurmasPorProfessor(professorLogado.getMatricula());

    if (turmas == null || turmas.isEmpty()) {
      System.out.println("Professor nao possui turmas cadastradas.");
      return;
    }

    List<Aluno> alunos = new ArrayList<>();
    List<Turma> turmasDosAlunos = new ArrayList<>();

    for (Turma turma : turmas) {
      if (turma.getMatriculados() == null || turma.getMatriculados().isEmpty()) {
        continue;
      }

      for (String matriculaAluno : turma.getMatriculados()) {
        Aluno aluno = usuarioService.buscarAlunoPorMatricula(matriculaAluno);
        alunos.add(aluno);
        turmasDosAlunos.add(turma);
      }
    }

    if (alunos.isEmpty()) {
      System.out.println("Nenhum aluno matriculado nas turmas do professor.");
      return;
    }

    System.out.println("Alunos das turmas do professor:");

    for (int i = 0; i < alunos.size(); i++) {
      exibirOpcaoAlunoDoProfessor(i + 1, alunos.get(i), turmasDosAlunos.get(i));
    }

    System.out.println("Informe o numero do aluno:");
    int opcao = lerOpcaoAluno(alunos.size());

    Aluno alunoSelecionado = alunos.get(opcao - 1);
    Usuario usuarioEncontrado =
        usuarioService.buscarUsuarioPorMatricula(professorLogado, alunoSelecionado.getMatricula());
    validarAlunoPertenceAoProfessor(professorLogado, usuarioEncontrado);
    exibirUsuario(usuarioEncontrado);
  }

  private void exibirOpcaoAlunoDoProfessor(int numeroOpcao, Aluno aluno, Turma turma) {
    System.out.println("\nOpcao " + numeroOpcao + ":");
    System.out.println("Aluno: " + aluno.getNome());
    System.out.println("Matricula: " + aluno.getMatricula());
    System.out.println(
        "Disciplina: " + turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
    System.out.println("Turma: " + turma.getCodigo());
  }

  private int lerOpcaoAluno(int quantidadeAlunos) {
    int opcao;

    try {
      opcao = Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      throw new RuntimeException("Opcao invalida. Digite o numero de uma opcao listada.");
    }

    if (opcao < 1 || opcao > quantidadeAlunos) {
      throw new RuntimeException("Opcao invalida. Escolha uma opcao da lista.");
    }

    return opcao;
  }

  private void validarAlunoPertenceAoProfessor(
      Professor professorLogado, Usuario usuarioEncontrado) {
    if (!(usuarioEncontrado instanceof Aluno aluno)) {
      throw new RuntimeException("Professor so pode visualizar alunos.");
    }

    if (!alunoEstaEmTurmaDoProfessor(professorLogado, aluno.getMatricula())) {
      throw new RuntimeException(
          "Professor nao pode visualizar aluno que nao esta matriculado em suas turmas.");
    }
  }

  private boolean alunoEstaEmTurmaDoProfessor(Professor professorLogado, String matriculaAluno) {
    List<Turma> turmas = turmaService.listarTurmasPorProfessor(professorLogado.getMatricula());

    for (Turma turma : turmas) {
      if (turma.getMatriculados() != null && turma.getMatriculados().contains(matriculaAluno)) {
        return true;
      }
    }

    return false;
  }

  private void exibirAlunoDoProfessor(Aluno aluno, Turma turma) {
    System.out.println("\nAluno:");
    System.out.println("Nome: " + aluno.getNome());
    System.out.println("Matricula: " + aluno.getMatricula());
    System.out.println(
        "Disciplina: " + turmaService.buscarNomeDisciplina(turma.getCodigoDisciplina()));
    System.out.println("Turma: " + turma.getCodigo());
    System.out.println();
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
      System.out.println(
          """
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
      TipoUsuario tipoUsuario, String nome, String email, String senha, String codigoCurso) {
    switch (tipoUsuario) {
      case ALUNO:
        Aluno aluno = new Aluno(nome, email, senha);
        aluno.setCodigoCurso(codigoCurso);
        return aluno;

      case PROFESSOR:
        Professor professor = new Professor(nome, email, senha);
        professor.setCodigoCurso(codigoCurso);
        return professor;

      case COORDENADOR:
        Coordenador coordenador = new Coordenador(nome, email, senha);
        coordenador.setCodigoCurso(codigoCurso);
        return coordenador;

      case ADMINISTRADOR:
        return new Administrador(nome, email, senha);

      default:
        throw new IllegalArgumentException("Tipo de usuário inválido.");
    }
  }

  private String lerCodigoCursoSeNecessario(TipoUsuario tipoUsuario) {
    if (tipoUsuario != TipoUsuario.ALUNO
        && tipoUsuario != TipoUsuario.PROFESSOR
        && tipoUsuario != TipoUsuario.COORDENADOR) {
      return null;
    }

    List<Curso> cursos = cursoService.listarCursos();

    if (cursos == null || cursos.isEmpty()) {
      if (tipoUsuario == TipoUsuario.COORDENADOR) {
        System.out.println("Nenhum curso cadastrado. O coordenador sera cadastrado sem curso.");
        return null;
      }

      throw new RuntimeException(
          "E necessario cadastrar um curso antes de cadastrar aluno ou professor.");
    }

    listarCursosParaSelecao(cursos, tipoUsuario);

    int opcao = lerOpcaoCurso(cursos.size(), tipoUsuario == TipoUsuario.COORDENADOR);

    if (opcao == 0) {
      return null;
    }

    return cursos.get(opcao - 1).getCodigo();
  }

  private void listarCursosParaSelecao(List<Curso> cursos, TipoUsuario tipoUsuario) {
    System.out.println("Cursos cadastrados:");

    if (tipoUsuario == TipoUsuario.COORDENADOR) {
      System.out.println("0 - Deixar sem curso por enquanto");
    }

    for (int i = 0; i < cursos.size(); i++) {
      Curso curso = cursos.get(i);
      System.out.println((i + 1) + " - " + curso.getNome() + " (" + curso.getCodigo() + ")");
    }
  }

  private int lerOpcaoCurso(int quantidadeCursos, boolean permiteSemCurso) {
    if (permiteSemCurso) {
      System.out.print("Escolha o numero do curso ou 0 para deixar sem curso: ");
    } else {
      System.out.print("Escolha o numero do curso: ");
    }

    String entrada = scanner.nextLine();
    int opcao;

    try {
      opcao = Integer.parseInt(entrada);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Opcao invalida. Digite o numero de uma opcao listada.");
    }

    if (permiteSemCurso && opcao == 0) {
      return opcao;
    }

    if (opcao < 1 || opcao > quantidadeCursos) {
      throw new RuntimeException("Opcao invalida. Escolha uma opcao da lista.");
    }

    return opcao;
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
