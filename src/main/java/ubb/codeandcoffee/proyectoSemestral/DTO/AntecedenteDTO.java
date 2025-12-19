package ubb.codeandcoffee.proyectoSemestral.DTO;

import jakarta.persistence.*;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;


// DTO para transferir datos de Antecedente entre capas
public class AntecedenteDTO {
    private Integer id_antecedente;
    private String respuestaIngresada;
    private int id_opcion;
    private SujetoEstudio sujetoEstudio;
    private DatoSolicitado datoSolicitado;

    public AntecedenteDTO() {
    }

    

    public Integer getId_antecedente() {
        return id_antecedente;
    }

    public void setId_antecedente(Integer id_antecedente) {
        this.id_antecedente = id_antecedente;
    }

    public String getRespuestaIngresada() {
        return respuestaIngresada;
    }

    public void setRespuestaIngresada(String respuestaIngresada) {
        this.respuestaIngresada = respuestaIngresada;
    }

    public int getId_opcion() {
        return id_opcion;
    }

    public void setId_opcion(int id_opcion) {
        this.id_opcion = id_opcion;
    }

    public SujetoEstudio getSujetoEstudio() {
        return sujetoEstudio;
    }

    public void setSujetoEstudio(SujetoEstudio sujetoEstudio) {
        this.sujetoEstudio = sujetoEstudio;
    }

    public DatoSolicitado getDatoSolicitado() {
        return datoSolicitado;
    }

    public void setDatoSolicitado(DatoSolicitado datoSolicitado) {
        this.datoSolicitado = datoSolicitado;
    }

}
