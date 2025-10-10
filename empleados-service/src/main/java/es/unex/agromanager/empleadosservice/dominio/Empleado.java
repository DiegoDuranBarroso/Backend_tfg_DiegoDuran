package es.unex.agromanager.empleadosservice.dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "empleado")
public class Empleado {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="usuario", unique = true, nullable = false)
    private String usuario;

    @Column(name="nombre_completo", nullable = false)
    private String nombreCompleto;

    // Getters/setters
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() { return id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }


}
