package hoteluq.model.estructurales;

import hoteluq.model.clientes.Cliente;

/**
 * Componente Adaptador estructural (Pattern Adapter).
 * Adapta las operaciones de saldo de {@link Cliente} al contrato único
 * {@link ProcesadorPago} que consume el caso de uso de reservas.
 */
public class PasarelaPagoAdapter implements ProcesadorPago{

    /**
     * Verifica fondos y ejecuta el débito mediante la API de tarjeta del dominio.
     * @param cliente Huésped titular del pago.
     * @param monto Cobertura financiera requerida.
     * @return true si el pago se aprobó y se debitó.
     */
    @Override
    public boolean procesarPago(Cliente cliente, double monto) {
        if (cliente == null || !cliente.verificarSaldo(monto)) return false;
        cliente.descontarSaldo(monto);
        return true;
    }

}
