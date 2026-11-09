package hoteluq.model.comportamiento;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.reserva.Reserva;
import hoteluq.repository.HotelUQData;
import hoteluq.service.ObservadorPuntos;
import hoteluq.service.SistemaNotificaciones;
import hoteluq.service.SistemaPuntosFidelidad;
import hoteluq.model.estructurales.ProcesadorPago;
import hoteluq.model.estructurales.PasarelaPagoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del patrón de diseño de comportamiento <b>Mediator (Mediador)</b>.
 * <p>
 * Esta clase centraliza y coordina la lógica compleja asociada al ciclo de confirmación
 * y pago de una reserva. Actúa como el intermediario absoluto, evitando el acoplamiento
 * directo entre la persistencia de datos, las cuentas financieras de los clientes
 * y los sistemas reactivos de notificaciones y puntos.
 * </p>
 *
 * @author Proyecto Programación II
 * @version 1.0
 */
public class MediadorReservas {

    // Referencia al repositorio Singleton para almacenar transacciones exitosas
    private final HotelUQData baseDatos;

    // Colección de observadores encargados de reaccionar a la confirmación de reservas
    private final List<ObservadorPuntos> observadores = new ArrayList<>();
    private final ProcesadorPago procesadorPago;

    /**
     * Constructor del mediador de reservas.
     * Vincula la base de datos simulada y registra automáticamente a los observadores
     * del sistema para desplegar la reactividad nativa.
     */
    public MediadorReservas() {
        this(new PasarelaPagoAdapter());
    }

    public MediadorReservas(ProcesadorPago procesadorPago) {
        if (procesadorPago == null) throw new IllegalArgumentException("El procesador de pago es obligatorio.");
        this.procesadorPago = procesadorPago;
        this.baseDatos = HotelUQData.getInstancia();

        // Registro de los observadores (Patrón Observer) vinculados al ciclo de vida
        registrarObservador(new SistemaPuntosFidelidad());
        registrarObservador(new SistemaNotificaciones());
    }

    /**
     * Permite inscribir dinámicamente nuevos escuchas de eventos en el sistema.
     *
     * @param observador Instancia que implementa la interfaz {@link ObservadorPuntos}.
     */
    public void registrarObservador(ObservadorPuntos observador) {
        if (observador != null) {
            this.observadores.add(observador);
        }
    }

    /**
     * Orquesta el flujo completo de confirmación de una reserva de forma centralizada.
     * <p>
     * Lógica coordinada:
     * 1. Verifica la disponibilidad física de la habitación en las colecciones.
     * 2. Valida la solvencia económica y el estado de la tarjeta virtual del cliente.
     * 3. Ejecuta de forma segura el descuento del saldo financiero.
     * 4. Almacena la reserva en la bitácora general del hotel.
     * 5. Dispara las notificaciones reactivas a todos los observadores registrados.
     * </p>
     *
     * @param reserva Instancia compleja construida previamente mediante el Builder.
     * @throws IllegalStateException Si la habitación no está libre o el cliente no posee fondos.
     */
    public synchronized void procesarConfirmacionReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La estructura de la reserva no puede ser nula.");
        }
        if (!reserva.estaPendiente()) {
            throw new IllegalStateException("La reserva ya fue procesada y no puede volver a cobrarse.");
        }

        Cliente cliente = reserva.getCliente();

        // 1. Evita cruces de fechas; una habitación puede volver a reservarse
        // en periodos distintos aunque haya tenido una reserva anterior.
        boolean periodoOcupado = baseDatos.getReservas().stream().anyMatch(existente ->
                existente.getHabitacion().getNumero().equals(reserva.getHabitacion().getNumero())
                        && "Confirmada".equalsIgnoreCase(existente.getEstado())
                        && reserva.getFechaEntrada().isBefore(existente.getFechaSalida())
                        && existente.getFechaEntrada().isBefore(reserva.getFechaSalida()));
        if (periodoOcupado) {
            throw new IllegalStateException("La habitación ya tiene una reserva confirmada para parte de esas fechas.");
        }

        // 2. Validación de saldo en la tarjeta virtual antes de la confirmación
        if (!procesadorPago.procesarPago(cliente, reserva.getValorTotal())) {
            throw new IllegalStateException("Regla de Negocio: Saldo insuficiente o tarjeta virtual inactiva.");
        }

        // 4. Actualización del estado físico del recurso de hospedaje e inserción en base de datos
        reserva.confirmarTrasPago();
        reserva.getHabitacion().setDisponible(false);
        baseDatos.agregarReserva(reserva);

        // 5. Despacho y notificación masiva a los observadores registrados (Patrón Observer)
        notificarObservadores(reserva);
    }

    /**
     * Recorre secuencialmente todos los escuchas registrados para detonar sus comportamientos específicos.
     *
     * @param reserva Instancia de la reserva confirmada de forma exitosa.
     */
    private void notificarObservadores(Reserva reserva) {
        for (ObservadorPuntos observador : observadores) {
            observador.actualizar(reserva);
        }
    }
}
