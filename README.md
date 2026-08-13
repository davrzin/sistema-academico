# ClassRoomPB - Sistema Academico

Sistema academico em Java para gerenciamento de usuarios, cursos, disciplinas,
periodos letivos, turmas, matriculas, notas e frequencia. A aplicacao usa menus
de terminal separados por perfil: administrador, coordenador, professor e aluno.

## Tecnologias

- Java 21
- Maven
- JUnit 5
- Checkstyle com Google Java Style
- JaCoCo para cobertura de testes
- Persistencia local em JSON com Jackson

## Como Rodar

No Windows, execute na raiz do projeto:

```bash
mvn.cmd exec:java
```

Tambem e possivel executar a classe principal pela IDE:

```text
src/main/java/br/com/classroompb/application/Program.java
```

## Como Validar

Execute na raiz do projeto:

```bash
mvn.cmd test
```

Baseline do HEAD `4507d5a`:

- compilacao do codigo de producao aprovada;
- 776 testes executados;
- 19 falhas e 41 erros em testes legados incompativeis com a Release 4;
- 60 ocorrencias conhecidas no total;
- fluxo completo da Release 4 validado manualmente.

Portanto, a suite completa ainda nao deve ser descrita como aprovada. O Checkstyle nao
faz parte deste baseline.

Para gerar o relatorio de cobertura de testes (JaCoCo):

```bash
mvn.cmd clean verify
```

O relatorio HTML fica em `target/site/jacoco/index.html`.

### Escopo da Cobertura

Os pacotes `br.com.classroompb.ui.menu`, `br.com.classroompb.ui.tela` e
`br.com.classroompb.application` sao excluidos do calculo de cobertura do
JaCoCo (veja `pom.xml`). Sao classes de interface de terminal, dominadas por
`Scanner`/`System.out` e delegacao para os services, sem regra de negocio
propria; medir sua cobertura tem baixo valor e distorce a porcentagem geral.
A regra de negocio do sistema (`entities`, `services`, `repository`)
continua 100% medida pelo relatorio.

## Persistencia

Os caminhos sao centralizados em `PersistenciaPaths` e partem de
`<diretorio-de-execucao>/data`. A estrutura efetivamente usada e:

```text
data/
  usuarios/
    administrador.json
    aluno.json
    coordenador.json
    professor.json
  cursos/cursos.json
  disciplinas/disciplinas.json
  turmas/turmas.json
  boletins/boletins.json
  periodos/periodos_letivos.json
  aulas/aulas.json
  diarios/
    diarios.json
    avaliacoes.json
    notas_avaliacoes.json
  historicos/historicos.json
```

Todos esses arquivos tem uma lista JSON (`[]`) na raiz. No fluxo da Release 4, os
registros principais seguem estas formas (valores ilustrativos):

`diarios/diarios.json`:

```json
[{"codigo":"dia00","codigoTurma":"tur00","descricao":"Teorico","matriculaProfessor":"pr00","horario":"SEG 08:00","sala":"A-01","cargaHoraria":60,"situacao":"ATIVO"}]
```

`aulas/aulas.json`:

```json
[{"id":"aul00","codigoTurma":"tur00","codigoDiario":"dia00","data":"13/08/2026","horario":"SEG 08:00","presencas":{"al00":true}}]
```

`diarios/avaliacoes.json` e `diarios/notas_avaliacoes.json`:

```json
[{"codigo":"avl00","codigoDiario":"dia00","descricao":"P1","peso":1.0,"etapa":1,"notaMaxima":10.0}]
[{"codigoAvaliacao":"avl00","matriculaAluno":"al00","valorNota":8.5}]
```

`historicos/historicos.json`:

```json
[{"matriculaAluno":"al00","nomeAluno":"Aluno","periodoLetivo":"2026.2","codigoTurma":"tur00","codigoDisciplina":"cmp00","nomeDisciplina":"Disciplina","nomeProfessor":null,"notaFinal":8.0,"frequencia":100.0,"situacao":"Aprovado"}]
```

`turmas/turmas.json` conserva a oferta: codigo, disciplina, periodo letivo, limite de
vagas, matriculados, lista de espera e a lista legada de aulas. Professor, horario, sala
e carga horaria pertencem ao diario. Os valores dos enums sao serializados pelo Jackson
conforme as classes do dominio.

Os arquivos `.json` nao sao versionados. Eles continuam ignorados pelo Git para
evitar que dados locais ou dados de demonstracao sejam enviados ao repositorio.

