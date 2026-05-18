package br.com.classroompb.ui;

import br.com.classroompb.model.entities.Usuario;
import br.com.classroompb.model.enums.TipoUsuario;

import java.util.Scanner;

public class MenuAluno {

    private Scanner scanner = new Scanner(System.in);

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
