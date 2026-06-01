package br.com.solarsystem.solar_system_nasa.service;

import br.com.solarsystem.solar_system_nasa.cache.EphemerisCache;
import br.com.solarsystem.solar_system_nasa.client.HorizonsResponseParser;
import br.com.solarsystem.solar_system_nasa.client.NasaHorizonsClient;
import br.com.solarsystem.solar_system_nasa.domain.Planet;
import br.com.solarsystem.solar_system_nasa.domain.PlanetPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EphemerisService {

    private final NasaHorizonsClient nasaClient;
    private final HorizonsResponseParser parser;
    private final EphemerisCache cache;



    public PlanetPosition getPosition(Planet planet){
        PlanetPosition cached = cache.find(planet);

        if (cached != null) {
            log.info("Retornando {} do cache", planet.getDisplayName());
            return cached;
        }

        log.info("Cache miss — buscando {} na NASA", planet.getDisplayName());
        String rawResponse = nasaClient.fetchRawPosition(planet);
        PlanetPosition position = parser.parse(rawResponse, planet);
        cache.save(position);

        return position;
    }

    public List<PlanetPosition> getAllPositions() {
        return Arrays.stream(Planet.values())
                .map(this::getPosition)
                .collect(Collectors.toList());
    }
}
