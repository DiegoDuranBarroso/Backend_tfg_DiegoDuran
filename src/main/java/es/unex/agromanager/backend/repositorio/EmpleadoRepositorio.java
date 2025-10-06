package es.unex.agromanager.backend.repositorio;

import es.unex.agromanager.backend.dominio.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpleadoRepositorio extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByUsuario(String usuario);
}
