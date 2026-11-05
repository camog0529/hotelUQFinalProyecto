package hoteluq.service;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.reserva.Reserva;

/**
 * Observador concreto encargado de la gestión automatizada del programa de fidelización.
 * <p>
 * Reacciona al evento de confirmación de reserva calculando y asignando la acumulación
 * de puntos: 100 puntos por cada $100.000 pagados, de acuerdo a las reglas del hotel.
 * </p>
 */
public class SistemaPuntosFidelidad implements ObservadorPuntos {

    /**
     * Procesa la reserva confirmada, extrae el cliente y calcula la bonificación
     * monetaria acumulada para actualizar su saldo de puntos.
     */
    @Override
    public void actualizar(Reserva reserva) {
        if (reserva == null) return;

        Cliente cliente = reserva.getCliente();
        double valorPagado = reserva.getValorTotal();

        // Regla de negocio: Por cada $100.000 pagados, se obtienen 100 puntos
        int puntosGanados = ((int) valorPagado / 100000) * 100;

        if (puntosGanados > 0) {
            cliente.acumularPuntos(puntosGanados);
        }
    }
}
