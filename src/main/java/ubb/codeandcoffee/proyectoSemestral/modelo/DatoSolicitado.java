package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.persistence.*;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


// Entidad que representa un DatoSolicitado en la base de datos
@Entity
@Table(name = "datosolicitado")
public class DatoSolicitado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_dato;

    private String nombre;

    private String nombreStata;

    private String leyenda;

    private Boolean estudio;
    private Integer valorMin;
    private Integer valorMax;
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Aplicable_a aplicable_a;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRespuesta tipoRespuesta;

    @ManyToOne
    @JoinColumn(name="id_seccion", nullable = false)
    private Seccion seccion;


    @OneToMany(mappedBy = "dato", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Opcion> opciones;

    @JsonIgnore
     @ManyToMany(mappedBy = "datosSolicitados", fetch = FetchType.LAZY)
    private Set<Criterio> criteriosAsociados = new HashSet<>();

    public Set<Criterio> getCriteriosAsociados() {
        return criteriosAsociados;
    }

    public void setCriteriosAsociados(Set<Criterio> criteriosAsociados) {
        this.criteriosAsociados = criteriosAsociados;
    }

    public String getNombresCriteriosParaMostrar() {
        if (this.criteriosAsociados == null || this.criteriosAsociados.isEmpty()) {
            return "";
        }

        return this.criteriosAsociados.stream()
                .map(c -> c.getNombre())
                .collect(java.util.stream.Collectors.joining(", "));
    }


    public void addOpcion(Opcion opcion) {
        opciones.add(opcion);
        opcion.setDatoSolicitado(this);
    }

    public void removeOpcion(Opcion opcion) {
        opciones.remove(opcion);
        opcion.setDatoSolicitado(null);
    }

    public DatoSolicitado(){
        this.opciones = new ArrayList<>();
        this.aplicable_a = Aplicable_a.AMBOS;
        this.estudio = true;
        this.tipoRespuesta=TipoRespuesta.OPCIONES;
        this.activo = true;
    }
    public DatoSolicitado(String nombre, String nombreStata, String leyenda, Boolean estudio,
                          Aplicable_a aplicable_a, Seccion seccion, TipoRespuesta tipoRespuesta,
                          Integer valorMin, Integer valorMax) {
        this.nombre = nombre;
        this.nombreStata = nombreStata;
        this.leyenda = leyenda;
        this.estudio = estudio;
        this.aplicable_a = aplicable_a;
        this.seccion = seccion;
        this.tipoRespuesta=tipoRespuesta;
        this.opciones = new ArrayList<>();
        this.valorMin=valorMin;
        this.valorMax=valorMax;
        this.activo = true;
    }
    public List<Opcion> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<Opcion> opciones) {
        this.opciones = opciones;
    }

    public Integer getId_dato () {
        return id_dato;
    }

    public void setId_dato(Integer id_dato) {
        this.id_dato = id_dato;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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

    public TipoRespuesta getTipoRespuesta(){
        return tipoRespuesta;
    }
    public void setTipoRespuesta(TipoRespuesta tipoRespuesta) {
        this.tipoRespuesta = tipoRespuesta;
    }

    public Integer getValorMin() {
        return valorMin;
    }

    public void setValorMin(Integer valorMin) {
        this.valorMin = valorMin;
    }

    public Integer getValorMax() {
        return valorMax;
    }

    public void setValorMax(Integer valorMax) {
        this.valorMax = valorMax;
    }

}

