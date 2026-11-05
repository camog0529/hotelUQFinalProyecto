package hoteluq.service;

import hoteluq.model.comportamiento.VisitanteEstadisticas;
import hoteluq.model.hotel.TipoHabitacion;
import hoteluq.model.clientes.Cliente;
import hoteluq.model.hotel.Habitacion;
import hoteluq.model.reserva.Reserva;
import hoteluq.model.creacionales.HabitacionFactory;
import hoteluq.model.estructurales.ProxyRecargas;
import hoteluq.repository.HotelUQData;
import hoteluq.model.creacionales.ReservaBuilder;
import hoteluq.model.comportamiento.MediadorReservas;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import hoteluq.model.seguridad.RolUsuario;
import hoteluq.model.promociones.Promocion;

public class HotelUQFacade {

    private final HotelUQData baseDatos;
    private final ProxyRecargas proxySeguridad;

    public HotelUQFacade() {
        this.baseDatos = HotelUQData.getInstancia();
        this.proxySeguridad = new ProxyRecargas();
    }

    // CASOS DE USO DE ADMINISTRADOR

    public void registrarHabitacion(String numero, TipoHabitacion tipo, double tarifaBase) {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        boolean numeroExiste = baseDatos.getHabitaciones().stream()
                .anyMatch(habitacion -> habitacion.getNumero().equalsIgnoreCase(numero == null ? "" : numero.trim()));
        if (numeroExiste) {
            throw new IllegalArgumentException("Ya existe una habitación con ese número.");
        }
        Habitacion nueva = HabitacionFactory.crearHabitacion(numero, tipo, tarifaBase);
        baseDatos.agregarHabitacion(nueva);
    }

    public List<Cliente> obtenerTodosLosClientes() {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        return baseDatos.getClientes();
    }

    public void registrarClienteAdministrativo(String cedula, String nombre, String correo, char[] contrasena) {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        String identificador = cedula == null ? null : cedula.trim();
        Cliente cliente = new Cliente(identificador,
                nombre == null ? null : nombre.trim(), correo == null ? null : correo.trim(), 0);
        hoteluq.model.seguridad.CuentaUsuario cuenta = hoteluq.model.seguridad.CuentaUsuario.crear(
                identificador, contrasena, RolUsuario.CLIENTE, identificador);
        baseDatos.registrarClienteConCuenta(cliente, cuenta);
    }

    public void actualizarCliente(String cedula, String nombre, String correo) {
        actualizarCliente(cedula, nombre, correo, null);
    }

    public void actualizarCliente(String cedula, String nombre, String correo, char[] nuevaContrasena) {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        if (nuevaContrasena != null && nuevaContrasena.length > 0 && nuevaContrasena.length < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres.");
        }
        Cliente cliente = baseDatos.buscarCliente(cedula)
                .orElseThrow(() -> new IllegalArgumentException("No existe un cliente con esa cédula."));
        boolean correoDuplicado = baseDatos.getClientes().stream()
                .anyMatch(otro -> otro != cliente && otro.getCorreo().equalsIgnoreCase(correo == null ? "" : correo.trim()));
        if (correoDuplicado) throw new IllegalArgumentException("Ese correo ya pertenece a otro cliente.");
        cliente.actualizarPerfil(nombre, correo);
        if (nuevaContrasena != null && nuevaContrasena.length > 0) {
            baseDatos.actualizarContrasenaCliente(cedula, nuevaContrasena);
        }
    }

    public void eliminarCliente(String cedula) {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        boolean tieneReservas = baseDatos.getReservas().stream()
                .anyMatch(reserva -> reserva.getCliente().getCedula().equalsIgnoreCase(cedula));
        if (tieneReservas) {
            throw new IllegalStateException("No se puede eliminar un cliente con reservas registradas; edita sus datos para conservar el historial.");
        }
        baseDatos.eliminarClienteYCuenta(cedula);
    }

    public List<Promocion> obtenerPromocionesAdministrativas() {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        return baseDatos.getPromociones();
    }

    public List<Promocion> obtenerPromocionesAplicables(LocalDate entrada, LocalDate salida, TipoHabitacion tipo) {
        SesionUsuario.exigirRol(RolUsuario.CLIENTE);
        return baseDatos.getPromociones().stream().filter(p -> p.aplica(entrada, salida, tipo)).toList();
    }

