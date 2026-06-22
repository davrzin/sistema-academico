package br.com.classroompb.model.services;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.classroompb.model.entities.GestaoAcademica.Aula;
import br.com.classroompb.model.entities.GestaoAcademica.Turma;
import br.com.classroompb.model.exception.EntradaInvalidaException;
import br.com.classroompb.model.repository.AulaRepository;
import br.com.classroompb.model.repository.TurmaRepository;

public class AulaService {
    private static final Path DIRETORIO_TURMAS = Path.of(System.getProperty("user.dir"), "turmas");
    private static final Path DIRETORIO_AULAS = Path.of(System.getProperty("user.dir"), "aulas");

    private final AulaRepository aulaRepository;
    private final TurmaRepository turmaRepository;

    
    private UsuarioService usuarioService;

    public AulaService() {
        this.aulaRepository = new AulaRepository(new ObjectMapper(), DIRETORIO_AULAS.toString());
        this.turmaRepository = new TurmaRepository(new ObjectMapper(), DIRETORIO_TURMAS.toString());
    }

    public AulaService(AulaRepository aulaRepository, TurmaRepository turmaRepository) {
        this.aulaRepository = aulaRepository;
        this.turmaRepository = turmaRepository;
    }

    public Aula gerarAula(Turma turma){
        return new Aula(gerarCodigoAula(), turma.getCodigo(), LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), turma.getHorario());

    }

    public void salvarAula(Aula aula){
        // VALIDAÇÕES... 
        if(aula == null){
            throw new EntradaInvalidaException("A aula não pode ser nula.");
        }

        if(aula.getCodigoTurma() == null || aula.getCodigoTurma().isBlank()){
            throw new EntradaInvalidaException(
                "Código da turma inválido."
            );
        }

        if(aula.getData() == null){
            throw new EntradaInvalidaException(
                "Data da aula inválida."
            );
        }

        if(aula.getHorario() == null ||
        aula.getHorario().isBlank()){
            throw new EntradaInvalidaException(
                "Horário inválido."
            );
        }

        if(aula.getPresencas() == null ||
        aula.getPresencas().isEmpty()){
            throw new EntradaInvalidaException(
                "A aula deve possuir registros de frequência."
            );
        }

        // -------------

        aulaRepository.salvarAula(aula);
    }

    private String gerarCodigoAula() {
        int contador = aulaRepository.listarAulas().size();
        String id;

        do {
            id = "aul" + String.format("%02d", contador);
            contador++;
        } while (aulaRepository.buscarAulaPorId(id) != null);

        return id;
    }

}
