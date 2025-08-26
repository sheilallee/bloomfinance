# 💜 BloomFinance

BloomFinance é um sistema web de **controle financeiro**, desenvolvido como projeto da disciplina **Programação para a Web II - IFPB**.  
O objetivo é oferecer uma plataforma para **correntistas e administradores** gerenciarem contas, transações, categorias e orçamentos anuais, com interface moderna e validações seguras.

---

## 🚀 Tecnologias Utilizadas
- **Java 21**
- **Spring Boot 3.5+ (MVC, JPA, Security)**
- **Thymeleaf** para templates
- **Bootstrap 5** para layout responsivo
- **PostgreSQL** como banco de dados
- **Maven** para build e gerenciamento de dependências
- **Git/GitHub** para versionamento

---

## 👥 Perfis de Usuário

### 🔹 Correntista
- Criar contas (corrente e cartão de crédito)  
- Registrar, editar e excluir transações  
- Adicionar, editar e excluir comentários em transações  
- Visualizar extrato da conta com filtros por período  
- Consultar orçamento anual em forma de tabela  

### 🔹 Administrador
- Todas as funcionalidades de correntista  
- Cadastrar, editar e desativar categorias  
- Cadastrar, listar e excluir correntistas  
- Bloquear correntistas  
- Excluir contas (com exclusão em cascata das transações)  

---

## 📊 Funcionalidades (Casos de Uso)

- UC01: Correntista cadastra conta  
- UC03: Correntista cria transação para conta  
- UC05: Correntista adiciona comentário à transação  
- UC07: Correntista consulta extrato da conta  
- UC08: Correntista consulta orçamento anual  
- UC21: Administrador cadastra correntista  
- UC23: Administrador cadastra/modifica categorias  
- UC24: Administrador bloqueia correntista  

---

## 🔐 Requisitos Não Funcionais
- Spring Boot / MVC  
- Thymeleaf com fragments  
- Bootstrap responsivo  
- Banco relacional (PostgreSQL)  
- Validações e mensagens de erro nos formulários  
- Padrão **Post-Redirect-Get (PRG)**  
- Paginação refletida no banco  
- Autenticação e autorização com **Spring Security 6**  

---

## 🛠️ Como Executar o Projeto

```bash
# Clone o repositório
git clone https://github.com/sheilallee/bloomfinance.git

# Entre na pasta do projeto
cd bloomfinance

# Configure o banco PostgreSQL
CREATE DATABASE bloomfinance;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE bloomfinance TO postgres;

# Execute a aplicação
mvn spring-boot:run
