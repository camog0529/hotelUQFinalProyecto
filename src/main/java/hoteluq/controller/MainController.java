package hoteluq.controller;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.hotel.Habitacion;
import hoteluq.model.reserva.Reserva;
import hoteluq.model.creacionales.ReservaBuilder;
import hoteluq.service.HotelUQFacade;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import hoteluq.model.comportamiento.TarifaTemporadaAlta;
import hoteluq.model.comportamiento.TarifaTemporadaBaja;
import java.util.UUID;
import java.text.NumberFormat;
import java.util.Locale;
import hoteluq.model.servicios.ComponenteServicio;
import hoteluq.model.servicios.DecoradorSeguroCancelacion;
import hoteluq.model.servicios.PaqueteServicios;
import hoteluq.model.servicios.ServicioIndividual;
import hoteluq.model.promociones.Promocion;
import hoteluq.model.seguridad.RolUsuario;
import hoteluq.service.SesionUsuario;
import javafx.application.Platform;

public class MainController {

    @FXML private Label lblClienteActual;
    @FXML private ComboBox<Habitacion> cmbHabitaciones;
    @FXML private ComboBox<Promocion> cmbPromociones;
    @FXML private DatePicker dpEntrada;
    @FXML private DatePicker dpSalida;
    @FXML private CheckBox chkSpa;
    @FXML private CheckBox chkDesayuno;
    @FXML private CheckBox chkParqueadero;
    @FXML private CheckBox chkSeguroCancelacion;
    @FXML private Button btnNavClientes;
    @FXML private Button btnCerrarSesion;

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colId;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, String> colHabitacion;
    @FXML private TableColumn<Reserva, String> colEstado;
    @FXML private TableColumn<Reserva, Double> colTotal;

    private HotelUQFacade fachada;
    private ObservableList<Reserva> masterReservas;

