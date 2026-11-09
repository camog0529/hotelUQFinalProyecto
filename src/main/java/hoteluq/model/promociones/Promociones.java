package hoteluq.model.promociones;

import hoteluq.model.hotel.TipoHabitacion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/** Descuento configurable con código, fechas, tipo de habitación y mínimo de noches. */
public class Promocion {
    private final String id;
    private String codigo;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private TipoHabitacion tipoHabitacion;
    private int nochesMinimas;
    private double porcentajeDescuento;
    private boolean activa;

    public Promocion(String codigo, String descripcion, LocalDate fechaInicio, LocalDate fechaFin,
                     TipoHabitacion tipoHabitacion, int nochesMinimas, double porcentajeDescuento, boolean activa) {
        this(UUID.randomUUID().toString(), codigo, descripcion, fechaInicio, fechaFin,
                tipoHabitacion, nochesMinimas, porcentajeDescuento, activa);
    }

    private Promocion(String id, String codigo, String descripcion, LocalDate fechaInicio, LocalDate fechaFin,
                      TipoHabitacion tipoHabitacion, int nochesMinimas, double porcentajeDescuento, boolean activa) {
        this.id = id;
        actualizar(codigo, descripcion, fechaInicio, fechaFin, tipoHabitacion, nochesMinimas, porcentajeDescuento, activa);
    }

    public void actualizar(String codigo, String descripcion, LocalDate fechaInicio, LocalDate fechaFin,
                           TipoHabitacion tipoHabitacion, int nochesMinimas, double porcentajeDescuento, boolean activa) {
        if (codigo == null || codigo.isBlank() || descripcion == null || descripcion.isBlank()
                || fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)
                || nochesMinimas < 1 || !Double.isFinite(porcentajeDescuento)
                || porcentajeDescuento <= 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("Revisa el código, las fechas, las noches y el porcentaje de descuento.");
        }
        this.codigo = codigo.trim().toUpperCase(java.util.Locale.ROOT);
        this.descripcion = descripcion.trim();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipoHabitacion = tipoHabitacion;
        this.nochesMinimas = nochesMinimas;
        this.porcentajeDescuento = porcentajeDescuento;
        this.activa = activa;
    }

    public boolean aplica(LocalDate entrada, LocalDate salida, TipoHabitacion tipo) {
        if (!activa || entrada == null || salida == null || !salida.isAfter(entrada)) return false;
        long noches = ChronoUnit.DAYS.between(entrada, salida);
        return !entrada.isBefore(fechaInicio) && !salida.isAfter(fechaFin)
                && (tipoHabitacion == null || tipoHabitacion == tipo)
                && noches >= nochesMinimas;
    }

    public double calcularDescuento(double subtotal) {
        return subtotal * porcentajeDescuento / 100.0;
    }

    public String getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public TipoHabitacion getTipoHabitacion() { return tipoHabitacion; }
    public int getNochesMinimas() { return nochesMinimas; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public boolean isActiva() { return activa; }

    @Override
    public String toString() {
        return codigo + " — " + descripcion + " (" + porcentajeDescuento + "% descuento)";
    }
}
