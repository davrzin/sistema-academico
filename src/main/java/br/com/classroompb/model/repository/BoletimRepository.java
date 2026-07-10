package br.com.classroompb.model.repository;

import br.com.classroompb.model.entities.gestaoacademica.Boletim;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.exception.PersistenciaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsavel pela persistencia de boletins em arquivo JSON.
 */
public class BoletimRepository {
  private static final String DIRETORIO_BOLETINS = PersistenciaPaths.BOLETINS.toString();

  private ObjectMapper objectMapper;
  private final String diretorioBoletins;

  /**
   * Cria o repositorio de boletins com diretorio padrao.
   *
   * @param objectMapper mapeador JSON.
   */
  public BoletimRepository(ObjectMapper objectMapper) {
    this(objectMapper, DIRETORIO_BOLETINS);
  }

  /**
   * Cria o repositorio de boletins com diretorio informado.
   *
   * @param objectMapper mapeador JSON.
   * @param diretorioBoletins diretorio dos boletins.
   */
  public BoletimRepository(ObjectMapper objectMapper, String diretorioBoletins) {
    this.objectMapper = objectMapper;
    this.diretorioBoletins = diretorioBoletins;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String getDiretorioBoletins() {
    return diretorioBoletins;
  }

  /**
   * Salva um boletim no arquivo JSON.
   *
   * @param boletim boletim a ser salvo.
   */
  public void salvarBoletim(Boletim boletim) {
    if (boletim == null) {
      throw new EntradaInvalidaException("Boletim não pode ser nulo.");
    }

    List<Boletim> boletins = listarBoletins();
    boletins.add(boletim);
    salvarListaBoletins(boletins);
  }

  private void salvarListaBoletins(List<Boletim> boletins) {
    String caminhoArquivo = getCaminhoArquivo();

    try {
      objectMapper.writeValue(new File(caminhoArquivo), boletins);
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao salvar boletins.", e);
    }
  }

  /**
   * Atualiza um boletim no arquivo JSON.
   *
   * @param boletimAtualizado boletim com dados atualizados.
   */
  public void atualizarBoletins(Boletim boletimAtualizado) {
    if (boletimAtualizado == null) {
      throw new EntradaInvalidaException("Boletim não pode ser null.");
    }

    List<Boletim> boletins = listarBoletins();

    for (int i = 0; i < boletins.size(); i++) {
      Boletim boletim = boletins.get(i);

      if (boletim.getIdBoletim().equalsIgnoreCase(boletimAtualizado.getIdBoletim())) {
        boletins.set(i, boletimAtualizado);
        salvarListaBoletins(boletins);
      }
    }
  }

  /**
   * Lista todos os boletins cadastrados.
   *
   * @return lista de boletins cadastrados.
   */
  public List<Boletim> listarBoletins() {
    File arquivo = new File(getCaminhoArquivo());
    RepositoryJsonFiles.garantirArquivoLista(arquivo);

    try {
      return lerBoletins(arquivo, Boletim.class);

    } catch (IOException e) {
      throw new PersistenciaException("Erro ao ler boletins", e);
    }
  }

  /**
   * Busca um boletim pelo codigo.
   *
   * @param codigo codigo do boletim.
   * @return boletim encontrado.
   */
  public Boletim buscarBoletimPorCodigo(String codigo) {
    validarCodigo(codigo);
    List<Boletim> boletins = this.listarBoletins();

    for (Boletim boletim : boletins) {
      if (boletim.getIdBoletim().equalsIgnoreCase(codigo)) {
        return boletim;
      }
    }

    return null;
  }

  /**
   * Busca boletins pelo codigo da turma.
   *
   * @param codigoTurma codigo da turma.
   * @return lista de boletins da turma.
   */
  public List<Boletim> buscarBoletinsPorTurma(String codigoTurma) {
    validarCodigo(codigoTurma);
    List<Boletim> boletins = listarBoletins();

    List<Boletim> boletinsMesmaTurma = new ArrayList<>();

    for (Boletim boletim : boletins) {
      if (boletim.getCodigoTurma().equalsIgnoreCase(codigoTurma)) {
        boletinsMesmaTurma.add(boletim);
      }
    }

    return boletinsMesmaTurma;
  }

  /**
   * Busca boletins pela matricula do aluno.
   *
   * @param matriculaAluno matricula do aluno.
   * @return lista de boletins do aluno.
   */
  public List<Boletim> buscarBoletinsPorAluno(String matriculaAluno) {
    validarCodigo(matriculaAluno);
    List<Boletim> boletins = listarBoletins();

    List<Boletim> boletinsPorAluno = new ArrayList<>();

    for (Boletim boletin : boletins) {
      if (boletin.getMatriculaAluno().equalsIgnoreCase(matriculaAluno)) {
        boletinsPorAluno.add(boletin);
      }
    }

    return boletinsPorAluno;
  }

  private void validarCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new EntradaInvalidaException("Código não pode ser null.");
    }
  }

  private <T extends Boletim> List<Boletim> lerBoletins(File arquivo, Class<T> tipo)
      throws IOException {

    return new ArrayList<>(
        objectMapper.readValue(
            arquivo, objectMapper.getTypeFactory().constructCollectionType(List.class, tipo)));
  }

  private String getCaminhoArquivo() {
    File diretorio = new File(diretorioBoletins);

    if (!diretorio.exists()) {
      diretorio.mkdirs();
    }

    return new File(diretorio, "boletins.json").getPath();
  }
}