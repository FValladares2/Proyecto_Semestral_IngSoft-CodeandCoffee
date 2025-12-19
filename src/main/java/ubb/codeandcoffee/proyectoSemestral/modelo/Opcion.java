package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.persistence.*;

// Entidad que representa una Opcion en la base de datos
@Entity
@Table(name = "opcion")
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_opcion;
    private String nombre;
    private Integer valor;
    @JoinColumn(name="requiere_texto")
    private boolean requiereTexto;
    @ManyToOne
    @JoinColumn(name="id_dato", nullable = false)
    private DatoSolicitado dato;

    public Opcion() {this.requiereTexto=false;}
    public Opcion(String nombre, int valor, DatoSolicitado dato) {
        this.nombre = nombre;
        this.valor = valor;
        this.dato=dato;
        this.requiereTexto=false;
    }
    public Opcion(String nombre, int valor, boolean requiereTexto, DatoSolicitado dato) {
        this.nombre = nombre;
        this.valor = valor;
        this.requiereTexto=requiereTexto;
        this.dato=dato;
    }

    // Getters y Setters

    public DatoSolicitado getDatoSolicitado(){
        return dato;
    }

    public void setDatoSolicitado(DatoSolicitado dato){
        this.dato=dato;
    }

    public Integer  getId_opcion() {
        return id_opcion;
    }
    public void setId_opcion(Integer id_opcion){
        this.id_opcion=id_opcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }
    public boolean isRequiereTexto() {
        return this.requiereTexto;
    }

    public void setRequiereTexto(boolean text) {
        this.requiereTexto=text;
    }

}
