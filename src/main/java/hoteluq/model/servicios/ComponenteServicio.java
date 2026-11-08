package hoteluq.model.servicios;

/**
 * INTERFAZ COMPONENTE BASE:
 * Define el contrato uniforme y la abstracción obligatoria para todos los
 * servicios individuales y compuestos del ecosistema HotelUQ.
 * Permite que los clientes traten objetos simples y agrupaciones de forma idéntica.
 */
public interface ComponenteServicio {

    /**
     * Obtiene el nombre, descripción o el desglose detallado del servicio o del paquete contratado.
     * @return Cadena de texto con la descripción del servicio.
     */
    String getNombre();

    /**
     * Calcula el costo financiero total. En el caso de componentes simples devuelve
     * su precio base, y en componentes compuestos realiza una sumatoria polimórfica.
     * @return Valor decimal con el precio del servicio o paquete.
     */
    double getPrecio();
}