package br.com.solarsystem.solar_system_nasa.controller;

import br.com.solarsystem.solar_system_nasa.domain.Planet;
import br.com.solarsystem.solar_system_nasa.domain.PlanetPosition;
import br.com.solarsystem.solar_system_nasa.service.EphemerisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final EphemerisService ephemerisService;

    @GetMapping
    public ResponseEntity<List<PlanetPosition>> getAllPositions() {
        log.info("Requisição recebida — buscando posições de todos os planetas");
        List<PlanetPosition> positions = ephemerisService.getAllPositions();
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/{planetName}")
    public ResponseEntity<PlanetPosition> getPosition(@PathVariable String planetName) {
        log.info("Requisição recebida — buscando posição de {}", planetName);

        Planet planet = Planet.valueOf(planetName.toUpperCase());

        PlanetPosition position = ephemerisService.getPosition(planet);

        return ResponseEntity.ok(position);
    }
}
