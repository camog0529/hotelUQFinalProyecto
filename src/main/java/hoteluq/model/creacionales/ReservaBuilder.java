package hoteluq.model.creacionales;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.hotel.Habitacion;
import hoteluq.model.reserva.Reserva;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import hoteluq.model.excepciones.ReglaNegocioException;
import hoteluq.model.comportamiento.EstrategiaTarifa;
import hoteluq.model.servicios.ComponenteServicio;
import hoteluq.model.promociones.Promocion;

/**
 * Implementación del patrón de diseño creacional <b>Builder</b> para la clase {@link Reserva}.
 * Permite la construcción paso a paso de objetos Reserva complejos, garantizando que
 * no se generen instancias inconsistentes o que violen las reglas de negocio iniciales.
 */
public class ReservaBuilder {
    private String idReserva;
    private Cliente cliente;
    private Habitacion habitacion;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private final List<String> serviciosContratados = new ArrayList<>();
    private double costoServicios;
    private EstrategiaTarifa estrategiaTarifa = Habitacion::getTarifaBase;
    private boolean seguroCancelacion;
    private Promocion promocion;
    private double descuentoPromocional;
    private double valorTotal;

    /**
     * Inicializa el proceso del constructor asignando un identificador único a la transacción.
     *
     * @param idReserva Código único para la nueva reserva.
     */
    public ReservaBuilder(String idReserva) {
        this.idReserva = idReserva;
    }

    public ReservaBuilder conCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public ReservaBuilder paraHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
        return this;
    }

    /**
     * Configura el rango de fechas de la estadía y ejecuta la validación de negocio obligatoria.
     *
     * @param fechaEntrada Fecha de llegada al hotel.
     * @param fechaSalida Fecha de salida del hotel.
     * @return El mismo builder estructurado.
     * @throws IllegalArgumentException Si la fecha de salida es igual o anterior a la de entrada.
     */
    public ReservaBuilder enFechas(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null || !fechaSalida.isAfter(fechaEntrada)) {
            throw new ReglaNegocioException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        return this;
    }

    /**
     * Agrega un servicio individual al listado de la reserva y actualiza el costo acumulado.
     *
     * @param nombreServicio Nombre del servicio contratado (ej. "Spa", "Desayuno").
     * @param costoServicio Precio del servicio.
     * @return El mismo builder estructurado.
     */
    public ReservaBuilder agregarServicio(String nombreServicio, double costoServicio) {
        if (nombreServicio == null || nombreServicio.isBlank()
                || !Double.isFinite(costoServicio) || costoServicio < 0) {
            throw new ReglaNegocioException("El servicio debe tener nombre y costo válido.");
        }
        this.serviciosContratados.add(nombreServicio);
        this.costoServicios += costoServicio;
        return this;
    }

    public ReservaBuilder agregarServicio(ComponenteServicio servicio) {
        if (servicio == null) throw new ReglaNegocioException("El componente de servicio es obligatorio.");
        return agregarServicio(servicio.getNombre(), servicio.getPrecio());
    }

    public ReservaBuilder conSeguroCancelacion(boolean incluido) {
        this.seguroCancelacion = incluido;
        return this;
    }

    public ReservaBuilder conPromocion(Promocion promocion) {
        this.promocion = promocion;
        return this;
    }

    public ReservaBuilder conEstrategiaTarifa(EstrategiaTarifa estrategiaTarifa) {
        if (estrategiaTarifa == null) throw new IllegalArgumentException("La estrategia de tarifa es obligatoria.");
        this.estrategiaTarifa = estrategiaTarifa;
        return this;
    }

    /**
     * Ensambla, valida monetariamente y construye el objeto {@link Reserva} definitivo.
     * Realiza el cálculo automático de noches multiplicado por el costo base de la habitación.
     *
     * @return Una instancia completamente válida de {@link Reserva}.
     * @throws IllegalStateException Si faltan campos mandatorios como Cliente o Habitación.
     */
    public Reserva build() {
        if (idReserva == null || idReserva.isBlank() || cliente == null || habitacion == null || fechaEntrada == null || fechaSalida == null) {
            throw new IllegalStateException("No se puede construir la reserva. Faltan datos obligatorios.");
        }

        // Calcular costo por noches de hospedaje e integrarlo al costo de servicios previos
        long noches = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        double subtotal = noches * estrategiaTarifa.calcularPrecioPorNoche(habitacion) + costoServicios;
        if (promocion != null && !promocion.aplica(fechaEntrada, fechaSalida, habitacion.getTipo())) {
            throw new ReglaNegocioException("La promoción seleccionada no cumple las condiciones de la reserva.");
        }
        this.descuentoPromocional = promocion == null ? 0 : promocion.calcularDescuento(subtotal);
        this.valorTotal = subtotal - descuentoPromocional;

        return new Reserva(this);
    }

    // =========================================================================
    // GETTERS REQUERIDOS POR LA ENTIDAD RESERVA PARA EXTRAER LOS DATOS
    // =========================================================================

    public String getIdReserva() { return idReserva; }
    public Cliente getCliente() { return cliente; }
    public Habitacion getHabitacion() { return habitacion; }
    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public List<String> getServiciosContratados() { return serviciosContratados; }
    public double getValorTotal() { return valorTotal; }
    public boolean tieneSeguroCancelacion() { return seguroCancelacion; }
    public String getCodigoPromocion() { return promocion == null ? null : promocion.getCodigo(); }
    public double getDescuentoPromocional() { return descuentoPromocional; }
}
