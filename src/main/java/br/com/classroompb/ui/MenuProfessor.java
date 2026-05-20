package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.Professor;

public class MenuProfessor {

    Scanner scanner = new Scanner(System.in);
    private Professor usuarioLogado;

    public MenuProfessor(Professor usuarioLogado){
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar(){

        int opcao;

        do{
            System.out.println("""
            ╔═══════════════════════════════════╗
            ║          MENU PROFESSOR           ║
            ╠═══════════════════════════════════╣
            ║ 1 - Consultar turmas              ║
            ║ 2 - Consultar alunos              ║
            ║ 3 - Lançar notas                  ║
            ║ 4 - Lançar frequência             ║
            ║ 5 - Consultar diário              ║
            ║ 0 - Voltar                        ║
            ╚═══════════════════════════════════╝
        \s""");

            System.out.print("Digite uma opção: ");

            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:

                    break;

                case 2:

                    break;

                case 3:

                    break;

                case 4:

                    break;

                case 5:

                    break;

                case 0:
                    System.out.println("Voltando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while(opcao != 0);
    }
}
