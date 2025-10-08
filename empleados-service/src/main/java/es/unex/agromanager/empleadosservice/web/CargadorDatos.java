package es.unex.agromanager.empleadosservice.web;

import es.unex.agromanager.empleadosservice.dominio.Empleado;
import es.unex.agromanager.empleadosservice.repositorio.EmpleadoRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargadorDatos implements CommandLineRunner {

    private final EmpleadoRepositorio repo;
    public CargadorDatos(EmpleadoRepositorio repo) { this.repo = repo; }

    @Override public void run(String... args) {
        repo.findByUsuario("juan").orElseGet(() -> {
            Empleado e = new Empleado();
            e.setUsuario("juan");
            e.setNombreCompleto("Juan Pérez");
            return repo.save(e);
        });
    }
}
