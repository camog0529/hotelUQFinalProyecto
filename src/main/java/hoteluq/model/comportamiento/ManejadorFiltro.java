package hoteluq.model.comportamiento;

import hoteluq.model.hotel.Habitacion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CLASE MANEJADORA BASE (CHAIN OF RESPONSIBILITY):
 * Define la estructura para enlazar los eslabones de filtrado de habitaciones.
 * Cada filtro procesa la lista y delega el resultado purificado al siguiente eslabón.
 */
public abstract class ManejadorFiltro {

    // Referencia al siguiente eslabón de la cadena de responsabilidad
    protected ManejadorFiltro siguienteManejador;

    /**
     * Enlaza el siguiente filtro en la cadena de ejecución.
     * @param siguiente El próximo manejador de la secuencia.
     * @return Devuelve el propio manejador para permitir una sintaxis fluida (Fluent API).
     */
    public ManejadorFiltro enlaceSiguiente(ManejadorFiltro siguiente) {
        this.siguienteManejador = siguiente;
        return siguiente;
    }

    /**
     * Método abstracto que ejecutará cada filtro concreto para purificar las habitaciones.
     * @param habitaciones Lista de habitaciones que entran al eslabón actual.
     * @param entrada Fecha de check-in seleccionada por el cliente.
     * @param salida Fecha de check-out seleccionada por el cliente.
     * @return Lista filtrada de habitaciones disponibles.
     */
    public abstract List<Habitacion> filtrar(List<Habitacion> habitaciones, LocalDate entrada, LocalDate salida);

    /**
     * Pasa la lista purificada de habitaciones al siguiente eslabón de la cadena si existe.
     */
    protected List<Habitacion> invocarSiguiente(List<Habitacion> filtradas, LocalDate entrada, LocalDate salida) {
        if (this.siguienteManejador != null) {
            return this.siguienteManejador.filtrar(filtradas, entrada, salida);
        }
        return filtradas;
    }
}