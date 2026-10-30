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
* Spring Web MVC
* Spring Data JPA
* Spring Security
* JWT (JSON Web Token)
* Bean Validation
* Lombok
* Spring Boot Actuator
* Micrometer
* Prometheus

### Banco de Dados

* Oracle Database — persistência dos dados da aplicação;
* Flyway — controle e versionamento das migrations do banco de dados.

### Documentação e Monitoramento

* Swagger / OpenAPI — documentação e exploração dos endpoints;
* Spring Boot Actuator — disponibilização de endpoints de monitoramento;
* Micrometer — instrumentação e coleta de métricas da aplicação;
* Prometheus — exposição das métricas em formato compatível com o Prometheus.

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
            ├── exception/
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
Oracle Database
```

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
* Tratamento de erros e respostas HTTP adequadas;
* Versionamento do banco de dados utilizando Flyway;
* Métricas de negócio e monitoramento da aplicação;
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

No IntelliJ IDEA, a variável pode ser configurada nas **Run Configurations** da aplicação.

Exemplo:

```text
JWT_SECRET=sua-chave-secreta-aqui
```

Para ambientes de produção, recomenda-se configurar a chave por meio de uma variável de ambiente, evitando que
informações sensíveis sejam armazenadas no código-fonte.

Para facilitar a execução e os testes deste projeto acadêmico, a variável é opcional. Caso não seja configurada, a
aplicação utiliza uma chave padrão definida para o ambiente de desenvolvimento.

---

## 💾 Banco de Dados Oracle e Flyway

A aplicação utiliza o **Oracle Database** como banco de dados relacional para persistência das informações.

A conexão é configurada por meio de variáveis de ambiente, evitando o armazenamento de credenciais diretamente no
código-fonte.

As principais variáveis de conexão são:

```text
ORACLE_USER
ORACLE_PASSWORD
```

A URL de conexão utiliza o serviço Oracle disponibilizado pela FIAP:

```text
jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL
```

As credenciais devem ser configuradas no ambiente de execução local, como nas **Run Configurations** do IntelliJ IDEA, e
nas variáveis de ambiente do serviço publicado no Render.

### Versionamento do banco de dados

As alterações estruturais do banco são controladas pelo **Flyway**, permitindo que as migrations sejam versionadas e
aplicadas de forma organizada.

Os arquivos ficam em:

```text
src/main/resources/db/migration/
```

Cada migration representa uma versão da estrutura do banco e é executada conforme o controle de versionamento do Flyway.

Exemplos de nomenclatura:

```text
V1__create_users.sql
V2__create_pets.sql
V3__create_veterinarios.sql
```

O projeto utiliza migrations para manter a evolução do esquema do banco de dados controlada e versionada, garantindo a
aplicação organizada das alterações estruturais.

---

## 📊 Observabilidade e Monitoramento

A aplicação possui instrumentação de métricas utilizando **Spring Boot Actuator**, **Micrometer** e **Prometheus**,
permitindo acompanhar a disponibilidade da API e observar o comportamento de operações importantes.

### Spring Boot Actuator

O Actuator disponibiliza endpoints de gerenciamento e monitoramento da aplicação.

Os principais endpoints utilizados são:

| Endpoint               | Finalidade                                                |
|------------------------|-----------------------------------------------------------|
| `/actuator/health`     | Verificar o estado de saúde da aplicação.                 |
| `/actuator/metrics`    | Consultar as métricas registradas.                        |
| `/actuator/prometheus` | Expor as métricas em formato compatível com o Prometheus. |

### Métricas de negócio

Foram implementados **counters** e **timers** para acompanhar operações relevantes dos serviços.

**Counters**

Contabilizam a ocorrência de operações de negócio bem-sucedidas, como a criação de usuários, pets, registros e alertas.

Também são contabilizadas operações específicas de solicitações veterinárias, como aceitação e recusa.

Exemplos de métricas:

```text
petcenter.usuarios.criados
petcenter.pets.criados
petcenter.registros.criados
petcenter.alertas.criados
petcenter.solicitacoes.criadas
petcenter.solicitacoes.aceitas
petcenter.solicitacoes.recusadas
```

**Timers**

Registram informações de duração e quantidade de execuções de operações selecionadas, permitindo observar o tempo gasto
em operações de criação, listagem e consultas específicas.

Exemplos:

```text
petcenter.usuarios.criacao.tempo
petcenter.pets.criacao.tempo
petcenter.solicitacoes.criacao.tempo
petcenter.solicitacoes.aceitacao.tempo
petcenter.solicitacoes.recusa.tempo
```

A instrumentação também contempla operações de listagem e consultas específicas de entidades, conforme os serviços
implementados.

### Objetivos da instrumentação

* Acompanhar a quantidade de operações de negócio realizadas;
* Observar a duração de operações selecionadas;
* Identificar possíveis variações de desempenho;
* Facilitar a inspeção do comportamento da aplicação;
* Disponibilizar métricas para ferramentas compatíveis com Prometheus.

As métricas são registradas durante a execução da aplicação e podem ser consultadas pelos endpoints de monitoramento
disponibilizados.

### Validação em produção

Os endpoints de saúde, métricas e Prometheus foram verificados no ambiente publicado no Render, confirmando o
funcionamento da configuração de observabilidade.

---

## ⚠️ Tratamento de Erros e Validações

A API possui mecanismos de validação e tratamento de erros para fornecer respostas HTTP adequadas às situações
encontradas durante o processamento das requisições.

### Validação de dados

O **Bean Validation** é utilizado para validar dados de entrada, contribuindo para impedir o processamento de
informações inválidas.

As regras de negócio também são verificadas na camada Service, incluindo validações de propriedade dos recursos e
restrições específicas das operações.

### Tratamento de exceções

O tratamento de erros foi revisado para evitar que exceções específicas sejam mascaradas por um tratamento genérico,
dificultando a identificação da causa real dos problemas.

As respostas devem refletir a natureza do erro, distinguindo situações como:

* Dados de entrada inválidos;
* Recursos não encontrados;
* Operações incompatíveis com o estado atual de um recurso;
* Falhas de autenticação ou autorização;
* Violações de restrições de integridade do banco de dados;
* Erros inesperados durante o processamento.

---

## ▶️ Como Executar o Projeto

### Pré-requisitos

Antes de executar o projeto, tenha instalado:

* Java 21;
* Git;
* IntelliJ IDEA (opcional, para execução pela IDE).

O projeto utiliza o Maven Wrapper, incluído no repositório, para executar os comandos Maven sem exigir uma instalação
global do Maven.

Também é necessário ter acesso ao **Oracle Database** utilizado pela aplicação e configurar as credenciais de conexão.

### 1. Clonar o repositório

```bash
git clone https://github.com/Marixavq/challenge-java-petcenter.git
```

### 2. Entrar no diretório

```bash
cd challenge-java-petcenter
```

### 3. Abrir o projeto no IntelliJ IDEA

Abra o projeto como um projeto Maven e aguarde o carregamento e o download das dependências.

### 4. Configurar as variáveis de ambiente

A aplicação utiliza variáveis de ambiente para acessar o banco de dados Oracle e assinar os tokens JWT.

Configure as seguintes variáveis:

```text
ORACLE_USER=seu_usuario_oracle
ORACLE_PASSWORD=sua_senha_oracle
JWT_SECRET=sua_chave_secreta
```

Substitua os valores de exemplo pelos valores correspondentes ao seu ambiente. Não compartilhe nem publique credenciais
reais no repositório.

No IntelliJ IDEA, as variáveis podem ser configuradas em **Run > Edit Configurations**, na configuração de execução da
aplicação.

### 5. Executar a aplicação

Execute a classe principal:

```text
ChallengepetcenterApplication
```

Ou utilize o Maven Wrapper no terminal.

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

**Windows:**

```bat
mvnw.cmd spring-boot:run
```

Certifique-se de que as variáveis de ambiente estejam disponíveis no processo que executa a aplicação.

### 6. Acessar a aplicação

Após iniciar a aplicação, a API estará disponível localmente em:

```text
http://localhost:8080
```

A rota raiz responde com uma mensagem indicando que a API está funcionando.

---

## ☁️ Deploy

A aplicação está publicada na plataforma **Render**.

### URL da API

https://challenge-java-petcenter.onrender.com

### Swagger em produção

https://challenge-java-petcenter.onrender.com/swagger-ui/index.html

A documentação interativa permite visualizar e testar os endpoints da API diretamente pelo navegador.

A aplicação utiliza variáveis de ambiente configuradas diretamente no serviço do Render para gerenciar as credenciais do
banco Oracle e a chave JWT.

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

* Visualizar os endpoints disponíveis;
* Consultar parâmetros e respostas;
* Visualizar os modelos de Request e Response;
* Testar as operações da API;
* Informar o token JWT para acessar endpoints protegidos.

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

O Tutor pode solicitar atendimento veterinário para um de seus pets. A solicitação fica pendente até que o veterinário
decida aceitá-la ou recusá-la.

```text
Tutor
  ↓
