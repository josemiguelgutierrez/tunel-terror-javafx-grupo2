package es.medac.grupo2.tunel;

import java.util.*;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import javafx.scene.image.Image;

public class SalaController {

    private static final String LETRA = "E";
    private static final String USUARIO = "caronte";   // pista acordada con 1.º
    private static final String[] ERRORES = {
            "Credenciales rechazadas. El sistema solo reconoce a los muertos.",
            "Este usuario fue dado de baja... hace 30 años.",
            "Error 13: el registro está manchado de sangre.",
            "Intento registrado. Ahora sabemos quién eres.",
            "Usuario desconocido. Pero tú no eres desconocido para nosotros."
    };

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Button btnEntrar;
    @FXML private Label lblMensaje, lblLetra, lblAyuda;
    @FXML private StackPane raiz;

    private ImageView susto;
    private Animation animSusto;
    private boolean comprobando;
    private Timeline escrituraFantasma;
    private int huidas;
    private final Random azar = new Random();
    private MediaPlayer ambiente;
    private final PauseTransition piedad = new PauseTransition(Duration.seconds(60));
    private final PauseTransition autoReinicio = new PauseTransition(Duration.seconds(12));

    @FXML
    private void initialize() {

        escrituraFantasma = new Timeline(new KeyFrame(Duration.millis(450), e -> {
            txtClave.appendText("x");
            if (txtClave.getLength() > 12) txtClave.clear();
        }));
        escrituraFantasma.setCycleCount(Animation.INDEFINITE);


        txtUsuario.textProperty().addListener((obs, antes, ahora) -> {
            if (usuarioCorrecto()) {
                btnEntrar.setTranslateX(0);
                btnEntrar.setTranslateY(0);
                if (!btnEntrar.getStyleClass().contains("despierto")) btnEntrar.getStyleClass().add("despierto");
            } else {
                btnEntrar.getStyleClass().removeAll("despierto");
            }
        });
        txtUsuario.setOnAction(e -> entrar());
        ambiente = new MediaPlayer(new Media(recurso("sonidos/ambiente.mp3")));
        ambiente.setCycleCount(MediaPlayer.INDEFINITE);
        ambiente.setVolume(0.4);
        ambiente.play();
        piedad.setOnFinished(e -> mostrarAyuda());
        autoReinicio.setOnFinished(e -> reiniciar());

        susto = new ImageView(new Image(recurso("img/susto.jpg")));
        susto.setPreserveRatio(false);
        susto.fitWidthProperty().bind(raiz.widthProperty());
        susto.fitHeightProperty().bind(raiz.heightProperty());
        susto.setManaged(false);
        susto.setMouseTransparent(true);
        susto.setVisible(false);
        raiz.getChildren().add(susto);   // la última de la lista = la capa de más arriba
        reiniciar();
    }

    private boolean usuarioCorrecto() {
        return txtUsuario.getText().trim().equalsIgnoreCase(USUARIO);
    }

    @FXML
    private void huir() {
        if (usuarioCorrecto() || lblLetra.isVisible()) return;
        huidas++;
        btnEntrar.setTranslateX(azar.nextDouble() * 600 - 300);
        btnEntrar.setTranslateY(azar.nextDouble() * 160 - 80);
        if (huidas % 4 == 0) {
            efecto("risa.wav");
            lblMensaje.setText("Nadie entra sin identificarse. Ni siquiera tú.");
        }
    }

    @FXML
    private void entrar() {
        if (lblLetra.isVisible() || comprobando) return;
        if (usuarioCorrecto()) {
            comprobando = true;
            escrituraFantasma.stop();
            piedad.stop();
            lblMensaje.setText("Comprobando credenciales...");
            ambiente.setVolume(0);

            PauseTransition espera = new PauseTransition(Duration.seconds(3));
            espera.setOnFinished(e -> lanzarSusto());
            animSusto = espera;
            espera.play();
        } else {
            efecto("fallo.wav");
            lblMensaje.setText(ERRORES[azar.nextInt(ERRORES.length)]);
            temblar(txtUsuario);
        }
    }

    private void lanzarSusto() {
        susto.setOpacity(0);
        susto.setScaleX(0.6);
        susto.setScaleY(0.6);
        susto.setVisible(true);
        efecto("grito.wav");

        FadeTransition aparece = new FadeTransition(Duration.millis(200), susto);
        aparece.setToValue(1);
        ScaleTransition acerca = new ScaleTransition(Duration.millis(200), susto);
        acerca.setToX(1.05);
        acerca.setToY(1.05);

        PauseTransition quieto = new PauseTransition(Duration.seconds(1.5));
        FadeTransition seVa = new FadeTransition(Duration.millis(600), susto);
        seVa.setToValue(0);

        SequentialTransition secuencia = new SequentialTransition(
                new ParallelTransition(aparece, acerca), quieto, seVa);
        secuencia.setOnFinished(e -> {
            susto.setVisible(false);
            ambiente.setVolume(0.4);
            lblMensaje.setText("Le estábamos esperando, " + USUARIO + ". Su camilla está lista...");
            comprobando = false;
            mostrarLetra();
        });
        animSusto = secuencia;
        secuencia.play();
    }

    public void reiniciar() {
        autoReinicio.stop();
        huidas = 0;
        txtUsuario.clear();
        txtClave.clear();
        btnEntrar.setTranslateX(0);
        btnEntrar.setTranslateY(0);
        lblLetra.setVisible(false);
        lblAyuda.setText("");
        lblMensaje.setText("");
        escrituraFantasma.play();
        txtUsuario.requestFocus();
        piedad.playFromStart();
        if (animSusto != null) animSusto.stop();
        susto.setVisible(false);
        comprobando = false;
        ambiente.setVolume(0.4);
    }

    private void mostrarAyuda() {
        lblAyuda.setText("Alguien ha dejado una nota: el usuario empieza por «" + USUARIO.substring(0, 3) + "»");
    }

    // ---------- Comun a todas las salas ----------
    private void mostrarLetra() {
        piedad.stop();
        lblLetra.setText(LETRA);
        lblLetra.setVisible(true);
        ScaleTransition escala = new ScaleTransition(Duration.millis(700), lblLetra);
        escala.setFromX(0.1); escala.setFromY(0.1);
        escala.setToX(1); escala.setToY(1);
        FadeTransition aparicion = new FadeTransition(Duration.millis(700), lblLetra);
        aparicion.setFromValue(0); aparicion.setToValue(1);
        new ParallelTransition(escala, aparicion).play();
        efecto("acierto.wav");
        autoReinicio.playFromStart();   // la sala se reinicia sola para el siguiente grupo
    }

    private void temblar(Node nodo) {
        TranslateTransition t = new TranslateTransition(Duration.millis(50), nodo);
        t.setFromX(-8); t.setToX(8);
        t.setCycleCount(6); t.setAutoReverse(true);
        t.setOnFinished(e -> nodo.setTranslateX(0));
        t.play();
    }

    private void efecto(String archivo) {
        new AudioClip(recurso("sonidos/" + archivo)).play();
    }

    private String recurso(String ruta) {
        // Si sale NullPointerException aqui: el archivo no esta en resources o el nombre no coincide
        return getClass().getResource(ruta).toExternalForm();
    }

    public void detener() {
        ambiente.stop();
    }
}