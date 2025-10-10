package es.unex.agromanager.empleadosservice;


import es.unex.agromanager.empleadosservice.dominio.Empleado;
import es.unex.agromanager.empleadosservice.repositorio.EmpleadoRepositorio;
import es.unex.agromanager.empleadosservice.servicio.EmpleadoServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio EmpleadoServicio
 * usando mocks (sin base de datos real).
 */
@ExtendWith(MockitoExtension.class)
class EmpleadoServicioTest {

    @Mock
    private EmpleadoRepositorio repo;

    @InjectMocks
    private EmpleadoServicio servicio;

    private Empleado empleado1;
    private Empleado empleado2;

    @BeforeEach
    void setUp() {
        empleado1 = new Empleado();
        empleado1.setId(1L);
        empleado1.setUsuario("juan");
        empleado1.setNombreCompleto("Juan Pérez");

        empleado2 = new Empleado();
        empleado2.setId(2L);
        empleado2.setUsuario("maria");
        empleado2.setNombreCompleto("María López");
    }

    @Test
    void listar_devuelveListaDeEmpleados() {
        when(repo.findAll()).thenReturn(List.of(empleado1, empleado2));

        List<Empleado> resultado = servicio.listar();

        assertEquals(2, resultado.size());
        assertEquals("juan", resultado.get(0).getUsuario());
        assertEquals("maria", resultado.get(1).getUsuario());
        verify(repo).findAll();
    }

    @Test
    void crear_guardaYDevuelveEmpleado() {
        when(repo.save(any(Empleado.class))).thenAnswer(inv -> {
            Empleado e = inv.getArgument(0);
            e.setId(99L);
            return e;
        });

        Empleado nuevo = new Empleado();
        nuevo.setUsuario("nuevo");
        nuevo.setNombreCompleto("Nuevo Empleado");

        Empleado guardado = servicio.crear(nuevo);

        assertNotNull(guardado.getId());
        assertEquals("nuevo", guardado.getUsuario());
        verify(repo).save(nuevo);
    }

    @Test
    void porUsuario_devuelveEmpleadoSiExiste() {
        when(repo.findByUsuario("juan")).thenReturn(Optional.of(empleado1));

        Empleado res = servicio.porUsuario("juan");

        assertEquals("juan", res.getUsuario());
        assertEquals("Juan Pérez", res.getNombreCompleto());
        verify(repo).findByUsuario("juan");
    }

    @Test
    void porUsuario_lanzaExcepcionSiNoExiste() {
        when(repo.findByUsuario("fantasma")).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.porUsuario("fantasma"));

        assertEquals("Empleado no encontrado", ex.getMessage());
        verify(repo).findByUsuario("fantasma");
    }
}
