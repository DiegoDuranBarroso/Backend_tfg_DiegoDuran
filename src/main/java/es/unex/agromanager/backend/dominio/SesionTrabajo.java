package es.unex.agromanager.backend.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "sesion_trabajo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"empleado_id", "salida"})
)
public class SesionTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @Column(name = "entrada", nullable = false)
    private LocalDateTime entrada;

    @Column(name = "salida")
    private LocalDateTime salida;

    @Column(name = "minutos_trabajados", nullable = false)
    private Integer minutosTrabajados = 0;

    // Getters y setters
    public Long getId() { return id; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public LocalDateTime getEntrada() { return entrada; }
    public void setEntrada(LocalDateTime entrada) { this.entrada = entrada; }

    public LocalDateTime getSalida() { return salida; }
    public void setSalida(LocalDateTime salida) { this.salida = salida; }

    public Integer getMinutosTrabajados() { return minutosTrabajados; }
    public void setMinutosTrabajados(Integer minutosTrabajados) { this.minutosTrabajados = minutosTrabajados; }
}
