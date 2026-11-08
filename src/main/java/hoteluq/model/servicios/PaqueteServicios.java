package hoteluq.model.servicios;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASE COMPUESTA (COMPOSITE):
 * Actúa como contenedor de otros componentes de tipo ComponenteServicio.
 * Puede almacenar tanto servicios individuales (Hojas) como otros paquetes (Sub-compuestos),
 * delegando el trabajo a sus hijos de forma totalmente recursiva.
 */
public class PaqueteServicios implements ComponenteServicio {

    // Nombre identificativo del paquete comercial (ej. "Plan Romántico")
    private final String nombrePaquete;

    // Lista polimórfica que almacena los servicios internos que conforman el paquete
    private final List<ComponenteServicio> componentes = new ArrayList<>();

    /**
     * Constructor para inicializar una oferta compuesta vacía.
     * @param nombrePaquete Nombre descriptivo de la promoción o combo de servicios.
     */
    public PaqueteServicios(String nombrePaquete) {
        this.nombrePaquete = nombrePaquete;
    }

    /**
     * Añade dinámicamente un servicio o subpaquete a la estructura compuesta.
     * @param componente Cualquier objeto que cumpla con el contrato ComponenteServicio.
     */
    public void agregarComponente(ComponenteServicio componente) {
        if (componente != null) {
            this.componentes.add(componente);
        }
    }

    /**
     * Elimina un componente específico de la colección interna del paquete.
     * @param componente Componente a remover de la estructura.
     */
    public void eliminarComponente(ComponenteServicio componente) {
        this.componentes.remove(componente);
    }

    /**
     * Construye una cadena descriptiva recorriendo dinámicamente
     * todos los componentes embebidos dentro de este paquete.
     */
    @Override
    public String getNombre() {
        StringBuilder sb = new StringBuilder(nombrePaquete).append(" (Contiene: ");
        for (int i = 0; i < componentes.size(); i++) {
            sb.append(componentes.get(i).getNombre());
            // Condicional estética para separar los nombres de los hijos con comas
            if (i < componentes.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * CÁLCULO RECURSIVO POLIMÓRFICO:
     * Recorre la colección completa sumando los precios de cada elemento.
     * Si un elemento interno es otra Hoja, sumará su valor; si es otro Composite,
     * este volverá a invocar internamente su propio método getPrecio() en cadena.
     */
    @Override
    public double getPrecio() {
        return componentes.stream()
                .mapToDouble(ComponenteServicio::getPrecio)
                .sum();
    }
}