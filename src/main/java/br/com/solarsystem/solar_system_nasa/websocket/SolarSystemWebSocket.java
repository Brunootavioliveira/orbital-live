package br.com.solarsystem.solar_system_nasa.websocket;

import br.com.solarsystem.solar_system_nasa.domain.PlanetPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolarSystemWebSocket {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendPositions(List<PlanetPosition> positions) {

        log.info("Enviando posições de {} planetas via WebSocket", positions.size());

        messagingTemplate.convertAndSend("/topic/planets", positions);

        log.info("Posições enviadas com sucesso via WebSocket");
    }
}
