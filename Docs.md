# Documentação Técnica — orbital-live (Backend)

## Visão Geral

API REST e WebSocket que consulta a **NASA JPL Horizons** para obter as coordenadas eclípticas reais dos 8 planetas do sistema solar, armazena em cache no Redis e transmite em tempo real via WebSocket (STOMP).

---

## Stack

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.5 | Framework principal |
| Spring WebSocket | — | Broker STOMP em memória |
| Spring Data Redis | — | Cache das efemérides |
| Lombok | — | Redução de boilerplate |
| Maven | — | Build |

---

## Arquitetura

```
Client (HTTP/WS)
      │
      ▼
PlanetController          ← Endpoints REST
      │
      ▼
EphemerisService          ← Lógica de negócio / cache-aside
      ├── EphemerisCache  ← Redis (TTL 5 min, prefixo planet:position:)
      └── NasaHorizonsClient → API NASA JPL Horizons
              └── HorizonsResponseParser ← Extrai X, Y, Z do texto bruto

PositionUpdateScheduler   ← Dispara a cada N ms (padrão 30s)
      └── SolarSystemWebSocket → broadcast /topic/planets
```

O padrão usado é **cache-aside**: toda requisição consulta o Redis primeiro; só vai à NASA em caso de cache miss, salvando o resultado em seguida.

---

## Domínio

### `Planet` (enum)

Representa os 8 planetas com seus IDs na NASA:

| Constante | NASA ID | Nome exibido |
|---|---|---|
| MERCURY | 199 | Mercúrio |
| VENUS | 299 | Vênus |
| EARTH | 399 | Terra |
| MARS | 499 | Marte |
| JUPITER | 599 | Júpiter |
| SATURN | 699 | Saturno |
| URANUS | 799 | Urano |
| NEPTUNE | 899 | Netuno |

### `PlanetPosition` (DTO)

```java
Planet planet   // enum do planeta
double x        // coordenada X em km (eclíptico heliocêntrico)
double y        // coordenada Y em km
double z        // coordenada Z em km
Instant timestamp
```

---

## Endpoints REST

Base URL: `https://orbital-live-production.up.railway.app`

### `GET /api/v1/planets`

Retorna as posições de todos os planetas.

**Resposta `200 OK`:**
```json
[
  {
    "planet": "EARTH",
    "x": 147102329.179,
    "y": -29473882.541,
    "z": 142.318,
    "timestamp": "2026-06-02T18:10:15Z"
  }
]
```

### `GET /api/v1/planets/{name}`

Retorna a posição de um planeta específico.

**Parâmetro:** `name` — nome do planeta em inglês, case-insensitive (ex: `earth`, `MARS`).

**Resposta `200 OK`:** mesmo formato do item acima, objeto único.

**Resposta `500`:** caso o nome não corresponda a nenhum planeta do enum.

---

## WebSocket

**Endpoint de conexão:** `/ws` (SockJS habilitado)

**Tópico de dados:** `/topic/planets`

O servidor faz broadcast da lista completa de planetas a cada intervalo definido pelo scheduler. O payload é idêntico ao retorno do `GET /api/v1/planets`.

**Exemplo de conexão com STOMP.js:**
```js
const client = new Client({
  webSocketFactory: () => new SockJS('https://orbital-live-production.up.railway.app/ws'),
});
client.onConnect = () => {
  client.subscribe('/topic/planets', (message) => {
    const planets = JSON.parse(message.body);
  });
};
client.activate();
```

---

## Cache (Redis)

- **Chave:** `planet:position:{displayName}` (ex: `planet:position:Terra`)
- **TTL:** 5 minutos
- **Estratégia:** cache-aside — leitura tenta o cache, miss vai à NASA

---

## Scheduler

Classe `PositionUpdateScheduler` — executa `getAllPositions()` e faz broadcast via WebSocket no intervalo configurado.

O intervalo padrão é **30 segundos** e pode ser sobrescrito via variável de ambiente.

---

## Configuração

Todas as variáveis são passadas por variável de ambiente:

| Variável | Padrão | Descrição |
|---|---|---|
| `SPRING_DATA_REDIS_HOST` | `localhost` | Host do Redis |
| `REDIS_PORT` | `6379` | Porta do Redis |
| `REDIS_PASSWORD` | _(vazio)_ | Senha do Redis |
| `NASA_HORIZONS_URL` | _(obrigatório)_ | URL base da API Horizons |
| `NASA_SCHEDULER_INTERVAL` | `30000` | Intervalo do scheduler em ms |
| `SERVER_PORT` | `8080` | Porta HTTP da aplicação |

---

## Como rodar localmente

**Pré-requisitos:** Java 21, Maven, Docker (para o Redis).

```bash
# Sobe o Redis
docker run -d -p 6379:6379 redis

# Exporta as variáveis
export NASA_HORIZONS_URL=https://ssd.jpl.nasa.gov/api/horizons.api

# Roda a aplicação
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## Parser da NASA

O `HorizonsResponseParser` extrai as coordenadas da resposta em texto da NASA usando regex:

```
X\s*=\s*([+-]?[\d.E+-]+)\s+Y\s*=\s*([+-]?[\d.E+-]+)\s+Z\s*=\s*([+-]?[\d.E+-]+)
```

A extração acontece apenas no trecho delimitado pelos marcadores `$$SOE` e `$$EOE` da resposta Horizons.

---

## Frontend

O frontend que consome esta API está em:
https://github.com/Brunootavioliveira/orbital-live-front

Live: https://orbital-live-front.brunootavioliveira.workers.dev