Seleciona um pet
  ↓
Seleciona um veterinário
  ↓
Cria a solicitação
  ↓
Solicitação PENDENTE
  ↓
Veterinário analisa a solicitação
  ↓
       ┌───────────────┐
       ↓               ↓
    ACEITA           RECUSA
       ↓               ↓
Vínculo criado     Solicitação
entre pet e        RECUSADA
veterinário
```

Quando a solicitação é aceita, é criado o vínculo entre o pet e o veterinário. Quando é recusada, a solicitação passa
para o estado correspondente.

### Fluxo de acompanhamento do pet

```text
Tutor
  ↓
Pet
  ↓
Entrada no diário
  ↓
Registro de acompanhamento
```

Esse fluxo organiza as informações relacionadas à rotina do pet, permitindo manter um histórico de acompanhamento.

### Fluxo de alertas

```text
Veterinário
  ↓
Cria alerta para um pet
  ↓
Alerta ativo
  ↓
Desativação do alerta
  ↓
Alerta inativo
```

O alerta pode ser desativado conforme as permissões e regras de negócio implementadas na aplicação.

---

## 📋 Validações e Regras de Negócio

A aplicação possui validações destinadas a impedir operações inválidas e proteger a integridade dos dados, incluindo:

* Validação de campos obrigatórios;
* Limites de tamanho dos campos;
* Validação de e-mail único para usuários;
* Controle de acesso por perfil;
* Validação de ownership, verificando a propriedade dos recursos quando aplicável;
* Prevenção de acesso ou alteração indevida de recursos;
* Controle dos estados das solicitações veterinárias;
* Validação das operações relacionadas aos vínculos entre pets e veterinários;
* Tratamento de conflitos de integridade durante operações de persistência;
* Validação dos dados recebidos utilizando Bean Validation;
* Tratamento de erros para retornar respostas HTTP compatíveis com as situações encontradas.

---

## 🧪 Testes

A API pode ser testada utilizando:

* Swagger UI;
* Insomnia;
* Postman.

Os principais cenários de teste incluem:

* Cadastro de usuário;
* Login e geração de JWT;
* Acesso a endpoints protegidos sem autenticação;
* Acesso com perfil autorizado;
* Tentativas de acesso com perfil não autorizado;
* Cadastro e gerenciamento de pets;
* Criação de entradas no diário;
* Criação de registros de acompanhamento;
* Criação de solicitações veterinárias;
* Aceitação e recusa de solicitações;
* Criação de vínculos entre pets e veterinários;
* Criação e desativação de alertas;
* Validação de ownership;
* Paginação de consultas;
* Validação dos dados enviados;
* Verificação dos endpoints de saúde e observabilidade;
* Consulta das métricas da aplicação e exposição no formato Prometheus.

### Verificação da observabilidade

No ambiente local, os endpoints podem ser acessados por:

```text
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/prometheus
```

Em produção, substitua `http://localhost:8080` pela URL pública do serviço no Render.

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

## 🔗 Links do Projeto

| Recurso                      | Link                                                                |
|------------------------------|---------------------------------------------------------------------|
| **Repositório GitHub**       | https://github.com/Marixavq/challenge-java-petcenter                |
| **API em produção (Render)** | https://challenge-java-petcenter.onrender.com                       |
| **Swagger / OpenAPI**        | https://challenge-java-petcenter.onrender.com/swagger-ui/index.html |
| **Health Check**             | https://challenge-java-petcenter.onrender.com/actuator/health       |
| **Métricas da aplicação**    | https://challenge-java-petcenter.onrender.com/actuator/metrics      |
| **Métricas Prometheus**      | https://challenge-java-petcenter.onrender.com/actuator/prometheus   |

### Vídeo no Youtube


