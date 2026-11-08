package hoteluq.service;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.reserva.Reserva;

/**
 * Observador concreto encargado de la gestión de alertas y comunicaciones internas del sistema.
 * <p>
 * Simula el envío de correos electrónicos informativos y confirmaciones de itinerario
 * hacia el cliente de manera reactiva en el momento exacto en que se aprueba su compra.
 * </p>
 */
public class SistemaNotificaciones implements ObservadorPuntos {

    /**
     * Intercepta la confirmación de la reserva y genera la bitácora correspondiente
     * simulando el envío de la notificación al buzón de correo del huésped.
     */
    @Override
    public void actualizar(Reserva reserva) {
        if (reserva == null) return;

        Cliente cliente = reserva.getCliente();

        System.out.println("[NOTIFICACIÓN] Enviando correo electrónico a: " + cliente.getCorreo());
        System.out.println("Asunto: Confirmación de Reserva Exitosa - HotelUQ");
        System.out.println("Estimado(a) " + cliente.getNombre() + ", su reserva con ID: "
                + reserva.getIdReserva() + " para la habitación " + reserva.getHabitacion().getNumero()
                + " ha sido pagada y confirmada satisfactoriamente.");

        // Dejamos constancia en la bitácora del cliente de que se despachó la alerta
        cliente.registrarMovimiento("Notificación: Confirmación enviada a " + cliente.getCorreo());
    }
}