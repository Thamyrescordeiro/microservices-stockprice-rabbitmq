# Microserviços Stock Price com RabbitMQ

Um projeto de arquitetura de microserviços assíncrona que demonstra a comunicação entre múltiplos serviços através do RabbitMQ. O projeto implementa um sistema de gerenciamento de preços e estoque de produtos com consumidores em Java e Node.js.

## 📋 Visão Geral

Este projeto implementa uma arquitetura de microserviços desacoplada onde:
- Um serviço produtor (pricestock) publica mensagens sobre preços e estoques
- Múltiplos consumidores (stockconsumer em Java, priceconsumer em Node.js) consomem essas mensagens
- A comunicação é realizada de forma assíncrona através do RabbitMQ

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────┐
│       PriceStock (Spring Boot)          │
│   - REST API para Stock (PUT)           │
│   - REST API para Price (PUT)           │
│   - Publisher de mensagens              │
└────────────────┬──────────────────────┘
                 │
                 │ Envia mensagens
                 │
        ┌────────▼────────┐
        │   RabbitMQ      │
        │ ┌──────────────┐│
        │ │ STOCK Queue  ││
        │ │ PRICE Queue  ││
        │ └──────────────┘│
        └────────┬────────┘
                 │
        ┌────────┴─────────┐
        │                  │
┌───────▼──────────┐  ┌───▼────────────────┐
│ StockConsumer    │  │ PriceConsumer      │
│ (Spring Boot)    │  │ (Node.js)          │
│ - Consome STOCK  │  │ - Consome PRICE    │
│ - Processa dados │  │ - Processa dados   │
└──────────────────┘  └────────────────────┘
```

## 📦 Componentes

### 1. **pricestock** - Produtor de Mensagens
- **Tecnologia**: Spring Boot 3.5.6 (Java 17)
- **Responsabilidade**: Publica mensagens de preço e estoque
- **Endpoints**:
  - `PUT /stock` - Atualiza informações de estoque
  - `PUT /price` - Atualiza informações de preço
- **Filas**: Cria e configura as filas STOCK e PRICE

### 2. **stockconsumer** - Consumidor de Estoque
- **Tecnologia**: Spring Boot 3.5.6 (Java 17)
- **Responsabilidade**: Consome mensagens da fila STOCK
- **Comportamento**: Processa e exibe dados de estoque recebidos

### 3. **priceconsumer** - Consumidor de Preços
- **Tecnologia**: Node.js com TypeScript
- **Responsabilidade**: Consome mensagens da fila PRICE
- **Comportamento**: Processa e exibe dados de preço recebidos

### 4. **librabbitmq** - Biblioteca Compartilhada
- **Tipo**: Biblioteca Maven
- **Conteúdo**:
  - DTOs (Data Transfer Objects):
    - `StockDto`: Contém `productcode` (String) e `quantity` (int)
    - `PriceDto`: Contém `productcode` (String) e `price` (double)
  - `RabbitMqConstants`: Define nomes das filas
    - `STOCK_QUEUE = "STOCK"`
    - `STOCK_PRICE = "PRICE"`

## 🚀 Como Iniciar

### Pré-requisitos

- Java 17+
- Maven 3.6+
- Node.js 16+ (para priceconsumer)
- RabbitMQ instalado e rodando na máquina local

### Instalação e Execução

#### 1. Iniciar RabbitMQ

```bash
# Windows
rabbitmq-server

# Linux/Mac
brew services start rabbitmq
```

Acesse a interface de gerenciamento: `http://localhost:15672` (padrão: guest/guest)

#### 2. Construir a Biblioteca Compartilhada

```bash
cd librabbitmq
mvn clean install
```

#### 3. Iniciar PriceStock (Spring Boot)

```bash
cd pricestock
mvn clean install
mvn spring-boot:run
```

O serviço estará disponível em: `http://localhost:8080`

#### 4. Iniciar StockConsumer

```bash
cd stockconsumer
mvn clean install
mvn spring-boot:run
```

#### 5. Iniciar PriceConsumer (Node.js)

```bash
cd priceconsumer
npm install
node Consumer.js
```

## 📡 Testando o Sistema

### Atualizar Estoque

```bash
curl -X PUT http://localhost:8080/stock \
  -H "Content-Type: application/json" \
  -d '{
    "productcode": "PROD001",
    "quantity": 100
  }'
```

**Resultado esperado no StockConsumer**:
```
PROD001
100
-----------------------------------------------------------
```

### Atualizar Preço

```bash
curl -X PUT http://localhost:8080/price \
  -H "Content-Type: application/json" \
  -d '{
    "productcode": "PROD001",
    "price": 99.99
  }'
```

**Resultado esperado no PriceConsumer**:
```
{"productcode":"PROD001","price":99.99}
```

