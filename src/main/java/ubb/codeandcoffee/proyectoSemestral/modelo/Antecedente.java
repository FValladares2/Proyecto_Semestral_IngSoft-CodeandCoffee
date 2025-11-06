package ubb.codeandcoffee.proyectoSemestral.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "antecedentes")
public class Antecedente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idantecedentes;

    private String valorString;

    private Float valorNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "id_sujeto_fk", referencedColumnName = "id_sujeto"),
            @JoinColumn(name = "tipo_fk", referencedColumnName = "tipo")
    })
    private SujetoEstudio sujetoEstudio;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_variable", nullable = false)
    private DatoSolicitado datoSolicitado;


    public Antecedente() {
    }

    public Antecedente(String valorString, Float valorNum, SujetoEstudio sujetoEstudio, DatoSolicitado datoSolicitado) {
        this.valorString = valorString;
        this.valorNum = valorNum;
        this.sujetoEstudio = sujetoEstudio;
        this.datoSolicitado = datoSolicitado;
    }


    public int getIdAntecedentes() {
        return idantecedentes;
    }

    public String getValorString() {
        return valorString;
    }

    public void setValorString(String valorString) {
        this.valorString = valorString;
    }

    public Float getValorNum() {
        return valorNum;
    }

    public void setValorNum(Float valorNum) {
        this.valorNum = valorNum;
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
