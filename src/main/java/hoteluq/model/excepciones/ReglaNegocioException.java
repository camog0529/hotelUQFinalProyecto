package hoteluq.model.excepciones;

/** Excepción de dominio para operaciones que violan una regla de HotelUQ. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