## 🔄 Fluxo de Comunicação

1. **Cliente envia requisição** para um dos endpoints REST (stock ou price)
2. **PriceStock valida e processa** a requisição
3. **PriceStock serializa os dados** em JSON usando ObjectMapper
4. **RabbitMqService envia a mensagem** para a fila apropriada (STOCK ou PRICE)
5. **RabbitMQ roteia a mensagem** através do DirectExchange
6. **Consumidores recebem a mensagem** através de listeners anotados com @RabbitListener
7. **Consumidores processam os dados** (no caso do projeto, apenas imprimem no console)

## 📋 Filas e Bindings

### Configuração no RabbitMQ

- **Exchange**: `amq.direct` (DirectExchange)
- **Filas**:
  - `STOCK` - Routing key: `STOCK`
  - `PRICE` - Routing key: `PRICE`

Cada fila é vinculada ao exchange com sua própria routing key, permitindo que mensagens sejam roteadas corretamente com base no tipo.

## 🛠️ Estrutura de Pastas

```
microservices-stockprice-rabbitmq/
├── librabbitmq/                    # Biblioteca compartilhada
│   ├── src/main/java/
│   │   ├── dto/
│   │   │   ├── StockDto.java
│   │   │   └── PriceDto.java
│   │   └── constants/
│   │       └── RabbitMqConstants.java
│   └── pom.xml
│
├── pricestock/                     # Produtor (Spring Boot)
│   ├── src/main/java/com/microservice/pricestock/
│   │   ├── controller/
│   │   │   ├── StockController.java
│   │   │   └── PriceController.java
│   │   ├── service/
│   │   │   └── RabbitMqService.java
│   │   ├── conections/
│   │   │   └── RabbitMqConection.java
│   │   └── PricestockApplication.java
│   └── pom.xml
│
├── stockconsumer/                  # Consumidor de Estoque (Spring Boot)
│   ├── src/main/java/com/microservice/stockconsumer/
│   │   ├── consumer/
│   │   │   └── StockConsumer.java
│   │   └── StockconsumerApplication.java
│   └── pom.xml
│
├── priceconsumer/                  # Consumidor de Preços (Node.js)
│   ├── Consumer.js
│   ├── package.json
│   └── node_modules/
│
└── README.md
```

## 💻 Dependências Principais

### Java (Spring Boot)
- `spring-boot-starter-amqp`: Suporte AMQP e RabbitMQ
- `spring-boot-starter-web`: Suporte REST e Web
- `librabbitmq`: Biblioteca interna com DTOs e constantes

### Node.js
- `amqplib`: Cliente AMQP para Node.js
- `typescript`: Suporte TypeScript
- `ts-node`: Execução de TypeScript

## 🔐 Configuração RabbitMQ

**Credenciais padrão**:
- Usuário: `guest`
- Senha: `guest`
- Host: `localhost`
- Porta: `5672`

Para produção, altere as credenciais no código:
- Java: `RabbitMqConection.java`
- Node.js: `Consumer.js`

## 📝 Detalhes de Implementação

### RabbitMqService (pricestock)
- Serializa objetos para JSON usando Jackson
- Envia mensagens através de RabbitTemplate
- Suporta tratamento de exceções

### RabbitMqConection (pricestock)
- Cria filas STOCK e PRICE ao inicializar
- Configura bindings com DirectExchange
- Executa durante a inicialização (@PostConstruct)

### StockConsumer (stockconsumer)
- Listener anotado com @RabbitListener
- Consome mensagens da fila STOCK
- Processa e exibe dados

### PriceConsumer (priceconsumer)
- Conecta ao RabbitMQ via AMQP
- Consome mensagens da fila PRICE
- Exibe conteúdo das mensagens

## 🐛 Troubleshooting

### RabbitMQ não está rodando
```
Erro: connect ECONNREFUSED 127.0.0.1:5672
```
**Solução**: Inicie o RabbitMQ e verifique se está na porta 5672

### Erro de conexão nos microserviços
```
Erro: Connection refused / Connection timeout
```
**Solução**: Verifique se RabbitMQ está acessível e as credenciais estão corretas

### Mensagens não são recebidas
**Solução**:
1. Verifique se as filas existem no RabbitMQ
2. Confirme que os consumers estão rodando
3. Verifique os logs dos serviços

## 📚 Referências

- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [Spring AMQP](https://spring.io/projects/spring-amqp)
- [amqplib (Node.js)](https://github.com/amqp-node/amqplib)
- [Spring Boot](https://spring.io/projects/spring-boot)

## 👨‍💻 Autor

Desenvolvido como exemplo de arquitetura de microserviços com comunicação assíncrona.

## 📄 Licença

ISC

---

**Última atualização**: 2026-07-15
