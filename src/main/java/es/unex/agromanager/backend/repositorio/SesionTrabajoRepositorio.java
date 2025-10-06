package es.unex.agromanager.backend.repositorio;

import es.unex.agromanager.backend.dominio.SesionTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SesionTrabajoRepositorio extends JpaRepository<SesionTrabajo, Long> {

    Optional<SesionTrabajo> findFirstByEmpleadoIdAndSalidaIsNull(Long empleadoId);

    @Query("""
    select coalesce(sum(s.minutosTrabajados), 0)
    from SesionTrabajo s
    where s.empleado.id = :empleadoId
      and s.entrada >= :inicio
      and (s.salida <= :fin or s.salida is null)
  """)
    int sumarMinutosEntre(@Param("empleadoId") Long empleadoId,
                          @Param("inicio") LocalDateTime inicio,
                          @Param("fin") LocalDateTime fin);
}

