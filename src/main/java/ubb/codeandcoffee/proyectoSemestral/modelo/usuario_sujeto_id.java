package ubb.codeandcoffee.proyectoSemestral.modelo;

import java.io.Serializable;
import java.util.Objects;

public class usuario_sujeto_id implements Serializable {
    SujetoEstudio sujetoEstudio;
    Usuario usuario;

    public usuario_sujeto_id(SujetoEstudio sujetoEstudio, Usuario usuario) {
        this.sujetoEstudio = sujetoEstudio;
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        usuario_sujeto_id that = (usuario_sujeto_id) o;
        return Objects.equals(usuario, that.usuario) && Objects.equals(sujetoEstudio, that.sujetoEstudio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sujetoEstudio, usuario);
    }
}
