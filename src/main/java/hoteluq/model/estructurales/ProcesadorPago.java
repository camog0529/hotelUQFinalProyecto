package hoteluq.model.estructurales;

import hoteluq.model.clientes.Cliente;

/** Contrato de cobro que usa el caso de uso de reservas. */
public interface ProcesadorPago {
    boolean procesarPago(Cliente cliente, double monto);
}