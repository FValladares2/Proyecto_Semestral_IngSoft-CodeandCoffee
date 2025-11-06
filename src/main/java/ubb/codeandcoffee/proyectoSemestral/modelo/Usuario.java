package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails{
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

    @OneToMany(mappedBy = "usuario")
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

    public int getId_usuario() {
        return id_usuario;
    }

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

    //de aqui pa abajo estara lo necesario para Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+rol.name()));
    }

    @Override
    public String getPassword() {
        return this.contraseña;
    }

    @Override
    public String getUsername() {
        // Usamos el correo como "username"
        return this.correo;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        // Asumiendo que tu Enum Estado tiene un valor 'ACTIVO'
        return this.estado == Estado.ACTIVO;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return this.estado == Estado.ACTIVO;
    }
}

