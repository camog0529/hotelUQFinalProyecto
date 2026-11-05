package hoteluq.repository;

import hoteluq.model.clientes.Cliente;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementación concreta del patrón de diseño de comportamiento <b>Iterator</b>.
 * <p>
 * Permite recorrer de manera secuencial la colección interna de clientes
 * centralizada en el repositorio de la aplicación sin exponer la estructura
 * subyacente (listas encadenadas, arreglos, etc.) al código exterior.
 * </p>
 *
 * @author Proyecto Programación II
 * @version 1.0
 */
public class IteradorClientes implements Iterator<Cliente> {

    // Referencia directa a la lista interna de clientes que se va a recorrer
    private final List<Cliente> clientes;

    // Indicador o puntero que realiza el seguimiento del índice actual del recorrido
    private int posicion = 0;

    /**
     * Construye un nuevo iterador enlazándolo con el listado de huéspedes provisto.
     *
     * @param clientes Lista de clientes de dominio sobre la cual iterar.
     */
    public IteradorClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    /**
     * Comprueba si aún quedan elementos pendientes por procesar en la lista.
     *
     * @return {@code true} si la posición actual es menor al tamaño de la colección;
     *         de lo contrario, {@code false}.
     */
    @Override
    public boolean hasNext() {
        return posicion < clientes.size();
    }

    /**
     * Retorna el siguiente elemento de la colección e incrementa el puntero de avance.
     *
     * @return Instancia de tipo {@link Cliente} apuntada en el índice actual.
     * @throws NoSuchElementException Si se invoca el método cuando ya no quedan
     *                                más elementos disponibles en la iteración.
     */
    @Override
    public Cliente next() {
        // Validación obligatoria previa para evitar desbordamientos de memoria
        if (!hasNext()) {
            throw new NoSuchElementException("No hay más clientes en la colección.");
        }
        // Retorna el cliente y realiza el post-incremento unitario de la variable física
        return clientes.get(posicion++);
    }
}
