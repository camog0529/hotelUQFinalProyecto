package hoteluq.model.comportamiento;

import hoteluq.model.clientes.Cliente;

/**
 * COMANDO CONCRETO:
 * Encapsula la lógica de negocio y la acción transaccional de realizar un cobro
 * o recarga sobre el balance financiero de la tarjeta virtual de un huésped.
 */
public class ComandoProcesarPago implements ComandoPago {
    private final Cliente cliente;
    private final double monto;
    private final boolean esRecarga; // true para recargar, false para debitar/pagar
    private MementoTarjeta instantaneaPrevio; // El respaldo para el Rollback

    public ComandoProcesarPago(Cliente cliente, double monto, boolean esRecarga) {
        this.cliente = cliente;
        this.monto = monto;
        this.esRecarga = esRecarga;
    }

    @Override
    public void ejecutar() {
        // 1. Tomamos una foto del saldo actual usando Memento por si el flujo falla después
        this.instantaneaPrevio = new MementoTarjeta(cliente.getSaldoTarjeta());

        // 2. Ejecutamos de forma condicional la lógica polimórfica según la naturaleza del comando
        if (esRecarga) {
            cliente.recargarTarjeta(monto);
        } else {
            cliente.descontarSaldo(monto);
        }
    }

    @Override
    public void deshacer() {
        if (instantaneaPrevio != null) {
            // Restauramos el saldo al estado exacto que tenía antes mediante el Memento
            // Para hacer esto de manera limpia, se inyectaría un método de restauración en el cliente o mediante el mediador.
            cliente.restaurarSaldoTarjeta(instantaneaPrevio.getSaldoGuardado());
            instantaneaPrevio = null;
        }
    }
}
