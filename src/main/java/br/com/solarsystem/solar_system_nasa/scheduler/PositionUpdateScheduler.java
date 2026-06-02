package br.com.solarsystem.solar_system_nasa.scheduler;

import br.com.solarsystem.solar_system_nasa.domain.PlanetPosition;
import br.com.solarsystem.solar_system_nasa.service.EphemerisService;
import br.com.solarsystem.solar_system_nasa.websocket.SolarSystemWebSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PositionUpdateScheduler {

    private final EphemerisService ephemerisService;
    private final SolarSystemWebSocket solarSystemWebSocket;

    @Value("${nasa.scheduler.interval}")
    private Long internal;

    @Scheduled(fixedRateString = "${nasa.scheduler.internal}")
    public void updatePositions() {
        log.info("Scheduler disparado — buscando posições de todos os planetas");

        List<PlanetPosition> positions = ephemerisService.getAllPositions();

        solarSystemWebSocket.sendPositions(positions);

        log.info("Posições atualizadas com sucesso — {} planetas processados", positions.size());
    }
}
