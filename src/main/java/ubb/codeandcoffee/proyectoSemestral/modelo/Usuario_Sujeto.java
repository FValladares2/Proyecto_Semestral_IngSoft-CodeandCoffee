package ubb.codeandcoffee.proyectoSemestral.modelo;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.time.LocalDateTime;

@Entity
@IdClass(usuario_sujeto_id.class)
@Table(name = "usuario_sujeto")
public class Usuario_Sujeto {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "id_sujeto", referencedColumnName = "id_sujeto"),
            @JoinColumn(name = "tipo", referencedColumnName = "tipo")
    })
    SujetoEstudio sujetoEstudio;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    })
    Usuario usuario;

    @Temporal(TemporalType.TIMESTAMP)
    Date fecha;

    @Nonnull
    String accion;

    // Campos para la invitacion por email
    @Column(name = "token_registro")
    private String tokenRegistro;

    @Column(name = "token_expiracion")
    private LocalDateTime tokenExpiracion;

    public Usuario_Sujeto(@Nonnull SujetoEstudio sujetoEstudio, @Nonnull Usuario usuario, @Nonnull String accion) {
        this.sujetoEstudio = sujetoEstudio;
        this.usuario = usuario;
        this.accion = accion;
        //fecha se ingresa en la bdd
    }

    public Usuario_Sujeto() {}

    public usuario_sujeto_id getId(){
        return new usuario_sujeto_id(sujetoEstudio, usuario);
    }

    @Nonnull
    public SujetoEstudio getSujetoEstudio() {
        return sujetoEstudio;
    }

    public void setSujetoEstudio(@Nonnull SujetoEstudio sujetoEstudio) {
        this.sujetoEstudio = sujetoEstudio;
    }

    @Nonnull
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(@Nonnull Usuario usuario) {
        this.usuario = usuario;
    }

    @Nonnull
    public String getAccion() {
        return accion;
    }

    public void setAccion(@Nonnull String accion) {
        this.accion = accion;
    }

    public Date getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario_Sujeto that = (Usuario_Sujeto) o;
        return Objects.equals(sujetoEstudio, that.sujetoEstudio) && Objects.equals(usuario, that.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sujetoEstudio, usuario);
    }

    // Getters y Setters para tokenRegistro y tokenExpiracion
    public String getTokenRegistro() {
        return tokenRegistro;
    }

    public void setTokenRegistro(String tokenRegistro) {
        this.tokenRegistro = tokenRegistro;
    }

    public LocalDateTime getTokenExpiracion() {
        return tokenExpiracion;
    }

    public void setTokenExpiracion(LocalDateTime tokenExpiracion) {
        this.tokenExpiracion = tokenExpiracion;
    }
}

