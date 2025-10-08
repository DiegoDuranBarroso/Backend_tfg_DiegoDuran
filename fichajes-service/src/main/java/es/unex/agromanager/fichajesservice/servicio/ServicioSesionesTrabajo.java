// fichajes-service/src/main/java/.../servicio/ServicioSesionesTrabajo.java
package es.unex.agromanager.fichajesservice.servicio;

import es.unex.agromanager.fichajesservice.dominio.SesionTrabajo;
import es.unex.agromanager.fichajesservice.repositorio.SesionTrabajoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ServicioSesionesTrabajo {

    private final SesionTrabajoRepositorio repo;
    private final EmpleadoCliente empleados;

    public ServicioSesionesTrabajo(SesionTrabajoRepositorio repo, EmpleadoCliente empleados) {
        this.repo = repo; this.empleados = empleados;
    }

    public SesionTrabajo ficharEntrada(String usuario, String bearerToken) {
        empleados.validarExiste(usuario, bearerToken);
        repo.findFirstByEmpleadoUsuarioAndSalidaIsNull(usuario)
                .ifPresent(s -> { throw new IllegalStateException("Ya hay una sesión abierta"); });
        SesionTrabajo s = new SesionTrabajo();
        s.setEmpleadoUsuario(usuario);
        s.setEntrada(LocalDateTime.now());
        return repo.save(s);
    }

    public SesionTrabajo ficharSalida(String usuario, String bearerToken) {
        empleados.validarExiste(usuario, bearerToken);
        SesionTrabajo s = repo.findFirstByEmpleadoUsuarioAndSalidaIsNull(usuario)
                .orElseThrow(() -> new IllegalStateException("No hay sesión abierta"));
        s.setSalida(LocalDateTime.now());
        int minutos = (int) Duration.between(s.getEntrada(), s.getSalida()).toMinutes();
        if (minutos < 0) throw new IllegalStateException("Rango horario inválido");
        s.setMinutosTrabajados(minutos);
        return repo.save(s);
    }

    @Transactional(readOnly = true)
    public int minutosHoy(String usuario, String bearerToken) {
        empleados.validarExiste(usuario, bearerToken);
        var hoy = LocalDate.now();
        var inicio = hoy.atStartOfDay();
        var fin = hoy.plusDays(1).atStartOfDay().minusSeconds(1);
        return repo.sumarMinutosEntre(usuario, inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<SesionTrabajo> listar(String usuario, String bearerToken) {
        empleados.validarExiste(usuario, bearerToken);
        return repo.findAllByEmpleadoUsuarioOrderByIdDesc(usuario);
    }
}
