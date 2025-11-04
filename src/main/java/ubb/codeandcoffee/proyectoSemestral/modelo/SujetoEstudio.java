package ubb.codeandcoffee.proyectoSemestral.modelo;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@IdClass(codigo_sujeto.class)
@Table(
        name = "sujetoestudio",
        uniqueConstraints = @UniqueConstraint(columnNames = {"nombre"})
)
public class SujetoEstudio {
    @Id
    @Nonnull
    private String id_sujeto;
    @Id
    @Nonnull
    private String tipo;

    @Nonnull
    private String nombre;

    private String direccion;
    private String ocupacion;
    private String telefono;
    private String email;
    private String nacionalidad;

    @OneToMany(mappedBy = "usuario_sujeto")
    Set<Usuario_Sujeto> usuj;

    @OneToMany(mappedBy = "antecedentes")
    Set<Antecedente> antecedentes;

    public SujetoEstudio(@Nonnull String tipo, @Nonnull String nombre,
                         String direccion, String ocupacion,
                         String telefono, String email,
                         String nacionalidad) {
        this.tipo = tipo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ocupacion = ocupacion;
        this.telefono = telefono;
        this.email = email;
        this.nacionalidad = nacionalidad;;
    }

    public SujetoEstudio() {}

    @Nonnull
    public String getTipo() {
        return tipo;
    }

    public void setTipo(@Nonnull String tipo) {
        this.tipo = tipo;
    }

    @Nonnull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@Nonnull String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Set<Usuario_Sujeto> getUsuj() {
        return usuj;
    }

    public void setUsuj(Set<Usuario_Sujeto> usuj) {
        this.usuj = usuj;
    }
}