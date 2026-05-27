package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.Usuario.Aluno;

public class MenuAluno {
    
    private Scanner scanner = new Scanner(System.in);
    private Aluno usuarioLogado;

    public MenuAluno(Aluno usuarioLogado){
        this.usuarioLogado = usuarioLogado;
    }

    public Aluno getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Aluno usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar(){

        int opcao;

        do{
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
            System.out.print("\nMatricula: " + usuarioLogado.getMatricula());
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:

                    break;
                case 2:
                    System.out.println();
                    break;
                case 0:
                    System.out.println("Fechando o Sistema");
                default:
                    System.out.println("Opção inválida");
            }

        } while(opcao != 0);
    }
}
