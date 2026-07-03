# 🎓 Sistema Acadêmico — ClassRoomPB

## 📋 Sumário  
- [🎯 Descrição](#-descrição)  
- [📦 Pré-requisitos](#-pré-requisitos)  
- [🛠️ Preparando o Ambiente](#️-preparando-o-ambiente)  
  - [💻 Windows](#-windows)  
  - [🐧 Linux](#-linux)  
  - [🍎 MacOS](#-macos)  
- [🚀 Instruções de Uso](#-instruções-de-uso)  
- [🧪 Executando os Testes](#-executando-os-testes)  
- [📁 Estrutura do Projeto](#-estrutura-do-projeto)  
- [👥 Equipe Envolvida](#-equipe-envolvida)

---

## 🎯 Descrição

O **ClassRoomPB** é um sistema acadêmico desenvolvido em **Java**, com foco na gestão de usuários, cursos, disciplinas, turmas e períodos letivos. O sistema conta com menus interativos por perfil de acesso (**Administrador, Coordenador, Professor e Aluno**) e persiste os dados localmente em arquivos **JSON**, utilizando a biblioteca **Jackson**.

### 🔍 Funcionalidades Principais

- 👤 Cadastro e autenticação de usuários por tipo (**Administrador, Coordenador, Professor e Aluno**)
- 📚 Gestão de cursos, disciplinas, turmas e períodos letivos
- 🧭 Menus de navegação distintos por perfil de usuário
- 💾 Persistência de dados em arquivos JSON nos diretórios `cursos/`, `disciplinas/`, `turmas/`, `periodos/` e `usuarios/`
- 🧪 Cobertura de testes automatizados com **JUnit 5**

---

## 📦 Pré-Requisitos

- Apache Maven versão **3.8.7** ou superior;
- Java versão **21** ou superior.

💡 **As dependências do projeto são gerenciadas automaticamente pelo Maven:**

- **JUnit Jupiter 5.10.2** — execução dos testes automatizados
- **Jackson Databind 2.17.0** — serialização/desserialização JSON para persistência dos dados

---

## 🛠️ Preparando o Ambiente

### 💻 Windows

#### 1. Instalando o JDK
- Baixe e instale a versão 21+ do JDK no [site da Oracle](https://www.oracle.com/br/java/technologies/downloads/)

#### 2. Configurando o Visual Studio Code
- Instale o [Visual Studio Code](https://code.visualstudio.com/docs/setup/windows)  
- Adicione o ["Extension Pack for Java"](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

💡 **Observação:** Este pacote já inclui o Apache Maven, dispensando o passo 3 caso utilize o Visual Studio Code.

#### 3. Instalando o Apache Maven
- Baixe o [Apache Maven](https://maven.apache.org/download.cgi)  
- Siga o [tutorial de instalação oficial](https://maven.apache.org/install.html)
- Ou consulte este [tutorial detalhado para Windows](https://charlesmms.azurewebsites.net/2017/09/04/instalando-maven-no-windows-10/)

---

### 🐧 Linux

📌 **Foco no Ubuntu:** As instruções abaixo são específicas para a distribuição Ubuntu. Se você utiliza outra distribuição Linux:

- Consulte a documentação oficial do seu sistema
- Adapte os comandos conforme necessário
- Pesquise por guias específicos para sua distro (Arch, Fedora, etc.)

💡 **Dica:** A maioria dos comandos pode ser adaptada trocando o gerenciador de pacotes (`apt` → `dnf` no Fedora).

#### 1. Instalando o JDK
- Tutorial: [Como instalar o JDK no Ubuntu](https://www.hostinger.com.br/tutoriais/como-instalar-java-no-ubuntu)

#### 2. Configurando o VS Code
- Instale o [VS Code para Linux](https://code.visualstudio.com/docs/setup/linux)  
- Adicione o ["Extension Pack for Java"](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

💡 **Observação:** Este pacote já inclui o Apache Maven, dispensando o passo 3 caso utilize o VS Code.

#### 3. Instalando o Apache Maven
- Tutorial: [Instalar Apache Maven no Ubuntu](https://www.hostinger.com.br/tutoriais/install-maven-ubuntu)

---

### 🍎 MacOS

#### 1. Instalando o JDK
- Baixe o JDK 21+ na [Oracle](https://www.oracle.com/br/java/technologies/downloads/)

#### 2. Configurando o VS Code
- Instale o [VS Code para Mac](https://code.visualstudio.com/docs/setup/mac)  
- Adicione o ["Extension Pack for Java"](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

💡 **Observação:** Este pacote já inclui o Apache Maven, dispensando o passo 3 caso utilize o Visual Studio Code.

#### 3. Instalando o Apache Maven
- Siga o [tutorial para MacOS](https://www.digitalocean.com/community/tutorials/install-maven-mac-os)

---

## 🚀 Instruções de Uso

Após instalar o Java (JDK 21+), o Apache Maven e o Visual Studio Code, siga os passos abaixo:

1. Clone o repositório:  

   ```bash
   git clone https://github.com/<seu-usuario>/sistema-academico.git
   ```

2. Ou baixe como `.zip` e descompacte.

3. Abra a pasta do projeto no Visual Studio Code:

   Vá em **File > Open Folder...** e selecione a pasta `sistema-academico-main`.

4. Aguarde o VS Code carregar automaticamente as dependências Maven (barra de progresso no rodapé).

5. Execute o sistema abrindo o arquivo:

   ```bash
   src/main/java/br/com/classroompb/application/Program.java
   ```

6. Clique no botão `▷ Run` acima do método `main`, ou utilize o atalho `F5`.

7. Interaja pelo terminal integrado do VS Code. O sistema exibirá o menu principal e solicitará login por tipo de usuário.

📁 Todos os dados persistidos (usuários, cursos, disciplinas, turmas e períodos letivos) serão salvos automaticamente nos diretórios `usuarios/`, `cursos/`, `disciplinas/`, `turmas/` e `periodos/` na raiz do projeto, em formato JSON.

---

## 🧪 Executando os Testes

O projeto possui testes automatizados com **JUnit 5**, cobrindo repositórios, serviços e entidades.

### Pelo terminal integrado do VS Code:

```bash
mvn test
```

### Pelo VS Code (interface gráfica):

1. Abra qualquer arquivo de teste em `src/test/java/...`
2. Clique no ícone de beaker (`⚗️`) na barra lateral esquerda para abrir o painel de testes
3. Clique em **Run All Tests**

---

## 🎨 Padrão de Código (Google Java Style)

- Indentação de **2 espaços** (sem uso de tabs)
- Limite de **100 caracteres** por linha
- Chaves obrigatórias em blocos `if`, `for`, `while`, mesmo com uma única instrução
- Nomenclatura `UpperCamelCase` para classes e `lowerCamelCase` para métodos/variáveis
- Um único `import` por linha, sem uso de `import` com wildcard (`*`)
- Javadoc obrigatório em classes e métodos públicos

---

## 📁 Estrutura do Projeto

```bash
sistema-academico-main/
├── cursos/              # Dados persistidos dos cursos (JSON)
├── disciplinas/         # Dados persistidos das disciplinas (JSON)
├── periodos/            # Dados persistidos dos períodos letivos (JSON)
├── turmas/              # Dados persistidos das turmas (JSON)
├── usuarios/            # Dados persistidos dos usuários (JSON)
├── pom.xml              # Configuração Maven (dependências e plugins)
└── src/
    ├── main/java/br/com/classroompb/
    │   ├── application/     # Classe principal (Program.java)
    │   ├── model/
    │   │   ├── entities/    # Entidades do sistema
    │   │   ├── enums/       # Enum TipoUsuario
    │   │   ├── exception/   # Exceções customizadas
    │   │   ├── repository/  # Persistência JSON via Jackson
    │   │   └── services/    # Regras de negócio
    │   └── ui/              # Menus por perfil de usuário
    └── test/java/           # Testes automatizados (JUnit 5)
```

---

## 👥 Equipe Envolvida

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Arthur-Donato">
        <img src="https://github.com/Arthur-Donato.png" width="100px;" alt="Foto do Arthur"/><br />
        <sub><b>Arthur Barbosa</b></sub>
      </a><br />
    </td>
    <td align="center">
      <a href="https://github.com/ArturOliveir4">
        <img src="https://github.com/ArturOliveir4.png" width="100px;" alt="Foto do Artur"/><br />
        <sub><b>Artur Oliveira</b></sub>
      </a><br />
    </td>
    <td align="center">
      <a href="https://github.com/davrzin">
        <img src="https://github.com/davrzin.png" width="100px;" alt="Foto do Davi"/><br />
        <sub><b>Davi Roberto</b></sub>
      </a><br />
    </td>
  </tr>
</table>
