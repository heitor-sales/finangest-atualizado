# Finangest - Controle Financeiro 💰

## 📋 Sobre o Projeto
O **Finangest** é um sistema web de gestão financeira pessoal construído em **Java com Spring Boot**. O objetivo principal é permitir que os usuários registrem e acompanhem suas transações (créditos e débitos) em diferentes tipos de contas (Contas-Correntes e Cartões de Crédito). 

Este projeto foi desenvolvido como parte de requisitos acadêmicos e **expandido para o mercado** com a implementação de uma **Arquitetura Híbrida**, oferecendo tanto a renderização de views no servidor quanto a exposição de dados via **API REST**.

## 🚀 Funcionalidades

O sistema possui regras de negócio completas para gestão financeira, divididas em perfis:

### 🛡️ Módulo do Administrador
- Cadastro, edição, bloqueio e ativação de usuários (Correntistas).
- Gerenciamento de Categorias de transações predefinidas e personalizadas, classificadas por natureza: `ENTRADA`, `SAÍDA` e `INVESTIMENTO`.

### 👤 Módulo do Correntista
- **Gestão de Contas:** Controle de múltiplas contas bancárias e cartões de crédito.
- **Transações e Extrato:** Registro de movimentações financeiras, filtragem por período e cálculo automático de saldos inicial e final.
- **Comentários:** Possibilidade de adicionar notas explicativas em cada transação vinculada.

## 🛠️ Tecnologias Utilizadas

**Back-end:**
- **Java**
- **Spring Boot** (Web, Data JPA)
- **BCrypt** (Hashing e segurança de senhas via `jbcrypt`)

**Front-end (MVC):**
- **Thymeleaf** (Template Engine)
- Utilização do padrão **Post-Redirect-Get (PRG)** para melhor experiência do usuário e prevenção de duplo envio de formulários.

**Banco de Dados:**
- Mapeamento Objeto-Relacional (ORM) com **Hibernate**.
- **Database Seeder** na inicialização para popular o banco de dados com categorias predefinidas e usuário Admin.

## 🛸 API REST (Integração)

O sistema conta com endpoints RESTful para fácil integração com front-ends SPA (como React, Angular ou Vue.js). Todas as rotas da API estão sob o prefixo `/api`.

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/categorias` | Retorna a lista de todas as categorias ativas (JSON) |
| `POST` | `/api/categorias` | Cadastra uma nova categoria |
| `PUT` | `/api/categorias/{id}` | Atualiza informações de uma categoria específica |
| `DELETE` | `/api/categorias/{id}` | Exclui/Desativa uma categoria do sistema |

## ⚙️ Como Executar Localmente

1. Acesse a pasta do projeto e execute com o Maven:
   `./mvnw spring-boot:run`
2. A aplicação estará disponível em `http://localhost:8081`.
3. **Acesso Admin padrão:**
   - **Login:** *Definido na classe DatabaseSeeder*
   - **Senha:** *Definida na classe DatabaseSeeder*

---
*Desenvolvido como projeto prático e portfólio.*
