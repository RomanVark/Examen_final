module ni.edu.uam.examen_final {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens ni.edu.uam.examen_final to javafx.fxml;
    exports ni.edu.uam.examen_final;
}