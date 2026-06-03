package br.com.solarsystem.solar_system_nasa.cache;

import br.com.solarsystem.solar_system_nasa.domain.Planet;
import br.com.solarsystem.solar_system_nasa.domain.PlanetPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Slf4j
@RequiredArgsConstructor
@Component
public class EphemerisCache {

    private static final String KEY_PREFIX = "planet:position:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, PlanetPosition> redisTemplate;

    public void save(PlanetPosition position) {
        String key = buildKey(position.getPlanet());
        redisTemplate.opsForValue().set(key, position, TTL);
        log.info("Posição de {} salva no cache", position.getPlanet().getDisplayName());
    }

    public PlanetPosition find(Planet planet) {
        String key = buildKey(planet);
        PlanetPosition position = (PlanetPosition) redisTemplate.opsForValue().get(key);

        if (position == null) {
            log.info("Cache miss para {}", planet.getDisplayName());
            return null;
        }

        log.info("Cache hit para {}", planet.getDisplayName());
        return position;
    }

    public boolean exists(Planet planet) {
        String key = buildKey(planet);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public String buildKey(Planet planet){
        return KEY_PREFIX + planet.name();
    }
}
