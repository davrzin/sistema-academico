# ClassRoomPB - Sistema Academico

Sistema academico em Java para gerenciamento de usuarios, cursos, disciplinas,
periodos letivos, turmas, matriculas, notas e frequencia. A aplicacao usa menus
de terminal separados por perfil: administrador, coordenador, professor e aluno.

## Tecnologias

- Java 21
- Maven
- JUnit 5
- Checkstyle com Google Java Style
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

Execute os comandos abaixo na raiz do projeto:

```bash
mvn.cmd clean test
mvn.cmd checkstyle:check
```

O estado atual esperado e:

- testes passando;
- Checkstyle com 0 violacoes.

## Persistencia

Os dados da aplicacao ficam em `data/`.

```text
data/
  aulas/
  boletins/
  cursos/
  disciplinas/
  periodos/
  turmas/
  usuarios/
```

Os arquivos `.json` nao sao versionados. Eles continuam ignorados pelo Git para
evitar que dados locais ou dados de demonstracao sejam enviados ao repositorio.

As pastas de persistencia sao mantidas no Git com arquivos `.gitkeep`. Quando um
JSON necessario nao existe ou esta vazio, os repositorios inicializam o arquivo
automaticamente com uma lista vazia (`[]`).

Em um clone limpo, a aplicacao deve criar os arquivos JSON conforme as operacoes
forem executadas.

## Usuarios de Teste e Apresentacao

Como os arquivos `.json` nao sao versionados, os usuarios de teste nao fazem
parte do repositorio.

Na primeira execucao de um clone limpo, pode ser necessario cadastrar os usuarios
pela propria aplicacao. Para uma apresentacao, o apresentador pode preparar os
dados localmente antes, mantendo esses JSONs fora do commit.

## Estrutura Principal

```text
src/main/java/
  br/com/classroompb/application/     Classe principal
  br/com/classroompb/model/entities/  Entidades do dominio
  br/com/classroompb/model/repository/Persistencia local
  br/com/classroompb/model/services/  Regras de negocio
  br/com/classroompb/ui/              Menus e telas de terminal

src/test/java/                        Testes automatizados
data/                                 Pastas de persistencia local
docs/                                 Documentos do projeto
```

## Roteiro Simples de Apresentacao

1. Rodar `mvn.cmd clean test` para mostrar que os testes passam.
2. Rodar `mvn.cmd checkstyle:check` para mostrar que o padrao de codigo passa.
3. Rodar `mvn.cmd exec:java` para abrir o sistema.
4. Cadastrar ou usar dados locais previamente preparados.
5. Demonstrar login por perfil.
6. Demonstrar fluxo de administrador: cadastro/listagem de usuarios e cursos.
7. Demonstrar fluxo de coordenador: disciplinas, periodos e turmas.
8. Demonstrar fluxo de aluno: listagem de turmas, matricula e cancelamento.
9. Demonstrar fluxo de professor: turmas, notas e frequencia.

Observacao: funcionalidades marcadas no menu como ainda nao implementadas, como
historico academico e diario, nao devem ser apresentadas como concluidas.

## Comandos Uteis

```bash
mvn.cmd clean test
mvn.cmd checkstyle:check
mvn.cmd exec:java
```

## Equipe

- Arthur Barbosa
- Artur Oliveira
- Davi Roberto
