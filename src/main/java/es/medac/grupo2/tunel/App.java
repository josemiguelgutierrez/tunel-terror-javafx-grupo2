package es.medac.grupo2.tunel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

    public static final String PROPIETARIO = "grupo2";

    private SalaController controlador;

    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("fuentes/Creepster-Regular.ttf"), 20);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sala.fxml"));
        Scene scene = new Scene(loader.load());
        controlador = loader.getController();

        scene.getStylesheets().add(getClass().getResource("estilos.css").toExternalForm());

        // F12 reinicia la sala y Ctrl+Shift+Q la cierra
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F12) {
                controlador.reiniciar();
            }
            if (e.isControlDown() && e.isShiftDown() && e.getCode() == KeyCode.Q) {
                stage.close();
            }
        });

        stage.setTitle("Sala 3 · Login maldito · " + PROPIETARIO);
        stage.setScene(scene);

        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        stage.show();
    }

    @Override
    public void stop() {

        if (controlador != null) {
            controlador.detener();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}