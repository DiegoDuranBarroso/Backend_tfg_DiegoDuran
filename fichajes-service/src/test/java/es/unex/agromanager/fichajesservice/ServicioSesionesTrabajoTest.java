package es.unex.agromanager.fichajesservice;

import es.unex.agromanager.fichajesservice.dominio.SesionTrabajo;
import es.unex.agromanager.fichajesservice.repositorio.SesionTrabajoRepositorio;
import es.unex.agromanager.fichajesservice.servicio.EmpleadoCliente;
import es.unex.agromanager.fichajesservice.servicio.ServicioSesionesTrabajo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioSesionesTrabajoTest {

    @Mock
    private SesionTrabajoRepositorio repo;

    @Mock
    private EmpleadoCliente empleados;

    @InjectMocks
    private ServicioSesionesTrabajo servicio;

    private final String USUARIO = "juan";
    private final String TOKEN = "fake-token";

    @Test
    void ficharEntrada_creaSesionCuandoNoAbierta() {
        // no hay sesión abierta
        when(repo.findFirstByEmpleadoUsuarioAndSalidaIsNull(USUARIO))
                .thenReturn(Optional.empty());
        // simulamos que el save devuelve la entidad con ID
        when(repo.save(any(SesionTrabajo.class))).thenAnswer(inv -> {
            SesionTrabajo s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        SesionTrabajo creada = servicio.ficharEntrada(USUARIO, TOKEN);

        // se validó el empleado con el token recibido
        verify(empleados).validarExiste(USUARIO, TOKEN);
        // se consultó que no hubiese sesión abierta
        verify(repo).findFirstByEmpleadoUsuarioAndSalidaIsNull(USUARIO);
        // se guardó la nueva sesión
        verify(repo).save(any(SesionTrabajo.class));

        assertNotNull(creada);
        assertEquals(1L, creada.getId());
        assertEquals(USUARIO, creada.getEmpleadoUsuario());
        assertNotNull(creada.getEntrada());
        assertNull(creada.getSalida());
        assertEquals(0, creada.getMinutosTrabajados());
    }

    @Test
    void ficharEntrada_lanzaSiYaAbierta() {
        SesionTrabajo abierta = new SesionTrabajo();
        abierta.setEmpleadoUsuario(USUARIO);
        abierta.setEntrada(LocalDateTime.now().minusMinutes(10));

        when(repo.findFirstByEmpleadoUsuarioAndSalidaIsNull(USUARIO))
                .thenReturn(Optional.of(abierta));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.ficharEntrada(USUARIO, TOKEN)
        );

        assertTrue(ex.getMessage().contains("Ya hay una sesión abierta"));
        verify(empleados).validarExiste(USUARIO, TOKEN);
        verify(repo).findFirstByEmpleadoUsuarioAndSalidaIsNull(USUARIO);
        verify(repo, never()).save(any());
    }

    @Test
    void ficharSalida_cierraSesionYCalculaMinutos() {
        // sesión abierta desde hace ~90 minutos
        SesionTrabajo abierta = new SesionTrabajo();
        abierta.setId(7L);
        abierta.setEmpleadoUsuario(USUARIO);
        abierta.setEntrada(LocalDateTime.now().minusMinutes(90));

        when(repo.findFirstByEmpleadoUsuarioAndSalidaIsNull(USUARIO))
                .thenReturn(Optional.of(abierta));

        // mock de save que devuelve la misma sesión
        when(repo.save(any(SesionTrabajo.class))).thenAnswer(inv -> inv.getArgument(0));

        SesionTrabajo cerrada = servicio.ficharSalida(USUARIO, TOKEN);

        verify(empleados).validarExiste(USUARIO, TOKEN);
        verify(repo).findFirstByEmpleadoUsuarioAndSalidaIsNull(USUARIO);
        verify(repo).save(any(SesionTrabajo.class));

        assertNotNull(cerrada.getSalida());
        // debería haber calculado minutos positivos y cercanos a 90
        assertTrue(cerrada.getMinutosTrabajados() >= 85 && cerrada.getMinutosTrabajados() <= 95,
                "Minutos calculados fuera de rango razonable");
    }

    @Test
    void ficharSalida_lanzaSiNoHaySesionAbierta() {
        when(repo.findFirstByEmpleadoUsuarioAndSalidaIsNull(USUARIO))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.ficharSalida(USUARIO, TOKEN)
        );

        assertTrue(ex.getMessage().contains("No hay sesión abierta"));
        verify(empleados).validarExiste(USUARIO, TOKEN);
        verify(repo, never()).save(any());
    }

    @Test
    void minutosHoy_devuelveSumaYUsaRangoDeHoy() {
        // vamos a capturar los parámetros de fecha para verificar que son de "hoy"
        ArgumentCaptor<LocalDateTime> inicioCap = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> finCap = ArgumentCaptor.forClass(LocalDateTime.class);

        when(repo.sumarMinutosEntre(eq(USUARIO), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(135);

        int minutos = servicio.minutosHoy(USUARIO, TOKEN);

        verify(empleados).validarExiste(USUARIO, TOKEN);
        verify(repo).sumarMinutosEntre(eq(USUARIO), inicioCap.capture(), finCap.capture());

        LocalDate hoy = LocalDate.now();
        LocalDateTime esperadoInicio = hoy.atStartOfDay();
        LocalDateTime esperadoFin = hoy.plusDays(1).atStartOfDay().minusSeconds(1);

        assertEquals(esperadoInicio, inicioCap.getValue(), "inicio de rango incorrecto");
        assertEquals(esperadoFin, finCap.getValue(), "fin de rango incorrecto");
        assertEquals(135, minutos);
    }

    @Test
    void listar_devuelveListaOrdenadaYValidaEmpleado() {
        List<SesionTrabajo> mockLista = List.of(
                sesion(3L, USUARIO, LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(2), 60),
                sesion(2L, USUARIO, LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(4), 60)
        );

        when(repo.findAllByEmpleadoUsuarioOrderByIdDesc(USUARIO)).thenReturn(mockLista);

        List<SesionTrabajo> res = servicio.listar(USUARIO, TOKEN);

        verify(empleados).validarExiste(USUARIO, TOKEN);
        verify(repo).findAllByEmpleadoUsuarioOrderByIdDesc(USUARIO);

        assertEquals(2, res.size());
        assertEquals(3L, res.get(0).getId());
        assertEquals(2L, res.get(1).getId());
    }

    // ---- utilidades ----
    private SesionTrabajo sesion(Long id, String usuario, LocalDateTime entrada, LocalDateTime salida, int minutos) {
        SesionTrabajo s = new SesionTrabajo();
        s.setId(id);
        s.setEmpleadoUsuario(usuario);
        s.setEntrada(entrada);
        s.setSalida(salida);
        s.setMinutosTrabajados(minutos);
        return s;
    }
}
