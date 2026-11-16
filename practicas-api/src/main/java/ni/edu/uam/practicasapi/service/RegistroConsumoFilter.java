package ni.edu.uam.practicasapi.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import ni.edu.uam.practicasapi.model.ConsumoApi;
import ni.edu.uam.practicasapi.repository.ConsumoApiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Registra llamadas a /api sin incluir parámetros, credenciales ni contenido personal. */
@Component
public class RegistroConsumoFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(RegistroConsumoFilter.class);
    private final ConsumoApiRepository repositorio;

    public RegistroConsumoFilter(ConsumoApiRepository repositorio) { this.repositorio = repositorio; }

    public static String normalizarRuta(String ruta) {
        if (ruta == null || !ruta.startsWith("/api/")) return "/api/otros";
        String sinIds = ruta.replaceAll("/[0-9]+(?=/|$)", "/{id}");
        return sinIds.length() <= 200 ? sinIds : sinIds.substring(0, 200);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long inicio = System.nanoTime();
        int estado = HttpServletResponse.SC_OK;
        try {
            chain.doFilter(request, response);
            estado = response.getStatus();
        } catch (ServletException | IOException | RuntimeException ex) {
            estado = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            throw ex;
        } finally {
            long duracion = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicio);
            try {
                repositorio.save(new ConsumoApi(request.getMethod(),
                    normalizarRuta(request.getRequestURI()), estado, duracion));
            } catch (RuntimeException ex) {
                // Una falla del almacenamiento de métricas no interrumpe una consulta funcional.
                LOG.warn("No se pudo registrar el consumo de API", ex);
            }
        }
    }
}
