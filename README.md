🎓 Sistema Acadêmico — ClassRoomPB
📋 Sumário

🎯 Descrição
📦 Pré-requisitos
🛠️ Preparando o Ambiente

💻 Windows
🐧 Linux
🍎 MacOS


🚀 Instruções de Uso
🧪 Executando os Testes
📁 Estrutura do Projeto
👥 Equipe Envolvida


🎯 Descrição
O ClassRoomPB é um sistema acadêmico desenvolvido em Java, com foco na gestão de usuários, cursos, disciplinas, turmas e períodos letivos. O sistema conta com menus interativos por perfil de acesso (Administrador, Coordenador, Professor e Aluno) e persiste os dados localmente em arquivos JSON, utilizando a biblioteca Jackson.
🔍 Funcionalidades Principais

Cadastro e autenticação de usuários por tipo (Administrador, Coordenador, Professor, Aluno)
Gestão de cursos, disciplinas, turmas e períodos letivos
Menus de navegação distintos por perfil de usuário
Persistência de dados em arquivos JSON nos diretórios cursos/, disciplinas/, turmas/, periodos/ e usuarios/
Cobertura de testes automatizados com JUnit 5


📦 Pré-requisitos

Java JDK versão 21 ou superior
Apache Maven versão 3.8.7 ou superior


💡 As dependências do projeto são gerenciadas automaticamente pelo Maven:

JUnit Jupiter 5.10.2 — execução dos testes automatizados
Jackson Databind 2.17.0 — serialização/desserialização JSON para persistência dos dados



🛠️ Preparando o Ambiente
💻 Windows
1. Instalando o JDK 21

Baixe e instale a versão 21+ do JDK no site da Oracle

2. Configurando o Visual Studio Code

Instale o Visual Studio Code
Adicione o "Extension Pack for Java"

💡 Observação: O Extension Pack for Java já inclui o Apache Maven integrado, dispensando a instalação manual caso utilize o VS Code.
3. Instalando o Apache Maven (caso não use o VS Code)

Baixe o Apache Maven
Siga o tutorial de instalação oficial
Ou consulte este tutorial detalhado para Windows 10


🐧 Linux
📌 Foco no Ubuntu: As instruções abaixo são específicas para o Ubuntu. Para outras distribuições, adapte os comandos conforme o gerenciador de pacotes da sua distro (ex: apt → dnf no Fedora).
1. Instalando o JDK 21

Siga o tutorial: Como instalar o JDK no Ubuntu

2. Configurando o VS Code

Instale o VS Code para Linux
Adicione o "Extension Pack for Java"

💡 Observação: O Extension Pack for Java já inclui o Apache Maven, dispensando o passo 3 se usar o VS Code.
3. Instalando o Apache Maven (caso não use o VS Code)

Siga o tutorial: Instalar Apache Maven no Ubuntu


🍎 MacOS
1. Instalando o JDK 21

Baixe o JDK 21+ na Oracle

2. Configurando o VS Code

Instale o VS Code para Mac
Adicione o "Extension Pack for Java"

💡 Observação: O Extension Pack for Java já inclui o Apache Maven, dispensando o passo 3 se usar o VS Code.
3. Instalando o Apache Maven (caso não use o VS Code)

Siga o tutorial para MacOS


🚀 Instruções de Uso
Após instalar o Java (JDK 21+), o Apache Maven e o Visual Studio Code, siga os passos abaixo:

Clone o repositório:

bash   git clone https://github.com/<seu-usuario>/sistema-academico.git
Ou baixe como .zip e descompacte.

Abra a pasta do projeto no VS Code:

Vá em File > Open Folder... e selecione a pasta sistema-academico-main


Aguarde o VS Code carregar as dependências Maven automaticamente (barra de progresso no rodapé).
Execute o sistema abrindo o arquivo:

   src/main/java/br/com/classroompb/application/Program.java
E clicando no botão ▷ Run que aparece acima do método main, ou usando o atalho F5.

Interaja pelo terminal integrado do VS Code. O sistema exibirá o menu principal e solicitará login por tipo de usuário.


📁 Os dados persistidos (usuários, cursos, disciplinas, turmas e períodos) serão salvos automaticamente nos diretórios usuarios/, cursos/, disciplinas/, turmas/ e periodos/ na raiz do projeto, em formato JSON.


🧪 Executando os Testes
O projeto possui testes automatizados com JUnit 5, cobrindo repositórios, serviços e entidades.
Pelo terminal integrado do VS Code:
bashmvn test
Pelo VS Code (interface gráfica):

Abra qualquer arquivo de teste em src/test/java/...
Clique no ícone de beaker (⚗️) na barra lateral esquerda para abrir o painel de testes
Clique em Run All Tests


📁 Estrutura do Projeto
sistema-academico-main/
├── cursos/              # Dados persistidos dos cursos (JSON)
├── disciplinas/         # Dados persistidos das disciplinas (JSON)
├── periodos/            # Dados persistidos dos períodos letivos (JSON)
├── turmas/              # Dados persistidos das turmas (JSON)
├── pom.xml              # Configuração Maven (dependências e plugins)
└── src/
    ├── main/java/br/com/classroompb/
    │   ├── application/     # Classe principal (Program.java)
    │   ├── model/
    │   │   ├── entities/    # Entidades: Usuário, Curso, Disciplina, Turma, PeriodoLetivo
    │   │   ├── enums/       # Enum TipoUsuario
    │   │   ├── exception/   # Exceções customizadas
    │   │   ├── repository/  # Persistência JSON via Jackson
    │   │   └── services/    # Regras de negócio
    │   └── ui/              # Menus por perfil (Admin, Coordenador, Professor, Aluno)
    └── test/java/           # Testes automatizados (JUnit 5)

👥 Equipe Envolvida
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Arthur-Donato ">
        <img src="https://github.com/Arthur-Donato.png" width="100px;" alt="Membro 1"/><br />
        <sub><b>Arthur Barbosa</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/ArturOliveir4">
        <img src="https://github.com/ArturOliveir4.png" width="100px;" alt="Membro 2"/><br />
        <sub><b>Artur Oliveira</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/davrzin">
        <img src="https://github.com/davrzin.png" width="100px;" alt="Membro 3"/><br />
        <sub><b>Davi Roberto</b></sub>
      </a>
    </td>
  </tr>
</table>
