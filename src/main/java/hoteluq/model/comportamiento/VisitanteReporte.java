package hoteluq.model.comportamiento;

import hoteluq.model.reserva.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Visitante Concreto enfocado en la estructuración de Reportes Exportables (Texto).
 * Cumple con los requerimientos obligatorios de la Sección 7.1 de la guía.
 */
public class VisitanteReporte {

    /**
     * Procesa y genera la estructura formal de un reporte de ingresos operacionales.
     * @param reservas Lista de reservas consolidadas en el repositorio global.
     * @return Cadena formateada de manera organizada.
     */
    public String generarReporteTextoIngresos(List<Reserva> reservas) {
        StringBuilder sb = new StringBuilder();
        double totalIngresos = 0.0;

        sb.append("==================================================\n");
        sb.append("         HOTELUQ - REPORTE DE INGRESOS OPERATIVOS  \n");
        sb.append("==================================================\n");
        sb.append("Fecha Generación: ").append(LocalDateTime.now()).append("\n");
        sb.append("Registros Encontrados: ").append(reservas.size()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format(Locale.forLanguageTag("es-CO"), "%-12s %-12s %-15s %-10s\n", "ID Reserva", "Cliente ID", "Estado", "Total pagado"));
        sb.append("--------------------------------------------------\n");

        for (Reserva r : reservas) {
            sb.append(String.format(Locale.forLanguageTag("es-CO"), "%-12s %-12s %-15s $%-10.2f\n",
                    r.getIdReserva(),
                    r.getCliente().getCedula(),
                    r.getEstado(),
                    r.getValorTotal()));

            if ("Confirmada".equalsIgnoreCase(r.getEstado())) {
                totalIngresos += r.getValorTotal();
            }
        }

        sb.append("--------------------------------------------------\n");
        sb.append(String.format(Locale.forLanguageTag("es-CO"), "TOTAL INGRESOS REGISTRADOS: $%.2f\n", totalIngresos));
        sb.append("==================================================\n");

        return sb.toString();
    }
}
