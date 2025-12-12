package ubb.codeandcoffee.proyectoSemestral.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "antecedentes")
public class Antecedente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idantecedentes;

    private String valorString;

    private Float valorNum;
    @Transient // Indica a JPA que ignore este campo
    private String textoOpcion;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "id_sujeto"),
            @JoinColumn(name = "tipo")
    })
    private SujetoEstudio sujetoEstudio;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dato", nullable = false)
    private DatoSolicitado datoSolicitado;


    public Antecedente() {
    }
    public Antecedente(DatoSolicitado dato) {
        this.datoSolicitado=dato;
    }

    public Antecedente(String valorString, Float valorNum, SujetoEstudio sujetoEstudio, DatoSolicitado datoSolicitado) {
        this.valorString = valorString;
        this.valorNum = valorNum;
        this.sujetoEstudio = sujetoEstudio;
        this.datoSolicitado = datoSolicitado;
    }

    public String getTextoOpcion() {
        return textoOpcion;
    }

    public void setTextoOpcion(String textoOpcion) {
        this.textoOpcion = textoOpcion;
    }

    public void setIdantecedentes(int idantecedentes) {
        this.idantecedentes = idantecedentes;
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
