package hoteluq.model.servicios;

/**
 * CLASE HOJA (LEAF):
 * Representa un servicio único, simple e indivisible que ofrece el hotel
 * (por ejemplo: Desayuno, Parqueadero, Spa o Transporte).
 * No contiene colecciones ni hijos, implementando el comportamiento final directo.
 */
public class ServicioIndividual implements ComponenteServicio {

    // Atributos finales para encapsular los datos básicos del servicio
    private final String nombre;
    private final double precio;

    /**
     * Constructor para inicializar un servicio individual con su tarifa fija.
     * @param nombre Descripción física del servicio (ej. "Acceso a Spa").
     * @param precio Costo asignado a este servicio específico.
     */
    public ServicioIndividual(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    /**
     * Implementación polimórfica que devuelve el nombre directo del servicio.
     */
    @Override
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Implementación polimórfica que devuelve el precio asignado sin modificaciones.
     */
    @Override
    public double getPrecio() {
        return this.precio;
    }
}
