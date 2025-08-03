# ERP Simplificado — Backend (Java + Spring Boot)

Projeto de estudo para desenvolvimento de um **Sistema ERP Simplificado**, com foco em gestão de clientes, produtos, estoque e pedidos.

Projeto FRONT do ERP — **[EM BREVE]**

## 🧑‍💻 Tecnologias Utilizadas
- Java 21+
- Spring Boot ( 3.5.4 )
- Spring Data JPA (Hibernate)
- Spring Security (JWT)
- PostgreSQL
- Lombok
- Mapstruct
- Liquibase (Migrations)
- Docker
- JUnit 5 + Mockito (Testes)
- Swagger / OpenAPI 3.0

## 🎯 Funcionalidades do Projeto
- Cadastro de Clientes
- Cadastro de Produtos
- Controle de Estoque
- Emissão de Pedidos/Vendas
- Relatórios Simples (PDF ou Excel)
- Autenticação e Autorização de Usuários (Role-based)

## 📋 Roadmap (Tasks)

## 📊 Progresso

🟩⬜⬜⬜⬜⬜⬜⬜⬜⬜ 16%

### 🏗️ Estrutura Inicial
- [x] Configurar projeto Spring Boot (Gradle)
- [x] Docker para base de dados teste 
- [x] Configurar conexão com banco de dados (application.yml e application-dev.yml)
- [x] Configurar Lombok e Liquibase
- [x] Swagger

### 🗄️ Módulo: Clientes
- [x] Criar base de dados Cliente
- [x] Criar entidade Cliente (Model)
- [x] Criar Repository Cliente (JpaRepository)
- [x]  Implementar camada de DTOs e Mappers (MapStruct) para isolamento do domínio.
- [x] Criar Service Cliente (CRUD)
- [x] Criar Controller Cliente (API REST)
- [x] Implementar validações (Bean Validation)
- [x] Testes unitários (Service/Repository)

###  🧑‍💼 Módulo: Fornecedores Módulo: Fornecedores
- [ ] Criar base de dados Fornecedor
- [ ] Criar entidade Fornecedor (Model)
- [ ] Criar Repository Fornecedor (JpaRepository)
- [ ] Implementar camada de DTOs e Mappers (MapStruct) para isolamento do domínio.
- [ ] Criar Service Fornecedor (CRUD)
- [ ] Criar Controller Fornecedor (API REST)
- [ ] Implementar validações (Bean Validation)
- [ ] Testes unitários (Service/Repository)

### 🏷️ Módulo: Produtos
- [ ] Criar base de dados Produto
- [ ] Criar entidade Produto (Model)
- [ ] Criar Repository Produto (JpaRepository)
- [ ] Implementar camada de DTOs e Mappers (MapStruct) para isolamento do domínio.
- [ ] Criar Service Produto (CRUD)
- [ ] Criar Controller Produto (API REST)
- [ ] Testes unitários (Service/Repository)

### 📦 Módulo: Estoque
- [ ] Ajuste base de dados para Produto com Estoque
- [ ] Relacionar Produto com Estoque (OneToOne)
- [ ] Criar entidade Estoque (Model)
- [ ] Criar Repository Estoque (JpaRepository)
- [ ] Implementar lógica de controle de estoque (entrada/saída)
- [ ] Criar Service Estoque (CRUD)
- [ ] Criar Controller Estoque (API REST)
- [ ] Implementar validações (Bean Validation)
- [ ] Testes unitários (Service/Repository)
- [ ] Criar operações de Entrada/Saída de Estoque
- [ ] API para consulta de saldo de estoque

### 🧾 Módulo: Pedidos/Vendas
- [ ] Ajuste base de dados para Pedidos/Vendas
- [ ] Criar entidade Pedido e ItemPedido
- [ ] Implementar lógica de geração de Pedido (relacionar Cliente + Itens)
- [ ] Atualizar Estoque ao concluir Pedido
- [ ] API de Listagem e Consulta de Pedidos

### 🔒 Segurança (Auth)
- [ ] Ajuste base de dados usuarios e login
- [ ] Configurar Spring Security (JWT)
- [ ] Criar roles de acesso (ADMIN, USER)
- [ ] API de login/logout

### 📊 Relatórios
- [ ] Gerar relatório simples em PDF (Ex: Vendas por Período)
- [ ] Exportar dados em Excel (opcional)

### 🧪 Testes
- [ ] Testes unitários (Service, Repository)
- [ ] Testes de Integração (Controller)

