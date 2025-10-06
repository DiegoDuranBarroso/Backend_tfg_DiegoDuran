package es.unex.agromanager.backend.web;

import es.unex.agromanager.backend.dominio.SesionTrabajo;
import es.unex.agromanager.backend.servicio.ServicioSesionesTrabajo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sesiones")
public class ControladorSesionesTrabajo {

    private final ServicioSesionesTrabajo servicio;

    public ControladorSesionesTrabajo(ServicioSesionesTrabajo servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/entrada")
    public ResponseEntity<?> entrada(@RequestParam String usuario) {
        SesionTrabajo s = servicio.ficharEntrada(usuario);
        return ResponseEntity.ok(Map.of("estado", "ENTRADA", "entrada", s.getEntrada()));
    }

    @PostMapping("/salida")
    public ResponseEntity<?> salida(@RequestParam String usuario) {
        SesionTrabajo s = servicio.ficharSalida(usuario);
        return ResponseEntity.ok(Map.of(
                "estado", "SALIDA",
                "salida", s.getSalida(),
                "minutos", s.getMinutosTrabajados()
        ));
    }

    @GetMapping("/hoy")
    public ResponseEntity<?> hoy(@RequestParam String usuario) {
        int minutos = servicio.minutosHoy(usuario);
        return ResponseEntity.ok(Map.of("minutos", minutos));
    }

}
