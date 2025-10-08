package es.unex.agromanager.empleadosservice.servicio;

import es.unex.agromanager.empleadosservice.dominio.Empleado;
import es.unex.agromanager.empleadosservice.repositorio.EmpleadoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoServicio {

    private final EmpleadoRepositorio repo;
    public EmpleadoServicio(EmpleadoRepositorio repo) { this.repo = repo; }

    public List<Empleado> listar() { return repo.findAll(); }
    public Empleado crear(Empleado e) { return repo.save(e); }
    public Empleado porUsuario(String usuario) {
        return repo.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
    }
}
