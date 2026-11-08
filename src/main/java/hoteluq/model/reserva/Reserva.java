package hoteluq.model.reserva;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.hotel.Habitacion;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * ENTIDAD PRINCIPAL DEL DOMINIO CON PATRÓN STATE INTEGRADO:
 * Mantiene la trazabilidad de la reserva y controla las transiciones de estado
 * (Confirmada, Cancelada, Finalizada) aplicando estrictamente las reglas de negocio
 * financieras de reembolsos según la anticipación temporal del cliente.
 */
public class Reserva {
    private final String idReserva;
    private final Cliente cliente;
    private final Habitacion habitacion;
    private final LocalDate fechaEntrada;
    private final LocalDate fechaSalida;
    private final List<String> serviciosContratados;
    private final double valorTotal;
    private final boolean seguroCancelacion;
    private final String codigoPromocion;
    private final double descuentoPromocional;

    // ATRIBUTO DE ESTADO (Patrón State): Modifica el comportamiento de los flujos de negocio
    private String estado;

    /**
     * Constructor utilizado por el patrón ReservaBuilder.
     */
    public Reserva(hoteluq.model.creacionales.ReservaBuilder builder) {
        this.idReserva = builder.getIdReserva();
        this.cliente = builder.getCliente();
        this.habitacion = builder.getHabitacion();
        this.fechaEntrada = builder.getFechaEntrada();
        this.fechaSalida = builder.getFechaSalida();
        this.serviciosContratados = new ArrayList<>(builder.getServiciosContratados());
        this.valorTotal = builder.getValorTotal();
        this.seguroCancelacion = builder.tieneSeguroCancelacion();
        this.codigoPromocion = builder.getCodigoPromocion();
        this.descuentoPromocional = builder.getDescuentoPromocional();
        this.estado = "Pendiente"; // El mediador confirma el estado solo después de aprobar el pago.
    }

    /**
     * LOGICA DE COMPORTAMIENTO SEGÚN EL ESTADO: CANCELACIÓN Y REEMBOLSOS
     * Cumple de forma estricta la regla de negocio del Hotel (Página 5 de la guía):
     * - Más de 72 horas: Reembolsa 100%
     * - Entre 24 y 72 horas: Reembolsa 80%
     * - Menos de 24 horas: 0% Devolución
     *
     * @param horasAnticipacion Cantidad de horas calculadas que faltan para la fecha de check-in.
     * @throws IllegalStateException Si la reserva no se encuentra en estado Confirmada.
     */
    private void solicitarCancelacionCliente(long horasAnticipacion) {
        // Validación de Estado previo: Solo se puede cancelar lo que está activo y pagado
        if (!"Confirmada".equalsIgnoreCase(this.estado)) {
            throw new IllegalStateException("Transición de Estado inválida: No se puede cancelar una reserva en estado: " + this.estado);
        }

        double porcentajeDevolucion = 0.0;

        // Evaluación de umbrales temporales (Reglas de Negocio del hotel)
        if (seguroCancelacion) {
            porcentajeDevolucion = 1.0;
        } else if (horasAnticipacion > 72) {
            porcentajeDevolucion = 1.0;  // 100% de reembolso
        } else if (horasAnticipacion >= 24) {
            porcentajeDevolucion = 0.8;  // 80% de reembolso
        } else {
            porcentajeDevolucion = 0.0;  // 0% de reembolso (Penalización total)
        }

        // Ejecución financiera del reembolso si aplica
        double montoReembolso = this.valorTotal * porcentajeDevolucion;
        if (montoReembolso > 0) {
            this.cliente.recargarTarjeta(montoReembolso); // Todo reembolso regresa automáticamente a la tarjeta virtual
            this.cliente.registrarMovimiento("Reembolso por cancelación (" + (porcentajeDevolucion * 100) + "%): +$" + montoReembolso);
        } else {
            this.cliente.registrarMovimiento("Cancelación sin derecho a devolución por penalización horaria. ID: " + this.idReserva);
        }

        // Transición de Estado exitosa
        this.estado = "Cancelada";
        this.habitacion.setDisponible(true); // Se libera físicamente el recurso de alojamiento
    }

    /** Cancela calculando las horas reales hasta el inicio de la estadía. */
    public void solicitarCancelacionCliente(LocalDateTime momentoSolicitud) {
        if (momentoSolicitud == null) {
            throw new IllegalArgumentException("El momento de solicitud es obligatorio.");
        }
        LocalDateTime inicioEstadia = fechaEntrada.atStartOfDay();
        if (!momentoSolicitud.isBefore(inicioEstadia)) {
            throw new IllegalStateException("La reserva no puede cancelarse después de la fecha de entrada.");
        }
        long horasAnticipacion = ChronoUnit.HOURS.between(momentoSolicitud, inicioEstadia);
        solicitarCancelacionCliente(horasAnticipacion);
    }

    /**
     * CANCELACIÓN INCONDICIONAL POR PARTE DEL HOTEL
     * Regla de negocio: Si el hotel cancela, siempre devuelve el 100% del valor pagado.
     */
    public void cancelarPorElHotel() {
        if (!"Confirmada".equalsIgnoreCase(this.estado)) {
            throw new IllegalStateException("No se puede revocar una reserva que no esté Confirmada.");
        }

        // Reembolso incondicional y absoluto del 100%
        this.cliente.recargarTarjeta(this.valorTotal);
        this.cliente.registrarMovimiento("Cancelación forzada por el HotelUQ (100% Reembolso): +$" + this.valorTotal);

        // Transición final de estado
        this.estado = "Cancelada";
        this.habitacion.setDisponible(true);
    }

    /**
     * Calcula la cantidad exacta de noches de hospedaje de forma nativa.
     */
    public long getCantidadNoches() {
        return ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
    }

    public boolean estaPendiente() {
        return "Pendiente".equalsIgnoreCase(estado);
    }

    public void confirmarTrasPago() {
        if (!estaPendiente()) {
            throw new IllegalStateException("Solo una reserva pendiente puede confirmarse.");
        }
        this.estado = "Confirmada";
    }

    // --- GETTERS Y SETTERS REQUERIDOS PARA BINDING EN JAVAFX Y GENERACIÓN DE REPORTES ---
    public String getIdReserva() { return idReserva; }
    public Cliente getCliente() { return cliente; }
    public Habitacion getHabitacion() { return habitacion; }
    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public List<String> getServiciosContratados() { return new ArrayList<>(serviciosContratados); }
    public double getValorTotal() { return valorTotal; }
    public boolean tieneSeguroCancelacion() { return seguroCancelacion; }
    public String getCodigoPromocion() { return codigoPromocion; }
    public double getDescuentoPromocional() { return descuentoPromocional; }
    public String getEstado() { return estado; }
}