    @FXML
    public void initialize() {
        SesionUsuario sesion = SesionUsuario.obtenerActual();
        if (sesion == null || !sesion.es(RolUsuario.CLIENTE)) {
            Platform.runLater(() -> cambiarVista("/fxml/LoginView.fxml"));
            return;
        }
        fachada = new HotelUQFacade(); // Fachada de servicios del dominio
        lblClienteActual.setText(sesion.getCliente().getNombre() + " (" + sesion.getCliente().getCedula() + ")");
        masterReservas = FXCollections.observableArrayList(fachada.obtenerReservasPorCliente(sesion.getCliente().getCedula()));

        // Las habitaciones se consultan por periodo, no por una bandera global.
        cmbHabitaciones.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Habitacion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Hab: " + item.getNumero() + " (" + item.getTipo() + ") - $" + item.getTarifaBase());
                }
            }
        });
        cmbHabitaciones.setButtonCell(cmbHabitaciones.getCellFactory().call(null));

        // Configuración de las columnas para Binding nativo
        colId.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colCliente.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCliente().getNombre()));
        colHabitacion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHabitacion().getNumero()));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : moneda.format(valor));
            }
        });

        tablaReservas.setItems(masterReservas);

        // Configurar navegación lateral a la pantalla de Clientes
        btnNavClientes.setOnAction(event -> cambiarVista("/fxml/ClienteView.fxml"));
        btnCerrarSesion.setOnAction(event -> cerrarSesion());
        dpEntrada.valueProperty().addListener((obs, oldValue, newValue) -> actualizarHabitacionesDisponibles());
        dpSalida.valueProperty().addListener((obs, oldValue, newValue) -> actualizarHabitacionesDisponibles());
        cmbHabitaciones.valueProperty().addListener((obs, oldValue, newValue) -> actualizarPromocionesDisponibles());
    }

    private void actualizarHabitacionesDisponibles() {
        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();
        if (entrada == null || salida == null || !salida.isAfter(entrada)) {
            cmbHabitaciones.getItems().clear();
            cmbHabitaciones.setValue(null);
            return;
        }
        Habitacion seleccionada = cmbHabitaciones.getValue();
        cmbHabitaciones.setItems(FXCollections.observableArrayList(
                fachada.obtenerHabitacionesDisponibles(entrada, salida)));
        if (seleccionada != null && cmbHabitaciones.getItems().contains(seleccionada)) {
            cmbHabitaciones.setValue(seleccionada);
        } else {
            cmbHabitaciones.setValue(null);
        }
        actualizarPromocionesDisponibles();
    }

    private void actualizarPromocionesDisponibles() {
        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();
        Habitacion habitacion = cmbHabitaciones.getValue();
        if (entrada == null || salida == null || habitacion == null) {
            cmbPromociones.getItems().clear();
            cmbPromociones.setValue(null);
            return;
        }
        Promocion seleccionada = cmbPromociones.getValue();
        cmbPromociones.setItems(FXCollections.observableArrayList(
                fachada.obtenerPromocionesAplicables(entrada, salida, habitacion.getTipo())));
        if (seleccionada != null && cmbPromociones.getItems().contains(seleccionada)) {
            cmbPromociones.setValue(seleccionada);
        } else {
            cmbPromociones.setValue(null);
        }
    }

    @FXML
    private void handleCalcularCosto() {
        try {
            Reserva reservaTemporal = construirReservaDesdeFormulario("TEMP-ID");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Costo Calculado",
                    "El costo estimado calculado por el ReservaBuilder es: $" + reservaTemporal.getValorTotal());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", e.getMessage());
        }
    }

    @FXML
    private void handleConfirmarReserva() {
        try {
            // Generar código autoincremental simulado para la transacción
            String idTransaccion = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Reserva nuevaReserva = construirReservaDesdeFormulario(idTransaccion);

            // Se despacha al Mediador de reservas a través del flujo transaccional
            // Nota: En tu backend se implementa mediante mediador.procesarConfirmacionReserva()
            fachada.confirmarReserva(nuevaReserva);

            // Refrescar la tabla operativa
            masterReservas.setAll(fachada.obtenerReservasPorCliente(SesionUsuario.obtenerActual().getCliente().getCedula()));
            String descuento = nuevaReserva.getDescuentoPromocional() > 0
                    ? " Descuento aplicado: $" + nuevaReserva.getDescuentoPromocional() + "." : "";
            mostrarAlerta(Alert.AlertType.CONFIRMATION, "Reserva Exitosa", "La transacción ha sido conciliada y pagada." + descuento);
            limpiarFormulario();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Fallo Transaccional", e.getMessage());
        }
    }

    @FXML
    private void handleCancelarReserva() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una reserva", "Selecciona primero una reserva de la tabla.");
            return;
        }
        try {
            fachada.cancelarReservaPorCliente(seleccionada.getIdReserva());
            masterReservas.setAll(fachada.obtenerReservasPorCliente(SesionUsuario.obtenerActual().getCliente().getCedula()));
            actualizarHabitacionesDisponibles();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Cancelación procesada", "Se aplicó la política de reembolso según las horas hasta la entrada.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo cancelar", e.getMessage());
        }
    }

    private Reserva construirReservaDesdeFormulario(String id) {
        Habitacion hab = cmbHabitaciones.getValue();
        LocalDate entrada = dpEntrada.getValue();
        LocalDate salida = dpSalida.getValue();

        if (hab == null || entrada == null || salida == null) {
            throw new IllegalStateException("Todos los campos obligatorios deben completarse.");
        }

        Cliente cliente = SesionUsuario.obtenerActual().getCliente();

        // Implementación del patrón de diseño Creacional Builder (Sección 4 del PDF)
        ReservaBuilder builder = new ReservaBuilder(id)
                .conCliente(cliente)
                .paraHabitacion(hab)
                .enFechas(entrada, salida)
                .conEstrategiaTarifa(esTemporadaAlta(entrada)
                        ? new TarifaTemporadaAlta()
                        : new TarifaTemporadaBaja());
        if (cmbPromociones.getValue() != null) builder.conPromocion(cmbPromociones.getValue());

        PaqueteServicios paquete = new PaqueteServicios("Servicios adicionales");
        if (chkSpa.isSelected()) paquete.agregarComponente(new ServicioIndividual("Acceso a spa", 50000));
        if (chkDesayuno.isSelected()) paquete.agregarComponente(new ServicioIndividual("Desayuno", 25000));
        if (chkParqueadero.isSelected()) paquete.agregarComponente(new ServicioIndividual("Parqueadero", 15000));
        ComponenteServicio serviciosSeleccionados = paquete;
        if (chkSeguroCancelacion.isSelected()) {
            serviciosSeleccionados = new DecoradorSeguroCancelacion(paquete, 30000);
            builder.conSeguroCancelacion(true);
        }
        if (chkSpa.isSelected() || chkDesayuno.isSelected() || chkParqueadero.isSelected()
                || chkSeguroCancelacion.isSelected()) {
            builder.agregarServicio(serviciosSeleccionados);
        }

        return builder.build();
    }

    private boolean esTemporadaAlta(LocalDate fecha) {
        return fecha.getMonthValue() == 12 || fecha.getMonthValue() == 1;
    }

    private void limpiarFormulario() {
        cmbHabitaciones.setValue(null);
        cmbPromociones.setValue(null);
        dpEntrada.setValue(null);
        dpSalida.setValue(null);
        chkSpa.setSelected(false);
        chkDesayuno.setSelected(false);
        chkParqueadero.setSelected(false);
        chkSeguroCancelacion.setSelected(false);
    }

    private void cerrarSesion() {
        SesionUsuario.cerrar();
        cambiarVista("/fxml/LoginView.fxml");
    }

    private void cambiarVista(String fxmlPath) {
        try {
            Stage stage = (Stage) btnNavClientes.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            boolean acceso = fxmlPath.contains("LoginView");
            stage.setScene(new Scene(root, acceso ? 560 : 1180, acceso ? 620 : 760));
            stage.setMinWidth(acceso ? 500 : 960);
            stage.setMinHeight(acceso ? 540 : 640);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la vista: " + fxmlPath);
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
