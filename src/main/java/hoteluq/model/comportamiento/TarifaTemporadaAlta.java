package hoteluq.model.comportamiento;

import hoteluq.model.hotel.Habitacion;

/**
 * ESTRATEGIA CONCRETA: TEMPORADA ALTA
 * Aplica un recargo o incremento porcentual (ej. +30%) a la tarifa base de la habitación
 * debido a la alta demanda del mercado hotelero en fechas específicas.
 */
public class TarifaTemporadaAlta implements EstrategiaTarifa {

    /**
     * Calcula la tarifa incrementando un 30% el valor base de la habitación de forma polimórfica.
     */
    @Override
    public double calcularPrecioPorNoche(Habitacion habitacion) {
        if (habitacion == null) return 0.0;
        return habitacion.getTarifaBase() * 1.30;
    }
}