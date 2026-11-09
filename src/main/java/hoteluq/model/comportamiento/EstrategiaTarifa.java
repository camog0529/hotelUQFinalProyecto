package hoteluq.model.comportamiento;

import hoteluq.model.hotel.Habitacion;

/**
 * INTERFAZ PATRÓN STRATEGY:
 * Define el contrato común para los diferentes algoritmos de cálculo de precios
 * y tarifas de hospedaje dentro del hotel, variando según la temporada comercial.
 */
public interface EstrategiaTarifa {

    /**
     * Calcula el costo final por noche basándose en el tipo de habitación y su tarifa base.
     * @param habitacion Instancia de la habitación a la que se aplicará el cálculo.
     * @return El costo final ajustado por noche.
     */
    double calcularPrecioPorNoche(Habitacion habitacion);
}