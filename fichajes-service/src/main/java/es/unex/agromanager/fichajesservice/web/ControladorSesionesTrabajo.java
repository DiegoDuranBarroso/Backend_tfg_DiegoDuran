// fichajes-service/src/main/java/.../web/ControladorSesionesTrabajo.java
package es.unex.agromanager.fichajesservice.web;

import es.unex.agromanager.fichajesservice.dominio.SesionTrabajo;
import es.unex.agromanager.fichajesservice.servicio.ServicioSesionesTrabajo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sesiones")
public class ControladorSesionesTrabajo {

    private final ServicioSesionesTrabajo servicio;

    public ControladorSesionesTrabajo(ServicioSesionesTrabajo servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/entrada")
    public ResponseEntity<?> entrada(@RequestParam String usuario,
                                     @AuthenticationPrincipal Jwt jwt) {
        servicio.ficharEntrada(usuario, jwt.getTokenValue()); // <-- ahora 2 args
        return ResponseEntity.ok(Map.of("estado", "ENTRADA"));
    }

    @PostMapping("/salida")
    public ResponseEntity<?> salida(@RequestParam String usuario,
                                    @AuthenticationPrincipal Jwt jwt) {
        var s = servicio.ficharSalida(usuario, jwt.getTokenValue());
        return ResponseEntity.ok(Map.of(
                "estado", "SALIDA",
                "salida", s.getSalida(),
                "minutos", s.getMinutosTrabajados()
        ));
    }

    @GetMapping("/hoy")
    public ResponseEntity<?> hoy(@RequestParam String usuario,
                                 @AuthenticationPrincipal Jwt jwt) {
        int minutos = servicio.minutosHoy(usuario, jwt.getTokenValue());
        return ResponseEntity.ok(Map.of("minutos", minutos));
    }

    @GetMapping("/listar")
    public List<SesionTrabajo> listar(@RequestParam String usuario,
                                      @AuthenticationPrincipal Jwt jwt) {
        return servicio.listar(usuario, jwt.getTokenValue());
    }
}
