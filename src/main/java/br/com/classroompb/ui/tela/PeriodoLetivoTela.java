package br.com.classroompb.ui.tela;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.classroompb.model.entities.GestaoAcademica.PeriodoLetivo;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.ExistePeriodoAtivoException;
import br.com.classroompb.model.exception.PeriodoLetivoExistenteException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.PeriodoLetivoService;

public class PeriodoLetivoTela {

    private final Scanner scanner;
    private final PeriodoLetivoService periodoLetivoService = new PeriodoLetivoService();

    public PeriodoLetivoTela(Scanner scanner) {
        this.scanner = scanner;
    }

    public void cadastrarPeriodoLetivo() {
        try {
            System.out.println("Informe o período letivo. Exemplo: 2026.2");
            String periodo = scanner.nextLine();

            System.out.println("Informe a data de início do período. Exemplo: 02/02/2026");
            String dataInicio = scanner.nextLine();

            System.out.println("Informe a data de fim do período. Exemplo: 15/09/2026");
            String dataFim = scanner.nextLine();

            PeriodoLetivo novoPeriodo = new PeriodoLetivo(periodo, dataInicio, dataFim);
            periodoLetivoService.cadastrarPeriodoLetivo(novoPeriodo);

            System.out.println("Período letivo cadastrado com sucesso.");

        } catch (PersistenciaException | EntradaInvalidaException | PeriodoLetivoExistenteException e) {
            System.out.println("Ocorreu um erro ao cadastrar período letivo: " + e.getMessage());
        }
    }

    public void listarPeriodosLetivos() {
        try {
            List<PeriodoLetivo> periodos = periodoLetivoService.listarPeriodosLetivos();
            exibirListaPeriodosLetivos(periodos);

        } catch (PersistenciaException e) {
            System.out.println("Ocorreu um erro ao listar períodos letivos: " + e.getMessage());
        }
    }

    public void ativarPeriodoLetivo() {
        try {
            List<PeriodoLetivo> periodosInativos = filtrarPeriodosInativos();

            if (periodosInativos.isEmpty()) {
                System.out.println("Não existem períodos letivos inativos para ativar.");
                return;
            }

            System.out.println("Selecione o período letivo que será ativado:");
            exibirPeriodosNumerados(periodosInativos);

            int opcao = lerOpcao();
            PeriodoLetivo periodoEscolhido = periodosInativos.get(opcao - 1);

            int indiceReal = buscarIndicePeriodo(periodoEscolhido);

            boolean deuCerto = periodoLetivoService.ativarPeriodoLetivo(periodoEscolhido, indiceReal);

            if (deuCerto) {
                System.out.println("Período ativado com sucesso.");
            } else {
                System.out.println("Período não ativado.");
            }

        } catch (PersistenciaException | ExistePeriodoAtivoException | IndexOutOfBoundsException e) {
            System.out.println("Ocorreu um erro ao ativar período letivo: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Opção inválida.");
        }
    }

    public void encerrarPeriodoLetivo() {
        try {
            List<PeriodoLetivo> periodosAtivos = filtrarPeriodosAtivos();

            if (periodosAtivos.isEmpty()) {
                System.out.println("Não existem períodos letivos ativos para encerrar.");
                return;
            }

            System.out.println("Selecione o período letivo que será encerrado:");
            exibirPeriodosNumerados(periodosAtivos);

            int opcao = lerOpcao();
            PeriodoLetivo periodoEscolhido = periodosAtivos.get(opcao - 1);

            int indiceReal = buscarIndicePeriodo(periodoEscolhido);

            boolean deuCerto = periodoLetivoService.desativarPeriodoLetivo(periodoEscolhido, indiceReal);

            if (deuCerto) {
                System.out.println("Período encerrado com sucesso.");
            } else {
                System.out.println("Período não encerrado.");
            }

        } catch (PersistenciaException | IndexOutOfBoundsException e) {
            System.out.println("Ocorreu um erro ao encerrar período letivo: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Opção inválida.");
        }
    }

    private List<PeriodoLetivo> filtrarPeriodosInativos() {
        List<PeriodoLetivo> periodos = periodoLetivoService.listarPeriodosLetivos();
        List<PeriodoLetivo> periodosInativos = new ArrayList<>();

        for (PeriodoLetivo periodo : periodos) {
            if (!periodo.getPeriodoAtivo()) {
                periodosInativos.add(periodo);
            }
        }

        return periodosInativos;
    }

    private List<PeriodoLetivo> filtrarPeriodosAtivos() {
        List<PeriodoLetivo> periodos = periodoLetivoService.listarPeriodosLetivos();
        List<PeriodoLetivo> periodosAtivos = new ArrayList<>();

        for (PeriodoLetivo periodo : periodos) {
            if (periodo.getPeriodoAtivo()) {
                periodosAtivos.add(periodo);
            }
        }

        return periodosAtivos;
    }

    private int buscarIndicePeriodo(PeriodoLetivo periodoBuscado) {
        List<PeriodoLetivo> periodos = periodoLetivoService.listarPeriodosLetivos();

        for (int i = 0; i < periodos.size(); i++) {
            PeriodoLetivo periodo = periodos.get(i);

            if (
                    periodo.getPeriodo().equals(periodoBuscado.getPeriodo())
                            && periodo.getDataInicio().equals(periodoBuscado.getDataInicio())
                            && periodo.getDataFim().equals(periodoBuscado.getDataFim())
            ) {
                return i;
            }
        }

        throw new IndexOutOfBoundsException("Período letivo não encontrado.");
    }

    private int lerOpcao() {
        System.out.print("Digite uma opção: ");
        return Integer.parseInt(scanner.nextLine());
    }

    private void exibirListaPeriodosLetivos(List<PeriodoLetivo> periodos) {
        if (periodos == null || periodos.isEmpty()) {
            System.out.println("Nenhum período letivo cadastrado.");
            return;
        }

        for (PeriodoLetivo periodo : periodos) {
            exibirPeriodoLetivo(periodo);
        }
    }

    private void exibirPeriodosNumerados(List<PeriodoLetivo> periodos) {
        for (int i = 0; i < periodos.size(); i++) {
            System.out.println((i + 1) + " - " + periodos.get(i).getPeriodo()
                    + " (" + periodos.get(i).getDataInicio()
                    + " - " + periodos.get(i).getDataFim() + ")");
        }
    }

    private void exibirPeriodoLetivo(PeriodoLetivo periodo) {
        String status = periodo.getPeriodoAtivo() ? "Ativo" : "Inativo";

        System.out.println("\nPeríodo letivo:");
        System.out.println("Período: " + periodo.getPeriodo());
        System.out.println("Início: " + periodo.getDataInicio());
        System.out.println("Fim: " + periodo.getDataFim());
        System.out.println("Status: " + status);
        System.out.println();
    }
}
