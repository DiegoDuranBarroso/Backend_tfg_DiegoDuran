package es.unex.agromanager.backend.web;

import es.unex.agromanager.backend.dominio.Empleado;
import es.unex.agromanager.backend.repositorio.EmpleadoRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargadorDatos implements CommandLineRunner {

    private final EmpleadoRepositorio empleados;

    public CargadorDatos(EmpleadoRepositorio empleados) {
        this.empleados = empleados;
    }

    @Override
    public void run(String... args) {
        empleados.findByUsuario("juan").orElseGet(() -> {
            Empleado e = new Empleado();
            e.setUsuario("juan");
            e.setNombreCompleto("Juan Pérez");
            return empleados.save(e);
        });
    }
}
