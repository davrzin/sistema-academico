package br.com.classroompb.model.repository;

import br.com.classroompb.model.exception.PersistenciaException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utilitario para inicializar arquivos JSON de lista usados pelos repositorios.
 */
final class RepositoryJsonFiles {

  private RepositoryJsonFiles() {}

  static void garantirArquivoLista(File arquivo) {
    File diretorio = arquivo.getParentFile();

    if (diretorio != null && !diretorio.exists() && !diretorio.mkdirs()) {
      throw new PersistenciaException(
          "Erro ao criar diretorio de persistencia.",
          new IOException("Diretorio nao foi criado: " + diretorio.getPath()));
    }

    try {
      if (!arquivo.exists() || arquivo.length() == 0) {
        Files.writeString(arquivo.toPath(), "[]");
      }
    } catch (IOException e) {
      throw new PersistenciaException("Erro ao inicializar arquivo JSON.", e);
    }
  }
}
