package ubb.codeandcoffee.proyectoSemestral.modelo;

import java.io.Serializable;
import java.util.Objects;


// Clase que representa el código único de un sujeto, compuesto por id_sujeto y tipo
public class codigo_sujeto implements Serializable {
    private String id_sujeto;
    private String tipo;

    // Constructor con parámetros
    public codigo_sujeto(String id_sujeto, String tipo) {
        this.id_sujeto = id_sujeto;
        this.tipo = tipo;
    }

    public codigo_sujeto() {}

    // Método para obtener el código completo
    public String getCodigo(){return ""+tipo+id_sujeto;}

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

    // Método para comparar dos objetos codigo_sujeto
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        codigo_sujeto that = (codigo_sujeto) o;
        return Objects.equals(id_sujeto, that.id_sujeto) && Objects.equals(tipo, that.tipo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_sujeto, tipo);
    }

    @Override
    public String toString() {
        return ""+tipo+id_sujeto;
    }
}