    public Promocion buscarPromocionAplicable(String codigo, LocalDate entrada, LocalDate salida, TipoHabitacion tipo) {
        return obtenerPromocionesAplicables(entrada, salida, tipo).stream()
                .filter(p -> p.getCodigo().equalsIgnoreCase(codigo == null ? "" : codigo.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El código no existe o no cumple las condiciones de esta reserva."));
    }

    public void crearPromocion(String codigo, String descripcion, LocalDate inicio, LocalDate fin,
                               TipoHabitacion tipo, int nochesMinimas, double porcentaje, boolean activa) {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        baseDatos.agregarPromocion(new Promocion(codigo, descripcion, inicio, fin, tipo, nochesMinimas, porcentaje, activa));
    }

    public void actualizarPromocion(String id, String codigo, String descripcion, LocalDate inicio, LocalDate fin,
                                    TipoHabitacion tipo, int nochesMinimas, double porcentaje, boolean activa) {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        Promocion promocion = baseDatos.buscarPromocionPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe esa promoción."));
        boolean codigoDuplicado = baseDatos.getPromociones().stream()
                .anyMatch(otra -> otra != promocion && otra.getCodigo().equalsIgnoreCase(codigo == null ? "" : codigo.trim()));
        if (codigoDuplicado) throw new IllegalArgumentException("Ese código ya pertenece a otra promoción.");
        promocion.actualizar(codigo, descripcion, inicio, fin, tipo, nochesMinimas, porcentaje, activa);
    }

    public void eliminarPromocion(String id) {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        baseDatos.eliminarPromocion(id);
    }

    public List<Habitacion> obtenerHabitacionesDisponibles(LocalDate entrada, LocalDate salida) {
        return baseDatos.obtenerHabitacionesDisponibles(entrada, salida);
    }

    public void redimirPuntos(String cedulaCliente, int puntos, String beneficio) {
        SesionUsuario.exigirRol(RolUsuario.CLIENTE);
        Cliente cliente = baseDatos.buscarCliente(cedulaCliente)
                .orElseThrow(() -> new IllegalArgumentException("No existe un cliente con esa cédula."));
        if (!cliente.getCedula().equals(SesionUsuario.obtenerActual().getCliente().getCedula())) {
            throw new SecurityException("Solo puedes redimir los puntos de tu propia cuenta.");
        }
        cliente.redimirPuntos(puntos, beneficio);
    }

    /**
     * Caso de uso crítico: Ejecuta una recarga de saldo financiero delegando en el Proxy
     */
    public void recargarSaldoTarjeta(String cedulaCliente, double monto) {
        // Buscamos el cliente directamente desde el repositorio del dominio
        Cliente cliente = baseDatos.getClientes().stream()
                .filter(c -> c.getCedula().equals(cedulaCliente))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Error: No se encontró ningún cliente con la cédula especificada."));

        // El controlador NO invoca al cliente. El Proxy intercepta y audita el rol
        proxySeguridad.solicitarRecargaSaldo(cliente, monto);
    }
    /**
     * Obtiene el porcentaje de ocupación actual delegando en el Visitante de Estadísticas.
     */
    public double obtenerPorcentajeOcupacionActual() {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        VisitanteEstadisticas visitante = new VisitanteEstadisticas();
        int totalHabitaciones = baseDatos.getHabitaciones().size();

        // Contamos las reservas que están activas ("Confirmada")
        long reservasActivas = baseDatos.getReservas().stream()
                .filter(r -> "Confirmada".equalsIgnoreCase(r.getEstado()))
                .filter(r -> !LocalDate.now().isBefore(r.getFechaEntrada())
                        && LocalDate.now().isBefore(r.getFechaSalida()))
                .count();

        return visitante.calcularPorcentajeOcupacion(totalHabitaciones, reservasActivas);
    }

    /**
     * Obtiene la cantidad de reservas confirmadas para un tipo de habitación específico.
     */
    public int obtenerCantidadReservasPorTipo(TipoHabitacion tipo) {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        VisitanteEstadisticas visitante = new VisitanteEstadisticas();
        return visitante.contarReservasPorTipo(baseDatos.getReservas(), tipo);
    }

    /**
     * Obtiene el historial completo de reservas registradas en el sistema para auditoría global (Admin).
     */
    public List<Reserva> obtenerTodasLasReservas() {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        return baseDatos.getReservas();
    }

    /**
     * Filtra y obtiene las reservas que le pertenecen exclusivamente a un cliente por su cédula (Cliente).
     */
    public List<Reserva> obtenerReservasPorCliente(String cedulaCliente) {
        SesionUsuario.exigirRol(RolUsuario.CLIENTE);
        if (SesionUsuario.obtenerActual().getCliente() == null
                || !SesionUsuario.obtenerActual().getCliente().getCedula().equals(cedulaCliente)) {
            throw new SecurityException("Solo puedes consultar las reservas de tu cuenta.");
        }
        return baseDatos.getReservas().stream()
                .filter(r -> r.getCliente().getCedula().equals(cedulaCliente))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Orquesta la confirmación de una reserva utilizando el mediador central del sistema.
     */
    public void confirmarReserva(Reserva reserva) {
        SesionUsuario.exigirRol(RolUsuario.CLIENTE);
        if (reserva == null || SesionUsuario.obtenerActual().getCliente() == null
                || !reserva.getCliente().getCedula().equals(SesionUsuario.obtenerActual().getCliente().getCedula())) {
            throw new SecurityException("La reserva debe pertenecer al cliente autenticado.");
        }
        MediadorReservas mediador = new MediadorReservas();
        // Este método lanza un IllegalStateException si la habitación está ocupada o si no hay saldo
        mediador.procesarConfirmacionReserva(reserva);
    }

    /**
     * Procesa la solicitud de cancelación de un cliente evaluando las penalizaciones horarias de reembolso.
     */
    public void cancelarReservaPorCliente(String idReserva) {
        SesionUsuario.exigirRol(RolUsuario.CLIENTE);
        Reserva reserva = baseDatos.getReservas().stream()
                .filter(r -> r.getIdReserva().equals(idReserva))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ninguna reserva con el ID suministrado."));

        if (!reserva.getCliente().getCedula().equals(SesionUsuario.obtenerActual().getCliente().getCedula())) {
            throw new SecurityException("Solo puedes cancelar una reserva de tu propia cuenta.");
        }

        // Lógica nativa del estado: lanza IllegalStateException si la reserva no está en estado "Confirmada"
        reserva.solicitarCancelacionCliente(LocalDateTime.now());
    }

    public void cancelarReservaPorHotel(String idReserva) {
        SesionUsuario.exigirRol(RolUsuario.ADMINISTRADOR);
        Reserva reserva = baseDatos.getReservas().stream()
                .filter(r -> r.getIdReserva().equals(idReserva))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ninguna reserva con el ID suministrado."));
        reserva.cancelarPorElHotel();
    }
}
