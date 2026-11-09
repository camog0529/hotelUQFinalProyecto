package hoteluq.model.clientes;

import java.time.LocalDate;

/** Lote de puntos con vencimiento individual para redimir primero los próximos a vencer. */
public final class LotePuntos {
    private final LocalDate fechaObtencion;
    private final LocalDate fechaVencimiento;
    private int puntosDisponibles;

    public LotePuntos(int puntos, LocalDate fechaObtencion) {
        if (puntos <= 0 || fechaObtencion == null) {
            throw new IllegalArgumentException("El lote requiere puntos positivos y una fecha válida.");
        }
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaObtencion.plusYears(1);
        this.puntosDisponibles = puntos;
    }

    public LocalDate getFechaObtencion() { return fechaObtencion; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public int getPuntosDisponibles() { return puntosDisponibles; }
    public boolean estaVigente(LocalDate fecha) { return !fecha.isAfter(fechaVencimiento); }

    public void consumir(int puntos) {
        if (puntos < 0 || puntos > puntosDisponibles) {
            throw new IllegalArgumentException("El consumo excede los puntos del lote.");
        }
        puntosDisponibles -= puntos;
    }
}
