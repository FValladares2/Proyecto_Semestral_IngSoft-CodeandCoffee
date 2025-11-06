package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "seccion")
public class Seccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_seccion;
    private String nombre;
    private Integer numero;
    

    public Seccion() {
        this.datosSolicitados = new ArrayList<>();
    }
    public Seccion(String nombre, int numero) {
        this.nombre = nombre;
        this.numero = numero;
        this.datosSolicitados = new ArrayList<>();
    }

    @OneToMany(mappedBy = "seccion")
    @JsonIgnore
    private List<DatoSolicitado> datosSolicitados;
    public List<DatoSolicitado> getDatosSolicitados(){
        return datosSolicitados;
    }

    public void setDatosSolicitados(List<DatoSolicitado> datoSolicitados){
        this.datosSolicitados=datoSolicitados;
    }
    public void setId_seccion(Integer id_seccion) {
        this.id_seccion = id_seccion;
    }

    public Integer getId_seccion() {
        return this.id_seccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }
}

