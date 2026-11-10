package hoteluq.controller;

import hoteluq.model.seguridad.RolUsuario;
import hoteluq.service.AutenticacionService;
import hoteluq.service.SesionUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class LoginController {
    private final AutenticacionService autenticacion = new AutenticacionService();
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;

    @FXML
    private void handleIniciarSesion() {
        char[] contrasena = txtContrasena.getText().toCharArray();
        try {
            autenticacion.iniciarSesion(txtUsuario.getText().trim(), contrasena);
            abrirVista(SesionUsuario.obtenerActual().es(RolUsuario.ADMINISTRADOR)
                    ? "/fxml/AdminView.fxml" : "/fxml/MainView.fxml");
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        } catch (IOException e) {
            mostrarError("No se pudo abrir el panel: " + e.getMessage());
        } finally {
            Arrays.fill(contrasena, '\0');
            txtContrasena.clear();
        }
    }

    @FXML
    private void handleAbrirRegistro() {
        try {
            abrirVista("/fxml/RegisterView.fxml");
        } catch (IOException e) {
            mostrarError("No se pudo abrir el registro: " + e.getMessage());
        }
    }

    private void abrirVista(String ruta) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(ruta));
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root, 1180, 760));
        stage.setMinWidth(960);
        stage.setMinHeight(640);
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Inicio de sesión");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
