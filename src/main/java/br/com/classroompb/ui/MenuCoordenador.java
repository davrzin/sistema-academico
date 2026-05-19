package br.com.classroompb.ui;

import java.util.Scanner;

import br.com.classroompb.model.entities.Coordenador;

public class MenuCoordenador {

    Scanner scanner = new Scanner(System.in);
    private Coordenador usuarioLogado;

    public MenuCoordenador(Coordenador usuarioLogado){
        this.usuarioLogado = usuarioLogado;
    }

    public void iniciar(){

        int opcao;

        do{
            System.out.println("""
            ╔════════════════════════════════════╗
            ║         MENU COORDENADOR          ║
            ╠════════════════════════════════════╣
            ║ 1 - Cadastrar disciplina          ║
            ║ 2 - Listar disciplinas            ║
            ║ 3 - Cadastrar período letivo      ║
            ║ 4 - Ativar período letivo         ║
            ║ 5 - Encerrar período letivo       ║
            ║ 6 - Ofertar turma                 ║
            ║ 7 - Alterar turma                 ║
            ║ 8 - Cancelar turma                ║
            ║ 0 - Voltar                        ║
            ╚════════════════════════════════════╝
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

                case 6:

                    break;

                case 7:

                    break;

                case 8:

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
