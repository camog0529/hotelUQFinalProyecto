package hoteluq.model.estructurales;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.comportamiento.ComandoPago;
import hoteluq.model.comportamiento.ComandoProcesarPago;
import hoteluq.model.seguridad.RolUsuario;
import hoteluq.service.SesionUsuario;

/** Verifica el permiso administrativo antes de delegar una recarga al comando de pago. */
public class ProxyRecargas {
    public void solicitarRecargaSaldo(Cliente cliente, double monto) {
        if (cliente == null) throw new IllegalArgumentException("El cliente es obligatorio.");
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        ComandoPago comando = new ComandoProcesarPago(cliente, monto, true);
        comando.ejecutar();
    }
}