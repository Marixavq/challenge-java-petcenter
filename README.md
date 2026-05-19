# Sistema de Diário Clínico PetCenter 🐾

## 📌 Objetivo do Projeto
Este projeto está sendo desenvolvido por estudantes de Análise e Desenvolvimento de Sistemas da FIAP para o Challenge proposto pela Clyvo.
O objetivo do sistema fazer o acompanhamento do pet e identificar possíveis anomalias comportamentais, a fim de procurar tratamentos de saúde rápidamente antes que o problema se agrave.
Também tem o objetivo de transformar registros cotidianos do pet em informações organizadas e analisáveis ao longo do tempo.

A proposta vai além de um simples CRUD, permitindo:
- registro contínuo da rotina do pet;
- organização em linha do tempo;
- persistência estruturada dos dados;
- apoio ao acompanhamento veterinário;
- futura geração de insights clínicos.
---

## 🛠 Tecnologias Utilizadas
- Java 21
- Spring Boot
- Spring Data JPA
- Maven
- H2 Database (desenvolvimento)
- Oracle Database (modelagem oficial)
- Hibernate
- Bean Validation
- Swagger/OpenAPI
- Git & GitHub
---

## 📂 Estrutura do Projeto

O projeto foi estruturado utilizando arquitetura em camadas:
```text
Controller → Service → Repository → Banco de Dados
```
Estrutura em pastas:

    src/main/java
        com.fiap.challengepetcenter
            controller
            DTO
            exception
            model
            repository
            service
            ChallengepetcenterApplication.java
---

## 📌 Funcionalidades Implementadas
### Usuários
- Cadastro de usuários
- Busca de usuários
- Atualização de usuários
- Remoção de usuários
--
### Pets
- Cadastro de pets
- Relacionamento entre tutor e pet
- Busca de pets
- Atualização de pets
- Remoção de pets
--
### Diário de Entradas
- Registro diário da rotina do pet
- Organização temporal das informações
- Resumos e observações gerais
--
### Registros
- Registro estruturado de:
  - alimentação
  - comportamento
  - sintomas
  - atividades
---

## 🔗 Relacionamentos
```text
User
 └── Pet
       └── DiarioEntrada
              └── Registro
```
---

## 📌 Exemplos de Endpoints
### Usuários
```http
POST /api/users
```
```http
GET /api/users
```
### Pets
```http
POST /api/pets
```
```http
GET /api/pets
```

Para mais detalhes dos endpoints acesse: http://localhost:8080/swagger-ui/index.html com o projeto em execução.

---
## 🧪 Testes da API
Os testes dos endpoints foram realizados utilizando:
- Insomnia
As requisições exportadas serão disponibilizadas na pasta:
```text
/documentos
```
---

## ▶ Como Executar o Projeto
### 1. Clonar o repositório
```bash
git clone <https://github.com/Marixavq/challenge-java-petcenter>
```

### 2. Abrir no IntelliJ IDEA
Importar como projeto Maven.

### 3. Executar a aplicação
Rodar a classe principal:
```java
@SpringBootApplication
```
--
### 4. Acessar API
```text
http://localhost:8080
```
---

## 👨‍💻 Equipe
- [ArthurCPV](https://github.com/ArthurCPV)
- [JuliaTButtler](https://github.com/JuliaTButtler)
- [Marixavq](https://github.com/Marixavq)
- [TaikaWaititi](https://github.com/TaikaWaititi)
- [ZeDio](https://github.com/ZeDio)

Este projeto foi desenvolvido para a disciplina de Java Advanced, mas se conecta diretamente a outras disciplinas desenvolvidas por outros integrantes.

---
## 📄 Endereço do projeto
Link do projeto no GitHub: https://github.com/Marixavq/challenge-java-petcenter
