package hoteluq.model.servicios;

/**
 * CLASE DECORADORA ABSTRACTA (DECORATOR BASE):
 * Implementa la interfaz del componente y mantiene una referencia interna (agregación)
 * al objeto que va a envolver. Sirve como la plantilla estructural común para
 * inyectar responsabilidades adicionales a los servicios sin alterar su código nativo.
 */
public abstract class ServicioDecorador implements ComponenteServicio {

    // Referencia protegida al componente envuelto (puede ser un ServicioIndividual o PaqueteServicios)
    protected final ComponenteServicio servicioDecorado;

    /**
     * Constructor que asocia obligatoriamente el componente que será modificado.
     * @param servicioDecorado Instancia del servicio original a envolver.
     */
    protected ServicioDecorador(ComponenteServicio servicioDecorado) {
        this.servicioDecorado = servicioDecorado;
    }

    /**
     * Delega la obtención del nombre directamente al objeto envuelto.
     */
    @Override
    public String getNombre() {
        return servicioDecorado.getNombre();
    }

    /**
     * Delega la obtención del coste financiero directamente al objeto envuelto.
     */
    @Override
    public double getPrecio() {
        return servicioDecorado.getPrecio();
    }
}