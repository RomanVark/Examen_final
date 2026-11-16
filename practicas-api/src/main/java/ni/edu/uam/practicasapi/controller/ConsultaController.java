package ni.edu.uam.practicasapi.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import ni.edu.uam.practicasapi.model.EmpresaEntity;
import ni.edu.uam.practicasapi.model.EstudianteEntity;
import ni.edu.uam.practicasapi.model.PracticaEntity;
import ni.edu.uam.practicasapi.repository.EmpresaRepository;
import ni.edu.uam.practicasapi.repository.EstudianteRepository;
import ni.edu.uam.practicasapi.repository.PracticaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Primeros endpoints solo de lectura, sin duplicar las reglas CRUD de JavaFX. */
@RestController
@Validated
@RequestMapping("/api")
public class ConsultaController {
    private final EstudianteRepository estudiantes;
    private final EmpresaRepository empresas;
    private final PracticaRepository practicas;

    public ConsultaController(EstudianteRepository estudiantes, EmpresaRepository empresas,
                              PracticaRepository practicas) {
        this.estudiantes = estudiantes;
        this.empresas = empresas;
        this.practicas = practicas;
    }

    public record EstudianteDto(Long id, String carnet, String nombres,
                                String apellidos, Long carreraId, Boolean activo) {
        static EstudianteDto de(EstudianteEntity e) {
            return new EstudianteDto(e.getId(), e.getCarnet(), e.getNombres(),
                e.getApellidos(), e.getCarreraId(), e.getActivo());
        }
    }
    public record EmpresaDto(Long id, String razonSocial, String sector, Boolean activa) {
        static EmpresaDto de(EmpresaEntity e) {
            return new EmpresaDto(e.getId(), e.getRazonSocial(), e.getSector(), e.getActiva());
        }
    }
    public record PracticaDto(Long id, Long estudianteId, Long ofertaId, String estado,
                              LocalDate fechaInicio, LocalDate fechaFin, Integer horasRequeridas) {
        static PracticaDto de(PracticaEntity p) {
            return new PracticaDto(p.getId(), p.getEstudianteId(), p.getOfertaId(), p.getEstado(),
                p.getFechaInicio(), p.getFechaFin(), p.getHorasRequeridas());
        }
    }

    @GetMapping("/estudiantes")
    public List<EstudianteDto> estudiantes(@RequestParam(defaultValue = "0") @Min(0) int pagina,
                                           @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamano) {
        return estudiantes.findAll(PageRequest.of(pagina, tamano)).map(EstudianteDto::de).getContent();
    }

    @GetMapping("/estudiantes/{id}")
    public EstudianteDto estudiante(@PathVariable @Min(1) long id) {
        return estudiantes.findById(id).map(EstudianteDto::de)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
    }

    @GetMapping("/empresas")
    public List<EmpresaDto> empresas(@RequestParam(defaultValue = "0") @Min(0) int pagina,
                                     @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamano) {
        return empresas.findAll(PageRequest.of(pagina, tamano)).map(EmpresaDto::de).getContent();
    }

    @GetMapping("/practicas")
    public List<PracticaDto> practicas(@RequestParam(defaultValue = "0") @Min(0) int pagina,
                                       @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamano) {
        return practicas.findAll(PageRequest.of(pagina, tamano)).map(PracticaDto::de).getContent();
    }
}
