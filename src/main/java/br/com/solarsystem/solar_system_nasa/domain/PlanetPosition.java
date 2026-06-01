package br.com.solarsystem.solar_system_nasa.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanetPosition {

    private Planet planet;
    private double x;
    private double y;
    private double z;
    private Instant timestamp;
}
