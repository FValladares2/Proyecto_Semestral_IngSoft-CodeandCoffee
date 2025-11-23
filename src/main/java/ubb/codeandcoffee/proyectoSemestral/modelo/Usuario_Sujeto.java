package ubb.codeandcoffee.proyectoSemestral.modelo;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "usuario_sujeto")
public class Usuario_Sujeto {
    @Id
    @Nonnull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_cambio;

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "id_sujeto", referencedColumnName = "id_sujeto"),
            @JoinColumn(name = "tipo", referencedColumnName = "tipo")
    })
    SujetoEstudio sujetoEstudio;

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "id_usuario", nullable = true)
    Usuario usuario;

    @Temporal(TemporalType.TIMESTAMP)
    Date fecha;

    @Nonnull
    String accion;

    public Usuario_Sujeto(@Nonnull Integer id_cambio, SujetoEstudio sujetoEstudio, Usuario usuario, @Nonnull String accion) {
        this.id_cambio = id_cambio;
        this.sujetoEstudio = sujetoEstudio;
        this.usuario = usuario;
        this.accion = accion;
        //fecha se ingresa en la bdd
    }

    public Usuario_Sujeto() {}

    public Integer getId(){
        return id_cambio;
    }

    @Nonnull
    public SujetoEstudio getSujetoEstudio() {
        return sujetoEstudio;
    }

    public void setSujetoEstudio(@Nonnull SujetoEstudio sujetoEstudio) {
        this.sujetoEstudio = sujetoEstudio;
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
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
}

