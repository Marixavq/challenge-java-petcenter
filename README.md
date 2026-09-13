# Sistema de Diário Clínico PetCenter 🐾

## 📌 Objetivo do Projeto

Este projeto está sendo desenvolvido por estudantes de **Análise e Desenvolvimento de Sistemas da FIAP** para o
Challenge proposto pela **Clyvo**.

O objetivo do sistema é realizar o acompanhamento do pet e identificar possíveis anomalias comportamentais, permitindo a
busca por tratamentos de saúde rapidamente antes que o problema se agrave.

A proposta também busca transformar registros cotidianos do pet em informações organizadas e analisáveis ao longo do
tempo, apoiando o acompanhamento veterinário.

O sistema vai além de um simples CRUD, permitindo:

* Registro contínuo da rotina do pet;
* Organização das informações em linha do tempo;
* Persistência estruturada dos dados;
* Acompanhamento veterinário;
* Solicitação e gerenciamento de atendimento veterinário;
* Geração de informações que podem futuramente apoiar insights clínicos.

---

## 🛠️ Tecnologias Utilizadas

### Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* JWT (JSON Web Token)
* Bean Validation
* Lombok

### Banco de Dados

* H2 Database — utilizado durante o desenvolvimento e testes;
* Oracle Database — previsto como alternativa para futura melhoria no ambiente de produção;
* Flyway — controle e versionamento das migrations.

### Documentação

* Swagger / OpenAPI

### Ferramentas

* Maven
* Git
* GitHub
* IntelliJ IDEA
* Insomnia

### Deploy

* Render

---

## 📂 Estrutura do Projeto

```text
src/
└── main/
    ├── java/
    │   └── com/fiap/challengepetcenter/
    │       ├── config/
    │       ├── controller/
    │       ├── dto/
    │       │   ├── request/
    │       │   └── response/
    │       ├── model/
    │       ├── repository/
    │       ├── security/
    │       ├── service/
    │       └── ChallengepetcenterApplication.java
    │
    └── resources/
        ├── db/
        │   └── migration/
        └── application.properties
```

A aplicação utiliza uma arquitetura em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Controller

Responsável pelo recebimento das requisições HTTP, validação dos dados de entrada e controle de acesso aos endpoints.

### Service

Responsável pelas regras de negócio, validações de ownership e execução dos principais fluxos da aplicação.

### Repository

Responsável pela persistência e consulta dos dados utilizando Spring Data JPA.

### DTO

Utilizados para controlar os dados de entrada e saída da API, evitando a exposição direta das entidades.

---

## 🚀 Funcionalidades

* Cadastro de usuários;
* Autenticação de usuários;
* Autenticação baseada em JWT;
* Controle de acesso por perfil;
* Perfil de Tutor;
* Perfil de Veterinário;
* Cadastro e gerenciamento de pets;
* Cadastro e gerenciamento de dados profissionais de veterinários;
* Solicitação de atendimento veterinário;
* Aceitação ou recusa de solicitações;
* Vínculo entre pet e veterinário;
* Diário de acompanhamento do pet;
* Registros associados às entradas do diário;
* Alertas para pets;
* Desativação de alertas;
* Validações de dados utilizando Bean Validation;
* Paginação de consultas;
* Versionamento do banco de dados utilizando Flyway;
* Documentação da API utilizando Swagger/OpenAPI.

---

## 👥 Perfis de Acesso

### Tutor

O Tutor é responsável pelo gerenciamento dos seus pets e pelo acompanhamento de informações relacionadas à saúde e
rotina dos animais.

Entre suas principais operações estão:

* Gerenciar seus pets;
* Criar e atualizar entradas do diário;
* Criar registros de acompanhamento;
* Solicitar atendimento veterinário;
* Acompanhar solicitações;
* Consultar veterinários disponíveis;
* Gerenciar recursos relacionados aos seus pets.

### Veterinário

O Veterinário é responsável pelo gerenciamento do seu perfil profissional e pelo atendimento das solicitações.

Entre suas principais operações estão:

* Gerenciar seu perfil profissional;
* Visualizar solicitações de atendimento;
* Aceitar ou recusar solicitações;
* Gerenciar vínculos com pets;
* Criar alertas;
* Gerenciar recursos relacionados ao acompanhamento veterinário.

