package hoteluq.service;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.seguridad.CuentaUsuario;
import hoteluq.model.seguridad.RolUsuario;

/** Sesión del usuario autenticado durante la ejecución de JavaFX. */
public final class SesionUsuario {
    private static SesionUsuario actual;
    private final CuentaUsuario cuenta;
    private final Cliente cliente;

    private SesionUsuario(CuentaUsuario cuenta, Cliente cliente) {
        this.cuenta = cuenta;
        this.cliente = cliente;
    }

    public static synchronized void iniciar(CuentaUsuario cuenta, Cliente cliente) {
        actual = new SesionUsuario(cuenta, cliente);
    }

    public static synchronized SesionUsuario obtenerActual() { return actual; }
    public static synchronized void cerrar() { actual = null; }

    public static void exigirRol(RolUsuario rol) {
        SesionUsuario sesion = obtenerActual();
        if (sesion == null || sesion.cuenta.getRol() != rol) {
            throw new SecurityException("Debes iniciar sesión con el perfil requerido para realizar esta acción.");
        }
    }

    public CuentaUsuario getCuenta() { return cuenta; }
    public Cliente getCliente() { return cliente; }
    public boolean es(RolUsuario rol) { return cuenta.getRol() == rol; }
}