package hoteluq.repository;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.hotel.Habitacion;
import hoteluq.model.reserva.Reserva;
import hoteluq.model.creacionales.HabitacionFactory;
import hoteluq.model.hotel.TipoHabitacion;
import hoteluq.model.seguridad.CuentaUsuario;
import hoteluq.model.seguridad.RolUsuario;
import hoteluq.model.promociones.Promocion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Repositorio central de persistencia simulada en memoria para el ecosistema HotelUQ.
 * <p>
 * Aplica de forma estricta el patrón de diseño creacional <b>Singleton</b> para garantizar
 * que exista una única y exclusiva instancia física de los datos compartida globalmente
 * entre todos los controladores de la interfaz gráfica desarrollada en JavaFX.
 * </p>
 *
 * @author Proyecto Programación II
 * @version 1.0
 */
public class HotelUQData {

    // Instancia única y estática de la base de datos simulada en memoria
    private static HotelUQData instancia;

    // Estructuras de datos internas para el almacenamiento de entidades del dominio
    private final List<Cliente> clientes;
    private final List<Habitacion> habitaciones;
    private final List<Reserva> reservas;
    private final List<CuentaUsuario> cuentas;
    private final List<Promocion> promociones;

    /**
     * Constructor de visibilidad privada absoluta.
     * Evita de forma contundente la instanciación externa mediante el operador 'new',
     * forzando el paso por el punto de acceso centralizado del patrón Singleton.
     */
    private HotelUQData() {
        this.clientes = new ArrayList<>();
        this.habitaciones = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.cuentas = new ArrayList<>();
        this.promociones = new ArrayList<>();
        // Carga automática del estado de prueba para validar la funcionalidad
        cargarDatosIniciales();
    }

    /**
     * Punto de entrada y acceso único de nivel global a la instancia del repositorio.
     * Implementa un bloqueo sincronizado para garantizar la seguridad en entornos multihilo.
     *
     * @return La única instancia activa de tipo {@link HotelUQData}.
     */
    public static synchronized HotelUQData getInstancia() {
        if (instancia == null) {
            instancia = new HotelUQData();
        }
        return instancia;
    }

    /**
     * Abastece al sistema con un conjunto de datos iniciales válidos para cumplir
     * con los criterios técnicos mínimos de las pruebas operativas en caliente.
     */
    private void cargarDatosIniciales() {
        // Creación y registro de habitaciones base delegando la lógica al Factory Method
        habitaciones.add(HabitacionFactory.crearHabitacion("101", TipoHabitacion.ESTANDAR, 120000));
        habitaciones.add(HabitacionFactory.crearHabitacion("102", TipoHabitacion.ESTANDAR, 120000));
        habitaciones.add(HabitacionFactory.crearHabitacion("201", TipoHabitacion.FAMILIAR, 220000));
        habitaciones.add(HabitacionFactory.crearHabitacion("301", TipoHabitacion.SUITE, 450000));

        // Registro de clientes de prueba con montos financieros configurados en su tarjeta virtual
        clientes.add(new Cliente("1111", "Juan Pérez", "juan@mail.com", 500000));
        clientes.add(new Cliente("2222", "Maria Lopez", "maria@mail.com", 1500000));
        cuentas.add(CuentaUsuario.crear("admin", "HotelUQ2026!".toCharArray(), RolUsuario.ADMINISTRADOR, null));
        cuentas.add(CuentaUsuario.crear("1111", "Cliente2026!".toCharArray(), RolUsuario.CLIENTE, "1111"));
        cuentas.add(CuentaUsuario.crear("2222", "Cliente2026!".toCharArray(), RolUsuario.CLIENTE, "2222"));
    }

    /**
     * Añade un nuevo huésped verificado al listado de persistencia interna.
     *
     * @param cliente Instancia a registrar.
     */
    public void agregarCliente(Cliente cliente) {
        if (cliente != null) {
            this.clientes.add(cliente);
        }
    }

    public synchronized void registrarClienteConCuenta(Cliente cliente, CuentaUsuario cuenta) {
        if (cliente == null || cuenta == null || cuenta.getRol() != RolUsuario.CLIENTE) {
            throw new IllegalArgumentException("El cliente y su cuenta son obligatorios.");
        }
        boolean cedulaExiste = clientes.stream().anyMatch(c -> c.getCedula().equalsIgnoreCase(cliente.getCedula()));
        boolean correoExiste = clientes.stream().anyMatch(c -> c.getCorreo().equalsIgnoreCase(cliente.getCorreo()));
        boolean usuarioExiste = cuentas.stream().anyMatch(c -> c.getUsuario().equalsIgnoreCase(cuenta.getUsuario()));
        if (cedulaExiste || correoExiste || usuarioExiste) {
            throw new IllegalArgumentException("La cédula, el correo o el usuario ya están registrados.");
        }
        clientes.add(cliente);
        cuentas.add(cuenta);
    }

