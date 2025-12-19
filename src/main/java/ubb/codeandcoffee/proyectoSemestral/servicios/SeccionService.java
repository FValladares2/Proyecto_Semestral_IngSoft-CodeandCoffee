package ubb.codeandcoffee.proyectoSemestral.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubb.codeandcoffee.proyectoSemestral.modelo.Aplicable_a;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SeccionRepository;

@Service //Marca esta clase como un servicio de Spring
public class SeccionService {
    @Autowired
    SeccionRepository seccionRepository; //instancia del repositorio de Seccion

    //Método para obtener las secciones de la base de datos
    public ArrayList<Seccion> getAllSecciones(){
        return(ArrayList<Seccion>) seccionRepository.findAll();
    }

    // metodo que sugiere el numero
    public Integer obtenerUltimoNumero() {
        return seccionRepository.obtenerUltimoNumero();
    }

    //metodo auxiliar por si acaso
    public List<Seccion> listarTodasOrdenadas() {
        return seccionRepository.findAllByOrderByNumeroAsc();
    }

    public List<Seccion> getSeccionesFiltradas(String tipoSujeto) {
        Aplicable_a tipoEnum = Aplicable_a.valueOf(tipoSujeto.toUpperCase());

        // obtenemos todas las secciones y las ordenamos
        List<Seccion> secciones = (List<Seccion>) seccionRepository.findAll();
        secciones.sort(Comparator.comparing(Seccion::getNumero));

        for (Seccion seccion : secciones) {
            // filtramos y ordenamos los datos solicitados en una nueva lista
            List<DatoSolicitado> filtrados = seccion.getDatosSolicitados().stream()
                    .filter(dato -> Boolean.TRUE.equals(dato.getActivo())) // Solo activos
                    .filter(dato -> dato.getAplicable_a() == Aplicable_a.AMBOS || dato.getAplicable_a() == tipoEnum)
                    .sorted(Comparator.comparing(DatoSolicitado::getId_dato)) // Orden de creación
                    .collect(Collectors.toList());

            // sobreescribimos la lista temporal para la vista
            seccion.setDatosSolicitados(filtrados);
        }

        // eliminamos secciones que hayan quedado vacías tras el filtrado
        return secciones.stream()
                .filter(s -> !s.getDatosSolicitados().isEmpty())
                .collect(Collectors.toList());
    }

    @Transactional
    public Seccion guardarSeccion(Seccion seccion) {

        if (seccion.getNombre() == null || seccion.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // si no trae numero o es 0, lo mandamos al final de la cola
        if (seccion.getNumero() == null || seccion.getNumero() <= 0) {
            Integer ultimo = seccionRepository.obtenerUltimoNumero();
            seccion.setNumero(ultimo + 1);
        }
        // Si trae numero, verificamos si 'choca' con otro para hacer espacio
        else {
            boolean ocupado = seccionRepository.existsByNumero(seccion.getNumero());
            if (ocupado) {
                // si el lugar esta ocupado, empujamos a todos los siguientes un paso adelante
                seccionRepository.empujarSecciones(seccion.getNumero());
            }
        }

        return seccionRepository.save(seccion);
    }

    public Optional<Seccion> getById(Integer id_seccion){
        return seccionRepository.findById(id_seccion);
    }

    @Transactional
    public Boolean deleteSeccion(Integer id_seccion){
        try{
            // buscamos cual era su numero antes de borrarla
            Seccion seccion = seccionRepository.findById(id_seccion).orElse(null);

            if(seccion != null) {
                Integer numeroBorrado = seccion.getNumero();

                // La borramos fisicamente
                seccionRepository.deleteById(id_seccion);

                // acomodamos los numeros restantes para que no queden huecos (1, 3, 4 -> 1, 2, 3)
                seccionRepository.reordenarDespuesDeBorrar(numeroBorrado);
                return true;
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }

    public Seccion updateById(Seccion request, Integer id_seccion) {
        Seccion seccion = seccionRepository.findById(id_seccion)
                .orElseThrow(() -> new RuntimeException("Seccion no encontrada"));

        if (request.getNumero() != null) {
            seccion.setNumero(request.getNumero());
        }

        if (request.getNombre() != null) {
            seccion.setNombre(request.getNombre());
        }

        return seccionRepository.save(seccion);
    }

    public void cambiarOrden(Integer idSeccion, String direccion) {
        // buscamos la sección que queremos mover
        Seccion seccionActual = seccionRepository.findById(idSeccion).orElseThrow();

        // obtenemos todas las secciones ordenadas
        List<Seccion> todas = seccionRepository.findAll();
        todas.sort(Comparator.comparing(Seccion::getNumero));

        int indexActual = -1;

        // encontramos en qué posición de la lista está nuestra sección
        for (int i = 0; i < todas.size(); i++) {
            if (todas.get(i).getId_seccion().equals(idSeccion)) {
                indexActual = i;
                break;
            }
        }

        // calculamos el índice del "vecino" con el que vamos a intercambiar
        int indexVecino = -1;
        if ("SUBIR".equals(direccion) && indexActual > 0) {
            indexVecino = indexActual - 1; // El de arriba
        } else if ("BAJAR".equals(direccion) && indexActual < todas.size() - 1) {
            indexVecino = indexActual + 1; // El de abajo
        }

        // si encontramos un vecino válido, hacemos el intercambio
        if (indexVecino != -1) {
            Seccion vecino = todas.get(indexVecino);

            // guardamos los números actuales
            int numActual = seccionActual.getNumero();
            int numVecino = vecino.getNumero();

            // intercambiamos
            seccionActual.setNumero(numVecino);
            vecino.setNumero(numActual);

            // guardamos los cambios en la BD
            seccionRepository.save(seccionActual);
            seccionRepository.save(vecino);
        }
    }
}
