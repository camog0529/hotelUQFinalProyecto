package hoteluq.model.clientes;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Comparator;

/**
 * Representa a un huésped dentro del sistema HotelUQ.
 * Gestiona la información del perfil del usuario, el saldo de su tarjeta virtual,
 * el historial de movimientos financieros y su cuenta de puntos de fidelización.
 *
 * @author Proyecto Programación II
 * @version 1.0
 */
public class Cliente {
    private final String cedula;
    private String nombre;
    private String correo;
    private double saldoTarjeta;
    private boolean tarjetaActiva;
    private final List<LotePuntos> lotesPuntos;
    private final List<String> historialMovimientos;

    /**
     * Constructor para registrar un nuevo cliente con una tarjeta virtual asociada.
     *
     * @param cedula Unicidad identificativa del cliente.
     * @param nombre Nombre completo del cliente.
     * @param correo Dirección de correo electrónico para notificaciones.
     * @param saldoInicial Monto de apertura asignado a la tarjeta virtual.
     */
    public Cliente(String cedula, String nombre, String correo, double saldoInicial) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.saldoTarjeta = saldoInicial;
        this.tarjetaActiva = true;
        if (cedula == null || cedula.isBlank() || nombre == null || nombre.isBlank()
                || correo == null || correo.isBlank() || !Double.isFinite(saldoInicial) || saldoInicial < 0) {
            throw new IllegalArgumentException("Los datos del cliente o el saldo inicial no son válidos.");
        }
        this.lotesPuntos = new ArrayList<>();
        this.historialMovimientos = new ArrayList<>();
        registrarMovimiento("Apertura de tarjeta virtual con saldo inicial: $" + saldoInicial);
    }

    /**
     * Verifica si la tarjeta se encuentra activa y posee fondos suficientes para una transacción.
     * Cumple con la regla de negocio de validación previa al pago.
     *
     * @param monto El valor económico a debitar.
     * @return true si la tarjeta está activa y tiene saldo suficiente; de lo contrario, false.
     */
    public boolean verificarSaldo(double monto) {
        return Double.isFinite(monto) && monto > 0 && tarjetaActiva && saldoTarjeta >= monto;
    }

    /**
     * Descuenta un monto específico del saldo disponible de la tarjeta virtual.
     *
     * @param monto El valor total a pagar por la reserva o servicio.
     * @throws IllegalStateException Si la tarjeta está inactiva o los fondos son insuficientes.
     */
    public void descontarSaldo(double monto) {
        if (!verificarSaldo(monto)) {
            throw new IllegalStateException("Saldo insuficiente o tarjeta inactiva.");
        }
        this.saldoTarjeta -= monto;
        registrarMovimiento("Pago realizado: -$" + monto);
    }

    /**
     * Permite al administrador recargar saldo en la tarjeta virtual del cliente.
     * Cumple con la regla de restricción de permisos del hotel.
     *
     * @param monto Cantidad de dinero a ingresar a la cuenta.
     * @throws IllegalArgumentException Si el monto ingresado es menor o igual a cero.
     */
    public void recargarTarjeta(double monto) {
        if (!Double.isFinite(monto) || monto <= 0) {
            throw new IllegalArgumentException("El monto de recarga debe ser mayor a cero.");
        }
        this.saldoTarjeta += monto;
        registrarMovimiento("Recarga autorizada por Administrador: +$" + monto);
    }

    /** Restaura el saldo exacto guardado por un comando compensable. */
    public void restaurarSaldoTarjeta(double saldo) {
        if (!Double.isFinite(saldo) || saldo < 0) {
            throw new IllegalArgumentException("El saldo restaurado no es válido.");
        }
        double diferencia = saldo - this.saldoTarjeta;
        this.saldoTarjeta = saldo;
        registrarMovimiento("Reversión de transacción: ajuste de saldo "
                + (diferencia >= 0 ? "+$" : "-$") + Math.abs(diferencia));
    }

    /**
     * Añade un registro textual a la bitácora o historial de transacciones de la tarjeta.
     *
     * @param movimiento Descripción del evento financiero ocurrido.
     */
    public void registrarMovimiento(String movimiento) {
        this.historialMovimientos.add(movimiento);
    }

    public void actualizarPerfil(String nombre, String correo) {
        if (nombre == null || nombre.isBlank() || correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("El nombre y el correo son obligatorios.");
        }
        this.nombre = nombre.trim();
        this.correo = correo.trim();
    }

    /**
     * Suma puntos de fidelización obtenidos a la cuenta del cliente.
     *
     * @param puntos Cantidad de puntos devueltos por la regla de acumulación.
     */
    public void acumularPuntos(int puntos) {
        acumularPuntos(puntos, LocalDate.now());
    }

    public void acumularPuntos(int puntos, LocalDate fechaObtencion) {
        LotePuntos lote = new LotePuntos(puntos, fechaObtencion);
        lotesPuntos.add(lote);
        registrarMovimiento("Fidelización: +" + puntos + " puntos; vencen el " + lote.getFechaVencimiento());
    }

    /** Redime por vencimiento más próximo, respetando la expiración anual de cada lote. */
    public void redimirPuntos(int puntos, String beneficio) {
        if (puntos <= 0 || beneficio == null || beneficio.isBlank()) {
            throw new IllegalArgumentException("Indica un beneficio y una cantidad positiva de puntos.");
        }
        LocalDate hoy = LocalDate.now();
        List<LotePuntos> vigentes = lotesPuntos.stream()
                .filter(lote -> lote.estaVigente(hoy) && lote.getPuntosDisponibles() > 0)
                .sorted(Comparator.comparing(LotePuntos::getFechaVencimiento))
                .toList();
        int disponibles = vigentes.stream().mapToInt(LotePuntos::getPuntosDisponibles).sum();
        if (puntos > disponibles) {
            throw new IllegalArgumentException("No hay suficientes puntos vigentes para este beneficio.");
        }
        int pendientes = puntos;
        for (LotePuntos lote : vigentes) {
            int consumo = Math.min(pendientes, lote.getPuntosDisponibles());
            lote.consumir(consumo);
            pendientes -= consumo;
            if (pendientes == 0) break;
        }
        registrarMovimiento("Fidelización: -" + puntos + " puntos redimidos por " + beneficio);
    }

    // =========================================================================
    // GETTERS Y SETTERS (Requeridos para el binding de datos en JavaFX)
    // =========================================================================

    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public double getSaldoTarjeta() { return saldoTarjeta; }
    public boolean isTarjetaActiva() { return tarjetaActiva; }
    public void setTarjetaActiva(boolean tarjetaActiva) { this.tarjetaActiva = tarjetaActiva; }
    public int getPuntosAcumulados() {
        LocalDate hoy = LocalDate.now();
        return lotesPuntos.stream().filter(lote -> lote.estaVigente(hoy))
                .mapToInt(LotePuntos::getPuntosDisponibles).sum();
    }


    /**
     * Devuelve una copia inmutable del historial para proteger la integridad de los datos.
     *
     * @return Lista de cadenas de texto con los movimientos registrados.
     */
    public List<String> getHistorialMovimientos() {
        return new ArrayList<>(historialMovimientos);
    }
}