---

## 🔐 Autenticação e Autorização

A API utiliza **Spring Security** juntamente com **JWT (JSON Web Token)**.

O usuário realiza login informando suas credenciais e recebe um token JWT.

Esse token deve ser enviado nas requisições protegidas através do header:

```text
Authorization: Bearer SEU_TOKEN
```

A aplicação possui dois perfis principais:

```text
TUTOR
VETERINARIO
```

Cada perfil possui permissões diferentes para acessar os recursos da API.

### Controle de acesso

A autorização por perfil é realizada através de `@PreAuthorize` nos Controllers.

Exemplo:

```java
@PreAuthorize("hasRole('VETERINARIO')")
```

Além do controle por perfil, algumas operações possuem validações de **ownership** no Service, garantindo que um usuário
não possa alterar ou excluir recursos pertencentes a outro usuário.

A divisão de responsabilidades é:

```text
Controller
→ verifica o perfil do usuário

Service
→ verifica ownership e regras de negócio
```

### Respostas de segurança

```text
401 Unauthorized
→ usuário não autenticado ou token inválido

403 Forbidden
→ usuário autenticado, mas sem permissão para executar a operação
```

---

## 🔑 Configuração da chave JWT

A aplicação utiliza uma chave secreta para assinar e validar os tokens JWT.

A chave deve ser configurada através da variável de ambiente:

```text
JWT_SECRET
```

### Execução local

A aplicação utiliza uma chave secreta para geração e validação dos tokens JWT.
Exemplo:

```text
JWT_SECRET=sua-chave-secreta-aqui
```

No IntelliJ IDEA, a variável pode ser configurada nas **Run Configurations** da aplicação.

Para ambientes de produção, recomenda-se configurar a chave por meio de uma variável de ambiente, evitando que
informações sensíveis sejam armazenadas no código-fonte.

Para facilitar a execução e os testes deste projeto acadêmico, a variável é opcional. Caso não seja configurada, a
aplicação utiliza uma chave padrão definida para o ambiente de desenvolvimento.

---

## 💾 Banco de Dados e Flyway

Durante o desenvolvimento, a aplicação utiliza o **H2 Database**.

O banco utilizado localmente é configurado como banco em memória, sendo adequado para desenvolvimento e testes.

As alterações da estrutura do banco são controladas pelo **Flyway**.

As migrations ficam em:

```text
src/main/resources/db/migration/
```

Cada migration representa uma versão da estrutura do banco e é executada automaticamente pela aplicação.

Exemplo:

```text
V1__create_users.sql
V2__create_pets.sql
V3__create_veterinarios.sql
...
```

O Flyway garante que as alterações estruturais sejam aplicadas de forma controlada e versionada.

---

## ▶️ Como Executar o Projeto

### Pré-requisitos

Antes de executar o projeto, tenha instalado:

* Java 21;
* Maven;
* Git.

### 1. Clonar o repositório

```bash
git clone https://github.com/Marixavq/challenge-java-petcenter.git
```

### 2. Entrar no diretório

```bash
cd challenge-java-petcenter
```

### 3. Abrir no IntelliJ IDEA

Abra o projeto como um projeto Maven.

Aguarde o download das dependências antes de executar a aplicação.

### 4. Configurar a chave JWT

Configure a variável de ambiente:

```text
JWT_SECRET=sua-chave-secreta-aqui
```

### 5. Executar a aplicação

Execute a classe principal:

```java
ChallengepetcenterApplication
```

Ou utilize o Maven:

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

### 6. Acessar a aplicação

Após iniciar o projeto:

```text
http://localhost:8080
```

---

## ☁️ Deploy

A aplicação está publicada utilizando o **Render**.

### URL da API

```text
https://challenge-java-petcenter.onrender.com
```

### Swagger em produção

```text
https://challenge-java-petcenter.onrender.com/swagger-ui/index.html
```

A URL do Swagger permite visualizar e testar os endpoints da API diretamente pelo navegador.

> Observação: o ambiente de deploy utiliza as variáveis de ambiente configuradas no Render, incluindo a chave
`JWT_SECRET`.

---

## 📚 Swagger / OpenAPI

Após iniciar a aplicação localmente, a documentação interativa pode ser acessada em:

