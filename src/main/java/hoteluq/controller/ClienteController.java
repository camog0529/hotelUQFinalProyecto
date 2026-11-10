package hoteluq.controller;

import hoteluq.model.clientes.Cliente;
import hoteluq.model.seguridad.RolUsuario;
import hoteluq.service.SesionUsuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

/** Muestra únicamente los datos y movimientos del cliente autenticado. */
public class ClienteController {
    @FXML private Label lblNombre;
    @FXML private Label lblCedula;
    @FXML private Label lblSaldo;
    @FXML private Label lblPuntos;
    @FXML private ListView<String> listMovimientos;
    @FXML private Button btnNavReservas;
    @FXML private Button btnCerrarSesion;

    @FXML
    public void initialize() {
        SesionUsuario sesion = SesionUsuario.obtenerActual();
        if (sesion == null || !sesion.es(RolUsuario.CLIENTE)) {
            Platform.runLater(() -> cambiarVista("/fxml/LoginView.fxml"));
            return;
        }
        Cliente cliente = sesion.getCliente();
        lblNombre.setText(cliente.getNombre());
        lblCedula.setText(cliente.getCedula());
        lblSaldo.setText(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
                .format(cliente.getSaldoTarjeta()));
        lblPuntos.setText(Integer.toString(cliente.getPuntosAcumulados()));
        listMovimientos.getItems().setAll(cliente.getHistorialMovimientos());
        btnNavReservas.setOnAction(event -> cambiarVista("/fxml/MainView.fxml"));
        btnCerrarSesion.setOnAction(event -> {
            SesionUsuario.cerrar();
            cambiarVista("/fxml/LoginView.fxml");
        });
    }

    private void cambiarVista(String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) btnNavReservas.getScene().getWindow();
            boolean acceso = ruta.contains("Login");
            stage.setScene(new javafx.scene.Scene(root, acceso ? 560 : 1180, acceso ? 620 : 760));
            stage.setMinWidth(acceso ? 500 : 960);
            stage.setMinHeight(acceso ? 540 : 640);
        } catch (IOException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo cargar la vista: " + ruta);
            alerta.showAndWait();
        }
    }
}
