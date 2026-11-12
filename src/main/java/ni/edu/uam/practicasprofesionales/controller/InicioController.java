package ni.edu.uam.practicasprofesionales.controller;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.model.alianzas.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.*;
import ni.edu.uam.practicasprofesionales.model.seguimiento.*;
import ni.edu.uam.practicasprofesionales.repository.JpaRepository;
import ni.edu.uam.practicasprofesionales.util.Alertas;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InicioController {
    @FXML private Label totalEstudiantes;
    @FXML private Label totalEmpresas;
    @FXML private Label totalOfertas;
    @FXML private Label totalPracticas;
    @FXML private Label resumen;

    @FXML public void initialize() { actualizar(); }
    @FXML private void actualizar() {
        try {
            totalEstudiantes.setText(String.valueOf(new JpaRepository<>(Estudiante.class).count()));
            totalEmpresas.setText(String.valueOf(new JpaRepository<>(Empresa.class).count()));
            totalOfertas.setText(String.valueOf(new JpaRepository<>(OfertaPractica.class).count()));
            totalPracticas.setText(String.valueOf(new JpaRepository<>(Practica.class).count()));
            resumen.setText("Panel actualizado desde PostgreSQL mediante Hibernate ORM.");
        } catch (Exception ex) {
            resumen.setText("No hay conexión activa. Revisa PostgreSQL y DB_PASSWORD.");
            Alertas.error(ex.getMessage());
        }
    }
}
