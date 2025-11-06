package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "opcion")
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_opcion;
    private String nombre;
    private int valor;
    @ManyToOne
    @JoinColumn(name="id_dato", nullable = false)
    private DatoSolicitado dato;

    public Opcion() {}
    public Opcion(String nombre, int valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public DatoSolicitado getDatoSolicitado(){
        return dato;
    }

    public void setDatoSolicitado(DatoSolicitado dato){
        this.dato=dato;
    }

    public int getId_opcion() {
        return id_opcion;
    }
    public void setId_opcion(int id_opcion){
        this.id_opcion=id_opcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}
