package hoteluq.controller;

import hoteluq.service.AutenticacionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class RegisterController {
    private final AutenticacionService autenticacion = new AutenticacionService();
    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private PasswordField txtConfirmarContrasena;

    @FXML
    private void handleRegistrar() {
        char[] contrasena = txtContrasena.getText().toCharArray();
        char[] confirmacion = txtConfirmarContrasena.getText().toCharArray();
        try {
            if (!Arrays.equals(contrasena, confirmacion)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }
            autenticacion.registrarCliente(txtCedula.getText(), txtNombre.getText(), txtCorreo.getText(), contrasena);
            autenticacion.iniciarSesion(txtCedula.getText(), contrasena);
            abrirVista("/fxml/MainView.fxml");
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        } catch (IOException e) {
            mostrarError("La cuenta se creó, pero no se pudo abrir la aplicación: " + e.getMessage());
        } finally {
            Arrays.fill(contrasena, '\0');
            Arrays.fill(confirmacion, '\0');
            txtContrasena.clear();
            txtConfirmarContrasena.clear();
        }
    }

    @FXML
    private void handleVolver() {
        try {
            abrirVista("/fxml/LoginView.fxml");
        } catch (IOException e) {
            mostrarError("No se pudo volver al inicio de sesión: " + e.getMessage());
        }
    }

    private void abrirVista(String ruta) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(ruta));
        Stage stage = (Stage) txtCedula.getScene().getWindow();
        boolean acceso = ruta.contains("Login");
        stage.setScene(new javafx.scene.Scene(root, acceso ? 560 : 1180, acceso ? 620 : 760));
        stage.setMinWidth(acceso ? 500 : 960);
        stage.setMinHeight(acceso ? 540 : 640);
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Registro de cliente");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
