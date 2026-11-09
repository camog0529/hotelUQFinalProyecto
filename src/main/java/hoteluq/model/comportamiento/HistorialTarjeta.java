package hoteluq.model.comportamiento;

import java.util.Stack;

/**
 * Clase Caretaker (Conserje) del patrón Memento.
 * Administra y resguarda el historial de estados financieros inmutables de la tarjeta virtual.
 */
public class HistorialTarjeta {

    // Estructura de pila (LIFO) idónea para operaciones de deshacer consecutivas
    private final Stack<MementoTarjeta> estadosPrevios = new Stack<>();

    /**
     * Guarda una nueva instantánea del balance en la pila de auditoría.
     * @param memento Estado inmutable capturado.
     */
    public void guardarEstado(MementoTarjeta memento) {
        if (memento != null) {
            estadosPrevios.push(memento);
        }
    }

    /**
     * Extrae el último estado financiero válido para efectuar una reversión segura.
     * @return El Memento más reciente o null si no hay registros previos.
     */
    public MementoTarjeta obtenerUltimoEstado() {
        if (!estadosPrevios.isEmpty()) {
            return estadosPrevios.pop();
        }
        return null;
    }

    /**
     * Devuelve la cantidad de respaldos en caché.
     */
    public int getCantidadRespaldos() {
        return estadosPrevios.size();
    }
}