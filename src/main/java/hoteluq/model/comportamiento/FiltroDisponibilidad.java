package hoteluq.model.comportamiento;

import hoteluq.model.hotel.Habitacion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ESLABÓN CONCRETO: FILTRO DE DISPONIBILIDAD TEMPORAL
 * Revisa el estado físico de la habitación y se asegura de que cumpla con las
 * reglas de negocio (ej. que no esté marcada como ocupada o reservada).
 */
public class FiltroDisponibilidad extends ManejadorFiltro {

    /**
     * Filtra las habitaciones que se encuentran físicamente libres y operativas.
     */
    @Override
    public List<Habitacion> filtrar(List<Habitacion> habitaciones, LocalDate entrada, LocalDate salida) {
        List<Habitacion> disponibles = new ArrayList<>();

        // Validación de Regla de Negocio elemental (Página 5)
        if (entrada == null || salida == null || salida.isBefore(entrada) || salida.isEqual(entrada)) {
            return disponibles; // Devuelve lista vacía si las fechas son inconsistentes
        }

        // Evaluamos polimórficamente cada habitación de la lista
        for (Habitacion hab : habitaciones) {
            if (hab.isDisponible()) { // Regla básica: El recurso debe estar marcado disponible
                disponibles.add(hab);
            }
        }

        // Delegamos la lista purificada al siguiente eslabón de la cadena (ej. Filtro por Precio)
        return invocarSiguiente(disponibles, entrada, salida);
    }
}