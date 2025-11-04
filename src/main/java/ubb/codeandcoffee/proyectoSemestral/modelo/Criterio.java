package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "criterio")
public class Criterio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_criterio;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String nombreStata;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo_Calculo tipoCalculo;

    private String leyenda;

    @Column(nullable = false)
    private String expresion;

    @ManyToMany(mappedBy = "criterios", fetch = FetchType.LAZY)
    private Set<DatoSolicitado> datosSolicitados = new HashSet<>();

    public Criterio() {
    }

    public Criterio(String nombre, String nombreStata, Tipo_Calculo tipoCalculo, String leyenda, String expresion) {
        this.nombre = nombre;
        this.nombreStata = nombreStata;
        this.tipoCalculo = tipoCalculo;
        this.leyenda = leyenda;
        this.expresion = expresion;
    }

    public int getId_criterio() {
        return id_criterio;
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

    public Tipo_Calculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(Tipo_Calculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public String getLeyenda() {
        return leyenda;
    }

    public void setLeyenda(String leyenda) {
        this.leyenda = leyenda;
    }

    public String getExpresion() {
        return expresion;
    }

    public void setExpresion(String expresion) {
        this.expresion = expresion;
    }

    // Getter y Setter para la nueva relación
    public Set<DatoSolicitado> getDatosSolicitados() {
        return datosSolicitados;
    }

    public void setDatosSolicitados(Set<DatoSolicitado> datosSolicitados) {
        this.datosSolicitados = datosSolicitados;
    }
}
