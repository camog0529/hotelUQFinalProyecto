package hoteluq.service;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.seguridad.CuentaUsuario;
import hoteluq.model.seguridad.RolUsuario;
import hoteluq.repository.HotelUQData;

/** Casos de uso de registro, autenticación y cierre de sesión. */
public class AutenticacionService {
    private final HotelUQData repositorio = HotelUQData.getInstancia();

    public void iniciarSesion(String usuario, char[] contrasena) {
        CuentaUsuario cuenta = repositorio.buscarCuenta(usuario)
                .orElseThrow(() -> new SecurityException("Usuario o contraseña incorrectos."));
        if (!cuenta.verificarContrasena(contrasena)) {
            throw new SecurityException("Usuario o contraseña incorrectos.");
        }
        Cliente cliente = cuenta.getRol() == RolUsuario.CLIENTE
                ? repositorio.buscarCliente(cuenta.getCedulaCliente())
                    .orElseThrow(() -> new IllegalStateException("La cuenta no tiene un cliente asociado."))
                : null;
        SesionUsuario.iniciar(cuenta, cliente);
    }

    public void registrarCliente(String cedula, String nombre, String correo, char[] contrasena) {
        Cliente cliente = new Cliente(cedula.trim(), nombre.trim(), correo.trim(), 0);
        CuentaUsuario cuenta = CuentaUsuario.crear(cedula.trim(), contrasena, RolUsuario.CLIENTE, cedula.trim());
        repositorio.registrarClienteConCuenta(cliente, cuenta);
    }

    public void cerrarSesion() {
        SesionUsuario.cerrar();
    }
}
