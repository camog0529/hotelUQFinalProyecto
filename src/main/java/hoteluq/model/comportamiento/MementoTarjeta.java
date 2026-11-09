package hoteluq.model.comportamiento;

/**
 * PATRÓN MEMENTO: El "Recuerdo".
 * Guarda instantáneas inmutables del estado financiero de la tarjeta virtual de un cliente
 * para soportar operaciones seguras de Rollback (reversión de saldo) ante fallos.
 */
public class MementoTarjeta {
    // Estado financiero encapsulado e inmutable
    private final double saldoGuardado;

    /**
     * Captura el saldo de la tarjeta virtual en un momento específico.
     * @param saldo El monto actual a respaldar.
     */
    public MementoTarjeta(double saldo) {
        this.saldoGuardado = saldo;
    }

    /**
     * Obtiene el saldo respaldado para restaurarlo cuando sea requerido.
     * @return El saldo financiero en caché.
     */
    public double getSaldoGuardado() {
        return this.saldoGuardado;
    }
}