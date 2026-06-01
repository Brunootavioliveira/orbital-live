package br.com.solarsystem.solar_system_nasa.client;

import br.com.solarsystem.solar_system_nasa.domain.Planet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class NasaHorizonsClient {

    private final RestClient restClient;

    public NasaHorizonsClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public String fetchRawPosition(Planet planet) {
        String url = buildUrl(planet);
        log.info("Buscando posição do planeta {} da NASA", planet.getDisplayName());

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }

    private String buildUrl(Planet planet) {
        return "?format=json"
                + "&COMMAND=" + planet.getNasaId()
                + "&OBJ_DATA=NO"
                + "&MAKE_EPHEM=YES"
                + "&EPHEM_TYPE=VECTORS"
                + "&CENTER=500@10"
                + "&START_TIME=2024-01-01"
                + "&STOP_TIME=2024-01-02"      
                + "&STEP_SIZE=1d";
    }
}
