package hoteluq.model.comportamiento;

/**
 * INTERFAZ PATRÓN COMMAND:
 * Define el contrato estándar para encapsular peticiones financieras,
 * permitiendo ejecutar transacciones, auditorías y revertir cambios de forma limpia.
 */
public interface ComandoPago {
    /**
     * Ejecuta formalmente la operación financiera.
     */
    void ejecutar();

    /**
     * Deshace la operación financiera (Rollback) usando el patrón Memento.
     */
    void deshacer();
}