```text
http://localhost:8080/swagger-ui/index.html
```

No ambiente de produção:

```text
https://challenge-java-petcenter.onrender.com/swagger-ui/index.html
```

O Swagger permite:

* Visualizar os endpoints;
* Consultar parâmetros;
* Visualizar modelos de Request e Response;
* Testar as operações da API;
* Enviar JWT para endpoints protegidos.

### Utilizando JWT no Swagger

1. Realize o login através do endpoint de autenticação;
2. Copie o JWT retornado;
3. Clique em **Authorize** no Swagger;
4. Informe:

```text
Bearer SEU_TOKEN
```

5. Clique em **Authorize**;
6. Execute os endpoints protegidos.

---

## 🔄 Principais Fluxos de Negócio

### Fluxo de solicitação veterinária

O Tutor pode solicitar o atendimento de um veterinário para um de seus pets.

```text
Tutor
  ↓
Seleciona um pet
  ↓
Seleciona um veterinário
  ↓
Cria solicitação
  ↓
Solicitação PENDENTE
  ↓
Veterinário visualiza a solicitação
  ↓
       ┌───────────────┐
       ↓               ↓
    ACEITA           RECUSA
       ↓               ↓
Vínculo criado     Solicitação
entre pet e        RECUSADA
veterinário
```

### Fluxo de acompanhamento do pet

```text
Tutor
  ↓
Pet
  ↓
Entrada no diário
  ↓
Registro
```

Esse fluxo permite manter um histórico estruturado da rotina do pet.

### Fluxo de alertas

```text
Veterinário
  ↓
Cria alerta para um pet
  ↓
Alerta ativo
  ↓
Tutor ou veterinário responsável
  ↓
Desativa alerta
  ↓
Alerta inativo
```

---

## 📋 Validações e Regras de Negócio

A aplicação possui validações para impedir operações inválidas, incluindo:

* Campos obrigatórios;
* Limites de tamanho dos campos;
* E-mail único para usuários;
* Controle de acesso por perfil;
* Validação de ownership;
* Impedimento de acesso ou alteração indevida de recursos;
* Impedimento de solicitações veterinárias duplicadas;
* Controle dos estados das solicitações;
* Controle dos vínculos entre pets e veterinários;
* Impedimento de exclusão de recursos que possuem dependências;
* Validação dos dados recebidos através de Bean Validation.

---

## 🧪 Testes

A API pode ser testada utilizando:

* Swagger UI;
* Insomnia;
* Postman;

Os principais cenários testados incluem:

* Cadastro de usuário;
* Login e geração de JWT;
* Acesso sem autenticação;
* Acesso com perfil correto;
* Acesso com perfil incorreto;
* Cadastro e gerenciamento de pets;
* Criação de entradas no diário;
* Criação de registros;
* Criação de solicitações veterinárias;
* Aceitação de solicitações;
* Recusa de solicitações;
* Criação de vínculos entre pets e veterinários;
* Criação e desativação de alertas;
* Validação de ownership;
* Paginação;
* Validações dos dados enviados.

---

## 👨‍💻 Equipe

| Integrante                | RM       | Perfil GitHub                                     |
|---------------------------|----------|---------------------------------------------------|
| Arthur dos Santos Cabral  | RM566515 | [ArthurCPV](https://github.com/ArthurCPV)         |
| Bruno Martins Bettio      | RM564939 | [TaikaWaititi](https://github.com/TaikaWaititi)   |
| José Diogo Da Silva Neves | RM562341 | [ZeDio](https://github.com/ZeDio)                 |
| Júlia Tiziotto Buttler    | RM564975 | [JuliaTButtler](https://github.com/JuliaTButtler) |
| Mariana Xavier Quispe     | RM566357 | [Marixavq](https://github.com/Marixavq)           |

Este projeto foi desenvolvido para a disciplina de **Java Advanced** e integra conceitos aplicados em outras disciplinas
do Challenge interdisciplinar da FIAP.

---

## 📄 Links do Projeto

### GitHub

https://github.com/Marixavq/challenge-java-petcenter

### Deploy

https://challenge-java-petcenter.onrender.com

### Swagger

https://challenge-java-petcenter.onrender.com/swagger-ui/index.html

### Vídeo

