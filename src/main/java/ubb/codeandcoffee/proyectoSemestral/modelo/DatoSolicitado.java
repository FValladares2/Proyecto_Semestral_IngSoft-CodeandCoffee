package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "datosolicitado")
public class DatoSolicitado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_dato;

    private String nombre;

    private String nombreStata;

    private String leyenda;

    private Boolean estudio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Aplicable_a aplicable_a;

    @ManyToOne
    @JoinColumn(name="id_seccion", nullable = false)
    private Seccion seccion;


    @OneToMany(mappedBy = "dato")
    @JsonIgnore
    private List<Opcion> opciones;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dato_criterio",
            joinColumns = @JoinColumn(name = "id_dato"),
            inverseJoinColumns = @JoinColumn(name = "id_criterio")
    )
    @JsonIgnore
    private Set<Criterio> criterios = new HashSet<>();
    

    public DatoSolicitado(){
        this.opciones = new ArrayList<>();
        this.aplicable_a = Aplicable_a.AMBOS;
        this.estudio = true;
    }
    public DatoSolicitado(String nombre, String nombreStata, String leyenda, Boolean estudio, Aplicable_a aplicable_a, Seccion seccion) {
        this.nombre = nombre;
        this.nombreStata = nombreStata;
        this.leyenda = leyenda;
        this.estudio = estudio;
        this.aplicable_a = aplicable_a;
        this.seccion = seccion;
        this.opciones = new ArrayList<>();
    }
    public List<Opcion> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<Opcion> opciones) {
        this.opciones = opciones;
    }

    public int getId_dato () {
        return id_dato;
    }

    public void setId_dato(int id_dato) {
        this.id_dato = id_dato;
    }


    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getNombreStata() {
        return nombreStata;
    }
    public void setNombreStata(String nombreStata) {
        this.nombreStata = nombreStata;
    }
    public String getLeyenda() {
        return leyenda;
    }
    public void setLeyenda(String leyenda) {
        this.leyenda = leyenda;
    }
    public Boolean getEstudio() {
        return estudio;
    }
    public void setEstudio(Boolean estudio) {
        this.estudio = estudio;
    }
    public Aplicable_a getAplicable_a() {
        return aplicable_a;
    }
    public void setAplicable_a(Aplicable_a aplicable_a) {
        this.aplicable_a = aplicable_a;
    }
    public Seccion getSeccion(){
        return seccion;
    }
    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public Set<Criterio> getCriterios() {
        return criterios;
    }

    public void setCriterios(Set<Criterio> criterios) {
        this.criterios = criterios;
    }
}

