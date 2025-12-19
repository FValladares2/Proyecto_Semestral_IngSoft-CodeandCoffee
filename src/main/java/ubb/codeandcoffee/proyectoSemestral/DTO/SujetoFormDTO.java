package ubb.codeandcoffee.proyectoSemestral.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario_Sujeto;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;

import java.util.List;
import java.util.Objects;
import java.util.Set;

// DTO para transferir datos de SujetoForm entre capas
public class SujetoFormDTO {
    private String id_sujeto;
    private String tipo;
    private String nombre;
    private String direccion;
    private String ocupacion;
    private String telefono;
    private String email;
    private String nacionalidad;
    Set<Usuario_Sujeto> usuj;
    private List<AntecedenteDTO> antecedentes;

    public codigo_sujeto getCodigo_sujeto() {
        return new codigo_sujeto(id_sujeto, tipo);
    }

    public String getId_sujeto() {
        return id_sujeto;
    }

    public void setId_sujeto(String id_sujeto) {
        this.id_sujeto = id_sujeto;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
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

    public List<AntecedenteDTO> getAntecedentes() {
        return antecedentes;
    }

    public void setAntecedentes(List<AntecedenteDTO> antecedentes) {
        this.antecedentes = antecedentes;
    }

}
