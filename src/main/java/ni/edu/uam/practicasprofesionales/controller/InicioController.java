package ni.edu.uam.practicasprofesionales.controller;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.model.alianzas.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.*;
import ni.edu.uam.practicasprofesionales.model.seguimiento.*;
import ni.edu.uam.practicasprofesionales.repository.JpaRepository;
import ni.edu.uam.practicasprofesionales.util.Alertas;
import ni.edu.uam.practicasprofesionales.util.FxDb;
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
        FxDb.ejecutar(() -> new long[] {
            new JpaRepository<>(Estudiante.class).count(),
            new JpaRepository<>(Empresa.class).count(),
            new JpaRepository<>(OfertaPractica.class).count(),
            new JpaRepository<>(Practica.class).count()
        }, cantidades -> {
            totalEstudiantes.setText(String.valueOf(cantidades[0]));
            totalEmpresas.setText(String.valueOf(cantidades[1]));
            totalOfertas.setText(String.valueOf(cantidades[2]));
            totalPracticas.setText(String.valueOf(cantidades[3]));
            resumen.setText("Panel actualizado desde PostgreSQL mediante Hibernate ORM.");
        }, error -> {
            resumen.setText("No hay conexión activa. Revisa PostgreSQL y DB_PASSWORD.");
            Alertas.error(error.getMessage());
        });
    }
}
