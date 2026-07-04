# Requisitos do Projeto — ClassRoomPB

Este documento resume os requisitos do projeto da disciplina Engenharia de Software 2, com base no PDF fornecido pela professora.

O sistema se chama **ClassRoomPB** e consiste em um sistema de controle acadêmico simplificado, com persistência local em arquivos e interface de comandos no terminal.

## Perfis de usuário

O sistema possui quatro tipos principais de usuário:

1. Aluno
2. Professor
3. Coordenador
4. Administrador

Cada perfil deve ter uma visão diferente do sistema e acesso apenas às funcionalidades compatíveis com seu papel.

---

# Requisitos Funcionais

## Cadastro e Autenticação

### RF01
O sistema deve permitir o cadastro de alunos, professores, coordenadores e administradores.

### RF02
O sistema deve permitir login com matrícula/e-mail e senha.

### RF03
O sistema deve validar perfis de acesso, exibindo funcionalidades diferentes para cada tipo de usuário.

### RF04
O sistema deve impedir cadastro duplicado por matrícula ou e-mail.

---

## Gestão Acadêmica Básica

### RF05
O administrador deve poder cadastrar cursos.

### RF06
O coordenador deve poder cadastrar disciplinas.

### RF07
Cada disciplina deve possuir:
- código;
- nome;
- carga horária;
- créditos;
- pré-requisitos opcionais.

### RF08
O coordenador deve poder cadastrar períodos letivos, como "2026.2".

### RF09
O sistema deve permitir ativar ou encerrar um período letivo.

---

## Oferta de Turmas

### RF10
O coordenador deve poder ofertar turmas para uma disciplina em um período letivo.

### RF11
Cada turma deve possuir:
- professor responsável;
- limite de vagas;
- horário;
- sala.

### RF12
O sistema deve impedir choque de horário para o professor.

### RF13
O sistema deve impedir oferta de turma sem professor responsável.

### RF14
O coordenador deve poder alterar ou cancelar uma turma antes do início das aulas.

---

## Matrícula

### RF15
O aluno deve poder consultar disciplinas/turmas disponíveis.

### RF16
O aluno deve poder solicitar matrícula em uma turma.

### RF17
O sistema deve verificar se há vagas disponíveis.

### RF18
O sistema deve verificar se o aluno cumpriu os pré-requisitos da disciplina.

### RF19
O sistema deve impedir choque de horário entre turmas do mesmo aluno.

### RF20
O sistema deve confirmar matrícula automaticamente quando todos os critérios forem atendidos.

### RF21
Caso não haja vaga, o aluno deve entrar em lista de espera.

### RF22
O aluno deve poder cancelar matrícula dentro do período permitido.

---

## Lista de Espera

### RF23
O sistema deve manter lista de espera por turma.

### RF24
Quando uma vaga for liberada, o sistema deve chamar automaticamente o próximo aluno da lista.

### RF25
A lista de espera deve respeitar a ordem de solicitação.

### RF26
O coordenador deve poder visualizar a lista de espera de cada turma.

---

## Frequência

### RF27
O professor deve poder registrar presença/falta dos alunos por aula.

### RF28
O sistema deve calcular automaticamente o percentual de frequência.

### RF29
O aluno deve poder consultar sua frequência por disciplina.

### RF30
O sistema deve alertar quando o aluno estiver abaixo do mínimo exigido.

---

## Notas e Avaliação

### RF31
O professor deve poder lançar notas das etapas:
- etapa 1;
- etapa 2.

### RF32
O sistema deve calcular automaticamente a média final.

### RF33
O aluno deve poder consultar suas notas.

### RF34
O sistema deve informar a situação do aluno:
- aprovado;
- reprovado por nota;
- reprovado por falta;
- em recuperação.

### RF35
O professor deve poder alterar notas antes do fechamento da turma.

---

## Histórico Acadêmico

### RF36
O sistema deve manter histórico das disciplinas cursadas pelo aluno.

### RF37
O histórico deve registrar:
- período;
- disciplina;
- professor;
- nota final;
- frequência;
- situação.

### RF38
O aluno deve poder consultar seu histórico acadêmico.

