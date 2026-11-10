package hoteluq.controller;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.comportamiento.VisitanteReporte;
import hoteluq.model.hotel.TipoHabitacion;
import hoteluq.model.promociones.Promocion;
import hoteluq.model.reserva.Reserva;
import hoteluq.model.seguridad.RolUsuario;
import hoteluq.service.HotelUQFacade;
import hoteluq.service.SesionUsuario;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

/** Controla las operaciones administrativas sobre reservas, clientes y promociones. */
public class AdminController {
    private final HotelUQFacade fachada = new HotelUQFacade();
    private String promocionSeleccionadaId;

    @FXML private PieChart chartOcupacion;
    @FXML private BarChart<String, Number> chartReservasPorTipo;
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colId;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, String> colHabitacion;
    @FXML private TableColumn<Reserva, String> colEstado;
    @FXML private TableColumn<Reserva, Number> colTotal;
    @FXML private Button btnCerrarSesion;
    @FXML private TextField txtCedulaCliente;
    @FXML private TextField txtMontoRecarga;
    @FXML private TextField txtNumeroHabitacion;
    @FXML private TextField txtTarifaHabitacion;
    @FXML private ComboBox<TipoHabitacion> cmbTipoHabitacion;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colClienteCedula;
    @FXML private TableColumn<Cliente, String> colClienteNombre;
    @FXML private TableColumn<Cliente, String> colClienteCorreo;
    @FXML private TableColumn<Cliente, Number> colClienteSaldo;
    @FXML private TableColumn<Cliente, Number> colClientePuntos;
    @FXML private TextField txtClienteCedula;
    @FXML private TextField txtClienteNombre;
    @FXML private TextField txtClienteCorreo;
    @FXML private PasswordField txtClienteContrasena;

    @FXML private TableView<Promocion> tablaPromociones;
    @FXML private TableColumn<Promocion, String> colPromoCodigo;
    @FXML private TableColumn<Promocion, String> colPromoDescripcion;
    @FXML private TableColumn<Promocion, LocalDate> colPromoInicio;
    @FXML private TableColumn<Promocion, LocalDate> colPromoFin;
    @FXML private TableColumn<Promocion, String> colPromoTipo;
    @FXML private TableColumn<Promocion, Number> colPromoNoches;
    @FXML private TableColumn<Promocion, Number> colPromoPorcentaje;
    @FXML private TableColumn<Promocion, String> colPromoActiva;
    @FXML private TextField txtPromoCodigo;
    @FXML private TextField txtPromoDescripcion;
    @FXML private DatePicker dpPromoInicio;
    @FXML private DatePicker dpPromoFin;
    @FXML private ComboBox<TipoHabitacion> cmbPromoTipo;
    @FXML private TextField txtPromoNoches;
    @FXML private TextField txtPromoPorcentaje;
    @FXML private CheckBox chkPromoActiva;

    @FXML
    public void initialize() {
        SesionUsuario sesion = SesionUsuario.obtenerActual();
        if (sesion == null || !sesion.es(RolUsuario.ADMINISTRADOR)) {
            Platform.runLater(() -> {
                SesionUsuario.cerrar();
                cambiarVista("/fxml/LoginView.fxml");
            });
            return;
        }
        inicializarReservas();
        inicializarClientes();
        inicializarPromociones();
        cmbTipoHabitacion.setItems(FXCollections.observableArrayList(TipoHabitacion.values()));
        cmbPromoTipo.setItems(FXCollections.observableArrayList(TipoHabitacion.values()));
        refrescarDatos();
        refrescarClientes();
        refrescarPromociones();
        btnCerrarSesion.setOnAction(event -> {
            SesionUsuario.cerrar();
            cambiarVista("/fxml/LoginView.fxml");
        });
    }

