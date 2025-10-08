package es.unex.agromanager.fichajesservice.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "sesion_trabajo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"empleado_usuario", "salida"})
)
public class SesionTrabajo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="empleado_usuario", nullable = false)
    private String empleadoUsuario;

    @Column(name="entrada", nullable = false)
    private LocalDateTime entrada;

    @Column(name="salida")
    private LocalDateTime salida;

    @Column(name="minutos_trabajados", nullable = false)
    private Integer minutosTrabajados = 0;

    // Getters/setters
    public Long getId() { return id; }
    public String getEmpleadoUsuario() { return empleadoUsuario; }
    public void setEmpleadoUsuario(String empleadoUsuario) { this.empleadoUsuario = empleadoUsuario; }
    public LocalDateTime getEntrada() { return entrada; }
    public void setEntrada(LocalDateTime entrada) { this.entrada = entrada; }
    public LocalDateTime getSalida() { return salida; }
    public void setSalida(LocalDateTime salida) { this.salida = salida; }
    public Integer getMinutosTrabajados() { return minutosTrabajados; }
    public void setMinutosTrabajados(Integer minutosTrabajados) { this.minutosTrabajados = minutosTrabajados; }
}
