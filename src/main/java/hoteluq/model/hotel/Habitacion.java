package hoteluq.model.hotel;

/**
 * Clase abstracta que define la estructura y el comportamiento base de cualquier
 * habitación dentro del ecosistema de HotelUQ.
 */
public abstract class Habitacion {
    private final String numero;
    private final TipoHabitacion tipo;
    private final double tarifaBase;
    private boolean disponible;

    /**
     * Constructor general para inicializar las propiedades de una habitación.
     *
     * @param numero Identificador único físico de la habitación (ej. "301").
     * @param tipo Categoría correspondiente de la enumeración {@link TipoHabitacion}.
     * @param tarifaBase Costo estándar por noche de alojamiento.
     */
    public Habitacion(String numero, TipoHabitacion tipo, double tarifaBase) {
        if (numero == null || numero.isBlank() || tipo == null
                || !Double.isFinite(tarifaBase) || tarifaBase <= 0) {
            throw new IllegalArgumentException("La habitación requiere número, tipo y tarifa positiva válida.");
        }
        this.numero = numero;
        this.tipo = tipo;
        this.tarifaBase = tarifaBase;
        this.disponible = true;
    }

    /**
     * Método abstracto para obtener el desglose detallado de las comodidades.
     * Cada subclase concreta debe proveer su propia implementación.
     *
     * @return Cadena de texto con las amenidades específicas de la habitación.
     */
    public abstract String getCaracteristicas();

    // =========================================================================
    // MÉTODOS COMUNES DEL DOMINIO
    // =========================================================================

    public String getNumero() { return numero; }
    public TipoHabitacion getTipo() { return tipo; }
    public double getTarifaBase() { return tarifaBase; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
