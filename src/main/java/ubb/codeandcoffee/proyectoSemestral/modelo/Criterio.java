package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// Entidad que representa un Criterio en la base de datos
@Entity
@Table(name = "criterio")
public class Criterio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_criterio;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String nombreStata;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo_Calculo tipoCalculo;

    private String leyenda;

    @Column(nullable = false)
    private String expresion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "criterio_datosolicitado",
            joinColumns = @JoinColumn(name = "id_criterio"),
            inverseJoinColumns = @JoinColumn(name = "id_dato")
    )
    private Set<DatoSolicitado> datosSolicitados = new HashSet<>();

    public Criterio() {
        this.activo=true;
    }

    public Criterio(String nombre, String nombreStata, Tipo_Calculo tipoCalculo, String leyenda, String expresion) {
        this.nombre = nombre;
        this.nombreStata = nombreStata;
        this.tipoCalculo = tipoCalculo;
        this.leyenda = leyenda;
        this.expresion = expresion;
        this.activo=true;
    }

    // Getters y Setters
    public Integer getId_criterio() {
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

    public Set<DatoSolicitado> getDatosSolicitados() {
        return datosSolicitados;
    }

    public void setDatosSolicitados(Set<DatoSolicitado> datosSolicitados) {
        this.datosSolicitados = datosSolicitados;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Criterio criterio = (Criterio) o;
        return id_criterio != null && id_criterio.equals(criterio.id_criterio);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
