# 🍉 Watermelon API

> API for Watermelon Manager - A Complete Management System for Watermelon Producers

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

## 📋 Sobre o Projeto

A Watermelon API é um sistema de gerenciamento completo para produtores de melancia, oferecendo funcionalidades essenciais para administrar operações agrícolas de forma eficiente e organizada.

### 🎯 Principais Funcionalidades

- **👥 Gerenciamento de Usuários**: Sistema de autenticação com roles (Admin/User)
- **🏢 Multi-tenancy**: Suporte a múltiplas contas com isolamento de dados
- **👨‍🌾 Gestão de Clientes**: Cadastro e controle de clientes
- **🏭 Fornecedores**: Gerenciamento de vendors e parceiros
- **📦 Produtos**: Catálogo de produtos e insumos
- **🛒 Compras**: Sistema completo de compras com itens
- **📋 Pedidos**: Gerenciamento de orders vinculados a campos e clientes
- **🌾 Campos**: Controle de propriedades rurais com diferentes unidades de medida
- **💰 Transações**: Sistema financeiro com categorização de receitas e despesas
- **📊 Controle de Conta**: Balanço automático baseado em transações

## 🏗️ Arquitetura

### Stack Tecnológica

- **Java 21**: Linguagem principal
- **Spring Boot 3.5.4**: Framework base
- **Spring Security**: Autenticação e autorização
- **Spring Data JPA**: Persistência de dados
- **Spring Session**: Gerenciamento de sessões com Redis
- **PostgreSQL**: Banco de dados principal
- **Redis**: Cache e gerenciamento de sessões
- **MapStruct**: Mapeamento entre DTOs e entidades
- **Maven**: Gerenciamento de dependências
- **OpenAPI/Swagger**: Documentação da API

### Estrutura do Projeto

```
src/main/java/br/com/hadryan/api/
├── account/          # Gestão de contas multi-tenant
├── auth/             # Autenticação e autorização
├── customer/         # Gerenciamento de clientes
├── exception/        # Tratamento global de exceções
├── field/            # Gestão de campos/propriedades
├── order/            # Sistema de pedidos
├── product/          # Catálogo de produtos
├── purchase/         # Sistema de compras
├── transaction/      # Controle financeiro
├── user/             # Gerenciamento de usuários
└── vendor/           # Gestão de fornecedores
```

## 🚀 Como Executar

### Pré-requisitos

- Java 21
- Maven 3.6+
- Docker e Docker Compose

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/watermelon-api.git
cd watermelon-api
```

### 2. Execute os serviços de infraestrutura

```bash
docker-compose up -d
```

Isso iniciará:
- PostgreSQL na porta 5432
- Redis na porta 6379

### 3. Execute a aplicação

#### Usando Maven Wrapper:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Ou compile e execute:

```bash
./mvnw clean package
java -jar target/watermelon-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 4. Acesse a aplicação

- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 📝 Primeiros Passos

### 1. Registrar Usuário Administrador

```bash
curl -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@example.com",
    "password": "password123",
    "phone": "11999999999"
  }'
```

### 2. Fazer Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "password123"
  }'
```

### 3. Criar um Cliente

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=seu-session-id" \
  -d '{
    "name": "João Silva",
    "phone": "11987654321",
    "email": "joao@example.com",
    "location": "São Paulo, SP"
  }'
```

## 🔐 Autenticação

O sistema utiliza **autenticação baseada em sessão** com Redis:

- Sessões expiram em 30 minutos de inatividade
- Suporte a múltiplas roles: `ROLE_ADMIN`, `ROLE_USER`
- Isolamento de dados por conta (multi-tenancy)

### Endpoints Públicos
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/users/register` - Registro inicial (apenas admin)
- `GET /api/v1/auth/logout` - Logout

### Endpoints Protegidos
Todos os outros endpoints requerem autenticação válida.

## 📚 Documentação da API

### Principais Recursos

| Recurso | Endpoint Base | Descrição |
|---------|---------------|-----------|
| **Auth** | `/api/v1/auth` | Autenticação e sessões |
| **Users** | `/api/v1/users` | Gerenciamento de usuários |
| **Customers** | `/api/v1/customers` | Gestão de clientes |
| **Vendors** | `/api/v1/vendors` | Gestão de fornecedores |
| **Products** | `/api/v1/products` | Catálogo de produtos |
| **Purchases** | `/api/v1/purchases` | Sistema de compras |
| **Orders** | `/api/v1/orders` | Gerenciamento de pedidos |
| **Fields** | `/api/v1/fields` | Gestão de campos |
| **Transactions** | `/api/v1/transactions` | Controle financeiro |

### Exemplos de Uso

#### Criar um Campo

```bash
curl -X POST http://localhost:8080/api/v1/fields \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Campo Norte",
    "location": "Setor Norte da Fazenda",
    "size": 50,
    "measureUnit": "HECTARE"
  }'
```

#### Registrar uma Transação

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Venda de Melancia",
    "description": "Venda para mercado local",
    "type": "INCOME",
    "category": "ORDER_PAYMENT",
    "amount": 5000.00,
    "date": "2025-08-15"
  }'
```

## 🗃️ Banco de Dados

### Configuração

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres?currentSchema=app_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### Schema Principal

- **Account**: Conta principal (multi-tenancy)
- **User**: Usuários do sistema
- **Customer**: Clientes
- **Vendor**: Fornecedores
- **Product**: Produtos e insumos
- **Field**: Campos/propriedades
- **Order**: Pedidos
- **Purchase**: Compras
- **Item**: Itens de compra
- **Transaction**: Transações financeiras
## 👨‍💻 Autor

**Hadryan Silva**

⭐ Se este projeto te ajudou, considere dar uma estrela no repositório!