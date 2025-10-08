package es.unex.agromanager.empleadosservice.web;

import es.unex.agromanager.empleadosservice.dominio.Empleado;
import es.unex.agromanager.empleadosservice.servicio.EmpleadoServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoServicio servicio;
    public EmpleadoController(EmpleadoServicio servicio) { this.servicio = servicio; }

    @GetMapping public List<Empleado> listar() { return servicio.listar(); }
    @PostMapping public Empleado crear(@RequestBody Empleado e) { return servicio.crear(e); }
    @GetMapping("/{usuario}") public Empleado porUsuario(@PathVariable String usuario) { return servicio.porUsuario(usuario); }
}

