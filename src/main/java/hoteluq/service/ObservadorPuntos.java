package hoteluq.service;
import hoteluq.model.reserva.Reserva;

/**
 * Interfaz que define el contrato base para el patrón de diseño de comportamiento <b>Observer</b>.
 * <p>
 * Permite que cualquier clase que implemente esta interfaz se registre ante un sujeto
 * y reaccione de manera inmediata ante la confirmación exitosa de una reserva en el hotel.
 * </p>
 *
 * @author Proyecto Programación II
 * @version 1.0
 */
public interface ObservadorPuntos {

    /**
     * Método de notificación activado automáticamente cuando una transacción financiera
     * o reserva es consolidada con éxito en el ecosistema.
     *
     * @param reserva Instancia de la reserva que generó el evento.
     */
    void actualizar(Reserva reserva);
}
