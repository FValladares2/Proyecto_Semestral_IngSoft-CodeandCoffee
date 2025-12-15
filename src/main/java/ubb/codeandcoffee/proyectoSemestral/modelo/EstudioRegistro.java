package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estudio_registro")
public class EstudioRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_estudio;

    @Column(nullable = false, unique = true)
    private String nombreBd;      

    @Column(nullable = false)
    private String nombreVisible; // Nombre para humanos

    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoEstudio estado; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creador")
    private Usuario creador;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) estado = EstadoEstudio.ACTIVO;
    }

    // Constructor vacío 
    public EstudioRegistro() {}

    // Constructor útil para crear nuevos registros
    public EstudioRegistro(String nombreBd, String nombreVisible, Usuario creador) {
        this.nombreBd = nombreBd;
        this.nombreVisible = nombreVisible;
        this.creador = creador;
    }

    // Getters y Setters...
    public String getNombreBd() { return nombreBd; }
    public String getNombreVisible() { return nombreVisible; }
    public Usuario getCreador() { return creador; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public EstadoEstudio getEstado() { return estado; }
    public void setEstado(EstadoEstudio estado) { this.estado = estado; }

}

// Enum simple para el estado del estudio
enum EstadoEstudio { ACTIVO, ARCHIVADO }