package es.unex.agromanager.fichajesservice.repositorio;

import es.unex.agromanager.fichajesservice.dominio.SesionTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SesionTrabajoRepositorio extends JpaRepository<SesionTrabajo, Long> {

    Optional<SesionTrabajo> findFirstByEmpleadoUsuarioAndSalidaIsNull(String empleadoUsuario);

    List<SesionTrabajo> findAllByEmpleadoUsuarioOrderByIdDesc(String empleadoUsuario);

    @Query("""
    select coalesce(sum(s.minutosTrabajados), 0)
    from SesionTrabajo s
    where s.empleadoUsuario = :usuario
      and s.entrada >= :inicio
      and (s.salida <= :fin or s.salida is null)
  """)
    int sumarMinutosEntre(String usuario, LocalDateTime inicio, LocalDateTime fin);
}