    public Optional<CuentaUsuario> buscarCuenta(String usuario) {
        if (usuario == null) return Optional.empty();
        return cuentas.stream().filter(c -> c.getUsuario().equalsIgnoreCase(usuario.trim())).findFirst();
    }

    public Optional<Cliente> buscarCliente(String cedula) {
        if (cedula == null) return Optional.empty();
        return clientes.stream().filter(c -> c.getCedula().equalsIgnoreCase(cedula.trim())).findFirst();
    }

    public synchronized void eliminarClienteYCuenta(String cedula) {
        Cliente cliente = buscarCliente(cedula)
                .orElseThrow(() -> new IllegalArgumentException("No existe un cliente con esa cédula."));
        clientes.remove(cliente);
        cuentas.removeIf(cuenta -> cedula.equalsIgnoreCase(cuenta.getCedulaCliente()));
    }

    public synchronized void actualizarContrasenaCliente(String cedula, char[] nuevaContrasena) {
        int indice = -1;
        for (int i = 0; i < cuentas.size(); i++) {
            CuentaUsuario cuenta = cuentas.get(i);
            if (cuenta.getRol() == RolUsuario.CLIENTE && cedula.equalsIgnoreCase(cuenta.getCedulaCliente())) {
                indice = i;
                break;
            }
        }
        if (indice < 0) throw new IllegalStateException("El cliente no tiene una cuenta de acceso.");
        CuentaUsuario anterior = cuentas.get(indice);
        cuentas.set(indice, CuentaUsuario.crear(anterior.getUsuario(), nuevaContrasena,
                RolUsuario.CLIENTE, cedula));
    }

    public List<Promocion> getPromociones() {
        return Collections.unmodifiableList(promociones);
    }

    public Optional<Promocion> buscarPromocionPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        return promociones.stream().filter(p -> p.getCodigo().equalsIgnoreCase(codigo.trim())).findFirst();
    }

    public Optional<Promocion> buscarPromocionPorId(String id) {
        if (id == null) return Optional.empty();
        return promociones.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public synchronized void agregarPromocion(Promocion promocion) {
        if (buscarPromocionPorCodigo(promocion.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una promoción con ese código.");
        }
        promociones.add(promocion);
    }

    public synchronized void eliminarPromocion(String id) {
        promociones.remove(buscarPromocionPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe esa promoción.")));
    }

    /**
     * Agrega una infraestructura de habitación al listado maestro del hotel.
     *
     * @param habitacion Instancia a registrar.
     */
    public void agregarHabitacion(Habitacion habitacion) {
        if (habitacion != null) {
            this.habitaciones.add(habitacion);
        }
    }

    /**
     * Registra una transacción física confirmada de reserva en la bitácora operativa.
     *
     * @param reserva Instancia de reserva a consolidar.
     */
    public void agregarReserva(Reserva reserva) {
        if (reserva != null) {
            this.reservas.add(reserva);
        }
    }

    /**
     * Retorna una copia de lectura blindada de la colección de clientes.
     * Cumple con los principios de encapsulamiento e inmutabilidad estructural.
     *
     * @return Una vista inmutable de la lista de clientes.
     */
    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(clientes);
    }

    /**
     * Retorna una copia protegida de las habitaciones registradas en el hotel.
     *
     * @return Una vista inmutable de la lista de habitaciones.
     */
    public List<Habitacion> getHabitaciones() {
        return Collections.unmodifiableList(habitaciones);
    }

    /**
     * Devuelve habitaciones que no tienen reservas confirmadas que se crucen
     * con el periodo solicitado. La fecha de salida se trata como límite abierto.
     */
    public List<Habitacion> obtenerHabitacionesDisponibles(LocalDate entrada, LocalDate salida) {
        if (entrada == null || salida == null || !salida.isAfter(entrada)) {
            return Collections.emptyList();
        }

        return habitaciones.stream()
                .filter(habitacion -> reservas.stream().noneMatch(reserva ->
                        reserva.getHabitacion().getNumero().equals(habitacion.getNumero())
                                && "Confirmada".equalsIgnoreCase(reserva.getEstado())
                                && entrada.isBefore(reserva.getFechaSalida())
                                && reserva.getFechaEntrada().isBefore(salida)))
                .toList();
    }

    /**
     * Retorna el histórico completo de las reservas del negocio bajo restricciones de escritura.
     *
     * @return Una vista inmutable de la lista de reservas.
     */
    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    /**
     * Método factoría encargado de instanciar y suministrar el recorrido desacoplado.
     *
     * @return Una instancia funcional de tipo {@link Iterator} especializada en Clientes.
     */
    public Iterator<Cliente> obtenerIteradorClientes() {
        return new IteradorClientes(this.clientes);
    }
}
