package es.unex.agromanager.backend.servicio;

import es.unex.agromanager.backend.dominio.Empleado;
import es.unex.agromanager.backend.dominio.SesionTrabajo;
import es.unex.agromanager.backend.repositorio.EmpleadoRepositorio;
import es.unex.agromanager.backend.repositorio.SesionTrabajoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class ServicioSesionesTrabajo {

    private final SesionTrabajoRepositorio sesiones;
    private final EmpleadoRepositorio empleados;

    public ServicioSesionesTrabajo(SesionTrabajoRepositorio sesiones,
                                   EmpleadoRepositorio empleados) {
        this.sesiones = sesiones;
        this.empleados = empleados;
    }

    public SesionTrabajo ficharEntrada(String usuario) {
        Empleado emp = empleados.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("El empleado no existe"));

        sesiones.findFirstByEmpleadoIdAndSalidaIsNull(emp.getId())
                .ifPresent(s -> { throw new IllegalStateException("Ya hay una sesión abierta"); });

        SesionTrabajo s = new SesionTrabajo();
        s.setEmpleado(emp);
        s.setEntrada(LocalDateTime.now());
        return sesiones.save(s);
    }

    public SesionTrabajo ficharSalida(String usuario) {
        Empleado emp = empleados.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("El empleado no existe"));

        SesionTrabajo s = sesiones.findFirstByEmpleadoIdAndSalidaIsNull(emp.getId())
                .orElseThrow(() -> new IllegalStateException("No hay sesión abierta"));

        s.setSalida(LocalDateTime.now());
        int minutos = (int) Duration.between(s.getEntrada(), s.getSalida()).toMinutes();
        if (minutos < 0) throw new IllegalStateException("Rango horario inválido");
        s.setMinutosTrabajados(minutos);
        return sesiones.save(s);
    }

    @Transactional(readOnly = true)
    public int minutosHoy(String usuario) {
        Empleado emp = empleados.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalArgumentException("El empleado no existe"));

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay().minusSeconds(1);
        return sesiones.sumarMinutosEntre(emp.getId(), inicio, fin);
    }


}
