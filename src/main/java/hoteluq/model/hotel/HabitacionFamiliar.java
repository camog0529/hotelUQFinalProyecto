package hoteluq.model.hotel;

/**
 * Representa una habitación adaptada para grupos familiares.
 */
public class HabitacionFamiliar extends Habitacion {

    public HabitacionFamiliar(String numero, double tarifaBase) {
        super(numero, TipoHabitacion.FAMILIAR, tarifaBase);
    }

    @Override
    public String getCaracteristicas() {
        return "Habitación Familiar: 2 Camas Dobles, Vista exterior, Smart TV, Área de estar.";
    }
}