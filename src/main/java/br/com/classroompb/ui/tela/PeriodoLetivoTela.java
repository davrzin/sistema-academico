package br.com.classroompb.ui.tela;

import br.com.classroompb.model.entities.gestaoacademica.PeriodoLetivo;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.ExistePeriodoAtivoException;
import br.com.classroompb.model.exception.PeriodoLetivoExistenteException;
import br.com.classroompb.model.exception.PersistenciaException;
import br.com.classroompb.model.services.PeriodoLetivoService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Tela de interacao para operacoes de periodo letivo.
 */
public class PeriodoLetivoTela {

  private static final DateTimeFormatter FORMATADOR_DATA =
      DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

  private final Scanner scanner;
  private final PeriodoLetivoService periodoLetivoService = new PeriodoLetivoService();

  /**
   * Cria a tela de periodos letivos.
   *
   * @param scanner leitor de entrada.
   */
  public PeriodoLetivoTela(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Solicita os dados para cadastro de periodo letivo.
   */
  public void cadastrarPeriodoLetivo() {
    try {
      final String ano = lerAnoPeriodo();

      System.out.println();
      System.out.println("Semestre:");
      System.out.println("1 - Primeiro semestre");
      System.out.println("2 - Segundo semestre");
      System.out.println("0 - Cancelar");
      String semestre = lerSemestre();

      if ("0".equals(semestre)) {
        System.out.println("Cadastro de periodo letivo cancelado.");
        return;
      }

      String periodo = ano + "." + semestre;
      System.out.println("Periodo gerado: " + periodo);

      LocalDate dataInicio = lerDataInicio(ano, semestre);
      LocalDate dataFim = lerDataFim(ano, semestre, dataInicio);

      PeriodoLetivo novoPeriodo =
          new PeriodoLetivo(periodo, formatarData(dataInicio), formatarData(dataFim));
      periodoLetivoService.cadastrarPeriodoLetivo(novoPeriodo);

      System.out.println("Periodo letivo cadastrado com sucesso.");

    } catch (EntradaTela.EntradaCanceladaException e) {
      System.out.println("Cadastro de periodo letivo cancelado.");

    } catch (PersistenciaException | EntradaInvalidaException | PeriodoLetivoExistenteException e) {
      System.out.println("Ocorreu um erro ao cadastrar periodo letivo: " + e.getMessage());
    }
  }

  /**
   * Lista os periodos letivos cadastrados no terminal.
   */
  public void listarPeriodosLetivos() {
    try {
      List<PeriodoLetivo> periodos = periodoLetivoService.listarPeriodosLetivos();
      exibirListaPeriodosLetivos(periodos);

    } catch (PersistenciaException e) {
      System.out.println("Ocorreu um erro ao listar periodos letivos: " + e.getMessage());
    }
  }

  /**
   * Solicita a ativacao de um periodo letivo.
   */
  public void ativarPeriodoLetivo() {
    try {
      List<PeriodoLetivo> periodosInativos = filtrarPeriodosInativos();

      if (periodosInativos.isEmpty()) {
        System.out.println("Nao existem periodos letivos inativos para ativar.");
        return;
      }

      System.out.println("Selecione o periodo letivo que sera ativado:");
      exibirPeriodosNumerados(periodosInativos);
      System.out.println();
      System.out.println("0 - Voltar");

      int opcao =
          EntradaTela.lerOpcaoOuCancelar(
              scanner,
              "Informe o numero do periodo que deseja ativar: ",
              periodosInativos.size());
      if (opcao == 0) {
        System.out.println("Voltando...");
        return;
      }

      PeriodoLetivo periodoEscolhido = periodosInativos.get(opcao - 1);

      int indiceReal = buscarIndicePeriodo(periodoEscolhido);

      boolean deuCerto = periodoLetivoService.ativarPeriodoLetivo(periodoEscolhido, indiceReal);

      if (deuCerto) {
        System.out.println("Periodo ativado com sucesso.");
      } else {
        System.out.println("Periodo nao ativado.");
      }

    } catch (PersistenciaException | ExistePeriodoAtivoException | IndexOutOfBoundsException e) {
      System.out.println("Ocorreu um erro ao ativar periodo letivo: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Opcao invalida.");
    }
  }

  /**
   * Solicita o encerramento de um periodo letivo.
   */
  public void encerrarPeriodoLetivo() {
    try {
      List<PeriodoLetivo> periodosAtivos = filtrarPeriodosAtivos();

      if (periodosAtivos.isEmpty()) {
        System.out.println("Nao existem periodos letivos ativos para encerrar.");
        return;
      }

      System.out.println("Selecione o periodo letivo que sera encerrado:");
      exibirPeriodosNumerados(periodosAtivos);
      System.out.println();
      System.out.println("0 - Voltar");

      int opcao =
          EntradaTela.lerOpcaoOuCancelar(
              scanner,
              "Informe o numero do periodo que deseja encerrar: ",
              periodosAtivos.size());
      if (opcao == 0) {
        System.out.println("Voltando...");
        return;
      }

      PeriodoLetivo periodoEscolhido = periodosAtivos.get(opcao - 1);

      int indiceReal = buscarIndicePeriodo(periodoEscolhido);

      boolean deuCerto = periodoLetivoService.desativarPeriodoLetivo(periodoEscolhido, indiceReal);

      if (deuCerto) {
        System.out.println("Periodo encerrado com sucesso.");
      } else {
        System.out.println("Periodo nao encerrado.");
      }

    } catch (PersistenciaException | IndexOutOfBoundsException e) {
      System.out.println("Ocorreu um erro ao encerrar periodo letivo: " + e.getMessage());

    } catch (NumberFormatException e) {
      System.out.println("Opcao invalida.");
    }
  }

  private String lerAnoPeriodo() {
    while (true) {
      System.out.print("Ano do periodo: ");
      String ano = scanner.nextLine();

      if ("0".equals(ano.trim())) {
        throw new EntradaTela.EntradaCanceladaException();
      }

      if (ano != null && ano.matches("\\d{4}")) {
        return ano;
      }

      System.out.println("Ano invalido. Informe um ano com 4 digitos.");
      System.out.println();
    }
  }

  private String lerSemestre() {
    while (true) {
      System.out.print("Escolha o semestre: ");
      String semestre = scanner.nextLine();

      if ("0".equals(semestre) || "1".equals(semestre) || "2".equals(semestre)) {
        return semestre;
      }

      System.out.println("Semestre invalido. Escolha 1 ou 2.");
      System.out.println();
    }
  }

  private LocalDate lerDataInicio(String ano, String semestre) {
    while (true) {
      System.out.print("Data de inicio: ");
      String data = scanner.nextLine();

      try {
        if ("0".equals(data.trim())) {
          throw new EntradaTela.EntradaCanceladaException();
        }
        LocalDate dataInicio = converterData(data);
        validarDataNoSemestre(dataInicio, ano, semestre);
        return dataInicio;
      } catch (EntradaInvalidaException e) {
        System.out.println(e.getMessage());
        System.out.println();
      }
    }
  }

  private LocalDate lerDataFim(String ano, String semestre, LocalDate dataInicio) {
    while (true) {
      System.out.print("Data de fim: ");
      String data = scanner.nextLine();

      try {
        if ("0".equals(data.trim())) {
          throw new EntradaTela.EntradaCanceladaException();
        }
        LocalDate dataFim = converterData(data);
        validarDataNoSemestre(dataFim, ano, semestre);
        if (!dataFim.isAfter(dataInicio)) {
          throw new EntradaInvalidaException(
              "A data de fim deve ser posterior a data de inicio.");
        }
        return dataFim;
      } catch (EntradaInvalidaException e) {
        System.out.println(e.getMessage());
        System.out.println();
      }
    }
  }

  private LocalDate converterData(String data) {
    if (data == null || data.isBlank()) {
      throw new EntradaInvalidaException("Data invalida. Use o formato dd/MM/aaaa.");
    }

    try {
      return LocalDate.parse(data, FORMATADOR_DATA);
    } catch (DateTimeParseException e) {
      throw new EntradaInvalidaException("Data invalida. Use o formato dd/MM/aaaa.");
    }
  }

  private void validarDataNoSemestre(LocalDate data, String ano, String semestre) {
    int anoPeriodo = Integer.parseInt(ano);
    int mes = data.getMonthValue();

    if (data.getYear() != anoPeriodo) {
      throw mensagemSemestreInvalido(anoPeriodo, semestre);
    }

    if ("1".equals(semestre) && (mes < 1 || mes > 6)) {
      throw mensagemSemestreInvalido(anoPeriodo, semestre);
    }

    if ("2".equals(semestre) && (mes < 7 || mes > 12)) {
      throw mensagemSemestreInvalido(anoPeriodo, semestre);
    }
  }

  private EntradaInvalidaException mensagemSemestreInvalido(int ano, String semestre) {
    if ("1".equals(semestre)) {
      return new EntradaInvalidaException(
          "Datas do primeiro semestre devem estar entre janeiro e junho de " + ano + ".");
    }

    return new EntradaInvalidaException(
        "Datas do segundo semestre devem estar entre julho e dezembro de " + ano + ".");
  }

  private String formatarData(LocalDate data) {
    return data.format(FORMATADOR_DATA);
  }

  private List<PeriodoLetivo> filtrarPeriodosInativos() {
    List<PeriodoLetivo> periodos = periodoLetivoService.listarPeriodosLetivos();
    List<PeriodoLetivo> periodosInativos = new ArrayList<>();

    for (PeriodoLetivo periodo : periodos) {
      if (!periodo.getPeriodoAtivo() && !periodo.getPeriodoEncerrado()) {
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

      if (periodo.getPeriodo().equals(periodoBuscado.getPeriodo())
          && periodo.getDataInicio().equals(periodoBuscado.getDataInicio())
          && periodo.getDataFim().equals(periodoBuscado.getDataFim())) {
        return i;
      }
    }

    throw new IndexOutOfBoundsException("Periodo letivo nao encontrado.");
  }

  private void exibirListaPeriodosLetivos(List<PeriodoLetivo> periodos) {
    if (periodos == null || periodos.isEmpty()) {
      System.out.println("Nenhum periodo letivo cadastrado.");
      return;
    }

    for (int i = 0; i < periodos.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirPeriodoLetivo(i + 1, periodos.get(i));
    }
  }

  private void exibirPeriodosNumerados(List<PeriodoLetivo> periodos) {
    for (int i = 0; i < periodos.size(); i++) {
      if (i > 0) {
        System.out.println();
      }

      exibirPeriodoLetivo(i + 1, periodos.get(i));
    }
  }

  private void exibirPeriodoLetivo(int numero, PeriodoLetivo periodo) {
    System.out.println(numero + " - " + periodo.getPeriodo());
    System.out.println("    Inicio: " + periodo.getDataInicio());
    System.out.println("    Fim: " + periodo.getDataFim());
    System.out.println("    Situacao: " + situacaoPeriodo(periodo));
  }

  private String situacaoPeriodo(PeriodoLetivo periodo) {
    return periodo.getPeriodoAtivo() ? "Ativo" : "Inativo";
  }
}
