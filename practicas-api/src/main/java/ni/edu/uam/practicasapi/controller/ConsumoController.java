package ni.edu.uam.practicasapi.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import ni.edu.uam.practicasapi.repository.ConsumoApiRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Reporte de solicitudes, códigos HTTP y latencias para los últimos N días. */
@RestController
@Validated
@RequestMapping("/api/reportes")
public class ConsumoController {
    private final ConsumoApiRepository repositorio;
    public ConsumoController(ConsumoApiRepository repositorio) { this.repositorio = repositorio; }

    public record EndpointDto(String metodo, String ruta, long solicitudes,
                              long exitosas, long errores, BigDecimal promedioMs) {}
    public record ResumenDto(int dias, Instant desde, long solicitudes,
                             long exitosas, long errores, List<EndpointDto> endpoints) {}

    @GetMapping("/consumo")
    public ResumenDto consumo(@RequestParam(defaultValue = "7") @Min(1) @Max(365) int dias) {
        Instant desde = Instant.now().minus(dias, ChronoUnit.DAYS);
        List<EndpointDto> endpoints = repositorio.resumirDesde(desde).stream()
            .map(r -> new EndpointDto((String) r[0], (String) r[1],
                ((Number) r[2]).longValue(), ((Number) r[3]).longValue(),
                ((Number) r[4]).longValue(), (new BigDecimal(r[5].toString()))))
            .toList();
        long total = endpoints.stream().mapToLong(EndpointDto::solicitudes).sum();
        long exitosas = endpoints.stream().mapToLong(EndpointDto::exitosas).sum();
        return new ResumenDto(dias, desde, total, exitosas, total - exitosas, endpoints);
    }
}
