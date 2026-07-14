package br.com.classroompb.ui.tela;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Testes da formatacao portavel do comprovante de notas. */
public class TurmaTelaTest {

  @Test
  public void deveFormatarNotaPendenteOuZeroCorretamente() {
    Assertions.assertEquals("--", TurmaTela.formatarNotaComprovante(null));
    Assertions.assertEquals("0,0", TurmaTela.formatarNotaComprovante(0.0f));
  }

  @Test
  public void deveFormatarMediaComDuasCasasSomenteQuandoDisponivel() {
    Assertions.assertEquals("--", TurmaTela.formatarMediaComprovante(null));
    Assertions.assertEquals("7,00", TurmaTela.formatarMediaComprovante(7.0f));
  }
}
