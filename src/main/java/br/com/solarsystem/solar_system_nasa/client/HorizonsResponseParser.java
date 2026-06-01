package br.com.solarsystem.solar_system_nasa.client;

import br.com.solarsystem.solar_system_nasa.domain.Planet;
import br.com.solarsystem.solar_system_nasa.domain.PlanetPosition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class HorizonsResponseParser {

    private static final Pattern XYZ_PATTERN = Pattern.compile(
            "X\\s*=\\s*([+-]?[\\d.E+-]+)\\s+Y\\s*=\\s*([+-]?[\\d.E+-]+)\\s+Z\\s*=\\s*([+-]?[\\d.E+-]+)"
    );

    public PlanetPosition parse(String rawResponse, Planet planet) {
        String dataSection = extractDataSection(rawResponse);
        Matcher matcher = XYZ_PATTERN.matcher(dataSection);

        if (!matcher.find()) {
            log.error("Não foi possível parsear posição do planeta {}", planet.getDisplayName()); //
            throw new RuntimeException("Falha ao parsear resposta da NASA para " + planet.getDisplayName());
        }

        double x = Double.parseDouble(matcher.group(1));
        double y = Double.parseDouble(matcher.group(2));
        double z = Double.parseDouble(matcher.group(3));

        log.info("Posição parseada para {}: X={}, Y={}, Z={}", planet.getDisplayName(), x, y, z);

        return new PlanetPosition(planet, x, y, z, Instant.now());
    }

    private String extractDataSection(String rawResponse) {
        int start = rawResponse.indexOf("$$SOE");
        int end = rawResponse.indexOf("$$EOE");

        if (start == -1 || end == -1) {
            throw new RuntimeException("Formato inesperado na resposta da NASA — marcadores $$SOE/$$EOE não encontrados");
        }

        return rawResponse.substring(start, end);
    }
}
