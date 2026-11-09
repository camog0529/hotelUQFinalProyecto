package hoteluq.model.comportamiento;

import hoteluq.model.reserva.Reserva;
import hoteluq.model.hotel.TipoHabitacion;
import java.util.List;

/**
 * Visitante Concreto enfocado en el cálculo de métricas y estadísticas operacionales.
 * Suministra los datos crudos para las visualizaciones gráficas de JavaFX (Sección 7.2).
 */
public class VisitanteEstadisticas {

    /**
     * Calcula el porcentaje general de ocupación del hotel.
     * @param totalHabitacionesHotel Cantidad física de habitaciones registradas.
     * @param reservasActivas Cantidad de reservas confirmadas actualmente.
     */
    public double calcularPorcentajeOcupacion(int totalHabitacionesHotel, long reservasActivas) {
        if (totalHabitacionesHotel == 0) return 0.0;
        return ((double) reservasActivas / totalHabitacionesHotel) * 100.0;
    }

    /**
     * Consolida la cantidad de reservas totales efectuadas para un Tipo de Habitación específico.
     */
    public int contarReservasPorTipo(List<Reserva> reservas, TipoHabitacion tipo) {
        int contador = 0;
        for (Reserva r : reservas) {
            if (r.getHabitacion().getTipo() == tipo && "Confirmada".equalsIgnoreCase(r.getEstado())) {
                contador++;
            }
        }
        return contador;
    }
}