package br.com.solarsystem.solar_system_nasa.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Planet {

    MERCURY(199, "Mercúrio"),
    VENUS(299, "Vênus"),
    EARTH(399, "Terra"),
    MARS(499, "Marte"),
    JUPITER(599, "Júpiter"),
    SATURN(699, "Saturno"),
    URANUS(799, "Urano"),
    NEPTUNE(899, "Netuno");

    private final int nasaId;
    private final String displayName;
}
