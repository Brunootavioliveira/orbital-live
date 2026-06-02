# orbital-live

API REST e WebSocket que fornece as posições reais dos planetas do sistema solar em tempo real, consultando a NASA JPL Horizons.

**API:** https://orbital-live-production.up.railway.app

---

## Tecnologias

- Java 21
- Spring Boot 3.5
- Spring WebSocket (STOMP)
- Redis (cache das efemérides)
- Lombok
- Hospedado no Railway

---

## Funcionalidades

- Consulta automática à API NASA JPL Horizons para os 8 planetas
- Cache das posições no Redis com atualização periódica via Scheduler
- Exposição dos dados via REST e broadcast em tempo real via WebSocket
- CORS configurado para aceitar requisições do frontend

---

## Endpoints REST

| Método | Rota                        | Descrição                          |
|--------|-----------------------------|------------------------------------|
| GET    | `/api/v1/planets`           | Retorna posições de todos os planetas |
| GET    | `/api/v1/planets/{name}`    | Retorna posição de um planeta específico |

**Exemplo de resposta:**

```json
{
  "planet": "EARTH",
  "x": 147102329.179,
  "y": -29473882.541,
  "z": 142.318,
  "timestamp": "2026-06-02T18:10:15Z"
}
```

Os valores de `x`, `y`, `z` estão em **km** no referencial eclíptico heliocêntrico.

---

## WebSocket

Conexão via STOMP em `/ws`. O servidor faz broadcast no tópico `/topic/planets` a cada atualização do scheduler.

---

## Estrutura

```
src/main/java/br/com/solarsystem/solar_system_nasa/
├── controller/
│   └── PlanetController.java       # Endpoints REST
├── service/
│   └── EphemerisService.java       # Lógica de negócio
├── client/
│   ├── NasaHorizonsClient.java     # Chamadas à API da NASA
│   └── HorizonsResponseParser.java # Parser da resposta
├── cache/
│   └── EphemerisCache.java         # Operações de cache Redis
├── scheduler/
│   └── PositionUpdateScheduler.java # Atualização periódica
├── websocket/
│   └── SolarSystemWebSocket.java   # Broadcast STOMP
├── config/
│   ├── WebSocketConfig.java
│   ├── RedisConfig.java
│   ├── CorsConfig.java
│   └── RestClientConfig.java
└── domain/
    ├── Planet.java                  # Enum dos planetas
    └── PlanetPosition.java          # DTO de posição
```

---

## Configuração

Variáveis de ambiente necessárias:

| Variável         | Descrição                  |
|------------------|----------------------------|
| `REDIS_URL`      | URL de conexão com o Redis |
| `SERVER_PORT`    | Porta da aplicação (padrão 8080) |

---

## Execução local

**Pré-requisitos:** Java 21, Maven, Redis rodando localmente.

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

---

## Frontend

O frontend que consome esta API está em:
https://github.com/Brunootavioliveira/orbital-live-front