### 🚀 Deploy 
- [ ] Dockerizar aplicação 
- [ ] Deploy na minha VPS ( https://ailtonjm.com.br ) usando Actions do Github

---

## 🛡️ Boas Práticas de Desenvolvimento (Análise e Aplicação)
Após a implementação das funcionalidades básicas, será realizada uma etapa dedicada à análise crítica do projeto, focada em aplicar as melhores práticas de desenvolvimento Java & Spring Boot.

### Objetivos:
- Revisar a estrutura de pacotes (Domain-Driven Design Light)
- Garantir separação correta de camadas (Controller, Service, Repository, DTO, Mapper)
- Refatorar códigos duplicados ou mal estruturados
- Implementar princípios SOLID
- Utilizar padrões de projeto (ex: Factory, Strategy, Adapter se fizer sentido)
- Melhorar manutenibilidade e legibilidade do código
- Garantir boas práticas de nomenclatura
- Aplicar boas práticas no versionamento de banco de dados (Flyway)
- Implementar testes de qualidade (unitários/integrados)
- Melhorar tratamento de exceções (GlobalExceptionHandler)
- Configurar um ambiente de logs adequado (Logback/Slf4j)
- Preparar para deploy em ambiente de produção (perfis de ambiente)

### Revisão das Tarefas
- [ ] Revisar estrutura de pacotes seguindo uma abordagem orientada ao domínio
- [ ] Aplicar princípios SOLID nas classes Service e Controller
- [ ] Identificar e eliminar código duplicado
- [ ] Implementar padrões de projeto onde aplicável
- [ ] Refatorar DTOs para evitar exposição direta de entidades
- [ ] Criar um Handler Global de Exceções (ControllerAdvice)
- [ ] Configurar perfis de ambiente (dev, prod)
- [ ] Implementar logs de forma estruturada (Slf4j/Logback)
- [ ] Escrever testes de unidade/integrados para fluxos críticos
- [ ] Analisar possíveis otimizações de queries (JPA/Hibernate)
- [ ] Documentar a API com Swagger/OpenAPI 3.0 (anotado e com exemplos)


## Estrutura Completa de Pastas

```markdown
src/
 └─ main/
     ├─ java/
     │    └─ com/
     │         └─ projeto/erp/
     │              ├─ cliente/                 # Domínio Cliente
     │              │    ├─ dto/                  # DTOs do Cliente (request/response)
     │              │    │    ├─ ClienteRequestDTO.java
     │              │    │    └─ ClienteResponseDTO.java
     │              │    ├─ mapper/              # Mappers para Cliente
     │              │    │    └─ ClienteMapper.java
     │              │    ├─ Cliente.java         # Entidade Cliente
     │              │    ├─ ClienteRepository.java   # Interface do Repositório
     │              │    ├─ ClienteService.java  # Regras de negócio
     │              │    └─ ClienteController.java  # API REST
     │              │
     │              ├─ produto/                 # Domínio Produto
     │              │    ├─ dto/
     │              │    │    ├─ ProdutoRequestDTO.java
     │              │    │    └─ ProdutoResponseDTO.java
     │              │    ├─ mapper/
     │              │    │    └─ ProdutoMapper.java
     │              │    ├─ Produto.java
     │              │    ├─ ProdutoRepository.java
     │              │    ├─ ProdutoService.java
     │              │    └─ ProdutoController.java
     │              │
     │              ├─ pedido/                  # Domínio Pedido
     │              │    ├─ dto/
     │              │    │    ├─ PedidoRequestDTO.java
     │              │    │    └─ PedidoResponseDTO.java
     │              │    ├─ mapper/
     │              │    │    └─ PedidoMapper.java
     │              │    ├─ Pedido.java
     │              │    ├─ ItemPedido.java
     │              │    ├─ PedidoRepository.java
     │              │    ├─ PedidoService.java
     │              │    └─ PedidoController.java
     │              │
     │              ├─ infrastructure/          # Camada de infraestrutura
     │              │    ├─ persistence/        # Implementação concreta dos repositórios
     │              │    │    ├─ ClienteRepositoryImpl.java
     │              │    │    ├─ ProdutoRepositoryImpl.java
     │              │    │    └─ PedidoRepositoryImpl.java
     │              │    ├─ messaging/          # Integrações com filas, Kafka etc.
     │              │    ├─ external/           # APIs externas, clients REST, SOAP etc.
     │              │    └─ configuration/      # Datasource, cache, configs de infra
     │              │
     │              ├─ common/                  # Código genérico e transversal
     │              │    ├─ exception/          # Exceções customizadas e handlers
     │              │    │    ├─ BusinessException.java
     │              │    │    ├─ ResourceNotFoundException.java
     │              │    │    └─ GlobalExceptionHandler.java
     │              │    ├─ config/             # Configurações gerais (Swagger, CORS)
     │              │    │    └─ SwaggerConfig.java
     │              │    └─ util/               # Funções utilitárias
     │              │         └─ DateUtils.java
     │              │
     │              ├─ auth/                    # Autenticação e autorização
     │              │    ├─ AuthController.java
     │              │    ├─ AuthService.java
     │              │    ├─ JwtUtil.java
     │              │    └─ UserDetailsServiceImpl.java
     │              │
     │              └─ Application.java         # Classe principal Spring Boot
     │
     └─ resources/
          ├─ application.yml                   # Configurações da aplicação
          └─ db/
               └─ migrations/                  # Scripts de banco (Liquibase)
```
---

## Contato

Ailton José Martins — [https://ailtonjm.com.br](https://ailtonjm.com.br)
