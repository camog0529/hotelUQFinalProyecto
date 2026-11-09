package hoteluq;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Punto de entrada JavaFX de HotelUQ. */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/LoginView.fxml"));
        Scene scene = new Scene(loader.load(), 560, 620);
        stage.setTitle("HotelUQ | Gestión hotelera");
        stage.setMinWidth(500);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
