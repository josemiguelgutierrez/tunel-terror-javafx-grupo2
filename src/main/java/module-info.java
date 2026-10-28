module es.medac.grupo2.tunel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens es.medac.grupo2.tunel to javafx.fxml;
    exports es.medac.grupo2.tunel;
}