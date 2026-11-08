package hoteluq.model.servicios;

/**
 * DECORADOR CONCRETO:
 * Extiende las capacidades del decorador base agregando un coste fijo y modificando
 * la salida del nombre. Es ideal para modelar reglas del negocio dinámicas en HotelUQ,
 * como la contratación opcional de un "Seguro de Cancelación Flexible" sobre un servicio.
 */
public class DecoradorSeguroCancelacion extends ServicioDecorador {

    // Costo adicional específico que introduce este decorador concreto
    private final double costoSeguro;

    /**
     * Constructor para estructurar la decoración dinámica sobre un componente base.
     * @param servicioDecorado El servicio o paquete que recibirá la protección.
     * @param costoSeguro El incremento monetario por añadir la cobertura de cancelación.
     */
    public DecoradorSeguroCancelacion(ComponenteServicio servicioDecorado, double costoSeguro) {
        super(servicioDecorado); // Invoca al constructor de la clase abstracta base
        this.costoSeguro = costoSeguro;
    }

    /**
     * Obtiene el nombre del servicio interno original y le concatena de
     * manera dinámica la etiqueta de protección del seguro contratado.
     */
    @Override
    public String getNombre() {
        return super.getNombre() + " + [Seguro de Cancelación Protegido]";
    }

    /**
     * Ejecuta el cálculo financiero extrayendo primero el precio del componente
     * base envuelto (sea simple o paquete) y sumándole el recargo del seguro.
     */
    @Override
    public double getPrecio() {
        return super.getPrecio() + this.costoSeguro;
    }
}