package hoteluq.model.comportamiento;

import hoteluq.model.hotel.Habitacion;

/**
 * ESTRATEGIA CONCRETA: TEMPORADA BAJA
 * Ofrece un descuento comercial estratégico (ej. -10% o precio base puro) para incentivar
 * el volumen de reservas en periodos de baja afluencia turística.
 */
public class TarifaTemporadaBaja implements EstrategiaTarifa {

    /**
     * Calcula la tarifa aplicando un 10% de descuento directo sobre el costo base.
     */
    @Override
    public double calcularPrecioPorNoche(Habitacion habitacion) {
        if (habitacion == null) return 0.0;
        return habitacion.getTarifaBase() * 0.90;
    }
}