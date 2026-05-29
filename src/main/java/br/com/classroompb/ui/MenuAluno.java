package br.com.classroompb.ui;

import java.util.List;
import java.util.Scanner;

import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.entities.Usuario.Aluno;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.TurmaService;

public class MenuAluno {
    
    private Scanner scanner = new Scanner(System.in);
    private Aluno usuarioLogado;
    private TurmaService turmaService = new TurmaService();

    public MenuAluno(Aluno usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public Aluno getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Aluno usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar() {

        int opcao;

        do {
            System.out.println("""
            ╔════════════════════════════════════╗
            ║          SISTEMA ACADÊMICO         ║
            ╠════════════════════════════════════╣
            ║ 1 - Consultar Disciplinas          ║
            ║ 2 - Consultar turmas               ║
            ║ 3 - Consultar matrícula            ║
            ║ 4 - Consultar boletim              ║
            ║ 5 - Consultar histórico acadêmico  ║
            ║ 6 - Cancelar matrícula             ║
            ║ 0 - Voltar                         ║
            ╚════════════════════════════════════╝
            \s""");

            System.out.print("Digite uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:

                    break;
                case 2:
                    consultarTurmas();
                    break;
                case 0:
                    System.out.println("Fechando o Sistema");
                    break;
                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    private void consultarTurmas() {
        try {
            List<Turma> turmas = turmaService.listarTurmas();

            if (turmas.isEmpty()) {
                System.out.println("Nenhuma turma cadastrada.");
                return;
            }

            for (Turma turma : turmas) {
                System.out.println(turma);
            }
        } catch (PersistenciaException e) {
            System.out.println("Ocorreu um erro ao consultar turmas: " + e.getMessage());
        }
    }
}
