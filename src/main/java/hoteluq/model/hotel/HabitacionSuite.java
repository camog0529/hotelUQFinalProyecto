package hoteluq.model.hotel;

/**
 * Representa la suite de lujo del hotel, con comodidades premium de alta gama.
 */
public class HabitacionSuite extends Habitacion {

    public HabitacionSuite(String numero, double tarifaBase) {
        super(numero, TipoHabitacion.SUITE, tarifaBase);
    }

    @Override
    public String getCaracteristicas() {
        return "Habitación Suite: Cama King, Jacuzzi privado, Minibar premium, Balcón panorámico.";
    }
}