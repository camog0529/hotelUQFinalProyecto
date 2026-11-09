package hoteluq.model.creacionales;

import hoteluq.model.hotel.*;

/**
 * Implementación del patrón de diseño creacional <b>Factory Method</b>.
 * Centraliza y encapsula por completo la lógica de instanciación de habitaciones,
 * aislando a los clientes de las subclases concretas.
 * <p>
 * Este diseño cumple con el principio SOLID <i>Open/Closed Principle (OCP)</i>,
 * ya que añadir nuevos tipos de habitación no requiere modificar el código cliente,
 * únicamente expandir esta fábrica.
 */
public class HabitacionFactory {

    /**
     * Método de fábrica estático que evalúa el tipo solicitado y construye la
     * instancia de habitación correspondiente.
     *
     * @param numero Identificador de la habitación.
     * @param tipo El tipo de habitación deseado (ESTANDAR, FAMILIAR, SUITE).
     * @param tarifaBase Costo base por noche asignado por el administrador.
     * @return Una instancia concreta derivada de la clase abstracta {@link Habitacion}.
     * @throws IllegalArgumentException Si se solicita un tipo que no está mapeado en la lógica.
     */
    public static Habitacion crearHabitacion(String numero, TipoHabitacion tipo, double tarifaBase) {
        switch (tipo) {
            case ESTANDAR:
                return new HabitacionEstandar(numero, tarifaBase);
            case FAMILIAR:
                return new HabitacionFamiliar(numero, tarifaBase);
            case SUITE:
                return new HabitacionSuite(numero, tarifaBase);
            default:
                throw new IllegalArgumentException("Tipo de habitación no soportado por la plataforma.");
        }
    }
}