package hoteluq.model.hotel;

/**
 * Representa una habitación de categoría económica/estándar.
 */
public class HabitacionEstandar extends Habitacion {

    public HabitacionEstandar(String numero, double tarifaBase) {
        super(numero, TipoHabitacion.ESTANDAR, tarifaBase);
    }

    @Override
    public String getCaracteristicas() {
        return "Habitación Estándar: 1 Cama Queen, WiFi de alta velocidad, Baño privado.";
    }
}