package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @Nonnull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id_usuario;

    @Nonnull
    private String nombre;
    @Nonnull
    private String contraseña;
    @Nonnull
    private String correo;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private Estado estado;
    @Nonnull
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @OneToMany(mappedBy = "usuario_sujeto")
    private List<Usuario_Sujeto> usuarioSujetos;

    public Usuario(@Nonnull String nombre,
                   @Nonnull String contraseña, @Nonnull String correo,
                   @Nonnull Estado estado, @Nonnull Rol rol) {
        //id_usuario se debería aumentar solo
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.correo = correo;
        this.estado = estado;
        this.rol = rol;
    }

    public Usuario() {}

    @Nonnull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@Nonnull String nombre) {
        this.nombre = nombre;
    }

    @Nonnull
    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(@Nonnull String contraseña) {
        this.contraseña = contraseña;
    }

    @Nonnull
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(@Nonnull String correo) {
        this.correo = correo;
    }

    @Nonnull
    public Estado getEstado() {
        return estado;
    }

    public void setEstado(@Nonnull Estado estado) {
        this.estado = estado;
    }

    @Nonnull
    public Rol getRol() {
        return rol;
    }

    public void setRol(@Nonnull Rol rol) {
        this.rol = rol;
    }

    public List<Usuario_Sujeto> getUsuarioSujetos() {
        return usuarioSujetos;
    }

    public void setUsuarioSujetos(List<Usuario_Sujeto> usuarioSujetos) {
        this.usuarioSujetos = usuarioSujetos;
    }
}