As pastas de persistencia sao mantidas no Git com arquivos `.gitkeep`. Quando um
JSON necessario nao existe ou esta vazio, os repositorios inicializam o arquivo
automaticamente com uma lista vazia (`[]`).

Em um clone limpo, a aplicacao deve criar os arquivos JSON conforme as operacoes
forem executadas.

## Fluxo Academico da Release 4

A **Turma** representa a oferta de uma disciplina em um periodo letivo, com vagas e
alunos matriculados. O **Diario** representa a execucao dessa oferta: uma turma pode ter
varios diarios (por exemplo, Teorico e Laboratorio), e cada diario tem um professor
responsavel, horario, sala, carga horaria e situacao propria.

O professor trabalha apenas nos diarios sob sua responsabilidade. Cada registro de
`Aula` e vinculado ao diario e equivale a duas horas-aula; uma ausencia soma duas
faltas-hora. O limite de registros e determinado pela carga horaria do diario.

As avaliacoes sao cadastradas no diario para a primeira ou a segunda unidade. Todas usam
peso 1 e nota maxima 10, e as notas sao lancadas por aluno e por avaliacao. Para encerrar
o diario, o professor precisa completar a carga horaria, cadastrar avaliacoes e lancar
todas as notas. Um diario encerrado fica bloqueado para alteracoes, novas aulas,
frequencias e notas.

Depois que todos os diarios validos estiverem encerrados, o coordenador usa
`consolidarResultadosTurma`. Diarios cancelados sao ignorados. O sistema calcula as
notas das unidades com as avaliacoes dos diarios validos, consolida a frequencia das
aulas, atualiza o boletim e cria ou atualiza o resultado final persistido em
`data/historicos/historicos.json`. Os detalhes permanecem nos arquivos de diario; o
historico armazena o resultado consolidado da turma.

## Usuarios de Teste e Apresentacao

Como os arquivos `.json` nao sao versionados, os usuarios de teste nao fazem
parte do repositorio.

Na primeira execucao de um clone limpo, pode ser necessario cadastrar os usuarios
pela propria aplicacao. Para uma apresentacao, o apresentador pode preparar os
dados localmente antes, mantendo esses JSONs fora do commit.

## Estrutura Principal

```text
src/main/java/
  br/com/classroompb/application/               Classe principal
  br/com/classroompb/model/entities/gestaoacademica/  Entidades academicas (curso, turma, boletim, etc.)
  br/com/classroompb/model/entities/usuario/    Entidades de usuario (aluno, professor, coordenador, admin)
  br/com/classroompb/model/enums/               Enumeracoes do dominio
  br/com/classroompb/model/exception/           Excecoes de dominio
  br/com/classroompb/model/repository/          Persistencia local em JSON
  br/com/classroompb/model/services/            Regras de negocio
  br/com/classroompb/ui/menu/                   Menus de terminal por perfil
  br/com/classroompb/ui/tela/                   Telas de interacao com o usuario

src/test/java/                        Testes automatizados
data/                                 Pastas de persistencia local
docs/                                 Documentos do projeto
Releases/                             Relatorios de processo e de release
```

## Roteiro de Apresentacao da Release 4

1. Rodar `mvn.cmd exec:java`, entrar como coordenador e preparar disciplina, periodo,
   turma e alunos matriculados.
2. Em **Diarios**, cadastrar dois diarios para a mesma turma (por exemplo, Teorico e
   Laboratorio), com professores, horarios, salas e cargas horarias definidos.
3. Entrar como cada professor e usar **Listar diario** para conferir somente os diarios
   sob sua responsabilidade.
4. Registrar as aulas e frequencias de cada diario, lembrando que cada aula corresponde
   a duas horas-aula e cada ausencia a duas faltas-hora.
5. Em **Avaliacoes e notas**, cadastrar avaliacoes das unidades 1 e 2 e lancar a nota de
   cada aluno por avaliacao.
6. Encerrar cada diario pelo menu do professor e demonstrar o bloqueio de edicao.
7. Voltar como coordenador e, em **Turmas**, usar **Consolidar resultados da turma**;
   se houver diario cancelado, mostrar que ele nao participa do calculo.
8. Entrar como aluno para consultar diarios, frequencia, notas por avaliacao e o historico
   academico consolidado; conferir tambem a persistencia em `data/historicos/`.

## Comandos Uteis

```bash
mvn.cmd test
mvn.cmd checkstyle:check
mvn.cmd clean verify
mvn.cmd exec:java
```

## Equipe (primeira unidade)

- Arthur Barbosa
- Artur Oliveira
- Davi Roberto

## Equipe (segunda unidade)

- Davi Roberto
- Alessia Bianca
- Eric Natan
