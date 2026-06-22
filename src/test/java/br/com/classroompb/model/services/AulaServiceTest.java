package br.com.classroompb.model.services;

import br.com.classroompb.model.entities.GestaoAcademica.Aula;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.TurmaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class AulaServiceTest {

    private static final Path DIRETORIO_AULAS = Path.of(System.getProperty("user.dir"), "aulas");
    private static final Path DIRETORIO_TURMAS = Path.of(System.getProperty("user.dir"), "turmas");

    private Turma turma;
    private AulaRepository aulaRepository;
    private AulaService aulaService;

    @BeforeEach
    public void criarTurma(){
        turma = new Turma("tur00", "dis00", "6", "pro00", 40, "Seg 08:00-10:00","C-108");
        aulaRepository = criarAulaRepository();
        aulaService = criarAulaService(aulaRepository);
    }


    private AulaRepository criarAulaRepository(){
        return new AulaRepository(new ObjectMapper(), DIRETORIO_AULAS.toString());
    }

    private AulaService criarAulaService(AulaRepository aulaRepository){
        return new AulaService(aulaRepository, criarTurmaRepository());
    }

    private TurmaRepository criarTurmaRepository(){
        return new TurmaRepository(new ObjectMapper(), DIRETORIO_TURMAS.toString());
    }

    @Test
    public void deveCriarAulaServiceCorretamenteComConstrutorVazio(){
        AulaService aulaService1 = new AulaService();

        Assertions.assertNotNull(aulaService1);
    }

    @Test
    public void deveCriarAulaServiceCorretamente(){
        AulaService aulaService1 = new AulaService(criarAulaRepository(), criarTurmaRepository());

        Assertions.assertNotNull(aulaService1);
    }

    @Test
    public void deveGerarAulaCorretamente(){
        Aula aula  = aulaService.gerarAula(turma);

        Assertions.assertNotNull(aula);

    }

    @Test
    public void deveLancarEntradaInvalidaException(){

        Assertions.assertThrows(NullPointerException.class, () -> aulaService.gerarAula(null));
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionEmSalvarAula(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(null));
    }

    @Test
    public void deveLancarEntradaInvalidaExceptionEmSalvarAulaEmAulaComAtributosNull(){

        Assertions.assertThrows(EntradaInvalidaException.class, () -> aulaService.salvarAula(new Aula(null, null, null, null, null)));
    }
}