### RF39
O coordenador deve poder consultar o histórico dos alunos do curso.

---

## Relatórios

### RF40
O coordenador deve gerar relatório de alunos matriculados por turma.

### RF41
O coordenador deve gerar relatório de ocupação de vagas.

### RF42
O coordenador deve gerar relatório de reprovação por disciplina.

### RF43
O administrador deve gerar relatório geral de usuários cadastrados.

### RF_SurpresaN
Requisitos surpresa podem ser definidos durante o semestre.

---

# Regras de Negócio

### RN01
Um aluno não pode se matricular duas vezes na mesma turma.

### RN02
Um aluno não pode se matricular em turmas com horários conflitantes.

### RN03
Uma turma não pode ultrapassar o número máximo de vagas.

### RN04
Uma disciplina pode ter pré-requisitos.

### RN05
Um aluno só pode cursar disciplina se tiver sido aprovado nos pré-requisitos.

### RN06
Professor não pode ministrar duas turmas no mesmo horário.

### RN07
Notas devem estar entre 0 e 10.

### RN08
Frequência mínima para aprovação: 75%.

### RN09
Média mínima para aprovação direta: 7,0.

### RN10
Média entre 4,0 e 6,9 gera recuperação.

### RN11
Média abaixo de 4,0 reprova por nota.

### RN12
Reprovação por falta prevalece sobre aprovação por nota.

---

# Considerações Técnicas

## Persistência

O armazenamento deve ser local.

O sistema deve armazenar em disco todos os dados necessários para manter seu estado mesmo depois de ser fechado.

Exemplos de dados persistidos:
- usuários;
- cursos;
- disciplinas;
- turmas;
- períodos letivos;
- matrículas;
- notas;
- frequência;
- histórico.

No projeto atual, a persistência é feita usando arquivos JSON.

## Interface

O sistema não precisa ter interface gráfica.

A interface deve ser uma interface de comandos simplificada no terminal.

O foco principal deve ser a qualidade do projeto, regras de negócio, persistência, testes e tratamento de erros.

## Restrições

Não é para usar API, Swagger ou tecnologias externas desse tipo.

O projeto deve usar:
- Java;
- JUnit;
- padrões de projeto;
- persistência local.

---

# Releases

## Release 1 — Núcleo Acadêmico

### Objetivo
Criar a base do sistema.

### Requisitos
RF01 a RF14.

### Entregas esperadas
- cadastro e login;
- cadastro de usuários;
- cadastro de cursos;
- cadastro de disciplinas;
- cadastro de períodos letivos;
- ativação/encerramento de períodos letivos;
- oferta de turmas;
- professores responsáveis;
- validações básicas;
- persistência;
- testes.

---

## Release 2 — Matrícula e Acompanhamento

### Objetivo
Implementar o fluxo principal do aluno.

### Requisitos
RF15 a RF30.

### Entregas esperadas
- consulta de turmas;
- consulta de disciplinas;
- solicitação de matrícula;
- validação de vagas;
- validação de pré-requisitos;
- validação de choque de horário;
- confirmação automática de matrícula;
- lista de espera;
- cancelamento de matrícula;
- frequência;
- cálculo de percentual de frequência;
- tratamento de erros;
- persistência;
- testes.

---

## Release 3 — Avaliação, Histórico e Evolução

### Objetivo
Fechar o ciclo acadêmico.

### Requisitos
RF31 a RF45.

### Entregas esperadas
- lançamento de notas;
- cálculo de média;
- situação final do aluno;
- consulta de notas;
- histórico acadêmico;
- relatórios;
- tratamento de erros;
- persistência;
- testes.

---

## Release 4 — Troca de Projetos

### Objetivo
Implementar novos requisitos definidos posteriormente.

---

# Entregas do Repositório

Cada release deve conter:

1. Implementação dos requisitos da entrega.
2. Persistência local.
3. Interface de comandos simplificada.
4. Testes.
5. Tratamento de erros.

Além do código, deve existir uma pasta chamada `releases`.

Dentro dela, devem ser adicionados os relatórios da entrega.

## Relatório da Release

Nome sugerido:

```text
relatorio-release1.pdf