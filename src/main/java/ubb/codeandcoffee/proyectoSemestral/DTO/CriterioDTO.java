package ubb.codeandcoffee.proyectoSemestral.DTO;
import java.util.*;

public class CriterioDTO {
    private String nombre;
    private String nombreStata;
    private String leyenda;
    private String tipoCalculo; // PROMEDIO, MEDIANA, PARTICULAR

    // PROMEDIO y MEDIANA(Solo 1 variable)
    private Integer idVariableSimple;
    private String operadorSimple;

    // PARTICULAR (Lista de reglas)
    private List<ReglaFila> reglas = new ArrayList<>();

    // clase interna para capturar cada fila del caso Particular
    public static class ReglaFila {
        private Integer idVariable;
        private String operador; // >, <, ==
        private String valor;    // 10, "Si", etc.
        private String conector; // AND, OR (para unir con la siguiente)

        public Integer getIdVariable() { return idVariable; }
        public void setIdVariable(Integer idVariable) { this.idVariable = idVariable; }
        public String getOperador() { return operador; }
        public void setOperador(String operador) { this.operador = operador; }
        public String getValor() { return valor; }
        public void setValor(String valor) { this.valor = valor; }
        public String getConector() { return conector; }
        public void setConector(String conector) { this.conector = conector; }
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

    public String getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(String tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public Integer getIdVariableSimple() {
        return idVariableSimple;
    }

    public void setIdVariableSimple(Integer idVariableSimple) {
        this.idVariableSimple = idVariableSimple;
    }

    public String getOperadorSimple() {
        return operadorSimple;
    }

    public void setOperadorSimple(String operadorSimple) {
        this.operadorSimple = operadorSimple;
    }

    public List<ReglaFila> getReglas() { return reglas; }

    public void setReglas(List<ReglaFila> reglas) { this.reglas = reglas; }



}