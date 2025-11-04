package ubb.codeandcoffee.proyectoSemestral.modelo;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "usuario_sujeto")
public class Usuario_Sujeto {
    @Id
    @Nonnull
    @ManyToOne(optional = false)
    SujetoEstudio sujetoEstudio;

    @Id
    @Nonnull
    @ManyToOne(optional = false)
    Usuario usuario;

    @Nonnull
    @Temporal(TemporalType.TIMESTAMP)
    Date fecha;  //java.util.Date o java.sql.Date?

    @Nonnull
    String accion;

    public Usuario_Sujeto(@Nonnull SujetoEstudio sujetoEstudio, @Nonnull Usuario usuario, @Nonnull String accion) {
        this.sujetoEstudio = sujetoEstudio;
        this.usuario = usuario;
        this.accion = accion;
        //fecha se ingresa en la bdd
    }

    public Usuario_Sujeto() {}

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
}