    private void inicializarReservas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colCliente.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCliente().getNombre()));
        colHabitacion.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getHabitacion().getNumero()));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colTotal.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getValorTotal()));
        NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : moneda.format(valor.doubleValue()));
            }
        });
    }

    private void inicializarClientes() {
        colClienteCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colClienteNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colClienteCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colClienteSaldo.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getSaldoTarjeta()));
        colClientePuntos.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getPuntosAcumulados()));
        NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
        colClienteSaldo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : moneda.format(valor.doubleValue()));
            }
        });
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, cliente) -> {
            if (cliente == null) return;
            txtClienteCedula.setText(cliente.getCedula());
            txtClienteCedula.setEditable(false);
            txtClienteNombre.setText(cliente.getNombre());
            txtClienteCorreo.setText(cliente.getCorreo());
            txtClienteContrasena.clear();
        });
    }

    private void inicializarPromociones() {
        colPromoCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colPromoDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPromoInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colPromoFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colPromoTipo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getTipoHabitacion() == null ? "Todos" : cell.getValue().getTipoHabitacion().name()));
        colPromoNoches.setCellValueFactory(new PropertyValueFactory<>("nochesMinimas"));
        colPromoPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentajeDescuento"));
        colPromoActiva.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().isActiva() ? "Sí" : "No"));
        tablaPromociones.getSelectionModel().selectedItemProperty().addListener((obs, anterior, promocion) -> {
            if (promocion == null) return;
            promocionSeleccionadaId = promocion.getId();
            txtPromoCodigo.setText(promocion.getCodigo());
            txtPromoDescripcion.setText(promocion.getDescripcion());
            dpPromoInicio.setValue(promocion.getFechaInicio());
            dpPromoFin.setValue(promocion.getFechaFin());
            cmbPromoTipo.setValue(promocion.getTipoHabitacion());
            txtPromoNoches.setText(Integer.toString(promocion.getNochesMinimas()));
            txtPromoPorcentaje.setText(Double.toString(promocion.getPorcentajeDescuento()));
            chkPromoActiva.setSelected(promocion.isActiva());
        });
    }

    @FXML
    private void handleCancelarReserva() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una reserva", "Elige una reserva de la tabla para cancelarla.");
            return;
        }
        try {
            fachada.cancelarReservaPorHotel(seleccionada.getIdReserva());
            refrescarDatos();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva cancelada", "Se registró la cancelación del hotel y el reembolso completo.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo cancelar", e.getMessage());
        }
    }

    @FXML
    private void handleRecargarSaldo() {
        try {
            String cedula = txtCedulaCliente.getText().trim();
            double monto = Double.parseDouble(txtMontoRecarga.getText().trim());
            fachada.recargarSaldoTarjeta(cedula, monto);
            txtCedulaCliente.clear();
            txtMontoRecarga.clear();
            refrescarClientes();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Recarga registrada", "El movimiento quedó registrado en la tarjeta del cliente.");
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Monto inválido", "Ingresa un monto numérico mayor que cero.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo recargar", e.getMessage());
        }
    }

    @FXML
    private void handleRegistrarHabitacion() {
        try {
            fachada.registrarHabitacion(txtNumeroHabitacion.getText().trim(), cmbTipoHabitacion.getValue(),
                    Double.parseDouble(txtTarifaHabitacion.getText().trim()));
            txtNumeroHabitacion.clear();
            txtTarifaHabitacion.clear();
            cmbTipoHabitacion.setValue(null);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Habitación registrada", "La habitación quedó disponible para reservas.");
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Tarifa inválida", "Ingresa una tarifa numérica mayor que cero.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo registrar", e.getMessage());
        }
    }

    @FXML
    private void handleGuardarCliente() {
        char[] contrasena = txtClienteContrasena.getText().toCharArray();
        try {
            if (tablaClientes.getSelectionModel().getSelectedItem() == null) {
                fachada.registrarClienteAdministrativo(txtClienteCedula.getText(), txtClienteNombre.getText(),
                        txtClienteCorreo.getText(), contrasena);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente creado", "La cuenta se creó con perfil de cliente y ya puede iniciar sesión.");
            } else {
                fachada.actualizarCliente(txtClienteCedula.getText(), txtClienteNombre.getText(), txtClienteCorreo.getText(), contrasena);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente actualizado", "Se guardaron los datos del cliente.");
            }
            refrescarClientes();
            handleLimpiarCliente();
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar el cliente", e.getMessage());
        } finally {
            Arrays.fill(contrasena, '\0');
            txtClienteContrasena.clear();
        }
    }

    @FXML
    private void handleEliminarCliente() {
        Cliente cliente = tablaClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un cliente", "Selecciona un registro antes de eliminarlo.");
            return;
        }
        if (!confirmar("¿Eliminar la cuenta de " + cliente.getNombre() + "?")) return;
        try {
            fachada.eliminarCliente(cliente.getCedula());
            refrescarClientes();
            handleLimpiarCliente();
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar", e.getMessage());
        }
    }

    @FXML
    private void handleLimpiarCliente() {
        tablaClientes.getSelectionModel().clearSelection();
        txtClienteCedula.clear();
        txtClienteCedula.setEditable(true);
        txtClienteNombre.clear();
        txtClienteCorreo.clear();
        txtClienteContrasena.clear();
    }

    @FXML
    private void handleGuardarPromocion() {
        try {
            String codigo = txtPromoCodigo.getText();
            String descripcion = txtPromoDescripcion.getText();
            LocalDate inicio = dpPromoInicio.getValue();
            LocalDate fin = dpPromoFin.getValue();
            TipoHabitacion tipo = cmbPromoTipo.getValue();
            int noches = Integer.parseInt(txtPromoNoches.getText().trim());
            double porcentaje = Double.parseDouble(txtPromoPorcentaje.getText().trim());
            if (promocionSeleccionadaId == null) {
                fachada.crearPromocion(codigo, descripcion, inicio, fin, tipo, noches, porcentaje, chkPromoActiva.isSelected());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Promoción creada", "Los clientes elegibles ya podrán seleccionarla en su reserva.");
            } else {
                fachada.actualizarPromocion(promocionSeleccionadaId, codigo, descripcion, inicio, fin, tipo,
                        noches, porcentaje, chkPromoActiva.isSelected());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Promoción actualizada", "Se guardaron las condiciones de la promoción.");
            }
            refrescarPromociones();
            handleLimpiarPromocion();
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Condición inválida", "Noches y descuento deben ser valores numéricos.");
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar la promoción", e.getMessage());
        }
    }

    @FXML
    private void handleEliminarPromocion() {
        if (promocionSeleccionadaId == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una promoción", "Selecciona un registro antes de eliminarlo.");
            return;
        }
        if (!confirmar("¿Eliminar la promoción seleccionada?")) return;
        try {
            fachada.eliminarPromocion(promocionSeleccionadaId);
            refrescarPromociones();
            handleLimpiarPromocion();
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar", e.getMessage());
        }
    }

    @FXML
    private void handleLimpiarPromocion() {
        tablaPromociones.getSelectionModel().clearSelection();
        promocionSeleccionadaId = null;
        txtPromoCodigo.clear();
        txtPromoDescripcion.clear();
        dpPromoInicio.setValue(null);
        dpPromoFin.setValue(null);
        cmbPromoTipo.setValue(null);
        txtPromoNoches.clear();
        txtPromoPorcentaje.clear();
        chkPromoActiva.setSelected(true);
    }

    @FXML
    private void handleExportarReporte() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte de ingresos");
        selector.setInitialFileName("reporte-hotelUQ.txt");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de texto (*.txt)", "*.txt"));
        java.io.File archivo = selector.showSaveDialog(tablaReservas.getScene().getWindow());
        if (archivo == null) return;
        try {
            String contenido = new VisitanteReporte().generarReporteTextoIngresos(fachada.obtenerTodasLasReservas());
            Files.writeString(archivo.toPath(), contenido, StandardCharsets.UTF_8);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Reporte exportado", "El reporte se guardó correctamente.");
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", e.getMessage());
        }
    }

    @FXML
    private void refrescarDatos() {
        tablaReservas.setItems(FXCollections.observableArrayList(fachada.obtenerTodasLasReservas()));
        double ocupadas = fachada.obtenerPorcentajeOcupacionActual();
        chartOcupacion.setData(FXCollections.observableArrayList(
                new PieChart.Data(String.format("Ocupación %.1f%%", ocupadas), ocupadas),
                new PieChart.Data(String.format("Disponibles %.1f%%", 100 - ocupadas), 100 - ocupadas)));
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Reservas confirmadas");
        for (TipoHabitacion tipo : TipoHabitacion.values()) {
            serie.getData().add(new XYChart.Data<>(tipo.name(), fachada.obtenerCantidadReservasPorTipo(tipo)));
        }
        chartReservasPorTipo.getData().clear();
        chartReservasPorTipo.getData().add(serie);
    }

    private void refrescarClientes() {
        tablaClientes.setItems(FXCollections.observableArrayList(fachada.obtenerTodosLosClientes()));
    }

    private void refrescarPromociones() {
        tablaPromociones.setItems(FXCollections.observableArrayList(fachada.obtenerPromocionesAdministrativas()));
    }

    private boolean confirmar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.CANCEL, ButtonType.OK);
        alerta.setTitle("Confirmar operación");
        alerta.setHeaderText(null);
        return alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void cambiarVista(String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            boolean acceso = ruta.contains("Login");
            stage.setScene(new javafx.scene.Scene(root, acceso ? 560 : 1180, acceso ? 620 : 760));
            stage.setMinWidth(acceso ? 500 : 960);
            stage.setMinHeight(acceso ? 540 : 640);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de navegación", e.getMessage());
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
